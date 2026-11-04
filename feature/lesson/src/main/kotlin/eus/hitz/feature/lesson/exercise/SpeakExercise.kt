package eus.hitz.feature.lesson.exercise

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.speech.RecognizerIntent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import eus.hitz.core.designsystem.component.HitzButton
import eus.hitz.core.designsystem.component.HitzButtonStyle
import eus.hitz.core.designsystem.icon.HitzIcons
import eus.hitz.core.designsystem.preview.HitzPreviews
import eus.hitz.core.designsystem.theme.HitzSpacing
import eus.hitz.core.designsystem.theme.HitzTheme
import eus.hitz.core.model.content.Language
import eus.hitz.feature.lesson.AudioClip
import eus.hitz.feature.lesson.ExerciseUi
import eus.hitz.feature.lesson.LessonAction
import eus.hitz.feature.lesson.R
import eus.hitz.feature.lesson.ui.LanguageText
import eus.hitz.feature.lesson.ui.PromptCard

private const val MAX_RESULTS = 5

/**
 * Say a Basque sentence aloud. Speech goes through the system recognizer (no microphone
 * permission needed); when the device has none, or the learner can't talk now, the exercise can be
 * skipped without penalty.
 */
@Composable
internal fun SpeakExercise(
    exercise: ExerciseUi.Speak,
    onAction: (LessonAction) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val context = LocalContext.current
    val prompt = stringResource(R.string.feature_lesson_speak_prompt)
    val intent = remember(exercise.id) { speechIntent(prompt) }
    var available by rememberSaveable {
        mutableStateOf(intent.resolveActivity(context.packageManager) != null)
    }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val candidates = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS).orEmpty()
        if (result.resultCode == Activity.RESULT_OK) onAction(LessonAction.SpeechResult(candidates))
    }
    Column(modifier, verticalArrangement = Arrangement.spacedBy(HitzSpacing.xl)) {
        PromptCard(exercise.text, Language.Basque, onPlay = { onAction(LessonAction.PlayAudio) })
        LanguageText(
            text = exercise.translation,
            language = Language.Spanish,
            style = MaterialTheme.typography.bodyLarge,
        )
        if (available) {
            HitzButton(
                text = stringResource(R.string.feature_lesson_speak_start),
                onClick = {
                    // The recognizer can disappear between the check and the tap.
                    runCatching { launcher.launch(intent) }
                        .onFailure { error ->
                            if (error is ActivityNotFoundException) {
                                available = false
                            } else {
                                throw error
                            }
                        }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = enabled,
                icon = HitzIcons.Mic,
            )
        } else {
            Text(
                text = stringResource(R.string.feature_lesson_speak_unavailable),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        HitzButton(
            text = stringResource(R.string.feature_lesson_speak_skip),
            onClick = { onAction(LessonAction.SkipSpeaking) },
            modifier = Modifier.fillMaxWidth(),
            style = HitzButtonStyle.Text,
            enabled = enabled,
        )
    }
}

private fun speechIntent(prompt: String): Intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
    .putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
    .putExtra(RecognizerIntent.EXTRA_LANGUAGE, "eu-ES")
    .putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, MAX_RESULTS)
    .putExtra(RecognizerIntent.EXTRA_PROMPT, prompt)

@HitzPreviews
@Composable
private fun SpeakExercisePreview() {
    HitzTheme {
        Surface {
            SpeakExercise(
                exercise = ExerciseUi.Speak("preview", AudioClip("preview", "Egun on, Ane!"), "¡Buenos días, Ane!"),
                onAction = {},
                modifier = Modifier.padding(HitzSpacing.lg),
            )
        }
    }
}
