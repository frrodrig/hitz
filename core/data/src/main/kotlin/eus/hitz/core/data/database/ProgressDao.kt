package eus.hitz.core.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
abstract class ProgressDao {
    @Query("SELECT id FROM completions")
    abstract fun observeCompletionIds(): Flow<List<String>>

    @Query("SELECT * FROM completions WHERE id = :id")
    abstract suspend fun completion(id: String): CompletionEntity?

    @Upsert
    abstract suspend fun upsertCompletion(completion: CompletionEntity)

    /** Keeps the best accuracy and the first completion time across repeats. */
    @Transaction
    open suspend fun recordCompletion(id: String, unitId: String, accuracy: Float, completedAt: Long) {
        val existing = completion(id)
        upsertCompletion(
            CompletionEntity(
                id = id,
                unitId = unitId,
                bestAccuracy = maxOf(accuracy, existing?.bestAccuracy ?: 0f),
                firstCompletedAt = existing?.firstCompletedAt ?: completedAt,
                lastCompletedAt = completedAt,
                timesCompleted = (existing?.timesCompleted ?: 0) + 1,
            ),
        )
    }

    @Insert
    abstract suspend fun insertMistake(mistake: MistakeEntity): Long

    @Insert
    abstract suspend fun insertTags(tags: List<MistakeTagEntity>)

    @Transaction
    open suspend fun insertMistakeWithTags(mistake: MistakeEntity, lemmas: List<String>, structures: List<String>) {
        val id = insertMistake(mistake)
        val tags = lemmas.distinct().map { MistakeTagEntity(id, MistakeTagEntity.LEMMA, it) } +
            structures.distinct().map { MistakeTagEntity(id, MistakeTagEntity.STRUCTURE, it) }
        if (tags.isNotEmpty()) insertTags(tags)
    }

    @Query("SELECT exerciseId AS `key`, COUNT(*) AS count FROM mistakes WHERE unitId = :unitId GROUP BY exerciseId")
    abstract suspend fun mistakesByExercise(unitId: String): List<KeyCount>

    @Query(
        """
        SELECT t.value AS `key`, COUNT(*) AS count FROM mistake_tags t
        JOIN mistakes m ON m.id = t.mistakeId
        WHERE m.unitId = :unitId AND t.kind = :kind
        GROUP BY t.value
        """,
    )
    abstract suspend fun mistakesByTag(unitId: String, kind: String): List<KeyCount>
}
