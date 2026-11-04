package eus.hitz.feature.lesson

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import eus.hitz.core.designsystem.component.PlaceholderScreen
import eus.hitz.core.designsystem.theme.HitzTheme

@Composable
fun LessonScreen(modifier: Modifier = Modifier) {
    PlaceholderScreen(
        title = stringResource(R.string.feature_lesson_title),
        description = stringResource(R.string.feature_lesson_placeholder),
        modifier = modifier,
    )
}

@PreviewLightDark
@Composable
private fun LessonScreenPreview() {
    HitzTheme { LessonScreen() }
}
