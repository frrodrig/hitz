@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package eus.hitz.core.designsystem.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import eus.hitz.core.designsystem.R
import eus.hitz.core.designsystem.icon.HitzIcons
import eus.hitz.core.designsystem.theme.HitzTheme

/**
 * The Galtzagorri mascot on an expressive "sunny" shape. Pass `contentDescription = null` when it
 * is purely decorative. Animated moods (Lottie) arrive in M8.
 */
@Composable
fun HitzMascot(
    modifier: Modifier = Modifier,
    size: Dp = 120.dp,
    contentDescription: String? = stringResource(R.string.core_designsystem_mascot_description),
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(MaterialShapes.Sunny.toShape())
            .background(MaterialTheme.colorScheme.secondaryContainer),
        contentAlignment = Alignment.Center,
    ) {
        Image(
            painter = painterResource(HitzIcons.Mascot),
            contentDescription = contentDescription,
            modifier = Modifier
                .padding(size * 0.12f)
                .size(size),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun HitzMascotPreview() {
    HitzTheme { HitzMascot() }
}
