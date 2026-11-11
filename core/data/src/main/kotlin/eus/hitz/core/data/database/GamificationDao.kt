package eus.hitz.core.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface GamificationDao {
    @Query("SELECT * FROM activities ORDER BY completedAt")
    fun observeActivities(): Flow<List<ActivityEntity>>

    @Insert
    suspend fun insertActivity(activity: ActivityEntity)

    @Query("SELECT * FROM study_days")
    fun observeStudyDays(): Flow<List<StudyDayEntity>>

    @Query("SELECT * FROM study_days WHERE epochDay = :epochDay")
    suspend fun studyDay(epochDay: Long): StudyDayEntity?

    @Upsert
    suspend fun upsertStudyDay(day: StudyDayEntity)
}
