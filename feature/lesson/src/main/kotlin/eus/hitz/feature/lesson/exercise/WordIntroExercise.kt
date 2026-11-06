package eus.hitz.feature.lesson.exercise

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import eus.hitz.core.designsystem.preview.HitzPreviews
import eus.hitz.core.designsystem.theme.HitzSpacing
import eus.hitz.core.designsystem.theme.HitzTheme
import eus.hitz.core.model.content.Language
import eus.hitz.feature.lesson.AudioClip
import eus.hitz.feature.lesson.ExerciseUi
import eus.hitz.feature.lesson.LessonAction
import eus.hitz.feature.lesson.ui.LanguageText

private val MAX_IMAGE_WIDTH = 240.dp

/** A new word: picture (if any), the Basque word with its audio and the Spanish translation. */
@Composable
internal fun WordIntroExercise(
    exercise: ExerciseUi.WordIntro,
    onAction: (LessonAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(HitzSpacing.lg)) {
        ElevatedCard(Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(HitzSpacing.xl)
                    .semantics(mergeDescendants = true) {},
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(HitzSpacing.md),
            ) {
                if (exercise.imageUri != null) {
                    AsyncImage(
                        model = exercise.imageUri,
                        contentDescription = null,
                        modifier = Modifier
                            .widthIn(max = MAX_IMAGE_WIDTH)
                            .fillMaxWidth()
                            .aspectRatio(1f)
                            .clip(MaterialTheme.shapes.large),
                    )
                }
                LanguageText(
                    text = exercise.word,
                    language = Language.Basque,
                    style = MaterialTheme.typography.displaySmall.copy(textAlign = TextAlign.Center),
                )
                LanguageText(
                    text = exercise.translation,
                    language = Language.Spanish,
                    style = MaterialTheme.typography.titleLarge.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                    ),
                )
            }
        }
        ListenButton(onAction)
    }
}

@HitzPreviews
@Composable
private fun WordIntroExercisePreview() {
    HitzTheme {
        Surface {
            WordIntroExercise(
                exercise = ExerciseUi.WordIntro("preview", AudioClip("preview", "egun on"), "buenos días", null),
                onAction = {},
                modifier = Modifier.padding(HitzSpacing.lg),
            )
        }
    }
}
