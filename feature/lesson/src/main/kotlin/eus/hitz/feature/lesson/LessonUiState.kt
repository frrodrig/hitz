package eus.hitz.feature.lesson

import androidx.compose.runtime.Immutable
import eus.hitz.core.domain.gamification.SessionReward
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

    /** [reward] arrives once the session is saved. */
    data class Finished(val result: LessonResult, val reward: RewardUi? = null) : LessonUiState

    data object OutOfHearts : LessonUiState
}

/** What a finished session earned: XP, the streak, and the gems of quests and achievements. */
data class RewardUi(val xp: Int, val streak: Int, val streakExtended: Boolean, val gems: Int) {
    companion object {
        fun of(reward: SessionReward) =
            RewardUi(reward.xp, reward.streak, reward.streakExtended, reward.rewards.sumOf { it.gems })
    }
}

/** What the learner sees and has answered in the current exercise. */
@Immutable
sealed interface ExerciseUi {
    val id: String
    val hasAnswer: Boolean

    /** Answered by tapping word tiles in order. */
    sealed interface TileAnswer : ExerciseUi {
        val tiles: List<Tile>
        val selected: List<Int>

        override val hasAnswer: Boolean get() = selected.isNotEmpty()

        fun withSelected(selected: List<Int>): TileAnswer
    }

    /** Answered by typing. */
    sealed interface TextAnswer : ExerciseUi {
        val input: String

        override val hasAnswer: Boolean get() = input.isNotBlank()

        fun withInput(input: String): TextAnswer
    }

    /** Answered by picking one option. */
    sealed interface SingleChoice : ExerciseUi {
        val selected: Int?

        override val hasAnswer: Boolean get() = selected != null

        fun withSelected(index: Int): SingleChoice
    }

    /** Has a Basque clip the learner can play. */
    sealed interface Audible : ExerciseUi {
        val clip: AudioClip
    }

    data class WordBank(
        override val id: String,
        val prompt: String,
        val promptLanguage: Language,
        override val tiles: List<Tile>,
        override val selected: List<Int> = emptyList(),
    ) : TileAnswer {
        val answerLanguage: Language get() = promptLanguage.other()

        override fun withSelected(selected: List<Int>) = copy(selected = selected)
    }

    data class Typing(
        override val id: String,
        val prompt: String,
        val promptLanguage: Language,
        override val input: String = "",
    ) : TextAnswer {
        val answerLanguage: Language get() = promptLanguage.other()

        override fun withInput(input: String) = copy(input = input)
    }

    data class ChooseImage(
        override val id: String,
        val prompt: String,
        val options: List<ImageOptionUi>,
        override val selected: Int? = null,
    ) : SingleChoice {
        override fun withSelected(index: Int) = copy(selected = index)
    }

    data class MatchPairs(
        override val id: String,
        val state: MatchPairsState,
    ) : ExerciseUi {
        override val hasAnswer: Boolean get() = state.isComplete
    }

    /** Listen to a Basque sentence and tap its words. */
    data class ListenTap(
        override val id: String,
        override val clip: AudioClip,
        override val tiles: List<Tile>,
        override val selected: List<Int> = emptyList(),
    ) : TileAnswer, Audible {
        override fun withSelected(selected: List<Int>) = copy(selected = selected)
    }

    /** Listen to a Basque sentence and type it. */
    data class ListenType(
        override val id: String,
        override val clip: AudioClip,
        override val input: String = "",
    ) : TextAnswer, Audible {
        override fun withInput(input: String) = copy(input = input)
    }

    /** Put the words of a Basque sentence in order; [translation] says what it means. */
    data class OrderSentence(
        override val id: String,
        val translation: String,
        override val tiles: List<Tile>,
        override val selected: List<Int> = emptyList(),
    ) : TileAnswer {
        override fun withSelected(selected: List<Int>) = copy(selected = selected)
    }

    /** A Basque sentence with a gap: [before] ___ [after]. */
    data class FillBlank(
        override val id: String,
        val before: String,
        val after: String,
        val translation: String,
        val options: List<String>,
        override val selected: Int? = null,
        /** Content index of each shown option (options are shuffled). */
        val order: List<Int> = options.indices.toList(),
    ) : SingleChoice {
        override fun withSelected(index: Int) = copy(selected = index)
    }

    /** Choose the Spanish meaning of a Basque text. */
    data class ChooseMeaning(
        override val id: String,
        override val clip: AudioClip,
        val options: List<String>,
        override val selected: Int? = null,
        /** Content index of each shown option (options are shuffled). */
        val order: List<Int> = options.indices.toList(),
    ) : SingleChoice, Audible {
        val text: String get() = clip.text

        override fun withSelected(index: Int) = copy(selected = index)
    }

    /** Choose the reply that completes a chat; the last message is the learner's. */
    data class CompleteChat(
        override val id: String,
        val messages: List<ChatMessageUi>,
        val options: List<String>,
        override val selected: Int? = null,
        /** Content index of each shown option (options are shuffled). */
        val order: List<Int> = options.indices.toList(),
    ) : SingleChoice {
        override fun withSelected(index: Int) = copy(selected = index)
    }

    /** Read a Basque passage and say whether a Spanish statement is true (option 0) or false (1). */
    data class ReadTrueFalse(
        override val id: String,
        val passage: String,
        val statement: String,
        override val selected: Int? = null,
    ) : SingleChoice {
        override fun withSelected(index: Int) = copy(selected = index)

        companion object {
            const val TRUE = 0
            const val FALSE = 1
        }
    }

    /** Say a Basque sentence aloud; answered through speech recognition, not the check button. */
    data class Speak(
        override val id: String,
        override val clip: AudioClip,
        val translation: String,
    ) : Audible {
        override val hasAnswer: Boolean get() = false
        val text: String get() = clip.text
    }
}

/** A pre-recorded clip ([id]) and the text it says, used as a text-to-speech fallback. */
data class AudioClip(val id: String, val text: String)

data class ChatMessageUi(val speaker: String, val text: String)

data class ImageOptionUi(val text: String, val imageUri: String)

enum class FeedbackType { Correct, AlmostCorrect, Incorrect }

/**
 * Shown after checking. [expected] is the right answer when it should be displayed; true/false
 * exercises show none, because the other option is the right one.
 */
data class FeedbackUi(val type: FeedbackType, val expected: String? = null)

/** User actions (unidirectional data flow). */
sealed interface LessonAction {
    data class SelectTile(val tileId: Int) : LessonAction

    data class RemoveTile(val tileId: Int) : LessonAction

    data class ChangeInput(val text: String) : LessonAction

    data class SelectOption(val index: Int) : LessonAction

    data class SelectPair(val side: PairSide, val pairIndex: Int) : LessonAction

    /** Plays the Basque audio of the current exercise, optionally slower. */
    data class PlayAudio(val slow: Boolean = false) : LessonAction

    /** Speech recognition candidates for a speaking exercise, best first. */
    data class SpeechResult(val candidates: List<String>) : LessonAction

    /** "I can't speak now": skips this and the remaining speaking exercises of the session. */
    data object SkipSpeaking : LessonAction

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
