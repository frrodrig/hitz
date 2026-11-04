package eus.hitz.core.testing

import eus.hitz.core.domain.gamification.Activity
import eus.hitz.core.domain.gamification.GameClock
import eus.hitz.core.domain.gamification.GamificationRepository
import eus.hitz.core.domain.gamification.LeagueState
import eus.hitz.core.domain.gamification.PlayerData
import eus.hitz.core.domain.gamification.StreakDayKind
import eus.hitz.core.domain.gamification.Wallet
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import java.time.ZoneOffset

/** In-memory gamification store with a fixed clock ([NOW], UTC). */
class FakeGamificationRepository(initial: PlayerData = PlayerData()) : GamificationRepository {
    val data = MutableStateFlow(initial)
    val clock = GameClock({ NOW }, { ZoneOffset.UTC })

    override fun observe(): Flow<PlayerData> = data

    override suspend fun recordActivity(activity: Activity) {
        data.value = data.value.copy(activities = data.value.activities + activity)
    }

    override suspend fun addStreakDays(days: Map<Long, StreakDayKind>) {
        data.value = data.value.copy(streakDays = data.value.streakDays + days)
    }

    override suspend fun updateWallet(transform: (Wallet) -> Wallet): Wallet =
        transform(data.value.wallet).also { data.value = data.value.copy(wallet = it) }

    override suspend fun updateLeague(transform: (LeagueState) -> LeagueState): LeagueState =
        transform(data.value.league).also { data.value = data.value.copy(league = it) }

    override suspend fun claim(rewardId: String, gems: Int, claimedAt: Long): Boolean {
        if (rewardId in data.value.claimed) return false
        data.value = data.value.copy(
            claimed = data.value.claimed + rewardId,
            wallet = data.value.wallet.copy(gems = data.value.wallet.gems + gems),
        )
        return true
    }

    companion object {
        /** 2026-11-04 (a Wednesday), 10:00 UTC. */
        const val NOW = 1_793_786_400_000L
    }
}
