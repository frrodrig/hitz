package eus.hitz.core.designsystem.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import eus.hitz.core.designsystem.R
import eus.hitz.core.designsystem.icon.HitzIcons
import eus.hitz.core.designsystem.preview.HitzPreviews
import eus.hitz.core.designsystem.theme.HitzSpacing
import eus.hitz.core.designsystem.theme.HitzTheme

/** Visual state of an answer option. */
enum class ChoiceState { Default, Selected, Correct, Incorrect, Disabled }

@Composable
private fun containerColor(state: ChoiceState): Pair<Color, Color> {
    val scheme = MaterialTheme.colorScheme
    val success = HitzTheme.extendedColors.success
    return when (state) {
        ChoiceState.Default -> scheme.surfaceContainerHigh to scheme.onSurface
        ChoiceState.Selected -> scheme.secondaryContainer to scheme.onSecondaryContainer
        ChoiceState.Correct -> success.container to success.onContainer
        ChoiceState.Incorrect -> scheme.errorContainer to scheme.onErrorContainer
        ChoiceState.Disabled -> scheme.surfaceContainerLow to scheme.onSurfaceVariant
    }
}

/**
 * Single-choice answer option: a Material 3 [ListItem] with a [RadioButton] on a tonal surface.
 * Used by "choose the meaning", "choose the image", chat and true/false exercises. The state is
 * announced by TalkBack, and correct/incorrect also show an icon, not only colour.
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
    val (container, content) = containerColor(state)
    val description = when (state) {
        ChoiceState.Selected -> stringResource(R.string.core_designsystem_state_selected)
        ChoiceState.Correct -> stringResource(R.string.core_designsystem_state_correct)
        ChoiceState.Incorrect -> stringResource(R.string.core_designsystem_state_incorrect)
        else -> null
    }
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        color = container,
        contentColor = content,
    ) {
        // Selection semantics live on the ListItem, which is the merging node in material3 1.5.
        ListItem(
            modifier = Modifier
                .selectable(
                    selected = state == ChoiceState.Selected,
                    enabled = state != ChoiceState.Disabled,
                    role = Role.RadioButton,
                    onClick = onClick,
                )
                .semantics { if (description != null) stateDescription = description },
            headlineContent = { Text(text, style = MaterialTheme.typography.bodyLarge) },
            supportingContent = supportingText?.let { { Text(it) } },
            leadingContent = leading ?: {
                RadioButton(
                    selected = state == ChoiceState.Selected,
                    onClick = null,
                    enabled = state != ChoiceState.Disabled,
                )
            },
            trailingContent = when (state) {
                ChoiceState.Correct -> { { Icon(painterResource(HitzIcons.Check), contentDescription = null) } }
                ChoiceState.Incorrect -> { { Icon(painterResource(HitzIcons.Close), contentDescription = null) } }
                else -> null
            },
            colors = ListItemDefaults.colors(
                containerColor = Color.Transparent,
                headlineColor = content,
                supportingColor = content,
                trailingIconColor = content,
            ),
        )
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
