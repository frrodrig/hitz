package eus.hitz.core.data.gamification

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import eus.hitz.core.data.database.HitzDatabase
import eus.hitz.core.domain.gamification.Activity
import eus.hitz.core.domain.gamification.ActivityKind
import eus.hitz.core.domain.gamification.StudyDay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [35])
class RoomGamificationRepositoryTest {
    private lateinit var database: HitzDatabase
    private lateinit var repository: RoomGamificationRepository

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(), HitzDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        repository = RoomGamificationRepository(database)
    }

    @After
    fun tearDown() = database.close()

    @Test
    fun aNewLearnerHasNothingYet() = runTest {
        val data = repository.observe().first()
        assertThat(data.activities).isEmpty()
        assertThat(data.studyDays).isEmpty()
    }

    @Test
    fun activitiesAreStored() = runTest {
        val activity =
            Activity("u01-1.l1.s1", ActivityKind.Lesson, 1f, true, 90_000, completedAt = 10, epochDay = 20_000)
        repository.recordActivity(activity)
        assertThat(repository.observe().first().activities).containsExactly(activity)
    }

    @Test
    fun studyTimeAddsUpAndADayTakesTheLatestGoal() = runTest {
        repository.addStudyTime(20_000, 120_000, goalMillis = 600_000)
        val day = repository.addStudyTime(20_000, 60_000, goalMillis = 300_000)
        assertThat(day).isEqualTo(StudyDay(20_000, 180_000, 300_000))
        repository.addStudyTime(20_001, 30_000, goalMillis = 900_000)
        assertThat(repository.observe().first().studyDays.values)
            .containsExactly(StudyDay(20_000, 180_000, 300_000), StudyDay(20_001, 30_000, 900_000))
    }
}
