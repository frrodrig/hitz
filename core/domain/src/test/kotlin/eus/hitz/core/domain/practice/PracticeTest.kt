package eus.hitz.core.domain.practice

import com.google.common.truth.Truth.assertThat
import eus.hitz.core.domain.gamification.ActivityKind
import eus.hitz.core.domain.gamification.CompleteSessionUseCase
import eus.hitz.core.domain.gamification.RecordStudyTimeUseCase
import eus.hitz.core.domain.lesson.LessonResult
import eus.hitz.core.domain.lesson.Mistake
import eus.hitz.core.domain.progress.MistakeStats
import eus.hitz.core.domain.session.PlanSessionUseCase
import eus.hitz.core.domain.session.RecordSessionUseCase
import eus.hitz.core.domain.testing.FakeGamificationRepository
import eus.hitz.core.domain.testing.FakeProgressRepository
import eus.hitz.core.domain.testing.FakeSettingsRepository
import eus.hitz.core.domain.testing.TestClock
import eus.hitz.core.domain.testing.TestContent
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Test

class PracticeTest {
    private val progress = FakeProgressRepository()
    private val hub = ObservePracticeHubUseCase(TestContent.courseRepository, progress)
    private val review = GetUnitReviewUseCase(TestContent.courseRepository, progress)
    private val plan = PlanSessionUseCase(TestContent.courseRepository, progress)
    private val gamification = FakeGamificationRepository()
    private val clock = TestClock()
    private val studyTime = RecordStudyTimeUseCase(gamification, FakeSettingsRepository(), clock.gameClock)
    private val record = RecordSessionUseCase(
        progress = progress,
        clock = { 1_000L },
        completeSession = CompleteSessionUseCase(gamification, clock.gameClock, studyTime),
        recordStudyTime = studyTime,
    )

    private fun complete(vararg ids: String) {
        progress.completed.value = progress.completed.value + ids
    }

    @Test
    fun `a new learner has nothing to practise yet`() = runTest {
        val empty = hub().first()
        assertThat(empty.units).isEmpty()
        assertThat(empty.available.getValue(PracticeMode.Mistakes)).isEqualTo(0)
    }

    @Test
    fun `the hub lists started units and what they hold`() = runTest {
        complete("u01-1.l1.s1")
        val started = hub().first()
        val unit = started.units.single()
        assertThat(unit.unitId).isEqualTo("u01-1")
        assertThat(unit.progress).isWithin(0.001f).of(0.2f)
        assertThat(unit.completed).isFalse()
        assertThat(unit.words).isEqualTo(1)
        assertThat(unit.hasStory).isFalse()
        // The new-word card is not practised; only the exercise after it.
        assertThat(started.available.getValue(PracticeMode.Mistakes)).isEqualTo(1)
        assertThat(started.available.getValue(PracticeMode.Listening)).isEqualTo(0)
    }

    @Test
    fun `a unit review shows how well each word is known`() = runTest {
        complete("u01-1.l1.s1", "u01-1.l3")
        progress.stats = MistakeStats(byLemma = mapOf("kaixo" to 3))
        val unit = review("u01-1")
        assertThat(unit.words).containsExactly(ReviewWord("kaixo", "hola", null, "u01-1.l1.s1.e00", Strength.Weak))
        assertThat(unit.story?.completed).isTrue()
        assertThat(unit.story?.levelId).isEqualTo("u01-1.l3")
    }

    @Test
    fun `practice only uses exercises the learner has seen`() = runTest {
        complete("u01-1.l1.s1", "u01-2.l2.s1")
        val all = plan.practice(PracticeMode.Mistakes)
        assertThat(all.sessionId).isEqualTo("practice.mistakes")
        assertThat(all.kind).isEqualTo(ActivityKind.Review)
        assertThat(all.exercises.map { it.id }).containsExactly("u01-1.l1.s1.e01", "u01-2.l2.s1.e01")
        val unit = plan.practice(PracticeMode.Mistakes, unitId = "u01-2")
        assertThat(unit.sessionId).isEqualTo("practice.unit.u01-2")
        assertThat(unit.exercises.map { it.id }).containsExactly("u01-2.l2.s1.e01")
        val nothing = runCatching { plan.practice(PracticeMode.Listening) }.exceptionOrNull()
        assertThat(nothing).isInstanceOf(NoSuchElementException::class.java)
    }

    @Test
    fun `practice saves mistakes by unit and completes no step of the path`() = runTest {
        val mistakes = listOf(
            Mistake("u01-1.l1.s1.e01", listOf("kaixo"), emptyList(), "a", "b"),
            Mistake("u01-2.l2.s1.e01", emptyList(), listOf("izan-present"), "c", "d"),
        )
        val result = LessonResult("practice.mistakes", true, 2, 0, null, mistakes)
        val reward = record.practice("practice.mistakes", result, activeMillis = 60_000)
        assertThat(progress.mistakesByUnit.keys).containsExactly("u01-1", "u01-2")
        assertThat(progress.completed.value).isEmpty()
        assertThat(reward).isNotNull()
        assertThat(gamification.data.value.activities.single().kind).isEqualTo(ActivityKind.Review)
    }
}
