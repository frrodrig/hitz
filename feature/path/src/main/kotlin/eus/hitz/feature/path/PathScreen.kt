package eus.hitz.feature.path

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import eus.hitz.core.designsystem.component.HitzButton
import eus.hitz.core.designsystem.component.PlaceholderScreen
import eus.hitz.core.designsystem.preview.HitzPreviews
import eus.hitz.core.designsystem.theme.HitzSpacing
import eus.hitz.core.designsystem.theme.HitzTheme

// Until the learning path arrives (M4), the path screen opens the sample lesson directly.
private const val SAMPLE_UNIT = "u01-1"
private const val SAMPLE_LESSON = "u01-1.l1.s1"

@Composable
fun PathScreen(
    onStartLesson: (unitId: String, lessonId: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        PlaceholderScreen(
            title = stringResource(R.string.feature_path_title),
            description = stringResource(R.string.feature_path_placeholder),
            modifier = Modifier.weight(1f),
        )
        HitzButton(
            text = stringResource(R.string.feature_path_try_lesson),
            onClick = { onStartLesson(SAMPLE_UNIT, SAMPLE_LESSON) },
            modifier = Modifier.padding(HitzSpacing.xl),
        )
    }
}

@HitzPreviews
@Composable
private fun PathScreenPreview() {
    HitzTheme { PathScreen(onStartLesson = { _, _ -> }) }
}
