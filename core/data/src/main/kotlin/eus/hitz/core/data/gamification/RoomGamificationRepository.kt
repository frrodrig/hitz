package eus.hitz.core.data.gamification

import androidx.room.withTransaction
import eus.hitz.core.data.database.ActivityEntity
import eus.hitz.core.data.database.ClaimedRewardEntity
import eus.hitz.core.data.database.GamificationDao
import eus.hitz.core.data.database.HitzDatabase
import eus.hitz.core.data.database.PlayerEntity
import eus.hitz.core.data.database.StreakDayEntity
import eus.hitz.core.domain.gamification.Activity
import eus.hitz.core.domain.gamification.ActivityKind
import eus.hitz.core.domain.gamification.GamificationRepository
import eus.hitz.core.domain.gamification.LeagueOutcome
import eus.hitz.core.domain.gamification.LeagueState
import eus.hitz.core.domain.gamification.PlayerData
import eus.hitz.core.domain.gamification.StreakDayKind
import eus.hitz.core.domain.gamification.Wallet
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

internal class RoomGamificationRepository @Inject constructor(
    private val database: HitzDatabase,
) : GamificationRepository {
    private val dao: GamificationDao = database.gamificationDao()

    override fun observe(): Flow<PlayerData> = combine(
        dao.observePlayer(),
        dao.observeActivities(),
        dao.observeStreakDays(),
        dao.observeClaimedIds(),
    ) { player, activities, days, claimed ->
        val row = player ?: defaultPlayer
        PlayerData(
            wallet = row.wallet(),
            league = row.league(),
            activities = activities.map { it.toDomain() },
            streakDays = days.associate { it.epochDay to StreakDayKind.valueOf(it.kind) },
            claimed = claimed.toSet(),
        )
    }

    override suspend fun recordActivity(activity: Activity) = dao.insertActivity(
        ActivityEntity(
            sessionId = activity.sessionId,
            kind = activity.kind.name,
            accuracy = activity.accuracy,
            perfect = activity.perfect,
            xp = activity.xp,
            completedAt = activity.completedAt,
            epochDay = activity.epochDay,
        ),
    )

    override suspend fun addStreakDays(days: Map<Long, StreakDayKind>) =
        dao.upsertStreakDays(days.map { (day, kind) -> StreakDayEntity(day, kind.name) })

    override suspend fun updateWallet(transform: (Wallet) -> Wallet): Wallet = database.withTransaction {
        val row = dao.player() ?: defaultPlayer
        val wallet = transform(row.wallet())
        dao.upsertPlayer(row.withWallet(wallet))
        wallet
    }

    override suspend fun updateLeague(transform: (LeagueState) -> LeagueState): LeagueState =
        database.withTransaction {
            val row = dao.player() ?: defaultPlayer
            val league = transform(row.league())
            dao.upsertPlayer(row.withLeague(league))
            league
        }

    override suspend fun claim(rewardId: String, gems: Int, claimedAt: Long): Boolean = database.withTransaction {
        val inserted = dao.insertClaimed(ClaimedRewardEntity(rewardId, gems, claimedAt)) != -1L
        if (inserted) {
            val row = dao.player() ?: defaultPlayer
            dao.upsertPlayer(row.copy(gems = row.gems + gems))
        }
        inserted
    }

    private companion object {
        val defaultPlayer: PlayerEntity = Wallet().let { wallet ->
            PlayerEntity(
                gems = wallet.gems,
                hearts = wallet.hearts,
                heartsUpdatedAt = wallet.heartsUpdatedAt,
                streakFreezes = wallet.streakFreezes,
                leagueTier = 0,
                leagueWeekStart = LeagueState.NOT_JOINED,
                leagueLastRank = null,
                leagueLastOutcome = null,
            )
        }

        fun PlayerEntity.wallet() = Wallet(gems, hearts, heartsUpdatedAt, streakFreezes)

        fun PlayerEntity.league() = LeagueState(
            tier = leagueTier,
            weekStart = leagueWeekStart,
            lastRank = leagueLastRank,
            lastOutcome = leagueLastOutcome?.let { LeagueOutcome.valueOf(it) },
        )

        fun PlayerEntity.withWallet(wallet: Wallet) = copy(
            gems = wallet.gems,
            hearts = wallet.hearts,
            heartsUpdatedAt = wallet.heartsUpdatedAt,
            streakFreezes = wallet.streakFreezes,
        )

        fun PlayerEntity.withLeague(league: LeagueState) = copy(
            leagueTier = league.tier,
            leagueWeekStart = league.weekStart,
            leagueLastRank = league.lastRank,
            leagueLastOutcome = league.lastOutcome?.name,
        )

        fun ActivityEntity.toDomain() =
            Activity(sessionId, ActivityKind.valueOf(kind), accuracy, perfect, xp, completedAt, epochDay)
    }
}
