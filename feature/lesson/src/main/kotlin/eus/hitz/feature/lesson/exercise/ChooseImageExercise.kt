package eus.hitz.feature.lesson.exercise

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import eus.hitz.core.designsystem.preview.HitzPreviews
import eus.hitz.core.designsystem.theme.HitzSpacing
import eus.hitz.core.designsystem.theme.HitzTheme
import eus.hitz.core.model.content.Language
import eus.hitz.feature.lesson.ExerciseUi
import eus.hitz.feature.lesson.ImageOptionUi
import eus.hitz.feature.lesson.LessonAction
import eus.hitz.feature.lesson.ui.LanguageText

/** Pick the picture that matches the prompt. Each card is a radio option labelled in Basque. */
@Composable
internal fun ChooseImageExercise(
    exercise: ExerciseUi.ChooseImage,
    onAction: (LessonAction) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .selectableGroup(),
        horizontalArrangement = Arrangement.spacedBy(HitzSpacing.md),
    ) {
        exercise.options.forEachIndexed { index, option ->
            val selected = exercise.selected == index
            OutlinedCard(
                modifier = Modifier
                    .weight(1f)
                    .selectable(
                        selected = selected,
                        enabled = enabled,
                        role = Role.RadioButton,
                        onClick = { onAction(LessonAction.SelectOption(index)) },
                    ),
                colors = CardDefaults.outlinedCardColors(
                    containerColor = if (selected) {
                        MaterialTheme.colorScheme.secondaryContainer
                    } else {
                        MaterialTheme.colorScheme.surface
                    },
                ),
                border = if (selected) {
                    BorderStroke(3.dp, MaterialTheme.colorScheme.secondary)
                } else {
                    CardDefaults.outlinedCardBorder()
                },
            ) {
                Column(
                    modifier = Modifier.padding(HitzSpacing.sm),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(HitzSpacing.sm),
                ) {
                    AsyncImage(
                        model = option.imageUri,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                            .clip(MaterialTheme.shapes.medium),
                    )
                    LanguageText(option.text, Language.Basque, style = MaterialTheme.typography.titleMedium)
                }
            }
        }
    }
}

@HitzPreviews
@Composable
private fun ChooseImageExercisePreview() {
    HitzTheme {
        Surface {
            ChooseImageExercise(
                exercise = ExerciseUi.ChooseImage(
                    id = "preview",
                    prompt = "buenas noches",
                    options = listOf("egun on", "arratsalde on", "gabon").map { ImageOptionUi(it, "") },
                    selected = 2,
                ),
                onAction = {},
                modifier = Modifier.padding(HitzSpacing.lg),
            )
        }
    }
}
