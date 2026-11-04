package eus.hitz.core.data.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [CompletionEntity::class, MistakeEntity::class, MistakeTagEntity::class],
    version = 1,
    exportSchema = true,
)
abstract class HitzDatabase : RoomDatabase() {
    abstract fun progressDao(): ProgressDao

    companion object {
        const val NAME = "hitz.db"
    }
}
