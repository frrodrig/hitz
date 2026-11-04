package eus.hitz.feature.profile

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import eus.hitz.core.designsystem.component.PlaceholderScreen
import eus.hitz.core.designsystem.theme.HitzTheme

@Composable
fun ProfileScreen(modifier: Modifier = Modifier) {
    PlaceholderScreen(
        title = stringResource(R.string.feature_profile_title),
        description = stringResource(R.string.feature_profile_placeholder),
        modifier = modifier,
    )
}

@PreviewLightDark
@Composable
private fun ProfileScreenPreview() {
    HitzTheme { ProfileScreen() }
}
