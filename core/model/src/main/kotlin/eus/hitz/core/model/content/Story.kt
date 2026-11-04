package eus.hitz.core.model.content

import kotlinx.serialization.Serializable

/** A short dialogue read line by line, with comprehension questions in between. */
@Serializable
data class Story(
    val id: String,
    val title: String,
    val lines: List<StoryLine>,
    val questions: List<StoryQuestion>,
)

@Serializable
data class StoryLine(
    val speaker: String,
    val eu: String,
    val es: String,
)

/** A question shown after line [afterLine] (0-based). */
@Serializable
data class StoryQuestion(
    val afterLine: Int,
    val prompt: String,
    val options: List<String>,
    val correct: Int,
)
