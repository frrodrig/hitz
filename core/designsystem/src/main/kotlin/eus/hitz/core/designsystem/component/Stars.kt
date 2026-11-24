package eus.hitz.core.designsystem.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import eus.hitz.core.designsystem.R
import eus.hitz.core.designsystem.icon.HitzIcons
import eus.hitz.core.designsystem.motion.rememberReducedMotion
import eus.hitz.core.designsystem.preview.HitzPreviews
import eus.hitz.core.designsystem.theme.HitzSpacing
import eus.hitz.core.designsystem.theme.HitzTheme

/** Stars a star challenge can give. */
const val MAX_STARS = 3

/** How small a star shrinks while it goes out. */
private const val OUT_SCALE = 0.85f

/**
 * The stars of a star challenge: [earned] of [MAX_STARS] in the tertiary colour, the rest dimmed.
 * A star that goes out or lights up changes smoothly (instantly with reduced motion). TalkBack
 * reads "2 of 3 stars".
 */
@Composable
fun HitzStars(earned: Int, modifier: Modifier = Modifier, size: Dp = 20.dp) {
    val description = stringResource(R.string.core_designsystem_stars, earned, MAX_STARS)
    Row(
        modifier = modifier.clearAndSetSemantics { contentDescription = description },
        horizontalArrangement = Arrangement.spacedBy(size / 5),
    ) {
        repeat(MAX_STARS) { index -> Star(lit = index < earned, size = size) }
    }
}

@Composable
private fun Star(lit: Boolean, size: Dp) {
    val still = rememberReducedMotion()
    val target = if (lit) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.outlineVariant
    val tint by animateColorAsState(target, label = "star")
    val bounce = spring<Float>(if (still) Spring.DampingRatioNoBouncy else Spring.DampingRatioMediumBouncy)
    val scale by animateFloatAsState(if (lit) 1f else OUT_SCALE, bounce, label = "star scale")
    Icon(
        painter = painterResource(HitzIcons.Star),
        contentDescription = null,
        tint = if (still) target else tint,
        modifier = Modifier
            .size(size)
            .scale(scale),
    )
}

@HitzPreviews
@Composable
private fun HitzStarsPreview() {
    HitzTheme {
        Column(verticalArrangement = Arrangement.spacedBy(HitzSpacing.sm)) {
            for (earned in 0..MAX_STARS) HitzStars(earned = earned, size = 28.dp)
        }
    }
}
