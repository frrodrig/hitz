package eus.hitz.core.model.content

import kotlinx.serialization.Serializable

/** Text shown in the UI language: Spanish (default) or Basque. */
@Serializable
data class LocalizedText(
    val es: String,
    val eu: String,
)

/**
 * Course index (`courses/<id>/course.json`). Units are stored in separate files and referenced
 * by id from their section, so the app can load them lazily.
 */
@Serializable
data class Course(
    val schemaVersion: Int,
    val id: String,
    val learningLanguage: String,
    val fromLanguage: String,
    val title: LocalizedText,
    val sections: List<Section>,
)

/**
 * A section of the course. [level] is the CEFR level it belongs to ([CefrLevel.A1] or
 * [CefrLevel.A2_1]); sections of the same level follow each other.
 */
@Serializable
data class Section(
    val id: String,
    val title: LocalizedText,
    val description: LocalizedText,
    val units: List<String>,
    val level: String = CefrLevel.A1,
)

/** The CEFR levels the course covers, as written in `course.json`. */
object CefrLevel {
    const val A1 = "A1"
    const val A2_1 = "A2.1"
}
