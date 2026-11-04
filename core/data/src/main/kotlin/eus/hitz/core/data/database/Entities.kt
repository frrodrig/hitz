package eus.hitz.core.data.database

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/** A completed lesson (by lesson id) or generated level (by level id). */
@Entity(tableName = "completions", indices = [Index("unitId")])
data class CompletionEntity(
    @PrimaryKey val id: String,
    val unitId: String,
    /** Best first-try accuracy, from 0 to 1. */
    val bestAccuracy: Float,
    val firstCompletedAt: Long,
    val lastCompletedAt: Long,
    val timesCompleted: Int,
)

/** A wrong answer. Its words and grammar structures are stored as [MistakeTagEntity]. */
@Entity(tableName = "mistakes", indices = [Index("unitId"), Index("exerciseId")])
data class MistakeEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val unitId: String,
    val exerciseId: String,
    val given: String,
    val expected: String,
    val createdAt: Long,
)

@Entity(
    tableName = "mistake_tags",
    primaryKeys = ["mistakeId", "kind", "value"],
    foreignKeys = [
        ForeignKey(
            entity = MistakeEntity::class,
            parentColumns = ["id"],
            childColumns = ["mistakeId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("kind", "value")],
)
data class MistakeTagEntity(
    val mistakeId: Long,
    val kind: String,
    val value: String,
) {
    companion object {
        const val LEMMA = "lemma"
        const val STRUCTURE = "structure"
    }
}

/** Result row for COUNT … GROUP BY queries. */
data class KeyCount(val key: String, val count: Int)
