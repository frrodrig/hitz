package eus.hitz.feature.lesson.exercise

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import eus.hitz.core.designsystem.component.HitzWordBank
import eus.hitz.core.designsystem.component.HitzWordChip
import eus.hitz.core.designsystem.preview.HitzPreviews
import eus.hitz.core.designsystem.theme.HitzSpacing
import eus.hitz.core.designsystem.theme.HitzTheme
import eus.hitz.core.model.content.Language
import eus.hitz.feature.lesson.ExerciseUi
import eus.hitz.feature.lesson.LessonAction
import eus.hitz.feature.lesson.R
import eus.hitz.feature.lesson.ui.localeList

private const val GAP = "      "

/** Complete the gap of a Basque sentence by tapping a word; another tap replaces it. */
@Composable
internal fun FillBlankExercise(
    exercise: ExerciseUi.FillBlank,
    onAction: (LessonAction) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val chosen = exercise.selected?.let { exercise.options[it] }
    val gapDescription = stringResource(R.string.feature_lesson_blank)
    val sentence = buildAnnotatedString {
        append(exercise.before)
        withStyle(
            SpanStyle(
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                textDecoration = TextDecoration.Underline,
            ),
        ) { append(chosen ?: GAP) }
        append(exercise.after)
    }
    val spoken = exercise.before + (chosen ?: gapDescription) + exercise.after
    Column(modifier, verticalArrangement = Arrangement.spacedBy(HitzSpacing.xl)) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.large,
            color = MaterialTheme.colorScheme.surfaceContainer,
        ) {
            Column(Modifier.padding(HitzSpacing.lg), verticalArrangement = Arrangement.spacedBy(HitzSpacing.sm)) {
                Text(
                    text = sentence,
                    style = MaterialTheme.typography.headlineSmall.copy(localeList = Language.Basque.localeList),
                    modifier = Modifier.semantics { contentDescription = spoken },
                )
                Text(
                    text = exercise.translation,
                    style = MaterialTheme.typography.bodyLarge.copy(localeList = Language.Spanish.localeList),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        HitzWordBank(Modifier.fillMaxWidth()) {
            exercise.options.forEachIndexed { index, option ->
                HitzWordChip(
                    word = option,
                    onClick = { if (enabled) onAction(LessonAction.SelectOption(index)) },
                    used = index == exercise.selected,
                )
            }
        }
    }
}

@HitzPreviews
@Composable
private fun FillBlankExercisePreview() {
    HitzTheme {
        Surface {
            FillBlankExercise(
                exercise = ExerciseUi.FillBlank(
                    id = "preview",
                    before = "Ni Ane ",
                    after = ".",
                    translation = "Yo soy Ane.",
                    options = listOf("zara", "naiz"),
                    selected = 1,
                ),
                onAction = {},
                modifier = Modifier.padding(HitzSpacing.lg),
            )
        }
    }
}
