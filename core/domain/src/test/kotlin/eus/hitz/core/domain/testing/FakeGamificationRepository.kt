package eus.hitz.core.domain.testing

import eus.hitz.core.domain.gamification.Activity
import eus.hitz.core.domain.gamification.GameClock
import eus.hitz.core.domain.gamification.GamificationRepository
import eus.hitz.core.domain.gamification.LeagueState
import eus.hitz.core.domain.gamification.PlayerData
import eus.hitz.core.domain.gamification.StreakDayKind
import eus.hitz.core.domain.gamification.Wallet
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import java.time.LocalDate
import java.time.ZoneOffset

internal class FakeGamificationRepository(initial: PlayerData = PlayerData()) : GamificationRepository {
    val data = MutableStateFlow(initial)

    override fun observe(): Flow<PlayerData> = data

    override suspend fun recordActivity(activity: Activity) {
        data.value = data.value.copy(activities = data.value.activities + activity)
    }

    override suspend fun addStreakDays(days: Map<Long, StreakDayKind>) {
        data.value = data.value.copy(streakDays = data.value.streakDays + days)
    }

    override suspend fun updateWallet(transform: (Wallet) -> Wallet): Wallet {
        val wallet = transform(data.value.wallet)
        data.value = data.value.copy(wallet = wallet)
        return wallet
    }

    override suspend fun updateLeague(transform: (LeagueState) -> LeagueState): LeagueState {
        val league = transform(data.value.league)
        data.value = data.value.copy(league = league)
        return league
    }

    override suspend fun claim(rewardId: String, gems: Int, claimedAt: Long): Boolean {
        if (rewardId in data.value.claimed) return false
        data.value = data.value.copy(
            claimed = data.value.claimed + rewardId,
            wallet = data.value.wallet.copy(gems = data.value.wallet.gems + gems),
        )
        return true
    }
}

/** A controllable clock in UTC. */
internal class TestClock(
    var now: Long = LocalDate.of(2026, 11, 4).atTime(10, 0).toInstant(ZoneOffset.UTC).toEpochMilli()
) {
    val gameClock = GameClock({ now }, { ZoneOffset.UTC })
    val today: Long get() = gameClock.today()

    fun advanceDays(days: Int) {
        now += days * DAY
    }

    fun advanceHours(hours: Int) {
        now += hours * HOUR
    }

    companion object {
        const val HOUR = 60 * 60 * 1000L
        const val DAY = 24 * HOUR
    }
}
