package eus.hitz.core.data.database

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/** A finished session and the XP it earned. */
@Entity(tableName = "activities", indices = [Index("epochDay")])
data class ActivityEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sessionId: String,
    val kind: String,
    val accuracy: Float,
    val perfect: Boolean,
    val xp: Int,
    val completedAt: Long,
    val epochDay: Long,
)

/** A day that counts for the streak: practiced or covered by a freeze. */
@Entity(tableName = "streak_days")
data class StreakDayEntity(
    @PrimaryKey val epochDay: Long,
    val kind: String,
)

/** The single row of wallet and league state. */
@Entity(tableName = "player")
data class PlayerEntity(
    @PrimaryKey val id: Int = ID,
    val gems: Int,
    val hearts: Int,
    val heartsUpdatedAt: Long,
    val streakFreezes: Int,
    val leagueTier: Int,
    val leagueWeekStart: Long,
    val leagueLastRank: Int?,
    val leagueLastOutcome: String?,
) {
    companion object {
        const val ID = 0
    }
}

/** A reward already granted (quest, achievement tier or league week), so it is granted once. */
@Entity(tableName = "claimed_rewards")
data class ClaimedRewardEntity(
    @PrimaryKey val id: String,
    val gems: Int,
    val claimedAt: Long,
)
