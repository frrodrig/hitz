package eus.hitz.core.domain.lesson

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import kotlin.random.Random

class MatchPairsStateTest {
    private val start = MatchPairsState.start(
        listOf("kaixo", "agur", "barkatu"),
        listOf("hola", "adiós", "perdón"),
        Random(3)
    )

    @Test
    fun `selecting both items of a pair matches it`() {
        val state = start.select(PairSide.Left, 1).select(PairSide.Right, 1)
        assertThat(state.matched).containsExactly(1)
        assertThat(state.selectedLeft).isNull()
    }

    @Test
    fun `a wrong pair is counted and remembered`() {
        val state = start.select(PairSide.Left, 0).select(PairSide.Right, 2)
        assertThat(state.matched).isEmpty()
        assertThat(state.mismatches).isEqualTo(1)
        assertThat(state.lastMismatch).isEqualTo(0 to 2)
    }

    @Test
    fun `matching every pair completes the exercise`() {
        var state = start
        (0..2).forEach { state = state.select(PairSide.Right, it).select(PairSide.Left, it) }
        assertThat(state.isComplete).isTrue()
    }

    @Test
    fun `matched items cannot be selected again`() {
        val matched = start.select(PairSide.Left, 0).select(PairSide.Right, 0)
        assertThat(matched.select(PairSide.Left, 0)).isEqualTo(matched)
    }
}
