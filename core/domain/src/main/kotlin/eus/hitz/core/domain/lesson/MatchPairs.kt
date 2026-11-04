package eus.hitz.core.domain.lesson

import kotlin.random.Random

/** Which column an item belongs to. */
enum class PairSide { Left, Right }

/**
 * State of a "match the pairs" exercise. Pair `i` is left item `i` and right item `i`; each
 * column is displayed in its own shuffled order.
 */
data class MatchPairsState(
    val left: List<String>,
    val right: List<String>,
    val leftOrder: List<Int>,
    val rightOrder: List<Int>,
    val matched: Set<Int> = emptySet(),
    val selectedLeft: Int? = null,
    val selectedRight: Int? = null,
    val mismatches: Int = 0,
    /** The pair indexes of the last wrong attempt, shown briefly as an error. */
    val lastMismatch: Pair<Int, Int>? = null,
) {
    val isComplete: Boolean get() = matched.size == left.size

    /** Selects the pair at [pairIndex] on [side]; matches or rejects when both sides are selected. */
    fun select(side: PairSide, pairIndex: Int): MatchPairsState {
        if (pairIndex in matched) return this
        val next = when (side) {
            PairSide.Left -> copy(selectedLeft = pairIndex, lastMismatch = null)
            PairSide.Right -> copy(selectedRight = pairIndex, lastMismatch = null)
        }
        val l = next.selectedLeft ?: return next
        val r = next.selectedRight ?: return next
        return if (l == r) {
            next.copy(matched = matched + l, selectedLeft = null, selectedRight = null)
        } else {
            next.copy(selectedLeft = null, selectedRight = null, mismatches = mismatches + 1, lastMismatch = l to r)
        }
    }

    companion object {
        fun start(left: List<String>, right: List<String>, random: Random): MatchPairsState {
            require(left.size == right.size) { "both columns need the same number of items" }
            val indexes = left.indices.toList()
            return MatchPairsState(left, right, indexes.shuffled(random), indexes.shuffled(random))
        }
    }
}
