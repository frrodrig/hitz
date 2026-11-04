package eus.hitz.core.domain.lesson

import javax.inject.Inject

/** Result of checking a free-text answer. [expected] is the accepted answer to show. */
sealed interface AnswerResult {
    val expected: String

    data class Correct(override val expected: String) : AnswerResult

    /** Accepted with a small typo; the learner sees the right spelling. */
    data class AlmostCorrect(override val expected: String) : AnswerResult

    data class Incorrect(override val expected: String) : AnswerResult

    val isAccepted: Boolean get() = this !is Incorrect
}

/**
 * Tolerant answer checking: any of the accepted answers is valid, comparison ignores case,
 * accents and punctuation, and a small typo (see [typoAllowance]) is "almost correct".
 */
class AnswerChecker @Inject constructor() {
    fun check(input: String, accepted: List<String>): AnswerResult {
        require(accepted.isNotEmpty()) { "an exercise needs at least one accepted answer" }
        val canonical = accepted.first()
        val given = AnswerNormalizer.normalize(input)
        if (given.isEmpty()) return AnswerResult.Incorrect(canonical)

        val normalized = accepted.map { it to AnswerNormalizer.normalize(it) }
        normalized.firstOrNull { (_, value) -> value == given }?.let { (answer, _) ->
            return AnswerResult.Correct(answer)
        }
        val closest = normalized
            .map { (answer, value) -> Triple(answer, value, editDistance(given, value)) }
            .minBy { it.third }
        val (answer, value, distance) = closest
        return if (distance <= typoAllowance(value) && sameWordCount(given, value)) {
            AnswerResult.AlmostCorrect(answer)
        } else {
            AnswerResult.Incorrect(canonical)
        }
    }

    private fun sameWordCount(a: String, b: String) = a.count { it == ' ' } == b.count { it == ' ' }

    internal companion object {
        private const val SHORT = 4
        private const val LONG = 12

        /** No typo allowed below 4 letters, 1 up to 11 letters, 2 from 12 letters. */
        fun typoAllowance(answer: String): Int = when {
            answer.length < SHORT -> 0
            answer.length < LONG -> 1
            else -> 2
        }
    }
}
