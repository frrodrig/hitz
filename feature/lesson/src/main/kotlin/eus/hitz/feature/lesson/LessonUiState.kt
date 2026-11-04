package eus.hitz.feature.lesson

import androidx.compose.runtime.Immutable
import eus.hitz.core.domain.lesson.LessonResult
import eus.hitz.core.domain.lesson.MatchPairsState
import eus.hitz.core.domain.lesson.PairSide
import eus.hitz.core.domain.lesson.Tile
import eus.hitz.core.model.content.Language

/** Immutable state of the lesson screen. */
sealed interface LessonUiState {
    data object Loading : LessonUiState

    data object Error : LessonUiState

    @Immutable
    data class Active(
        val progress: Float,
        val hearts: Int?,
        val exercise: ExerciseUi,
        val feedback: FeedbackUi? = null,
        val showQuitDialog: Boolean = false,
    ) : LessonUiState {
        val canCheck: Boolean get() = feedback == null && exercise.hasAnswer
    }

    data class Finished(val result: LessonResult) : LessonUiState

    data object OutOfHearts : LessonUiState
}

/** What the learner sees and has answered in the current exercise. */
@Immutable
sealed interface ExerciseUi {
    val id: String
    val hasAnswer: Boolean

    data class WordBank(
        override val id: String,
        val prompt: String,
        val promptLanguage: Language,
        val tiles: List<Tile>,
        val selected: List<Int> = emptyList(),
    ) : ExerciseUi {
        override val hasAnswer: Boolean get() = selected.isNotEmpty()
        val answerLanguage: Language get() = promptLanguage.other()
    }

    data class Typing(
        override val id: String,
        val prompt: String,
        val promptLanguage: Language,
        val input: String = "",
    ) : ExerciseUi {
        override val hasAnswer: Boolean get() = input.isNotBlank()
        val answerLanguage: Language get() = promptLanguage.other()
    }

    data class ChooseImage(
        override val id: String,
        val prompt: String,
        val options: List<ImageOptionUi>,
        val selected: Int? = null,
    ) : ExerciseUi {
        override val hasAnswer: Boolean get() = selected != null
    }

    data class MatchPairs(
        override val id: String,
        val state: MatchPairsState,
    ) : ExerciseUi {
        override val hasAnswer: Boolean get() = state.isComplete
    }
}

data class ImageOptionUi(val text: String, val imageUri: String)

enum class FeedbackType { Correct, AlmostCorrect, Incorrect }

/** Shown after checking. [expected] is the right answer when it should be displayed. */
data class FeedbackUi(val type: FeedbackType, val expected: String? = null)

/** User actions (unidirectional data flow). */
sealed interface LessonAction {
    data class SelectTile(val tileId: Int) : LessonAction

    data class RemoveTile(val tileId: Int) : LessonAction

    data class ChangeInput(val text: String) : LessonAction

    data class SelectOption(val index: Int) : LessonAction

    data class SelectPair(val side: PairSide, val pairIndex: Int) : LessonAction

    data object Check : LessonAction

    data object Continue : LessonAction

    data object RequestQuit : LessonAction

    data object DismissQuit : LessonAction

    data object ConfirmQuit : LessonAction
}

/** One-off events for the screen. */
sealed interface LessonEvent {
    data object Close : LessonEvent
}

internal fun Language.other(): Language = if (this == Language.Basque) Language.Spanish else Language.Basque
