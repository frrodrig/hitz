package eus.hitz.core.designsystem.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import eus.hitz.core.designsystem.R
import eus.hitz.core.designsystem.icon.HitzIcons
import eus.hitz.core.designsystem.theme.HitzTheme

/**
 * The Galtzagorri mascot. Pass `contentDescription = null` when it is purely decorative.
 * Animated moods (Lottie) arrive in M8.
 */
@Composable
fun HitzMascot(
    modifier: Modifier = Modifier,
    size: Dp = 120.dp,
    contentDescription: String? = stringResource(R.string.core_designsystem_mascot_description),
) {
    Image(
        painter = painterResource(HitzIcons.Mascot),
        contentDescription = contentDescription,
        modifier = modifier.size(size),
    )
}

@Preview(showBackground = true)
@Composable
private fun HitzMascotPreview() {
    HitzTheme { HitzMascot() }
}
