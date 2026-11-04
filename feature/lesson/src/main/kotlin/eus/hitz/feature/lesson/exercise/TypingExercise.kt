package eus.hitz.feature.lesson.exercise

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import eus.hitz.core.designsystem.preview.HitzPreviews
import eus.hitz.core.designsystem.theme.HitzSpacing
import eus.hitz.core.designsystem.theme.HitzTheme
import eus.hitz.core.model.content.Language
import eus.hitz.feature.lesson.ExerciseUi
import eus.hitz.feature.lesson.LessonAction
import eus.hitz.feature.lesson.R
import eus.hitz.feature.lesson.ui.PromptCard
import eus.hitz.feature.lesson.ui.localeList

/** Translate by typing. Autocorrect is off so the keyboard does not "fix" Basque words. */
@Composable
internal fun TypingExercise(
    exercise: ExerciseUi.Typing,
    onAction: (LessonAction) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(HitzSpacing.xl)) {
        PromptCard(exercise.prompt, exercise.promptLanguage)
        OutlinedTextField(
            value = exercise.input,
            onValueChange = { onAction(LessonAction.ChangeInput(it)) },
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            label = { Text(stringResource(R.string.feature_lesson_input_hint)) },
            minLines = 3,
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Sentences,
                autoCorrectEnabled = false,
                imeAction = ImeAction.Done,
                hintLocales = exercise.answerLanguage.localeList,
            ),
            keyboardActions = KeyboardActions(onDone = { onAction(LessonAction.Check) }),
        )
    }
}

@HitzPreviews
@Composable
private fun TypingExercisePreview() {
    HitzTheme {
        Surface {
            TypingExercise(
                exercise = ExerciseUi.Typing("preview", "Muchas gracias.", Language.Spanish, input = "Eskerrik"),
                onAction = {},
                modifier = Modifier.padding(HitzSpacing.lg),
            )
        }
    }
}
