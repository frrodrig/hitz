package eus.hitz.core.data.progress

import eus.hitz.core.data.database.KeyCount
import eus.hitz.core.data.database.MistakeEntity
import eus.hitz.core.data.database.MistakeTagEntity
import eus.hitz.core.data.database.ProgressDao
import eus.hitz.core.domain.lesson.Mistake
import eus.hitz.core.domain.progress.MistakeStats
import eus.hitz.core.domain.progress.ProgressRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class RoomProgressRepository @Inject constructor(
    private val dao: ProgressDao,
) : ProgressRepository {
    override fun observeCompleted(): Flow<Set<String>> =
        dao.observeCompletionIds().map { it.toSet() }.distinctUntilChanged()

    override suspend fun recordCompletion(unitId: String, completionId: String, accuracy: Float, completedAt: Long) =
        dao.recordCompletion(completionId, unitId, accuracy, completedAt)

    override suspend fun recordMistakes(unitId: String, mistakes: List<Mistake>, recordedAt: Long) {
        mistakes.forEach { mistake ->
            dao.insertMistakeWithTags(
                mistake = MistakeEntity(
                    unitId = unitId,
                    exerciseId = mistake.exerciseId,
                    given = mistake.given,
                    expected = mistake.expected,
                    createdAt = recordedAt,
                ),
                lemmas = mistake.vocabulary,
                structures = mistake.structures,
            )
        }
    }

    override suspend fun mistakeStats(unitId: String): MistakeStats = MistakeStats(
        byExercise = dao.mistakesByExercise(unitId).toMap(),
        byLemma = dao.mistakesByTag(unitId, MistakeTagEntity.LEMMA).toMap(),
        byStructure = dao.mistakesByTag(unitId, MistakeTagEntity.STRUCTURE).toMap(),
    )

    private fun List<KeyCount>.toMap(): Map<String, Int> = associate { it.key to it.count }
}
