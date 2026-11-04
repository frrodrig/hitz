package eus.hitz.core.data.database

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        CompletionEntity::class,
        MistakeEntity::class,
        MistakeTagEntity::class,
        ActivityEntity::class,
        StreakDayEntity::class,
        PlayerEntity::class,
        ClaimedRewardEntity::class,
    ],
    version = 2,
    exportSchema = true,
    autoMigrations = [
        // 2: gamification tables (activities, streak days, player, claimed rewards).
        AutoMigration(from = 1, to = 2),
    ],
)
abstract class HitzDatabase : RoomDatabase() {
    abstract fun progressDao(): ProgressDao

    abstract fun gamificationDao(): GamificationDao

    companion object {
        const val NAME = "hitz.db"
    }
}
