package eus.hitz.feature.lesson

import androidx.compose.runtime.Composable
import eus.hitz.core.designsystem.preview.HitzPreviews
import eus.hitz.core.designsystem.theme.HitzTheme
import eus.hitz.core.domain.lesson.LessonResult
import eus.hitz.core.domain.lesson.MatchPairsState
import eus.hitz.core.domain.lesson.PairSide
import eus.hitz.core.domain.lesson.Tile
import eus.hitz.core.model.content.Gloss
import eus.hitz.core.model.content.Glossary
import eus.hitz.core.model.content.Language
import kotlin.random.Random

internal object LessonPreviewData {
    val wordBank = ExerciseUi.WordBank(
        id = "preview",
        prompt = "Kaixo! Ni Ane naiz.",
        promptLanguage = Language.Basque,
        tiles = listOf("Soy", "tú", "Hola", "adiós", "Ane").mapIndexed { i, t -> Tile(i, t) },
        selected = listOf(2, 0, 4),
    )

    val typing = ExerciseUi.Typing("preview-typing", "Muchas gracias.", Language.Spanish, input = "Eskerik asko")

    val image = ExerciseUi.ChooseImage(
        id = "preview-image",
        prompt = "buenas noches",
        options = listOf("egun on", "arratsalde on", "gabon").map { ImageOptionUi(it, "") },
        selected = 2,
    )

    val pairs = ExerciseUi.MatchPairs(
        id = "preview-pairs",
        state = MatchPairsState.start(
            left = listOf("kaixo", "agur", "mesedez", "barkatu"),
            right = listOf("hola", "adiós", "por favor", "perdón"),
            random = Random(4),
        ).select(PairSide.Left, 1).select(PairSide.Right, 1).select(PairSide.Left, 0),
    )

    val listenTap = ExerciseUi.ListenTap(
        id = "preview-listen",
        clip = AudioClip("preview-listen", "Gero arte, Jon!"),
        tiles = listOf("agur", "Jon", "Gero", "gabon", "arte").mapIndexed { i, t -> Tile(i, t) },
        selected = listOf(2, 4),
    )

    val fillBlank = ExerciseUi.FillBlank(
        id = "preview-blank",
        before = "Ni Ane ",
        after = ".",
        translation = "Yo soy Ane.",
        options = listOf("zara", "naiz"),
        selected = 1,
    )

    val meaning = ExerciseUi.ChooseMeaning(
        id = "preview-meaning",
        clip = AudioClip("preview-meaning", "Barkatu!"),
        options = listOf("¡Gracias!", "¡Perdón!", "¡De nada!"),
        selected = 1,
    )

    val chat = ExerciseUi.CompleteChat(
        id = "preview-chat",
        messages = listOf(ChatMessageUi("Mikel", "Kaixo! Ni Mikel naiz. Eta zu?")),
        options = listOf("Ez horregatik.", "Ni Itziar naiz.", "Gabon, Mikel."),
        selected = 1,
    )

    val trueFalse = ExerciseUi.ReadTrueFalse(
        id = "preview-true-false",
        passage = "Egun on! Ni Jon naiz. Zorionak, Ane!",
        statement = "Jon felicita a Ane.",
    )

    val speak = ExerciseUi.Speak("preview-speak", AudioClip("preview-speak", "Egun on, Ane!"), "¡Buenos días, Ane!")

    val wordIntro = ExerciseUi.WordIntro("preview-word", AudioClip("preview-word", "gabon"), "buenas noches", null)

    val glossary = Glossary(
        entries = mapOf("kaixo" to Gloss("hola"), "ni" to Gloss("yo"), "naiz" to Gloss("soy / estoy", "izan")),
    )

    val active = LessonUiState.Active(progress = 0.4f, hearts = 4, exercise = wordBank, glossary = glossary)
    val typingAlmost = active.copy(
        exercise = typing,
        feedback = FeedbackUi(FeedbackType.AlmostCorrect, "Eskerrik asko.")
    )
    val incorrect = active.copy(feedback = FeedbackUi(FeedbackType.Incorrect, "¡Hola! Soy Ane."))
    val chatCorrect = active.copy(exercise = chat, feedback = FeedbackUi(FeedbackType.Correct))
    val trueFalseIncorrect = active.copy(
        exercise = trueFalse.copy(selected = ExerciseUi.ReadTrueFalse.FALSE),
        feedback = FeedbackUi(FeedbackType.Incorrect),
    )
    val finished = LessonUiState.Finished(
        result = LessonResult("preview", true, 5, 4, 4, emptyList()),
        reward = RewardUi(xp = 10, streak = 4, streakExtended = true, gems = 15),
    )
}

@HitzPreviews
@Composable
private fun LessonActivePreview() {
    HitzTheme { LessonContent(LessonPreviewData.active, onAction = {}, onClose = {}) }
}

@HitzPreviews
@Composable
private fun LessonIncorrectPreview() {
    HitzTheme { LessonContent(LessonPreviewData.incorrect, onAction = {}, onClose = {}) }
}

@HitzPreviews
@Composable
private fun LessonFinishedPreview() {
    HitzTheme { LessonContent(LessonPreviewData.finished, onAction = {}, onClose = {}) }
}
