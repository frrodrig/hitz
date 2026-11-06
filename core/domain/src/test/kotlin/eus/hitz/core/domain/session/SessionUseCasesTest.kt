package eus.hitz.core.domain.session

import com.google.common.truth.Truth.assertThat
import eus.hitz.core.domain.gamification.ActivityKind
import eus.hitz.core.domain.gamification.CompleteSessionUseCase
import eus.hitz.core.domain.gamification.GrantRewardsUseCase
import eus.hitz.core.domain.lesson.LessonResult
import eus.hitz.core.domain.lesson.Mistake
import eus.hitz.core.domain.progress.MistakeStats
import eus.hitz.core.domain.testing.FakeGamificationRepository
import eus.hitz.core.domain.testing.FakeProgressRepository
import eus.hitz.core.domain.testing.TestClock
import eus.hitz.core.domain.testing.TestContent
import eus.hitz.core.model.content.Exercise
import kotlinx.coroutines.test.runTest
import org.junit.Test
import kotlin.random.Random

class SessionUseCasesTest {
    private val progress = FakeProgressRepository()
    private val plan = PlanSessionUseCase(TestContent.courseRepository, progress)
    private val gamification = FakeGamificationRepository()
    private val clock = TestClock()

    private fun record(now: Long) = RecordSessionUseCase(
        progress = progress,
        clock = { now },
        completeSession = CompleteSessionUseCase(
            gamification,
            clock.gameClock,
            GrantRewardsUseCase(gamification, clock.gameClock),
        ),
    )

    @Test
    fun `a lesson level plays the requested lesson`() = runTest {
        val session = plan("u01-1", "u01-1.l2", "u01-1.l2.s2")
        assertThat(session.sessionId).isEqualTo("u01-1.l2.s2")
        assertThat(session.hearts).isEqualTo(HeartPolicy.Shared)
        assertThat(session.kind).isEqualTo(ActivityKind.Lesson)
    }

    @Test
    fun `the review puts the most missed exercises first`() = runTest {
        progress.stats = MistakeStats(
            byExercise = mapOf("u01-1.l2.s2.e01" to 1),
            byStructure = mapOf("izan-present" to 5),
        )
        val session = plan("u01-1", "u01-1.l4", null, Random(1))
        assertThat(session.sessionId).isEqualTo("u01-1.l4")
        assertThat(session.hearts).isEqualTo(HeartPolicy.Unlimited)
        assertThat(session.exercises.map { it.id })
            .containsExactly("u01-1.l2.s1.e01", "u01-1.l2.s2.e01", "u01-1.l1.s1.e01")
            .inOrder()
    }

    @Test
    fun `new word cards stay in their lesson and legendary levels have no hints`() = runTest {
        val lesson = plan("u01-1", "u01-1.l1", "u01-1.l1.s1")
        assertThat(lesson.exercises.first()).isInstanceOf(Exercise.WordIntro::class.java)
        assertThat(lesson.glossary).isNotNull()

        val review = plan("u01-1", "u01-1.l4", null, Random(1))
        val legendary = plan("u01-1", "u01-1.l5", null, Random(1))
        assertThat(review.exercises.none { it is Exercise.WordIntro }).isTrue()
        assertThat(legendary.exercises.none { it is Exercise.WordIntro }).isTrue()
        assertThat(legendary.glossary).isNull()
    }

    @Test
    fun `a section review draws from every unit of the section`() = runTest {
        val session = plan("u01-2", "u01-2.l6", null, Random(1))
        assertThat(session.sessionId).isEqualTo("u01-2.l6")
        assertThat(session.exercises.map { it.id.substringBefore('.') }.toSet()).containsExactly("u01-1", "u01-2")
        assertThat(session.kind).isEqualTo(ActivityKind.Review)
    }

    @Test
    fun `the legendary level plays every exercise with fewer hearts`() = runTest {
        val session = plan("u01-1", "u01-1.l5", null, Random(1))
        assertThat(session.exercises).hasSize(3)
        assertThat(session.hearts).isEqualTo(HeartPolicy.Limited(PlanSessionUseCase.LEGENDARY_HEARTS))
    }

    @Test
    fun `recording saves mistakes and the completion`() = runTest {
        val mistake = Mistake("u01-1.l1.s1.e01", listOf("kaixo"), emptyList(), "a", "b")
        val reward = record(42L)(
            "u01-1",
            "u01-1.l1.s1",
            LessonResult("u01-1.l1.s1", true, 2, 1, 4, listOf(mistake)),
            ActivityKind.Lesson,
        )
        assertThat(reward?.xp).isEqualTo(10)
        assertThat(gamification.data.value.activities.single().sessionId).isEqualTo("u01-1.l1.s1")
        assertThat(progress.recordedMistakes).containsExactly(mistake)
        assertThat(progress.completed.value).containsExactly("u01-1.l1.s1")
        assertThat(progress.accuracies["u01-1.l1.s1"]).isEqualTo(0.5f)
    }

    @Test
    fun `an unfinished session saves mistakes only`() = runTest {
        val mistake = Mistake("e", emptyList(), emptyList(), "a", "b")
        val reward = record(0L)("u01-1", "s", LessonResult("s", false, 1, 0, 0, listOf(mistake)), ActivityKind.Lesson)
        assertThat(reward).isNull()
        assertThat(gamification.data.value.activities).isEmpty()
        assertThat(progress.completed.value).isEmpty()
        assertThat(progress.recordedMistakes).hasSize(1)
    }
}
