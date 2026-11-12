package eus.hitz.feature.path

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import eus.hitz.core.domain.content.CourseRepository
import eus.hitz.core.domain.gamification.ObservePlayerUseCase
import eus.hitz.core.domain.lesson.Mistake
import eus.hitz.core.domain.path.ObserveLearningPathUseCase
import eus.hitz.core.domain.progress.MistakeStats
import eus.hitz.core.domain.progress.ProgressRepository
import eus.hitz.core.domain.score.ObserveScoreUseCase
import eus.hitz.core.model.CourseId
import eus.hitz.core.model.content.Course
import eus.hitz.core.model.content.CourseUnit
import eus.hitz.core.model.content.Exercise
import eus.hitz.core.model.content.Guidebook
import eus.hitz.core.model.content.Lesson
import eus.hitz.core.model.content.Level
import eus.hitz.core.model.content.LocalizedText
import eus.hitz.core.model.content.Section
import eus.hitz.core.testing.FakeGamificationRepository
import eus.hitz.core.testing.FakeSettingsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.time.ZoneOffset

class PathViewModelTest {
    private val text = LocalizedText("x", "x")
    private val unit = CourseUnit(
        schemaVersion = 1,
        id = "u01-1",
        section = "s1",
        bookUnit = 1,
        title = text,
        goals = listOf(text),
        guidebook = Guidebook(emptyList()),
        levels = listOf(
            Level.Lessons("u01-1.l1", text, listOf(Lesson("u01-1.l1.s1", listOf(Exercise.Speak("e", "a", "b"))))),
        ),
    )
    private val courses = object : CourseRepository {
        override suspend fun getCourse(courseId: CourseId) =
            Course(1, "eu-es", "eu", "es", text, listOf(Section("s1", text, text, listOf("u01-1"))))

        override suspend fun getUnit(courseId: CourseId, unitId: String): CourseUnit = unit
    }
    private val completed = MutableStateFlow<Set<String>>(emptySet())
    private val progress = object : ProgressRepository {
        override fun observeCompleted(): Flow<Set<String>> = completed

        override fun observeCompletions(): Flow<Map<String, Long>> = completed.map { ids -> ids.associateWith { 0L } }

        override suspend fun recordCompletion(
            unitId: String,
            completionId: String,
            accuracy: Float,
            completedAt: Long,
        ) = Unit

        override suspend fun recordMistakes(unitId: String, mistakes: List<Mistake>, recordedAt: Long) = Unit

        override suspend fun mistakeStats(unitId: String) = MistakeStats()
    }

    @Before
    fun setUp() = Dispatchers.setMain(UnconfinedTestDispatcher())

    @After
    fun tearDown() = Dispatchers.resetMain()

    @Test
    fun `the path updates when a lesson is completed`() = runTest {
        val gamification = FakeGamificationRepository()
        val settings = FakeSettingsRepository()
        val vm = PathViewModel(
            ObserveLearningPathUseCase(courses, progress),
            ObservePlayerUseCase(gamification, settings, gamification.clock),
            ObserveScoreUseCase(courses, progress, { 0L }, { ZoneOffset.UTC }),
            settings,
        )
        vm.state.test {
            var state = awaitItem()
            if (state is PathUiState.Loading) state = awaitItem()
            val ready = state as PathUiState.Ready
            assertThat(ready.path.currentNode?.levelId).isEqualTo("u01-1.l1")
            assertThat(ready.dailyGoal).isEqualTo(0f)
            assertThat(ready.seenUnitIntros).isEmpty()
            assertThat(ready.score).isEqualTo(0)
            settings.markUnitIntroSeen("u01-1")
            assertThat((awaitItem() as PathUiState.Ready).seenUnitIntros).containsExactly("u01-1")
            completed.value = setOf("u01-1.l1.s1")
            // The path and the score both change: wait for the path.
            var next = awaitItem() as PathUiState.Ready
            while (next.path.currentNode != null) next = awaitItem() as PathUiState.Ready
            assertThat(next.path.currentNode).isNull()
            cancelAndIgnoreRemainingEvents()
        }
    }
}
