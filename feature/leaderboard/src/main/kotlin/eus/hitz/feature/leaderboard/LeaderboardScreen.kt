package eus.hitz.feature.leaderboard

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import eus.hitz.core.designsystem.component.PlaceholderScreen
import eus.hitz.core.designsystem.theme.HitzTheme

@Composable
fun LeaderboardScreen(modifier: Modifier = Modifier) {
    PlaceholderScreen(
        title = stringResource(R.string.feature_leaderboard_title),
        description = stringResource(R.string.feature_leaderboard_placeholder),
        modifier = modifier,
    )
}

@PreviewLightDark
@Composable
private fun LeaderboardScreenPreview() {
    HitzTheme { LeaderboardScreen() }
}
