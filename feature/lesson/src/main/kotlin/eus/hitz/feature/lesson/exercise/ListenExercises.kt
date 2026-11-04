package eus.hitz.feature.lesson.exercise

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import eus.hitz.core.designsystem.component.HitzButton
import eus.hitz.core.designsystem.component.HitzButtonStyle
import eus.hitz.core.designsystem.icon.HitzIcons
import eus.hitz.core.designsystem.preview.HitzPreviews
import eus.hitz.core.designsystem.theme.HitzSpacing
import eus.hitz.core.designsystem.theme.HitzTheme
import eus.hitz.core.domain.lesson.Tile
import eus.hitz.core.model.content.Language
import eus.hitz.feature.lesson.AudioClip
import eus.hitz.feature.lesson.ExerciseUi
import eus.hitz.feature.lesson.LessonAction
import eus.hitz.feature.lesson.R

/** Listen to a Basque sentence and tap its words. The clip plays on start and on demand. */
@Composable
internal fun ListenTapExercise(
    exercise: ExerciseUi.ListenTap,
    onAction: (LessonAction) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(HitzSpacing.xl)) {
        ListenButton(onAction)
        TileAnswerArea(exercise, onAction, enabled = enabled)
    }
}

/** Listen to a Basque sentence and type it. */
@Composable
internal fun ListenTypeExercise(
    exercise: ExerciseUi.ListenType,
    onAction: (LessonAction) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(HitzSpacing.xl)) {
        ListenButton(onAction)
        AnswerField(exercise.input, Language.Basque, onAction, enabled = enabled)
    }
}

/** Plays the clip of the current exercise. It stays enabled after checking, to listen again. */
@Composable
internal fun ListenButton(onAction: (LessonAction) -> Unit, modifier: Modifier = Modifier) {
    HitzButton(
        text = stringResource(R.string.feature_lesson_listen),
        onClick = { onAction(LessonAction.PlayAudio) },
        modifier = modifier.fillMaxWidth(),
        style = HitzButtonStyle.Tonal,
        icon = HitzIcons.Speaker,
    )
}

@HitzPreviews
@Composable
private fun ListenTapExercisePreview() {
    HitzTheme {
        Surface {
            ListenTapExercise(
                exercise = ExerciseUi.ListenTap(
                    id = "preview",
                    clip = AudioClip("preview", "Gero arte, Jon!"),
                    tiles = listOf("agur", "Jon", "Gero", "gabon", "arte").mapIndexed { i, t -> Tile(i, t) },
                    selected = listOf(2),
                ),
                onAction = {},
                modifier = Modifier.padding(HitzSpacing.lg),
            )
        }
    }
}

@HitzPreviews
@Composable
private fun ListenTypeExercisePreview() {
    HitzTheme {
        Surface {
            ListenTypeExercise(
                exercise = ExerciseUi.ListenType("preview", AudioClip("preview", "Eskerrik asko!"), input = "Eskerrik"),
                onAction = {},
                modifier = Modifier.padding(HitzSpacing.lg),
            )
        }
    }
}
