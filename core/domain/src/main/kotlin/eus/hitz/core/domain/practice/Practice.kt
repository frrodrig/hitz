package eus.hitz.core.domain.practice

import eus.hitz.core.model.content.Example
import eus.hitz.core.model.content.GuidebookSection
import eus.hitz.core.model.content.LocalizedText

/** Practice that mixes everything the learner has already seen. */
enum class PracticeMode {
    /** What the learner gets wrong most: exercises, words and structures (spaced repetition). */
    Mistakes,

    /** Listening exercises only. */
    Listening,

    /** Speaking exercises only. */
    Speaking,
}

/** A unit the learner has started, as listed in the practice hub. */
data class LearnedUnit(
    val unitId: String,
    val sectionId: String,
    val title: LocalizedText,
    /** Share of the unit's required steps completed, from 0 to 1. */
    val progress: Float,
    val words: Int,
    val guides: Int,
    val hasStory: Boolean,
) {
    val completed: Boolean get() = progress >= 1f
}

/** The practice hub: what can be practised and reviewed. */
data class PracticeHub(
    /** Started units, in course order. */
    val units: List<LearnedUnit>,
    /** Words, structures and exercises the learner has got wrong, to practise in [PracticeMode.Mistakes]. */
    val weakItems: Int,
    /** Exercises available for each mode; a mode with none is hidden. */
    val available: Map<PracticeMode, Int>,
) {
    val words: Int get() = units.sumOf { it.words }
    val guides: Int get() = units.sumOf { it.guides }
    val stories: Int get() = units.count { it.hasStory }
}

/** How well a word is known, from the mistakes made with it. */
enum class Strength { Weak, Medium, Strong }

/**
 * A word the learner has seen, with its translation, picture and [strength]. [clipId] is its
 * recorded audio: `<unit>.kw<index>` for key words, the exercise id for new-word cards.
 */
data class ReviewWord(
    val eu: String,
    val es: String,
    val image: String?,
    val clipId: String,
    val strength: Strength,
)

/** A finished or completed story of a unit, to read again. */
data class ReviewStory(val unitId: String, val levelId: String, val title: LocalizedText, val completed: Boolean)

/** Everything to review in a unit: words, grammar notes, example sentences and its story. */
data class UnitReview(
    val unitId: String,
    val title: LocalizedText,
    val goals: List<LocalizedText>,
    val progress: Float,
    val words: List<ReviewWord>,
    val guide: List<GuidebookSection>,
    val phrases: List<Example>,
    val story: ReviewStory?,
) {
    val completed: Boolean get() = progress >= 1f
}
