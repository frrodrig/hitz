@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package eus.hitz.core.designsystem.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import eus.hitz.core.designsystem.R
import eus.hitz.core.designsystem.icon.HitzIcons
import eus.hitz.core.designsystem.preview.HitzPreviews
import eus.hitz.core.designsystem.theme.HitzSpacing
import eus.hitz.core.designsystem.theme.HitzTheme

/** Visual state of an answer option. */
enum class ChoiceState { Default, Selected, Correct, Incorrect, Disabled }

/** Position of an item inside a grouped (segmented) list. */
enum class GroupPosition {
    Single,
    First,
    Middle,
    Last,
    ;

    companion object {
        fun of(index: Int, count: Int): GroupPosition = when {
            count <= 1 -> Single
            index == 0 -> First
            index == count - 1 -> Last
            else -> Middle
        }
    }
}

// Material 3 corner tokens: extra small, large and extra large.
private val InnerCorner = 4.dp
private val OuterCorner = 16.dp
private val ActiveCorner = 28.dp

@Composable
private fun containerColors(state: ChoiceState): Pair<Color, Color> {
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

private val ChoiceState.isActive: Boolean
    get() = this == ChoiceState.Selected || this == ChoiceState.Correct || this == ChoiceState.Incorrect

/** Top and bottom corner radii for a grouped item; active items are fully rounded. */
private fun targetCorners(position: GroupPosition, active: Boolean): Pair<Dp, Dp> {
    if (active) return ActiveCorner to ActiveCorner
    val top = if (position == GroupPosition.Single || position == GroupPosition.First) OuterCorner else InnerCorner
    val bottom = if (position == GroupPosition.Single || position == GroupPosition.Last) OuterCorner else InnerCorner
    return top to bottom
}

@Composable
private fun stateDescription(state: ChoiceState): String? = when (state) {
    ChoiceState.Selected -> stringResource(R.string.core_designsystem_state_selected)
    ChoiceState.Correct -> stringResource(R.string.core_designsystem_state_correct)
    ChoiceState.Incorrect -> stringResource(R.string.core_designsystem_state_incorrect)
    else -> null
}

@Composable
private fun animatedCorner(target: Dp, label: String): Dp {
    val corner by animateDpAsState(target, MaterialTheme.motionScheme.fastSpatialSpec(), label = label)
    return corner
}

/**
 * Single-choice answer option: a Material 3 [ListItem] with a [RadioButton] on a tonal surface.
 *
 * Options are grouped like Material 3 Expressive segmented lists (see [HitzChoiceGroup]): large
 * outer corners, small inner corners, and the active option morphs to fully rounded corners with
 * the expressive spatial spring. The state is announced by TalkBack, and correct/incorrect also
 * show an icon, not only colour.
 */
@Composable
fun HitzChoiceCard(
    text: String,
    state: ChoiceState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    position: GroupPosition = GroupPosition.Single,
    supportingText: String? = null,
    leading: (@Composable () -> Unit)? = null,
) {
    val (targetContainer, targetContent) = containerColors(state)
    val colorSpec = MaterialTheme.motionScheme.defaultEffectsSpec<Color>()
    val container by animateColorAsState(targetContainer, colorSpec, label = "choiceContainer")
    val content by animateColorAsState(targetContent, colorSpec, label = "choiceContent")

    val (top, bottom) = targetCorners(position, active = state.isActive)
    val topCorner = animatedCorner(top, "choiceTop")
    val bottomCorner = animatedCorner(bottom, "choiceBottom")
    val description = stateDescription(state)
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(topCorner, topCorner, bottomCorner, bottomCorner),
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

/** Groups answer options with the 2 dp gap of Material 3 Expressive segmented lists. */
@Composable
fun HitzChoiceGroup(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Column(
        modifier = modifier.selectableGroup(),
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) { content() }
}

@HitzPreviews
@Composable
private fun HitzChoiceCardPreview() {
    HitzTheme {
        Surface {
            val options = listOf(
                "etxea" to ChoiceState.Default,
                "kalea" to ChoiceState.Selected,
                "eskola" to ChoiceState.Correct,
                "mendia" to ChoiceState.Incorrect,
                "hondartza" to ChoiceState.Disabled,
            )
            HitzChoiceGroup(Modifier.padding(HitzSpacing.lg)) {
                options.forEachIndexed { index, (text, state) ->
                    HitzChoiceCard(text, state, {}, position = GroupPosition.of(index, options.size))
                }
            }
        }
    }
}
