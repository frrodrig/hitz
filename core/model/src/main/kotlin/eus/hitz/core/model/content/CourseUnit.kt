package eus.hitz.core.model.content

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** A unit of the path (`courses/<id>/units/<unitId>.json`), with its guidebook and levels. */
@Serializable
data class CourseUnit(
    val schemaVersion: Int,
    val id: String,
    val section: String,
    /** Unit of the reference textbook this unit is based on (1–12). */
    val bookUnit: Int,
    val title: LocalizedText,
    val goals: List<LocalizedText>,
    val guidebook: Guidebook,
    val levels: List<Level>,
    /** Words shown in the unit's introduction, before its first lesson. */
    val keyWords: List<KeyWord> = emptyList(),
)

/** A key word of a unit; its clip is `<unit id>.kw<index>`. */
@Serializable
data class KeyWord(
    val eu: String,
    val es: String,
    /** File name in `assets/images/`. */
    val image: String? = null,
)

/** Short grammar and usage notes shown before or during the unit. */
@Serializable
data class Guidebook(
    val sections: List<GuidebookSection>,
)

@Serializable
data class GuidebookSection(
    val title: LocalizedText,
    val body: LocalizedText,
    val examples: List<Example> = emptyList(),
)

@Serializable
data class Example(
    val eu: String,
    val es: String,
)

/** A node of the learning path. */
@Serializable
sealed interface Level {
    val id: String
    val title: LocalizedText

    /** A level made of authored lessons. */
    @Serializable
    @SerialName("lesson")
    data class Lessons(
        override val id: String,
        override val title: LocalizedText,
        val lessons: List<Lesson>,
    ) : Level

    /** A short interactive story. */
    @Serializable
    @SerialName("story")
    data class StoryLevel(
        override val id: String,
        override val title: LocalizedText,
        val story: Story,
    ) : Level

    /**
     * Review generated at run time from the content and the learner's mistakes: of its unit, or of
     * the whole section when [scope] is [ReviewScope.Section] (the section review closes a section).
     */
    @Serializable
    @SerialName("review")
    data class Review(
        override val id: String,
        override val title: LocalizedText,
        val scope: ReviewScope = ReviewScope.Unit,
    ) : Level

    /** Optional legendary level: a harder generated review with no hints. */
    @Serializable
    @SerialName("legendary")
    data class Legendary(
        override val id: String,
        override val title: LocalizedText,
    ) : Level
}

@Serializable
enum class ReviewScope {
    @SerialName("unit")
    Unit,

    @SerialName("section")
    Section,
}

@Serializable
data class Lesson(
    val id: String,
    val exercises: List<Exercise>,
)
