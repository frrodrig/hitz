package eus.hitz.core.domain.testing

import eus.hitz.core.domain.gamification.Activity
import eus.hitz.core.domain.gamification.GameClock
import eus.hitz.core.domain.gamification.GamificationRepository
import eus.hitz.core.domain.gamification.PlayerData
import eus.hitz.core.domain.gamification.StudyDay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import java.time.LocalDate
import java.time.ZoneOffset

internal class FakeGamificationRepository(initial: PlayerData = PlayerData()) : GamificationRepository {
    val data = MutableStateFlow(initial)

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
}

/** A controllable clock in UTC. */
internal class TestClock(
    var now: Long = LocalDate.of(2026, 11, 4).atTime(10, 0).toInstant(ZoneOffset.UTC).toEpochMilli()
) {
    val gameClock = GameClock({ now }, { ZoneOffset.UTC })
    val today: Long get() = gameClock.today()

    fun advanceDays(days: Int) {
        now += days * DAY
    }

    fun advanceHours(hours: Int) {
        now += hours * HOUR
    }

    companion object {
        const val HOUR = 60 * 60 * 1000L
        const val DAY = 24 * HOUR
    }
}
