package eus.hitz.core.testing

import eus.hitz.core.domain.gamification.Activity
import eus.hitz.core.domain.gamification.GameClock
import eus.hitz.core.domain.gamification.GamificationRepository
import eus.hitz.core.domain.gamification.PlayerData
import eus.hitz.core.domain.gamification.StudyDay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import java.time.ZoneOffset

/** In-memory store of sessions and study time, with a fixed clock ([NOW], UTC). */
class FakeGamificationRepository(initial: PlayerData = PlayerData()) : GamificationRepository {
    val data = MutableStateFlow(initial)
    val clock = GameClock({ NOW }, { ZoneOffset.UTC })

    override fun observe(): Flow<PlayerData> = data

    override suspend fun recordActivity(activity: Activity) {
        data.value = data.value.copy(activities = data.value.activities + activity)
    }

    override suspend fun addStudyTime(epochDay: Long, millis: Long, goalMillis: Long): StudyDay {
        val stored = data.value.studyDays[epochDay] ?: StudyDay(epochDay, 0, goalMillis)
        val day = stored.copy(activeMillis = stored.activeMillis + millis, goalMillis = goalMillis)
        data.value = data.value.copy(studyDays = data.value.studyDays + (epochDay to day))
        return day
    }

    companion object {
        /** 2026-11-04 (a Wednesday), 10:00 UTC. */
        const val NOW = 1_793_786_400_000L
    }
}
