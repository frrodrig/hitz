package eus.hitz.core.domain.gamification

import kotlinx.coroutines.flow.Flow

/** Local store of XP, streak, wallet, rewards and league. A future backend would sync it. */
interface GamificationRepository {
    fun observe(): Flow<PlayerData>

    suspend fun recordActivity(activity: Activity)

    suspend fun addStreakDays(days: Map<Long, StreakDayKind>)

    /** Reads, transforms and saves the wallet atomically; returns the saved wallet. */
    suspend fun updateWallet(transform: (Wallet) -> Wallet): Wallet

    suspend fun updateLeague(transform: (LeagueState) -> LeagueState): LeagueState

    /** Adds [gems] once for [rewardId]; returns false if it was already claimed. */
    suspend fun claim(rewardId: String, gems: Int, claimedAt: Long): Boolean
}
