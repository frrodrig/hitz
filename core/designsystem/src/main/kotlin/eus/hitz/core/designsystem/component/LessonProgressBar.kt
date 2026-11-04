@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package eus.hitz.core.designsystem.component

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LinearWavyProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import eus.hitz.core.designsystem.preview.HitzPreviews
import eus.hitz.core.designsystem.theme.HitzSpacing
import eus.hitz.core.designsystem.theme.HitzTheme

/** Lesson progress: the Material 3 Expressive wavy indicator. [progress] goes from 0 to 1. */
@Composable
fun HitzLessonProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
) {
    val animated by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = MaterialTheme.motionScheme.defaultSpatialSpec(),
        label = "lessonProgress",
    )
    LinearWavyProgressIndicator(
        progress = { animated },
        modifier = modifier.fillMaxWidth(),
    )
}

@HitzPreviews
@Composable
private fun HitzLessonProgressBarPreview() {
    HitzTheme { HitzLessonProgressBar(0.6f, Modifier.padding(HitzSpacing.lg)) }
}
