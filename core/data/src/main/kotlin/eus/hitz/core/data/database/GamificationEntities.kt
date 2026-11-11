package eus.hitz.core.data.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/** A finished session and its active study time. */
@Entity(tableName = "activities", indices = [Index("epochDay")])
data class ActivityEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sessionId: String,
    val kind: String,
    val accuracy: Float,
    val perfect: Boolean,
    @ColumnInfo(defaultValue = "0") val activeMillis: Long,
    val completedAt: Long,
    val epochDay: Long,
)

/** Study time of a local day and the daily goal it had. */
@Entity(tableName = "study_days")
data class StudyDayEntity(
    @PrimaryKey val epochDay: Long,
    val activeMillis: Long,
    val goalMillis: Long,
)
