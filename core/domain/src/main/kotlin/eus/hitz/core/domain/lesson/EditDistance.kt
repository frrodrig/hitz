package eus.hitz.core.domain.lesson

import kotlin.math.min

/**
 * Optimal string alignment distance (Damerau–Levenshtein with adjacent transpositions):
 * insertions, deletions, substitutions and swaps of two neighbouring letters cost 1.
 */
internal fun editDistance(a: String, b: String): Int {
    if (a == b) return 0
    if (a.isEmpty()) return b.length
    if (b.isEmpty()) return a.length
    val d = Array(a.length + 1) { IntArray(b.length + 1) }
    for (i in 0..a.length) d[i][0] = i
    for (j in 0..b.length) d[0][j] = j
    for (i in 1..a.length) {
        for (j in 1..b.length) {
            val cost = if (a[i - 1] == b[j - 1]) 0 else 1
            var best = min(min(d[i - 1][j] + 1, d[i][j - 1] + 1), d[i - 1][j - 1] + cost)
            if (isTransposition(a, b, i, j)) {
                best = min(best, d[i - 2][j - 2] + 1)
            }
            d[i][j] = best
        }
    }
    return d[a.length][b.length]
}

/** Whether the two letters before position [i] in [a] are the two before [j] in [b], swapped. */
private fun isTransposition(a: String, b: String, i: Int, j: Int): Boolean =
    i > 1 && j > 1 && a[i - 1] == b[j - 2] && a[i - 2] == b[j - 1]
