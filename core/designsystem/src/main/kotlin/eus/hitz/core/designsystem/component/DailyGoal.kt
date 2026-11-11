@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package eus.hitz.core.designsystem.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import eus.hitz.core.designsystem.R
import eus.hitz.core.designsystem.icon.HitzIcons
import eus.hitz.core.designsystem.preview.HitzPreviews
import eus.hitz.core.designsystem.theme.HitzSpacing
import eus.hitz.core.designsystem.theme.HitzTheme

private const val PERCENT = 100

/**
 * Today's progress towards the daily goal as a ring: wavy while in progress, a check mark once
 * the goal is met. [progress] can go over 1; the ring stays full.
 */
@Composable
fun HitzDailyGoalRing(
    progress: Float,
    modifier: Modifier = Modifier,
    size: Dp = 40.dp,
) {
    val done = progress >= 1f
    Box(modifier.size(size), contentAlignment = Alignment.Center) {
        CircularWavyProgressIndicator(
            progress = { progress.coerceIn(0f, 1f) },
            modifier = Modifier.size(size),
            color = if (done) HitzTheme.extendedColors.success.color else MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.surfaceContainerHighest,
            amplitude = { if (done) 0f else 1f },
        )
        if (done) {
            Icon(
                painter = painterResource(HitzIcons.Check),
                contentDescription = null,
                tint = HitzTheme.extendedColors.success.color,
                modifier = Modifier.size(size * 0.5f),
            )
        }
    }
}

private val RING_SIZE = 22.sp

/**
 * The daily goal in the top bar: a small ring and today's percentage in a tonal container, with
 * a full spoken description. Tapping it opens the details (the profile).
 */
@Composable
fun HitzDailyGoalChip(
    progress: Float,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
    val percent = (progress * PERCENT).toInt()
    val description = stringResource(R.string.core_designsystem_daily_goal, percent)
    Surface(
        onClick = onClick,
        modifier = modifier.clearAndSetSemantics {
            contentDescription = description
            role = Role.Button
            this.onClick(label = null) {
                onClick()
                true
            }
        },
        shape = MaterialTheme.shapes.extraLarge,
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = HitzSpacing.md, vertical = HitzSpacing.xs),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(HitzSpacing.sm),
        ) {
            // Sized in sp so the ring grows with the font scale, like its number.
            HitzDailyGoalRing(progress, size = with(LocalDensity.current) { RING_SIZE.toDp() })
            Text(
                text = stringResource(R.string.core_designsystem_percent, percent),
                style = MaterialTheme.typography.labelLargeEmphasized,
            )
        }
    }
}

@HitzPreviews
@Composable
private fun HitzDailyGoalPreview() {
    HitzTheme {
        Surface {
            Row(Modifier.padding(HitzSpacing.sm), horizontalArrangement = Arrangement.spacedBy(HitzSpacing.md)) {
                HitzDailyGoalChip(0.6f)
                HitzDailyGoalChip(1.2f)
                HitzDailyGoalRing(0.35f, size = 64.dp)
            }
        }
    }
}
