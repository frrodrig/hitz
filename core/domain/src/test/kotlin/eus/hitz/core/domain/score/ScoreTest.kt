package eus.hitz.core.domain.score

import com.google.common.truth.Truth.assertThat
import eus.hitz.core.domain.testing.FakeProgressRepository
import eus.hitz.core.domain.testing.TestContent
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
        assertThat(ScoreRules.value(ScoreRules.points(11, 11))).isEqualTo(ScoreRules.MAX)
        assertThat(ScoreRules.value(ScoreRules.points(5, 11))).isEqualTo(13)
        assertThat(ScoreRules.progressToNext(ScoreRules.points(11, 11))).isEqualTo(1f)
    }

    @Test
    fun `a new learner starts at zero`() = runTest {
        val score = observe().first()
        assertThat(score.value).isEqualTo(0)
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
        assertThat(score.value).isEqualTo(ScoreRules.MAX)
        assertThat(score.isTop).isTrue()
        assertThat(score.band).isEqualTo(ScoreBand.Confident)
        assertThat(score.canDo.map { it.unitId }).containsExactly("u01-1", "u01-2").inOrder()
    }

    private companion object {
        const val DAY = 86_400_000L

        /** 2026-11-04, 10:00 UTC. */
        const val NOW = 1_793_786_400_000L
        const val TODAY = 20_761L
    }
}
