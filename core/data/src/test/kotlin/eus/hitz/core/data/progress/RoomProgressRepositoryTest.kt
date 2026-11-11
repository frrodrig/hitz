package eus.hitz.core.data.progress

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import eus.hitz.core.data.database.HitzDatabase
import eus.hitz.core.domain.lesson.Mistake
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [35])
class RoomProgressRepositoryTest {
    private lateinit var database: HitzDatabase
    private lateinit var repository: RoomProgressRepository

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(), HitzDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        repository = RoomProgressRepository(database.progressDao())
    }

    @After
    fun tearDown() = database.close()

    @Test
    fun completionsAreObservedAndKeepTheBestAccuracy() = runTest {
        repository.observeCompleted().test {
            assertThat(awaitItem()).isEmpty()
            repository.recordCompletion("u01-1", "u01-1.l1.s1", accuracy = 0.8f, completedAt = 10)
            assertThat(awaitItem()).containsExactly("u01-1.l1.s1")
            repository.recordCompletion("u01-1", "u01-1.l1.s1", accuracy = 0.5f, completedAt = 20)
            expectNoEvents()
        }
        val stored = database.progressDao().completion("u01-1.l1.s1")!!
        assertThat(stored.bestAccuracy).isEqualTo(0.8f)
        assertThat(stored.firstCompletedAt).isEqualTo(10)
        assertThat(stored.lastCompletedAt).isEqualTo(20)
        assertThat(stored.timesCompleted).isEqualTo(2)
    }

    @Test
    fun completionsKeepTheirFirstTime() = runTest {
        repository.recordCompletion("u01-1", "u01-1.l1.s1", accuracy = 0.8f, completedAt = 10)
        repository.recordCompletion("u01-1", "u01-1.l1.s1", accuracy = 1f, completedAt = 30)
        repository.recordCompletion("u01-1", "u01-1.l1.s2", accuracy = 1f, completedAt = 20)
        repository.observeCompletions().test {
            assertThat(awaitItem()).containsExactly("u01-1.l1.s1", 10L, "u01-1.l1.s2", 20L)
        }
    }

    @Test
    fun mistakesAreCountedByExerciseWordAndStructure() = runTest {
        val greeting = Mistake("u01-1.l1.s1.e01", listOf("kaixo", "ni"), listOf("izan-present"), "x", "y")
        val thanks = Mistake("u01-1.l1.s1.e03", listOf("eskerrik asko"), listOf("courtesy"), "x", "y")
        repository.recordMistakes("u01-1", listOf(greeting, greeting, thanks), recordedAt = 1)
        repository.recordMistakes("u01-2", listOf(greeting), recordedAt = 2)

        val stats = repository.mistakeStats("u01-1")
        assertThat(stats.byExercise).containsExactly("u01-1.l1.s1.e01", 2, "u01-1.l1.s1.e03", 1)
        assertThat(stats.byLemma).containsExactly("kaixo", 2, "ni", 2, "eskerrik asko", 1)
        assertThat(stats.byStructure).containsExactly("izan-present", 2, "courtesy", 1)
    }
}
