package eus.hitz.core.domain.path

import com.google.common.truth.Truth.assertThat
import eus.hitz.core.domain.testing.TestContent
import org.junit.Test

class LearningPathBuilderTest {
    private fun nodes(completed: Set<String>) =
        LearningPathBuilder.build(TestContent.course, TestContent.units, completed)
            .sections.single().units.flatMap { it.nodes }

    @Test
    fun `a new learner can only start the first lesson`() {
        val nodes = nodes(emptySet())
        assertThat(nodes.first().state).isEqualTo(NodeState.Available)
        assertThat(nodes.first().isCurrent).isTrue()
        assertThat(nodes.drop(1).map { it.state }.toSet()).containsExactly(NodeState.Locked)
    }

    @Test
    fun `a lesson level is completed only when all its lessons are`() {
        val nodes = nodes(setOf("u01-1.l1.s1", "u01-1.l2.s1"))
        val level2 = nodes[1]
        assertThat(level2.state).isEqualTo(NodeState.Available)
        assertThat(level2.isCurrent).isTrue()
        assertThat(level2.completedLessons).isEqualTo(1)
        assertThat(level2.totalLessons).isEqualTo(2)
        assertThat(level2.nextLessonId).isEqualTo("u01-1.l2.s2")
    }

    @Test
    fun `stories are required before the review`() {
        val nodes = nodes(setOf("u01-1.l1.s1", "u01-1.l2.s1", "u01-1.l2.s2"))
        assertThat(nodes[2].type).isEqualTo(NodeType.Story)
        assertThat(nodes[2].isCurrent).isTrue()
        assertThat(nodes[3].type).isEqualTo(NodeType.Review)
        assertThat(nodes[3].state).isEqualTo(NodeState.Locked)

        val afterStory = nodes(setOf("u01-1.l1.s1", "u01-1.l2.s1", "u01-1.l2.s2", "u01-1.l3"))
        assertThat(afterStory[2].state).isEqualTo(NodeState.Completed)
        assertThat(afterStory[3].isCurrent).isTrue()
    }

    @Test
    fun `the star challenge is beside the path, locked until the unit is completed`() {
        val path = LearningPathBuilder.build(TestContent.course, TestContent.units, setOf("u01-1.l1.s1"))
        val unit = path.sections.single().units.first()
        assertThat(unit.nodes.map { it.levelId }).doesNotContain("u01-1.l5")
        assertThat(unit.challenge?.levelId).isEqualTo("u01-1.l5")
        assertThat(unit.challenge?.host).isEqualTo("ane")
        assertThat(unit.challenge?.isAvailable).isFalse()
        assertThat(unit.challenge?.stars).isEqualTo(0)
    }

    @Test
    fun `finishing the review unlocks the star challenge and the next unit`() {
        val done = setOf("u01-1.l1.s1", "u01-1.l2.s1", "u01-1.l2.s2", "u01-1.l3", "u01-1.l4")
        val path = LearningPathBuilder.build(TestContent.course, TestContent.units, done)
        assertThat(path.challenges.first().isAvailable).isTrue()
        val nextUnitFirst = path.sections.single().units[1].nodes.first()
        assertThat(nextUnitFirst.unitId).isEqualTo("u01-2")
        assertThat(nextUnitFirst.isCurrent).isTrue()
    }

    @Test
    fun `a unit closing a section unlocks its challenge before the section review`() {
        val first = setOf("u01-1.l1.s1", "u01-1.l2.s1", "u01-1.l2.s2", "u01-1.l3", "u01-1.l4")
        val second = setOf("u01-2.l1.s1", "u01-2.l2.s1", "u01-2.l2.s2", "u01-2.l3", "u01-2.l4")
        val path = LearningPathBuilder.build(TestContent.course, TestContent.units, first + second)
        assertThat(path.currentNode?.levelId).isEqualTo("u01-2.l6")
        assertThat(path.challenges[1].isAvailable).isTrue()
    }

    @Test
    fun `a challenge shows its best stars`() {
        val stars = listOf(1f / 3, 2f / 3, 1f, 0.98f).map { accuracy ->
            LearningPathBuilder.build(
                TestContent.course,
                TestContent.units,
                setOf("u01-1.l5"),
                mapOf("u01-1.l5" to accuracy),
            ).challenges.first().stars
        }
        // 0.98: an old legendary level, passed with a couple of mistakes, counts as three stars.
        assertThat(stars).containsExactly(1, 2, 3, 3).inOrder()
    }

    @Test
    fun `the current node is unique`() {
        val path = LearningPathBuilder.build(TestContent.course, TestContent.units, setOf("u01-1.l1.s1"))
        val currents = path.sections.flatMap { it.units }.flatMap { it.nodes }.filter { it.isCurrent }
        assertThat(currents).hasSize(1)
        assertThat(path.currentNode).isEqualTo(currents.single())
    }
}
