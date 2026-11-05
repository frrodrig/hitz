package eus.hitz.core.domain.progress

import eus.hitz.core.domain.lesson.Mistake
import kotlinx.coroutines.flow.Flow

/** How often the learner got each exercise, lemma and grammar structure wrong. */
data class MistakeStats(
    val byExercise: Map<String, Int> = emptyMap(),
    val byLemma: Map<String, Int> = emptyMap(),
    val byStructure: Map<String, Int> = emptyMap(),
) {
    /** Counts of both, added up (used to review several units together). */
    operator fun plus(other: MistakeStats) = MistakeStats(
        byExercise = byExercise.sum(other.byExercise),
        byLemma = byLemma.sum(other.byLemma),
        byStructure = byStructure.sum(other.byStructure),
    )

    private fun Map<String, Int>.sum(other: Map<String, Int>): Map<String, Int> =
        (keys + other.keys).associateWith { (this[it] ?: 0) + (other[it] ?: 0) }
}

/**
 * Local learning progress. A *completion id* is a lesson id for authored lessons, or a level id for
 * generated levels (review, legendary) and stories.
 */
interface ProgressRepository {
    fun observeCompleted(): Flow<Set<String>>

    suspend fun recordCompletion(unitId: String, completionId: String, accuracy: Float, completedAt: Long)

    suspend fun recordMistakes(unitId: String, mistakes: List<Mistake>, recordedAt: Long)

    suspend fun mistakeStats(unitId: String): MistakeStats
}
