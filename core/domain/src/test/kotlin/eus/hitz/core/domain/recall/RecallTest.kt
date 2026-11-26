package eus.hitz.core.domain.recall

import com.google.common.truth.Truth.assertThat
import eus.hitz.core.domain.gamification.ActivityKind
import eus.hitz.core.domain.gamification.CompleteSessionUseCase
import eus.hitz.core.domain.gamification.RecordStudyTimeUseCase
import eus.hitz.core.domain.lesson.LessonResult
import eus.hitz.core.domain.lesson.Mistake
import eus.hitz.core.domain.path.LearningPathBuilder
import eus.hitz.core.domain.path.NodeState
import eus.hitz.core.domain.progress.MistakeStats
import eus.hitz.core.domain.session.PlanSessionUseCase
import eus.hitz.core.domain.session.RecordSessionUseCase
import eus.hitz.core.domain.testing.FakeGamificationRepository
import eus.hitz.core.domain.testing.FakeProgressRepository
import eus.hitz.core.domain.testing.FakeSettingsRepository
import eus.hitz.core.domain.testing.TestClock
import eus.hitz.core.domain.testing.TestContent
import eus.hitz.core.model.content.Exercise
import kotlinx.coroutines.test.runTest
import org.junit.Test
import kotlin.random.Random

class RecallTest {
    private val order = TestContent.longCourse.sections.flatMap { it.units }

    private fun exercises(unit: String, count: Int) =
        (1..count).map { TestContent.exercise("$unit.l2.s1.e%02d".format(it)) }

    @Test
    fun `a recall closes every textbook unit but the first`() {
        assertThat(order.filter { RecallRules.hasRecall(it, order) }).containsExactly("u02-2", "u03-2", "u04-2")
        assertThat(RecallRules.sourceUnits("u03-2", order)).containsExactly("u01-1", "u01-2", "u02-1", "u02-2")
        assertThat(RecallRules.isRecall(RecallRules.levelId("u03-2"))).isTrue()
        assertThat(RecallRules.unitOf("u03-2.recall")).isEqualTo("u03-2")
    }

    @Test
    fun `a recall is varied and starts with mistakes and old units`() {
        val seen = order.take(6).associateWith { exercises(it, 6) }
        val stats = MistakeStats(byExercise = mapOf("u03-1.l2.s1.e04" to 2))
        val now = 100L * DAY
        // u01-1 and u01-2 were studied ten weeks ago; the rest, today.
        val lastStudied = order.associateWith { if (it.startsWith("u01")) 30L * DAY else now }
        val recall = RecallRules.select(seen, stats, lastStudied, now, Random(1))

        assertThat(recall).hasSize(RecallRules.SIZE)
        assertThat(recall.groupingBy { it.id.substringBefore('.') }.eachCount().values.max())
            .isAtMost(RecallRules.PER_UNIT)
        assertThat(recall.map { it.id }).contains("u03-1.l2.s1.e04")
        assertThat(recall.count { it.id.startsWith("u01-1") }).isEqualTo(RecallRules.PER_UNIT)
        assertThat(recall.count { it.id.startsWith("u01-2") }).isEqualTo(RecallRules.PER_UNIT)
    }

    @Test
    fun `with few units seen the recall fills up anyway`() {
        val recall = RecallRules.select(
            mapOf("u01-1" to exercises("u01-1", 20)),
            MistakeStats(),
            emptyMap(),
            0L,
            Random(1)
        )
        assertThat(recall).hasSize(RecallRules.SIZE)
    }

    @Test
    fun `the recall becomes available with its unit and never blocks the path`() {
        val stepsBefore = TestContent.longUnits.values.filter { it.id < "u02-2" }.flatMap { unit ->
            unit.levels.flatMap { level ->
                when (level) {
                    is eus.hitz.core.model.content.Level.Lessons -> level.lessons.map { it.id }
                    is eus.hitz.core.model.content.Level.Challenge -> emptyList()
                    else -> listOf(level.id)
                }
            }
        }.toSet()
        val unitDone = stepsBefore + setOf("u02-2.l1.s1", "u02-2.l2.s1", "u02-2.l2.s2", "u02-2.l3", "u02-2.l4")
        val path = LearningPathBuilder.build(TestContent.longCourse, TestContent.longUnits, unitDone)
        val units = path.sections.flatMap { it.units }.associateBy { it.id }
        assertThat(units.getValue("u01-2").recall).isNull()
        assertThat(units.getValue("u02-1").recall).isNull()
        val recall = checkNotNull(units.getValue("u02-2").recall)
        assertThat(recall.isAvailable).isTrue()
        assertThat(recall.isDone).isFalse()
        // The section review comes next, then the next section: the recall is not in the way.
        assertThat(path.currentNode?.levelId).isEqualTo("u02-2.l6")
        assertThat(units.getValue("u03-2").recall?.isAvailable).isFalse()

        val done = LearningPathBuilder.build(
            TestContent.longCourse,
            TestContent.longUnits,
            unitDone + "u02-2.l6" + RecallRules.levelId("u02-2"),
        )
        val u02 = done.sections.flatMap { it.units }.single { it.id == "u02-2" }
        assertThat(u02.recall?.isDone).isTrue()
        assertThat(done.sections[0].isCompleted).isTrue()
        assertThat(done.currentSection?.id).isEqualTo("s2")
        assertThat(done.sections[1].units.first().nodes.first().state).isEqualTo(NodeState.Available)
    }

    @Test
    fun `a recall session mixes the earlier units and saves each mistake in its unit`() = runTest {
        val progress = FakeProgressRepository()
        listOf("u01-1", "u01-2", "u02-1").forEach { unit ->
            listOf("l1.s1", "l2.s1", "l2.s2").forEach { progress.recordCompletion(unit, "$unit.$it", 1f, 0L) }
        }
        val plan = PlanSessionUseCase(TestContent.longCourseRepository, progress) { DAY }
        val session = plan("u03-2", RecallRules.levelId("u03-2"), null, Random(1))
        assertThat(session.sessionId).isEqualTo("u03-2.recall")
        assertThat(session.kind).isEqualTo(ActivityKind.Review)
        assertThat(session.exercises.map { it.id.substringBefore('.') }.toSet())
            .containsExactly("u01-1", "u01-2", "u02-1")
        assertThat(session.exercises.none { it is Exercise.WordIntro }).isTrue()

        val gamification = FakeGamificationRepository()
        val clock = TestClock()
        val studyTime = RecordStudyTimeUseCase(gamification, FakeSettingsRepository(), clock.gameClock)
        val record = RecordSessionUseCase(progress, {
            DAY
        }, CompleteSessionUseCase(gamification, clock.gameClock, studyTime), studyTime)
        fun mistake(id: String) = Mistake(id, emptyList(), emptyList(), "x", "y")
        record(
            "u03-2",
            "u03-2.recall",
            LessonResult(
                "u03-2.recall",
                true,
                15,
                13,
                null,
                listOf(mistake("u01-1.l2.s1.e01"), mistake("u02-1.l1.s1.e01"))
            ),
            ActivityKind.Review,
            60_000L,
        )
        assertThat(progress.mistakesByUnit.keys).containsExactly("u01-1", "u02-1")
        assertThat(progress.completed.value).contains("u03-2.recall")
    }

    private companion object {
        const val DAY = 86_400_000L
    }
}
