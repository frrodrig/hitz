package eus.hitz.feature.lesson.exercise

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import eus.hitz.core.designsystem.component.HitzWordBank
import eus.hitz.core.designsystem.component.HitzWordChip
import eus.hitz.core.designsystem.component.HitzWordTileHeight
import eus.hitz.core.designsystem.preview.HitzPreviews
import eus.hitz.core.designsystem.theme.HitzSpacing
import eus.hitz.core.designsystem.theme.HitzTheme
import eus.hitz.core.domain.lesson.Tile
import eus.hitz.core.model.content.Language
import eus.hitz.feature.lesson.ExerciseUi
import eus.hitz.feature.lesson.LessonAction
import eus.hitz.feature.lesson.R
import eus.hitz.feature.lesson.ui.PromptCard

/** Translate by tapping words. */
@Composable
internal fun WordBankExercise(
    exercise: ExerciseUi.WordBank,
    onAction: (LessonAction) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(HitzSpacing.xl)) {
        PromptCard(
            text = exercise.prompt,
            language = exercise.promptLanguage,
            // Basque prompts can be heard; Spanish ones need no audio.
            onPlay = { onAction(LessonAction.PlayAudio()) }.takeIf { exercise.promptLanguage == Language.Basque },
        )
        TileAnswerArea(exercise, onAction, enabled = enabled)
    }
}

/** Put the words of a Basque sentence in order; the Spanish meaning is the prompt. */
@Composable
internal fun OrderSentenceExercise(
    exercise: ExerciseUi.OrderSentence,
    onAction: (LessonAction) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(HitzSpacing.xl)) {
        PromptCard(exercise.translation, Language.Spanish)
        TileAnswerArea(exercise, onAction, enabled = enabled)
    }
}

/**
 * The answer line and the word bank: selected words move to the answer line; tapping them there
 * puts them back.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun TileAnswerArea(
    exercise: ExerciseUi.TileAnswer,
    onAction: (LessonAction) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val answerDescription = stringResource(R.string.feature_lesson_answer_area)
    Column(modifier, verticalArrangement = Arrangement.spacedBy(HitzSpacing.xl)) {
        AnswerTray(Modifier.semantics { contentDescription = answerDescription }) {
            exercise.selected.forEach { id ->
                val tile = exercise.tiles.first { it.id == id }
                HitzWordChip(tile.text, onClick = { if (enabled) onAction(LessonAction.RemoveTile(id)) })
            }
        }
        HitzWordBank(Modifier.fillMaxWidth()) {
            exercise.tiles.forEach { tile ->
                HitzWordChip(
                    word = tile.text,
                    onClick = { if (enabled) onAction(LessonAction.SelectTile(tile.id)) },
                    used = tile.id in exercise.selected,
                )
            }
        }
    }
}

private val TRAY_PADDING = 14.dp
private val TRAY_LINE = 2.dp
private const val TRAY_ROWS = 2

/**
 * Where the answer is built: a tonal tray ruled like a notebook, one line under each row of
 * tiles, two rows tall at least.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun AnswerTray(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    val line = MaterialTheme.colorScheme.outlineVariant
    val rowHeight = HitzWordTileHeight + HitzSpacing.sm
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surfaceContainerLow,
    ) {
        FlowRow(
            modifier = Modifier
                .heightIn(min = rowHeight * TRAY_ROWS - HitzSpacing.sm + TRAY_PADDING * 2)
                .drawBehind {
                    val step = rowHeight.toPx()
                    val inset = TRAY_PADDING.toPx()
                    var y = inset + HitzWordTileHeight.toPx() + (HitzSpacing.sm.toPx() - TRAY_LINE.toPx()) / 2
                    while (y < size.height - inset / 2) {
                        drawLine(line, Offset(inset, y), Offset(size.width - inset, y), TRAY_LINE.toPx())
                        y += step
                    }
                }
                .padding(TRAY_PADDING),
            horizontalArrangement = Arrangement.spacedBy(HitzSpacing.sm),
            verticalArrangement = Arrangement.spacedBy(HitzSpacing.sm),
        ) { content() }
    }
}

@HitzPreviews
@Composable
private fun WordBankExercisePreview() {
    HitzTheme {
        Surface {
            WordBankExercise(
                exercise = ExerciseUi.WordBank(
                    id = "preview",
                    prompt = "Buenos días, ¿qué tal?",
                    promptLanguage = Language.Spanish,
                    tiles = listOf("zer", "gabon", "Egun", "moduz", "agur", "on").mapIndexed { i, t -> Tile(i, t) },
                    selected = listOf(2, 5),
                ),
                onAction = {},
                modifier = Modifier.padding(HitzSpacing.lg),
            )
        }
    }
}

@HitzPreviews
@Composable
private fun OrderSentenceExercisePreview() {
    HitzTheme {
        Surface {
            OrderSentenceExercise(
                exercise = ExerciseUi.OrderSentence(
                    id = "preview",
                    translation = "¿Tú eres Mikel?",
                    tiles = listOf("zara", "Zu", "Mikel").mapIndexed { i, t -> Tile(i, t) },
                    selected = listOf(1),
                ),
                onAction = {},
                modifier = Modifier.padding(HitzSpacing.lg),
            )
        }
    }
}
