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
        val a1 = CefrLevel.A1
        assertThat(ScoreRules.value(ScoreRules.points(a1, 11, 11), 29)).isEqualTo(ScoreRules.A1_TOP)
        assertThat(ScoreRules.value(ScoreRules.points(a1, 5, 11), 29)).isEqualTo(13)
        assertThat(ScoreRules.progressToNext(ScoreRules.points(a1, 11, 11), 29)).isEqualTo(1f)
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

    @Test
    fun `a2_1 fills its own stretch after A1, so adding it lowers no score`() = runTest {
        val a2 = listOf(TestContent.unit("u13-1"), TestContent.unit("u13-2", sectionReview = true))
        val units = TestContent.units + a2.associateBy { it.id }
        val text = TestContent.course.title
        val course = TestContent.course.copy(
            sections = TestContent.course.sections +
                Section("s5", text, text, a2.map { it.id }, level = CefrLevel.A2_1),
        )
        val repository = object : CourseRepository {
            override suspend fun getCourse(courseId: CourseId): Course = course

            override suspend fun getUnit(courseId: CourseId, unitId: String): CourseUnit = units.getValue(unitId)
        }
        val observeBoth = ObserveScoreUseCase(repository, progress, { NOW }, { ZoneOffset.UTC })
        ScoreRules.steps(TestContent.course, TestContent.units).forEach { complete(it.substringBefore('.'), it) }

        val afterA1 = observeBoth().first()
        assertThat(afterA1.value).isEqualTo(ScoreRules.A1_TOP)
        assertThat(afterA1.max).isEqualTo(ScoreRules.A2_1_TOP)
        assertThat(afterA1.level).isEqualTo(CefrLevel.A2_1)
        assertThat(afterA1.isTop).isFalse()

        // 5 of the 11 A2.1 steps: 15 points × 5/11 on top of 29.
        complete("u13-1", "u13-1.l1.s1", "u13-1.l2.s1", "u13-1.l2.s2", "u13-1.l3", "u13-1.l4")
        val halfway = observeBoth().first()
        assertThat(halfway.value).isEqualTo(35)
        assertThat(halfway.band).isEqualTo(ScoreBand.TowardsA2)

        ScoreRules.stepsByLevel(
            course,
            units
        ).getValue(CefrLevel.A2_1).forEach { complete(it.substringBefore('.'), it) }
        val top = observeBoth().first()
        assertThat(top.value).isEqualTo(ScoreRules.A2_1_TOP)
        assertThat(top.isTop).isTrue()
    }

    private companion object {
        const val DAY = 86_400_000L

        /** 2026-11-04, 10:00 UTC. */
        const val NOW = 1_793_786_400_000L
        const val TODAY = 20_761L
    }
}
