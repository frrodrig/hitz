package eus.hitz.core.data.database

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.DeleteColumn
import androidx.room.DeleteTable
import androidx.room.RoomDatabase
import androidx.room.migration.AutoMigrationSpec

@Database(
    entities = [
        CompletionEntity::class,
        MistakeEntity::class,
        MistakeTagEntity::class,
        ActivityEntity::class,
        StudyDayEntity::class,
    ],
    version = 3,
    exportSchema = true,
    autoMigrations = [
        // 2: gamification tables (activities, streak days, player, claimed rewards).
        AutoMigration(from = 1, to = 2),
        // 3: the daily goal replaces XP, streak, hearts, gems and leagues.
        AutoMigration(from = 2, to = 3, spec = HitzDatabase.DailyGoalMigration::class),
    ],
)
abstract class HitzDatabase : RoomDatabase() {
    abstract fun progressDao(): ProgressDao

    abstract fun gamificationDao(): GamificationDao

    @DeleteTable(tableName = "streak_days")
    @DeleteTable(tableName = "player")
    @DeleteTable(tableName = "claimed_rewards")
    @DeleteColumn(tableName = "activities", columnName = "xp")
    class DailyGoalMigration : AutoMigrationSpec

    companion object {
        const val NAME = "hitz.db"
    }
}
