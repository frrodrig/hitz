package eus.hitz.core.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface GamificationDao {
    @Query("SELECT * FROM activities ORDER BY completedAt")
    fun observeActivities(): Flow<List<ActivityEntity>>

    @Insert
    suspend fun insertActivity(activity: ActivityEntity)

    @Query("SELECT * FROM streak_days")
    fun observeStreakDays(): Flow<List<StreakDayEntity>>

    @Upsert
    suspend fun upsertStreakDays(days: List<StreakDayEntity>)

    @Query("SELECT * FROM player WHERE id = ${PlayerEntity.ID}")
    fun observePlayer(): Flow<PlayerEntity?>

    @Query("SELECT * FROM player WHERE id = ${PlayerEntity.ID}")
    suspend fun player(): PlayerEntity?

    @Upsert
    suspend fun upsertPlayer(player: PlayerEntity)

    @Query("SELECT id FROM claimed_rewards")
    fun observeClaimedIds(): Flow<List<String>>

    /** Returns -1 when the reward was already claimed. */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertClaimed(reward: ClaimedRewardEntity): Long
}
