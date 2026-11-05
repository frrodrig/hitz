@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package eus.hitz.core.designsystem.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.unit.sp
import eus.hitz.core.designsystem.R
import eus.hitz.core.designsystem.icon.HitzIcons
import eus.hitz.core.designsystem.preview.HitzPreviews
import eus.hitz.core.designsystem.theme.ColorFamily
import eus.hitz.core.designsystem.theme.HitzSpacing
import eus.hitz.core.designsystem.theme.HitzTheme

/** The counters shown in the top bar. */
enum class StatKind { Streak, Gems, Hearts, Xp }

@Composable
private fun StatKind.colors(): ColorFamily = with(HitzTheme.extendedColors) {
    when (this@colors) {
        StatKind.Streak -> streak
        StatKind.Gems -> gem
        StatKind.Hearts -> heart
        StatKind.Xp -> xp
    }
}

private val StatKind.icon: Int
    get() = when (this) {
        StatKind.Streak -> HitzIcons.Streak
        StatKind.Gems -> HitzIcons.Gem
        StatKind.Hearts -> HitzIcons.Heart
        StatKind.Xp -> HitzIcons.Xp
    }

private val StatKind.plural: Int
    get() = when (this) {
        StatKind.Streak -> R.plurals.core_designsystem_stat_streak
        StatKind.Gems -> R.plurals.core_designsystem_stat_gems
        StatKind.Hearts -> R.plurals.core_designsystem_stat_hearts
        StatKind.Xp -> R.plurals.core_designsystem_stat_xp
    }

private val ICON_SIZE = 18.sp

/** Streak, gems, hearts or XP as a Material 3 [AssistChip] with a full spoken description. */
@Composable
fun HitzStatCounter(
    kind: StatKind,
    value: Int,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
    val description = pluralStringResource(kind.plural, value, value)
    AssistChip(
        onClick = onClick,
        label = { Text(value.toString(), style = MaterialTheme.typography.labelLargeEmphasized) },
        leadingIcon = {
            // Sized in sp so the icon grows with the font scale, like its number.
            Icon(
                painter = painterResource(kind.icon),
                contentDescription = null,
                tint = kind.colors().color,
                modifier = Modifier.size(with(LocalDensity.current) { ICON_SIZE.toDp() }),
            )
        },
        modifier = modifier.clearAndSetSemantics {
            contentDescription = description
            this.onClick(label = null) {
                onClick()
                true
            }
        },
        colors = AssistChipDefaults.assistChipColors(containerColor = Color.Transparent),
        border = null,
    )
}

/**
 * Groups the counters in a single tonal container (Material 3 Expressive containment), as shown in
 * the top bar of the path and lesson screens.
 */
@Composable
fun HitzStatBar(
    stats: List<Pair<StatKind, Int>>,
    modifier: Modifier = Modifier,
    onStatClick: (StatKind) -> Unit = {},
) {
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.extraLarge,
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = HitzSpacing.xs),
            horizontalArrangement = Arrangement.spacedBy(HitzSpacing.xxs),
        ) {
            stats.forEach { (kind, value) ->
                HitzStatCounter(kind, value, onClick = { onStatClick(kind) })
            }
        }
    }
}

@HitzPreviews
@Composable
private fun HitzStatCounterPreview() {
    HitzTheme {
        Surface {
            HitzStatBar(
                stats = listOf(StatKind.Streak to 12, StatKind.Gems to 340, StatKind.Hearts to 5, StatKind.Xp to 1250),
                modifier = Modifier.padding(HitzSpacing.sm),
            )
        }
    }
}
