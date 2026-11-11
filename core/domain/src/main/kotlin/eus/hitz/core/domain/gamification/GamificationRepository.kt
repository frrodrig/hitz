package eus.hitz.core.domain.gamification

import kotlinx.coroutines.flow.Flow

/** Local store of sessions and study time. A future backend would sync it. */
interface GamificationRepository {
    fun observe(): Flow<PlayerData>

    suspend fun recordActivity(activity: Activity)

    /**
     * Adds [millis] of study time to [epochDay] and sets its goal to [goalMillis] (the learner's
     * goal at that moment). Returns the updated day.
     */
    suspend fun addStudyTime(epochDay: Long, millis: Long, goalMillis: Long): StudyDay
}
