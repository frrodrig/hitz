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
    fun `stories are optional and do not block the review`() {
        val nodes = nodes(setOf("u01-1.l1.s1", "u01-1.l2.s1", "u01-1.l2.s2"))
        assertThat(nodes[2].type).isEqualTo(NodeType.Story)
        assertThat(nodes[2].state).isEqualTo(NodeState.Available)
        assertThat(nodes[2].isCurrent).isFalse()
        assertThat(nodes[3].type).isEqualTo(NodeType.Review)
        assertThat(nodes[3].isCurrent).isTrue()
    }

    @Test
    fun `finishing the review unlocks the legendary level and the next unit`() {
        val nodes = nodes(setOf("u01-1.l1.s1", "u01-1.l2.s1", "u01-1.l2.s2", "u01-1.l4"))
        assertThat(nodes[4].type).isEqualTo(NodeType.Legendary)
        assertThat(nodes[4].state).isEqualTo(NodeState.Available)
        val nextUnitFirst = nodes[5]
        assertThat(nextUnitFirst.unitId).isEqualTo("u01-2")
        assertThat(nextUnitFirst.isCurrent).isTrue()
    }

    @Test
    fun `the current node is unique`() {
        val path = LearningPathBuilder.build(TestContent.course, TestContent.units, setOf("u01-1.l1.s1"))
        val currents = path.sections.flatMap { it.units }.flatMap { it.nodes }.filter { it.isCurrent }
        assertThat(currents).hasSize(1)
        assertThat(path.currentNode).isEqualTo(currents.single())
    }
}
