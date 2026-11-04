package eus.hitz.core.domain.session

import com.google.common.truth.Truth.assertThat
import eus.hitz.core.domain.lesson.LessonResult
import eus.hitz.core.domain.lesson.Mistake
import eus.hitz.core.domain.progress.MistakeStats
import eus.hitz.core.domain.testing.FakeProgressRepository
import eus.hitz.core.domain.testing.TestContent
import kotlinx.coroutines.test.runTest
import org.junit.Test
import kotlin.random.Random

class SessionUseCasesTest {
    private val progress = FakeProgressRepository()
    private val plan = PlanSessionUseCase(TestContent.courseRepository, progress)

    @Test
    fun `a lesson level plays the requested lesson`() = runTest {
        val session = plan("u01-1", "u01-1.l2", "u01-1.l2.s2")
        assertThat(session.sessionId).isEqualTo("u01-1.l2.s2")
        assertThat(session.hearts).isEqualTo(PlanSessionUseCase.LESSON_HEARTS)
    }

    @Test
    fun `the review puts the most missed exercises first`() = runTest {
        progress.stats = MistakeStats(
            byExercise = mapOf("u01-1.l2.s2.e01" to 1),
            byStructure = mapOf("izan-present" to 5),
        )
        val session = plan("u01-1", "u01-1.l4", null, Random(1))
        assertThat(session.sessionId).isEqualTo("u01-1.l4")
        assertThat(session.exercises.map { it.id })
            .containsExactly("u01-1.l2.s1.e01", "u01-1.l2.s2.e01", "u01-1.l1.s1.e01")
            .inOrder()
    }

    @Test
    fun `the legendary level plays every exercise with fewer hearts`() = runTest {
        val session = plan("u01-1", "u01-1.l5", null, Random(1))
        assertThat(session.exercises).hasSize(3)
        assertThat(session.hearts).isEqualTo(PlanSessionUseCase.LEGENDARY_HEARTS)
    }

    @Test
    fun `recording saves mistakes and the completion`() = runTest {
        val mistake = Mistake("u01-1.l1.s1.e01", listOf("kaixo"), emptyList(), "a", "b")
        val record = RecordSessionUseCase(progress) { 42L }
        record("u01-1", "u01-1.l1.s1", LessonResult("u01-1.l1.s1", true, 2, 1, 4, listOf(mistake)))
        assertThat(progress.recordedMistakes).containsExactly(mistake)
        assertThat(progress.completed.value).containsExactly("u01-1.l1.s1")
        assertThat(progress.accuracies["u01-1.l1.s1"]).isEqualTo(0.5f)
    }

    @Test
    fun `an unfinished session saves mistakes only`() = runTest {
        val mistake = Mistake("e", emptyList(), emptyList(), "a", "b")
        RecordSessionUseCase(progress) { 0L }("u01-1", "s", LessonResult("s", false, 1, 0, 0, listOf(mistake)))
        assertThat(progress.completed.value).isEmpty()
        assertThat(progress.recordedMistakes).hasSize(1)
    }
}
