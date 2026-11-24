package eus.hitz.core.domain.session

import com.google.common.truth.Truth.assertThat
import eus.hitz.core.domain.challenge.ChallengeRules
import eus.hitz.core.domain.gamification.ActivityKind
import eus.hitz.core.domain.gamification.CompleteSessionUseCase
import eus.hitz.core.domain.gamification.RecordStudyTimeUseCase
import eus.hitz.core.domain.lesson.LessonResult
import eus.hitz.core.domain.lesson.Mistake
import eus.hitz.core.domain.progress.MistakeStats
import eus.hitz.core.domain.testing.FakeGamificationRepository
import eus.hitz.core.domain.testing.FakeProgressRepository
import eus.hitz.core.domain.testing.FakeSettingsRepository
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
    private val studyTime = RecordStudyTimeUseCase(gamification, FakeSettingsRepository(), clock.gameClock)

    private fun record(now: Long) = RecordSessionUseCase(
        progress = progress,
        clock = { now },
        completeSession = CompleteSessionUseCase(gamification, clock.gameClock, studyTime),
        recordStudyTime = studyTime,
    )

    @Test
    fun `a lesson level plays the requested lesson`() = runTest {
        val session = plan("u01-1", "u01-1.l2", "u01-1.l2.s2")
        assertThat(session.sessionId).isEqualTo("u01-1.l2.s2")
        assertThat(session.mistakesAllowed).isNull()
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
        assertThat(session.mistakesAllowed).isNull()
        assertThat(session.exercises.map { it.id })
            .containsExactly("u01-1.l2.s1.e01", "u01-1.l2.s2.e01", "u01-1.l1.s1.e01")
            .inOrder()
    }

    @Test
    fun `new word cards stay in their lesson and star challenges have no hints`() = runTest {
        val lesson = plan("u01-1", "u01-1.l1", "u01-1.l1.s1")
        assertThat(lesson.exercises.first()).isInstanceOf(Exercise.WordIntro::class.java)
        assertThat(lesson.glossary).isNotNull()

        val review = plan("u01-1", "u01-1.l4", null, Random(1))
        val challenge = plan("u01-1", "u01-1.l5", null, Random(1))
        assertThat(review.exercises.none { it is Exercise.WordIntro }).isTrue()
        assertThat(challenge.exercises.none { it is Exercise.WordIntro }).isTrue()
        assertThat(challenge.glossary).isNull()
    }

    @Test
    fun `a section review draws from every unit of the section`() = runTest {
        val session = plan("u01-2", "u01-2.l6", null, Random(1))
        assertThat(session.sessionId).isEqualTo("u01-2.l6")
        assertThat(session.exercises.map { it.id.substringBefore('.') }.toSet()).containsExactly("u01-1", "u01-2")
        assertThat(session.kind).isEqualTo(ActivityKind.Review)
    }

    @Test
    fun `a star challenge asks each exercise once, allowing two mistakes`() = runTest {
        val session = plan("u01-1", "u01-1.l5", null, Random(1))
        assertThat(session.kind).isEqualTo(ActivityKind.Challenge)
        assertThat(session.exercises).hasSize(3)
        assertThat(session.mistakesAllowed).isEqualTo(ChallengeRules.MISTAKES_ALLOWED)
        assertThat(session.retries).isFalse()
    }

    @Test
    fun `a star challenge keeps its stars as the completion`() = runTest {
        val mistake = Mistake("u01-1.l1.s1.e01", listOf("kaixo"), emptyList(), "a", "b")
        record(42L)(
            "u01-1",
            "u01-1.l5",
            LessonResult("u01-1.l5", true, 30, 29, 1, listOf(mistake)),
            ActivityKind.Challenge,
            activeMillis = 60_000,
        )
        assertThat(progress.accuracies["u01-1.l5"]).isEqualTo(2f / 3)
        record(43L)(
            "u01-1",
            "u01-1.l5",
            LessonResult("u01-1.l5", true, 30, 28, 0, listOf(mistake, mistake)),
            ActivityKind.Challenge,
            activeMillis = 60_000,
        )
        assertThat(progress.accuracies["u01-1.l5"]).isEqualTo(2f / 3)
    }

    @Test
    fun `recording saves mistakes and the completion`() = runTest {
        val mistake = Mistake("u01-1.l1.s1.e01", listOf("kaixo"), emptyList(), "a", "b")
        val reward = record(42L)(
            "u01-1",
            "u01-1.l1.s1",
            LessonResult("u01-1.l1.s1", true, 2, 1, null, listOf(mistake)),
            ActivityKind.Lesson,
            activeMillis = 120_000,
        )
        assertThat(reward?.after?.activeMillis).isEqualTo(120_000)
        assertThat(gamification.data.value.activities.single().sessionId).isEqualTo("u01-1.l1.s1")
        assertThat(progress.recordedMistakes).containsExactly(mistake)
        assertThat(progress.completed.value).containsExactly("u01-1.l1.s1")
        assertThat(progress.accuracies["u01-1.l1.s1"]).isEqualTo(0.5f)
    }

    @Test
    fun `an unfinished session saves mistakes and its study time only`() = runTest {
        val mistake = Mistake("e", emptyList(), emptyList(), "a", "b")
        val result = LessonResult("s", false, 1, 0, 0, listOf(mistake))
        val reward = record(0L)("u01-1", "s", result, ActivityKind.Lesson, activeMillis = 45_000)
        assertThat(reward).isNull()
        assertThat(gamification.data.value.activities).isEmpty()
        assertThat(gamification.data.value.studyDays.values.single().activeMillis).isEqualTo(45_000)
        assertThat(progress.completed.value).isEmpty()
        assertThat(progress.recordedMistakes).hasSize(1)
    }
}
