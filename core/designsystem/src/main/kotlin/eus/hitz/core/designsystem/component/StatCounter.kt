package eus.hitz.core.designsystem.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.onClick
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
        label = { Text(value.toString(), style = MaterialTheme.typography.labelLarge) },
        leadingIcon = {
            Icon(
                painter = painterResource(kind.icon),
                contentDescription = null,
                tint = kind.colors().color,
            )
        },
        modifier = modifier.clearAndSetSemantics {
            contentDescription = description
            this.onClick(label = null) {
                onClick()
                true
            }
        },
        colors = AssistChipDefaults.assistChipColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
        border = null,
    )
}

@HitzPreviews
@Composable
private fun HitzStatCounterPreview() {
    HitzTheme {
        Surface {
            Row(Modifier.padding(HitzSpacing.sm), horizontalArrangement = Arrangement.spacedBy(HitzSpacing.sm)) {
                HitzStatCounter(StatKind.Streak, 12)
                HitzStatCounter(StatKind.Gems, 340)
                HitzStatCounter(StatKind.Hearts, 5)
                HitzStatCounter(StatKind.Xp, 1250)
            }
        }
    }
}
