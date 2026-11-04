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

@Serializable
data class Section(
    val id: String,
    val title: LocalizedText,
    val description: LocalizedText,
    val units: List<String>,
)
