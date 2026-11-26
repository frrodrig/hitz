package eus.hitz.core.domain.score

import com.google.common.truth.Truth.assertThat
import eus.hitz.core.domain.content.CourseRepository
import eus.hitz.core.domain.testing.FakeProgressRepository
import eus.hitz.core.domain.testing.TestContent
import eus.hitz.core.model.CourseId
import eus.hitz.core.model.content.CefrLevel
import eus.hitz.core.model.content.Course
import eus.hitz.core.model.content.CourseUnit
import eus.hitz.core.model.content.Section
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Test
import java.time.ZoneOffset

class ScoreTest {
    private val progress = FakeProgressRepository()
    private val observe = ObserveScoreUseCase(TestContent.courseRepository, progress, { NOW }, { ZoneOffset.UTC })

    private suspend fun complete(unitId: String, vararg steps: String, at: Long = NOW) =
        steps.forEach { progress.recordCompletion(unitId, it, 1f, at) }

    @Test
    fun `required steps share the points evenly`() {
        // 5 steps in u01-1 and 6 in u01-2 (with the section review).
        assertThat(ScoreRules.steps(TestContent.course, TestContent.units)).hasSize(11)
        val a1 = ScoreRules.groups(TestContent.course, TestContent.units).single()
        assertThat(a1.span).isEqualTo(29f)
        assertThat(ScoreRules.value(ScoreRules.points(a1, 11), 29)).isEqualTo(ScoreRules.A1_TOP)
        assertThat(ScoreRules.value(ScoreRules.points(a1, 5), 29)).isEqualTo(13)
        assertThat(ScoreRules.progressToNext(ScoreRules.points(a1, 11), 29)).isEqualTo(1f)
    }

    @Test
    fun `a new learner starts at zero`() = runTest {
        val score = observe().first()
        assertThat(score.value).isEqualTo(0)
        assertThat(score.max).isEqualTo(ScoreRules.A1_TOP)
        assertThat(score.level).isEqualTo(CefrLevel.A1)
        assertThat(score.band).isEqualTo(ScoreBand.FirstSteps)
        assertThat(score.history).containsExactly(ScorePoint(TODAY, 0))
        assertThat(score.canDo).isEmpty()
    }

    @Test
    fun `the score follows the completed steps over time`() = runTest {
        complete("u01-1", "u01-1.l1.s1", "u01-1.l2.s1", "u01-1.l2.s2", "u01-1.l3", "u01-1.l4", at = NOW - 10 * DAY)
        complete("u01-1", "u01-1.l5")
        complete("u01-2", "u01-2.l1.s1")

        val score = observe().first()
        assertThat(score.value).isEqualTo(15)
        assertThat(score.band).isEqualTo(ScoreBand.Basics)
        assertThat(score.progressToNext).isWithin(0.01f).of(0.82f)
        assertThat(score.gainedThisWeek).isEqualTo(2)
        assertThat(score.history).containsExactly(ScorePoint(TODAY - 10, 13), ScorePoint(TODAY, 15)).inOrder()
        assertThat(score.canDo.map { it.unitId }).containsExactly("u01-1")
    }

    @Test
    fun `finishing the course gives the top score`() = runTest {
        ScoreRules.steps(TestContent.course, TestContent.units).forEach { complete(it.substringBefore('.'), it) }
        val score = observe().first()
        assertThat(score.value).isEqualTo(ScoreRules.A1_TOP)
        assertThat(score.isTop).isTrue()
        assertThat(score.band).isEqualTo(ScoreBand.Confident)
        assertThat(score.canDo.map { it.unitId }).containsExactly("u01-1", "u01-2").inOrder()
    }

    /** The A1 course plus A2 sections made of two units each, like TestContent's s1. */
    private fun withA2(vararg sections: String): Pair<Course, Map<String, CourseUnit>> {
        val text = TestContent.course.title
        val a2 = sections.associateWith { id ->
            val book = 12 + id.removePrefix("s").toInt()
            listOf(TestContent.unit("u$book-1"), TestContent.unit("u$book-2", sectionReview = true))
        }
        val course = TestContent.course.copy(
            sections = TestContent.course.sections + a2.map { (id, units) ->
                Section(id, text, text, units.map { it.id }, level = CefrLevel.A2)
            },
        )
        return course to TestContent.units + a2.values.flatten().associateBy { it.id }
    }

    private fun observing(course: Course, units: Map<String, CourseUnit>): ObserveScoreUseCase {
        val repository = object : CourseRepository {
            override suspend fun getCourse(courseId: CourseId): Course = course

            override suspend fun getUnit(courseId: CourseId, unitId: String): CourseUnit = units.getValue(unitId)
        }
        return ObserveScoreUseCase(repository, progress, { NOW }, { ZoneOffset.UTC })
    }

    @Test
    fun `each A2 section fills its share after A1, so publishing more never lowers a score`() = runTest {
        ScoreRules.steps(TestContent.course, TestContent.units).forEach { complete(it.substringBefore('.'), it) }

        // Section 5 published alone: a quarter of the A2 stretch, 29 + 7.5.
        val (oneCourse, oneUnits) = withA2("s5")
        val afterA1 = observing(oneCourse, oneUnits)().first()
        assertThat(afterA1.value).isEqualTo(ScoreRules.A1_TOP)
        assertThat(afterA1.max).isEqualTo(36)
        assertThat(afterA1.level).isEqualTo(CefrLevel.A2)

        ScoreRules.stepsByLevel(oneCourse, oneUnits).getValue(CefrLevel.A2)
            .forEach { complete(it.substringBefore('.'), it) }
        val sectionDone = observing(oneCourse, oneUnits)().first()
        assertThat(sectionDone.value).isEqualTo(36)
        assertThat(sectionDone.isTop).isTrue()
        assertThat(sectionDone.band).isEqualTo(ScoreBand.TowardsA2)

        // Section 6 arrives: the score stays where it was, and the top becomes 44.
        val (twoCourse, twoUnits) = withA2("s5", "s6")
        val later = observing(twoCourse, twoUnits)().first()
        assertThat(later.value).isEqualTo(36)
        assertThat(later.max).isEqualTo(44)
        assertThat(later.isTop).isFalse()

        ScoreRules.steps(twoCourse, twoUnits).forEach { complete(it.substringBefore('.'), it) }
        assertThat(observing(twoCourse, twoUnits)().first().value).isEqualTo(44)
    }

    private companion object {
        const val DAY = 86_400_000L

        /** 2026-11-04, 10:00 UTC. */
        const val NOW = 1_793_786_400_000L
        const val TODAY = 20_761L
    }
}
