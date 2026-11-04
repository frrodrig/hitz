package eus.hitz.feature.lesson.story

import androidx.compose.runtime.Immutable
import eus.hitz.feature.lesson.AudioClip

/** Immutable state of the story player. */
sealed interface StoryUiState {
    data object Loading : StoryUiState

    data object Error : StoryUiState

    @Immutable
    data class Active(
        val title: String,
        val progress: Float,
        val lines: List<StoryLineUi>,
        val question: StoryQuestionUi? = null,
        /** Lines whose Spanish translation is shown. */
        val translated: Set<Int> = emptySet(),
    ) : StoryUiState {
        /** A line can always be passed; a question only once it is answered. */
        val canContinue: Boolean get() = question == null || question.correct != null
    }

    data class Finished(val accuracy: Float) : StoryUiState
}

data class StoryLineUi(
    val index: Int,
    val speaker: String,
    val text: String,
    val translation: String,
    val clip: AudioClip,
)

/** A comprehension question; [wrong] options were tried, [correct] is set once answered. */
data class StoryQuestionUi(
    val prompt: String,
    val options: List<String>,
    val wrong: Set<Int> = emptySet(),
    val correct: Int? = null,
)

sealed interface StoryAction {
    data object Continue : StoryAction

    data class Answer(val option: Int) : StoryAction

    data class ToggleTranslation(val line: Int) : StoryAction

    data class PlayLine(val line: Int) : StoryAction

    data object Close : StoryAction
}

sealed interface StoryEvent {
    data object Close : StoryEvent
}
