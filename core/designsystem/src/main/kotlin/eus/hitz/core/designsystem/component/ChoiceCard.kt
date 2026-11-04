package eus.hitz.core.designsystem.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.dp
import eus.hitz.core.designsystem.R
import eus.hitz.core.designsystem.icon.HitzIcons
import eus.hitz.core.designsystem.preview.HitzPreviews
import eus.hitz.core.designsystem.theme.HitzSpacing
import eus.hitz.core.designsystem.theme.HitzTheme

/** Visual state of an answer option. */
enum class ChoiceState { Default, Selected, Correct, Incorrect, Disabled }

private data class ChoiceColors(val container: Color, val content: Color, val border: Color, val borderWidth: Int)

@Composable
private fun choiceColors(state: ChoiceState): ChoiceColors {
    val scheme = MaterialTheme.colorScheme
    val success = HitzTheme.extendedColors.success
    return when (state) {
        ChoiceState.Default -> ChoiceColors(scheme.surface, scheme.onSurface, scheme.outlineVariant, 2)
        ChoiceState.Selected -> ChoiceColors(
            scheme.secondaryContainer,
            scheme.onSecondaryContainer,
            scheme.secondary,
            3
        )
        ChoiceState.Correct -> ChoiceColors(success.container, success.onContainer, success.color, 3)
        ChoiceState.Incorrect -> ChoiceColors(scheme.errorContainer, scheme.onErrorContainer, scheme.error, 3)
        ChoiceState.Disabled -> ChoiceColors(
            scheme.surfaceContainerLow,
            scheme.onSurfaceVariant,
            scheme.outlineVariant,
            2
        )
    }
}

/**
 * Selectable answer option used by "choose the meaning", "choose the image", chat and
 * true/false exercises. The state is announced by TalkBack, and correct/incorrect are shown
 * with an icon as well as colour.
 */
@Composable
fun HitzChoiceCard(
    text: String,
    state: ChoiceState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    supportingText: String? = null,
    leading: (@Composable () -> Unit)? = null,
) {
    val colors = choiceColors(state)
    val container by animateColorAsState(colors.container, label = "choiceContainer")
    val border by animateColorAsState(colors.border, label = "choiceBorder")
    val description = when (state) {
        ChoiceState.Selected -> stringResource(R.string.core_designsystem_state_selected)
        ChoiceState.Correct -> stringResource(R.string.core_designsystem_state_correct)
        ChoiceState.Incorrect -> stringResource(R.string.core_designsystem_state_incorrect)
        else -> null
    }
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp)
            .selectable(
                selected = state == ChoiceState.Selected,
                enabled = state != ChoiceState.Disabled,
                role = Role.RadioButton,
                onClick = onClick,
            )
            .semantics { if (description != null) stateDescription = description },
        shape = MaterialTheme.shapes.medium,
        color = container,
        contentColor = colors.content,
        border = BorderStroke(colors.borderWidth.dp, border),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = HitzSpacing.lg, vertical = HitzSpacing.md),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(HitzSpacing.md),
        ) {
            leading?.invoke()
            Column(Modifier.weight(1f)) {
                Text(text, style = MaterialTheme.typography.bodyLarge)
                if (supportingText != null) {
                    Text(supportingText, style = MaterialTheme.typography.bodySmall)
                }
            }
            when (state) {
                ChoiceState.Correct -> Icon(painterResource(HitzIcons.Check), null, Modifier.size(24.dp))
                ChoiceState.Incorrect -> Icon(painterResource(HitzIcons.Close), null, Modifier.size(24.dp))
                else -> Unit
            }
        }
    }
}

@HitzPreviews
@Composable
private fun HitzChoiceCardPreview() {
    HitzTheme {
        Surface {
            Column(Modifier.padding(HitzSpacing.lg), verticalArrangement = Arrangement.spacedBy(HitzSpacing.sm)) {
                HitzChoiceCard("etxea", ChoiceState.Default, {}, supportingText = "la casa")
                HitzChoiceCard("kalea", ChoiceState.Selected, {})
                HitzChoiceCard("eskola", ChoiceState.Correct, {})
                HitzChoiceCard("mendia", ChoiceState.Incorrect, {})
                HitzChoiceCard("hondartza", ChoiceState.Disabled, {})
            }
        }
    }
}
