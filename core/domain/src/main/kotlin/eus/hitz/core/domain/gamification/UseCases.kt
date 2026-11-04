package eus.hitz.core.domain.gamification

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/** A reward granted automatically: gems for a quest, an achievement tier or a league rank. */
sealed interface Reward {
    val gems: Int

    data class QuestDone(val quest: Quest) : Reward {
        override val gems: Int get() = quest.gems
    }

    data class AchievementTier(val type: AchievementType, val tier: Int) : Reward {
        override val gems: Int get() = Achievements.tierGems(tier)
    }

    data class LeagueRank(val rank: Int) : Reward {
        override val gems: Int get() = Leagues.gemsForRank(rank)
    }
}

/** What a finished session earned. [streakExtended] is true for the first session of the day. */
data class SessionReward(
    val xp: Int,
    val streak: Int,
    val streakExtended: Boolean,
    val rewards: List<Reward>,
)

class ObservePlayerUseCase @Inject constructor(
    private val repository: GamificationRepository,
    private val clock: GameClock,
) {
    operator fun invoke(): Flow<PlayerSummary> = repository.observe().map { it.summary(clock.now(), clock.today()) }
}

/** Grants the gems of every quest done today and every achievement tier reached, once. */
class GrantRewardsUseCase @Inject constructor(
    private val repository: GamificationRepository,
    private val clock: GameClock,
) {
    suspend operator fun invoke(): List<Reward> {
        val data = repository.observe().first()
        val today = clock.today()
        val now = clock.now()
        val todayActivities = data.activities.filter { it.epochDay == today }
        val quests = DailyQuests.forDay(today)
            .filter { DailyQuests.progress(it, todayActivities) >= it.target }
            .filter { repository.claim(DailyQuests.rewardId(today, it), it.gems, now) }
            .map { Reward.QuestDone(it) }
        val achievements = Achievements.progress(data).flatMap { progress ->
            (1..progress.tier)
                .filter { tier ->
                    repository.claim(
                        Achievements.rewardId(progress.type, tier),
                        Achievements.tierGems(tier),
                        now
                    )
                }
                .map { tier -> Reward.AchievementTier(progress.type, tier) }
        }
        return quests + achievements
    }
}

/** Saves a finished session's XP and streak day, gives a heart for reviews and grants rewards. */
class CompleteSessionUseCase @Inject constructor(
    private val repository: GamificationRepository,
    private val clock: GameClock,
    private val grantRewards: GrantRewardsUseCase,
) {
    suspend operator fun invoke(
        sessionId: String,
        kind: ActivityKind,
        accuracy: Float,
        perfect: Boolean
    ): SessionReward {
        val now = clock.now()
        val today = clock.today()
        val before = repository.observe().first()
        val xp = XpRules.xp(kind, perfect)
        repository.recordActivity(Activity(sessionId, kind, accuracy, perfect, xp, now, today))
        val extended = before.streakDays[today] != StreakDayKind.Practiced
        repository.addStreakDays(mapOf(today to StreakDayKind.Practiced))
        if (kind == ActivityKind.Review) repository.updateWallet { HeartRules.gain(it, now) }
        val rewards = grantRewards()
        val streak = StreakRules.current(before.streakDays + (today to StreakDayKind.Practiced), today)
        return SessionReward(xp, streak, extended, rewards)
    }
}

/**
 * Brings the stored state up to date, on app start and resume: regenerates hearts, spends streak
 * freezes on missed days, and closes the past league week (promotion, demotion and rank gems).
 */
class RefreshPlayerUseCase @Inject constructor(
    private val repository: GamificationRepository,
    private val clock: GameClock,
    private val grantRewards: GrantRewardsUseCase,
) {
    suspend operator fun invoke() {
        val now = clock.now()
        val today = clock.today()
        repository.updateWallet { HeartRules.regenerate(it, now) }
        val data = repository.observe().first()
        val frozen = StreakRules.daysToFreeze(data.streakDays, today, data.wallet.streakFreezes)
        if (frozen.isNotEmpty()) {
            repository.addStreakDays(frozen.associateWith { StreakDayKind.Frozen })
            repository.updateWallet { it.copy(streakFreezes = it.streakFreezes - frozen.size) }
        }
        closeLeagueWeek(data, today, now)
        grantRewards()
    }

    private suspend fun closeLeagueWeek(data: PlayerData, today: Long, now: Long) {
        val league = data.league
        val currentWeek = Leagues.weekStart(today)
        if (league.weekStart == LeagueState.NOT_JOINED) {
            repository.updateLeague { it.copy(weekStart = currentWeek) }
            return
        }
        if (league.weekStart >= currentWeek) return
        val standings = Leagues.standings(league.weekStart, league.tier, data.weeklyXp(league.weekStart), elapsed = 1.0)
        val rank = standings.first { it.isLearner }.rank
        val outcome = Leagues.outcome(rank, league.tier)
        val gems = Leagues.gemsForRank(rank)
        if (gems > 0) repository.claim(Leagues.rewardId(league.weekStart), gems, now)
        repository.updateLeague {
            LeagueState(Leagues.nextTier(it.tier, outcome), currentWeek, lastRank = rank, lastOutcome = outcome)
        }
    }
}

/** Shared hearts for lessons. */
class HeartsUseCase @Inject constructor(
    private val repository: GamificationRepository,
    private val clock: GameClock,
) {
    suspend fun current(): Int = HeartRules.regenerate(repository.observe().first().wallet, clock.now()).hearts

    suspend fun lose(): Int = repository.updateWallet { HeartRules.lose(it, clock.now()) }.hearts
}

enum class PurchaseResult { Bought, NotEnoughGems, NotNeeded }

class BuyItemUseCase @Inject constructor(
    private val repository: GamificationRepository,
    private val clock: GameClock,
) {
    suspend operator fun invoke(item: ShopItem): PurchaseResult {
        var result = PurchaseResult.Bought
        repository.updateWallet { stored ->
            val wallet = HeartRules.regenerate(stored, clock.now())
            val needed = when (item) {
                ShopItem.HeartRefill -> wallet.hearts < HeartRules.MAX_HEARTS
                ShopItem.StreakFreeze -> wallet.streakFreezes < StreakRules.MAX_FREEZES
            }
            result = when {
                !needed -> PurchaseResult.NotNeeded
                wallet.gems < item.price -> PurchaseResult.NotEnoughGems
                else -> PurchaseResult.Bought
            }
            if (result != PurchaseResult.Bought) {
                wallet
            } else {
                when (item) {
                    ShopItem.HeartRefill -> wallet.copy(gems = wallet.gems - item.price, hearts = HeartRules.MAX_HEARTS)
                    ShopItem.StreakFreeze -> wallet.copy(
                        gems = wallet.gems - item.price,
                        streakFreezes = wallet.streakFreezes + 1
                    )
                }
            }
        }
        return result
    }
}

/** The weekly ranking as it stands now. [daysLeft] counts today. */
data class LeagueView(
    val tier: Int,
    val daysLeft: Int,
    val standings: List<Standing>,
    val lastRank: Int?,
    val lastOutcome: LeagueOutcome?,
)

class ObserveLeagueUseCase @Inject constructor(
    private val repository: GamificationRepository,
    private val clock: GameClock,
) {
    operator fun invoke(): Flow<LeagueView> = repository.observe().map { data ->
        val today = clock.today()
        val league = data.league
        val week = if (league.weekStart == LeagueState.NOT_JOINED) Leagues.weekStart(today) else league.weekStart
        LeagueView(
            tier = league.tier,
            daysLeft = (week + Leagues.DAYS_PER_WEEK - today).toInt().coerceAtLeast(1),
            standings = Leagues.standings(week, league.tier, data.weeklyXp(week), clock.weekElapsed(week)),
            lastRank = league.lastRank,
            lastOutcome = league.lastOutcome,
        )
    }
}
