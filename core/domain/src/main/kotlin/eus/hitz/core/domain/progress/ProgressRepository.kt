package eus.hitz.core.domain.progress

import eus.hitz.core.domain.lesson.Mistake
import kotlinx.coroutines.flow.Flow

/** How often the learner got each exercise, lemma and grammar structure wrong. */
data class MistakeStats(
    val byExercise: Map<String, Int> = emptyMap(),
    val byLemma: Map<String, Int> = emptyMap(),
    val byStructure: Map<String, Int> = emptyMap(),
)

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
