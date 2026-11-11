package eus.hitz.core.data.gamification

import androidx.room.withTransaction
import eus.hitz.core.data.database.ActivityEntity
import eus.hitz.core.data.database.GamificationDao
import eus.hitz.core.data.database.HitzDatabase
import eus.hitz.core.data.database.StudyDayEntity
import eus.hitz.core.domain.gamification.Activity
import eus.hitz.core.domain.gamification.ActivityKind
import eus.hitz.core.domain.gamification.GamificationRepository
import eus.hitz.core.domain.gamification.PlayerData
import eus.hitz.core.domain.gamification.StudyDay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

internal class RoomGamificationRepository @Inject constructor(
    private val database: HitzDatabase,
) : GamificationRepository {
    private val dao: GamificationDao = database.gamificationDao()

    override fun observe(): Flow<PlayerData> = combine(
        dao.observeActivities(),
        dao.observeStudyDays(),
    ) { activities, days ->
        PlayerData(
            activities = activities.map { it.toDomain() },
            studyDays = days.associate { it.epochDay to StudyDay(it.epochDay, it.activeMillis, it.goalMillis) },
        )
    }

    override suspend fun recordActivity(activity: Activity) = dao.insertActivity(
        ActivityEntity(
            sessionId = activity.sessionId,
            kind = activity.kind.name,
            accuracy = activity.accuracy,
            perfect = activity.perfect,
            activeMillis = activity.activeMillis,
            completedAt = activity.completedAt,
            epochDay = activity.epochDay,
        ),
    )

    override suspend fun addStudyTime(epochDay: Long, millis: Long, goalMillis: Long): StudyDay =
        database.withTransaction {
            val stored = dao.studyDay(epochDay) ?: StudyDayEntity(epochDay, 0, goalMillis)
            val updated = stored.copy(
                activeMillis = stored.activeMillis + millis.coerceAtLeast(0),
                goalMillis = goalMillis
            )
            dao.upsertStudyDay(updated)
            StudyDay(updated.epochDay, updated.activeMillis, updated.goalMillis)
        }

    private companion object {
        fun ActivityEntity.toDomain() =
            Activity(sessionId, ActivityKind.valueOf(kind), accuracy, perfect, activeMillis, completedAt, epochDay)
    }
}
