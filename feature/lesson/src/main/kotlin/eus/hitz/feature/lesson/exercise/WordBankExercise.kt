package eus.hitz.feature.lesson.exercise

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import eus.hitz.core.designsystem.component.HitzWordBank
import eus.hitz.core.designsystem.component.HitzWordChip
import eus.hitz.core.designsystem.preview.HitzPreviews
import eus.hitz.core.designsystem.theme.HitzSpacing
import eus.hitz.core.designsystem.theme.HitzTheme
import eus.hitz.core.domain.lesson.Tile
import eus.hitz.core.model.content.Language
import eus.hitz.feature.lesson.ExerciseUi
import eus.hitz.feature.lesson.LessonAction
import eus.hitz.feature.lesson.R
import eus.hitz.feature.lesson.ui.PromptCard

/** Translate by tapping words: selected words move to the answer line; tap again to remove. */
@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun WordBankExercise(
    exercise: ExerciseUi.WordBank,
    onAction: (LessonAction) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val answerDescription = stringResource(R.string.feature_lesson_answer_area)
    Column(modifier, verticalArrangement = Arrangement.spacedBy(HitzSpacing.xl)) {
        PromptCard(exercise.prompt, exercise.promptLanguage)
        Column(verticalArrangement = Arrangement.spacedBy(HitzSpacing.sm)) {
            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 112.dp)
                    .semantics { contentDescription = answerDescription },
                horizontalArrangement = Arrangement.spacedBy(HitzSpacing.sm),
                verticalArrangement = Arrangement.spacedBy(HitzSpacing.sm),
            ) {
                exercise.selected.forEach { id ->
                    val tile = exercise.tiles.first { it.id == id }
                    HitzWordChip(tile.text, onClick = { if (enabled) onAction(LessonAction.RemoveTile(id)) })
                }
            }
            HorizontalDivider()
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
