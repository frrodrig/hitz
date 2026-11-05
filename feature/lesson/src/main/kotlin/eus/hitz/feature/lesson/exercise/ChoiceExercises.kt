package eus.hitz.feature.lesson.exercise

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import eus.hitz.core.designsystem.component.ChoiceState
import eus.hitz.core.designsystem.component.GroupPosition
import eus.hitz.core.designsystem.component.HitzChoiceCard
import eus.hitz.core.designsystem.component.HitzChoiceGroup
import eus.hitz.core.designsystem.preview.HitzPreviews
import eus.hitz.core.designsystem.theme.HitzSpacing
import eus.hitz.core.designsystem.theme.HitzTheme
import eus.hitz.core.model.content.Language
import eus.hitz.feature.lesson.AudioClip
import eus.hitz.feature.lesson.ChatMessageUi
import eus.hitz.feature.lesson.ExerciseUi
import eus.hitz.feature.lesson.FeedbackType
import eus.hitz.feature.lesson.LessonAction
import eus.hitz.feature.lesson.R
import eus.hitz.feature.lesson.ui.LanguageText
import eus.hitz.feature.lesson.ui.PromptCard

/** Choose the Spanish meaning of a Basque word or sentence. */
@Composable
internal fun ChooseMeaningExercise(
    exercise: ExerciseUi.ChooseMeaning,
    feedback: FeedbackType?,
    onAction: (LessonAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(HitzSpacing.xl)) {
        PromptCard(exercise.text, Language.Basque, onPlay = { onAction(LessonAction.PlayAudio()) })
        ChoiceOptions(exercise.options, exercise.selected, feedback, onAction)
    }
}

/** Complete a chat: the other people's messages, then the learner's reply, chosen below. */
@Composable
internal fun CompleteChatExercise(
    exercise: ExerciseUi.CompleteChat,
    feedback: FeedbackType?,
    onAction: (LessonAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(HitzSpacing.xl)) {
        Column(verticalArrangement = Arrangement.spacedBy(HitzSpacing.md)) {
            exercise.messages.forEach { ChatBubble(it.speaker, it.text, mine = false) }
            ChatBubble(
                speaker = stringResource(R.string.feature_lesson_chat_you),
                text = exercise.selected?.let { exercise.options[it] } ?: "…",
                mine = true,
            )
        }
        ChoiceOptions(exercise.options, exercise.selected, feedback, onAction)
    }
}

/** Read a Basque text and decide whether a Spanish statement about it is true. */
@Composable
internal fun ReadTrueFalseExercise(
    exercise: ExerciseUi.ReadTrueFalse,
    feedback: FeedbackType?,
    onAction: (LessonAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(HitzSpacing.xl)) {
        PromptCard(exercise.passage, Language.Basque)
        Text(exercise.statement, style = MaterialTheme.typography.titleLarge)
        ChoiceOptions(
            options = listOf(
                stringResource(R.string.feature_lesson_true),
                stringResource(R.string.feature_lesson_false),
            ),
            selected = exercise.selected,
            feedback = feedback,
            onAction = onAction,
        )
    }
}

/**
 * Single-choice options as a Material 3 Expressive segmented list. After checking, the chosen
 * option shows whether it was right and the others are disabled.
 */
@Composable
internal fun ChoiceOptions(
    options: List<String>,
    selected: Int?,
    feedback: FeedbackType?,
    onAction: (LessonAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    HitzChoiceGroup(modifier.fillMaxWidth()) {
        options.forEachIndexed { index, option ->
            HitzChoiceCard(
                text = option,
                state = choiceState(index, selected, feedback),
                onClick = { if (feedback == null) onAction(LessonAction.SelectOption(index)) },
                position = GroupPosition.of(index, options.size),
            )
        }
    }
}

private fun choiceState(index: Int, selected: Int?, feedback: FeedbackType?): ChoiceState = when {
    feedback == null -> if (index == selected) ChoiceState.Selected else ChoiceState.Default
    index != selected -> ChoiceState.Disabled
    feedback == FeedbackType.Incorrect -> ChoiceState.Incorrect
    else -> ChoiceState.Correct
}

@Composable
private fun ChatBubble(speaker: String, text: String, mine: Boolean) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (mine) Alignment.End else Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(HitzSpacing.xs),
    ) {
        Text(speaker, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
        val corner = 20.dp
        val scheme = MaterialTheme.colorScheme
        Surface(
            modifier = Modifier.widthIn(max = 300.dp),
            shape = if (mine) {
                RoundedCornerShape(corner, 4.dp, corner, corner)
            } else {
                RoundedCornerShape(4.dp, corner, corner, corner)
            },
            color = if (mine) scheme.primaryContainer else scheme.surfaceContainerHigh,
            contentColor = if (mine) scheme.onPrimaryContainer else scheme.onSurface,
        ) {
            LanguageText(
                text = text,
                language = Language.Basque,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(horizontal = HitzSpacing.lg, vertical = HitzSpacing.md),
            )
        }
    }
}

@HitzPreviews
@Composable
private fun ChooseMeaningExercisePreview() {
    HitzTheme {
        Surface {
            ChooseMeaningExercise(
                exercise = ExerciseUi.ChooseMeaning(
                    id = "preview",
                    clip = AudioClip("preview", "Barkatu!"),
                    options = listOf("¡Gracias!", "¡Perdón!", "¡De nada!"),
                    selected = 1,
                ),
                feedback = null,
                onAction = {},
                modifier = Modifier.padding(HitzSpacing.lg),
            )
        }
    }
}

@HitzPreviews
@Composable
private fun CompleteChatExercisePreview() {
    HitzTheme {
        Surface {
            CompleteChatExercise(
                exercise = ExerciseUi.CompleteChat(
                    id = "preview",
                    messages = listOf(ChatMessageUi("Mikel", "Kaixo! Ni Mikel naiz. Eta zu?")),
                    options = listOf("Ez horregatik.", "Ni Itziar naiz.", "Gabon, Mikel."),
                    selected = 1,
                ),
                feedback = FeedbackType.Correct,
                onAction = {},
                modifier = Modifier.padding(HitzSpacing.lg),
            )
        }
    }
}

@HitzPreviews
@Composable
private fun ReadTrueFalseExercisePreview() {
    HitzTheme {
        Surface {
            ReadTrueFalseExercise(
                exercise = ExerciseUi.ReadTrueFalse(
                    id = "preview",
                    passage = "Egun on! Ni Jon naiz. Zorionak, Ane!",
                    statement = "Jon felicita a Ane.",
                ),
                feedback = null,
                onAction = {},
                modifier = Modifier.padding(HitzSpacing.lg),
            )
        }
    }
}
