package eus.hitz.core.domain.placement

import com.google.common.truth.Truth.assertThat
import eus.hitz.core.domain.path.LearningPathBuilder
import eus.hitz.core.domain.testing.FakeProgressRepository
import eus.hitz.core.domain.testing.TestContent
import eus.hitz.core.model.content.Exercise
import kotlinx.coroutines.test.runTest
import org.junit.Test
import kotlin.random.Random

class PlacementUseCaseTest {
    private val progress = FakeProgressRepository()
    private val placement = PlacementUseCase(TestContent.courseRepository, progress)

    @Test
    fun `a test asks the later lessons of every unit of the section`() = runTest {
        val test = placement.test("s1", Random(1))
        assertThat(test.sectionNumber).isEqualTo(1)
        assertThat(test.nextSectionId).isNull()
        assertThat(
            test.exercises.map { it.id.substringBefore(".l") }
        ).containsExactly("u01-1", "u01-1", "u01-2", "u01-2")
        assertThat(test.exercises.all { it.id.contains(".l2.") }).isTrue()
        assertThat(test.exercises.none { it is Exercise.WordIntro }).isTrue()
    }

    @Test
    fun `passing skips the whole section but legendary levels`() = runTest {
        placement.skipSection("s1", accuracy = 0.9f, now = 0L)
        val path = LearningPathBuilder.build(TestContent.course, TestContent.units, progress.completed.value)
        assertThat(path.currentNode).isNull()
        assertThat(progress.completed.value).doesNotContain("u01-1.l5")
        assertThat(progress.completed.value).contains("u01-2.l6")
    }

    @Test
    fun `the pass mark is 80 percent`() {
        assertThat(PlacementUseCase.passed(0.8f)).isTrue()
        assertThat(PlacementUseCase.passed(0.75f)).isFalse()
    }
}
