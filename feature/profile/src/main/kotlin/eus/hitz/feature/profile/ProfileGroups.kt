package eus.hitz.feature.profile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import eus.hitz.core.designsystem.theme.HitzSpacing

private val GROUP_GAP = 2.dp
private val GROUP_OUTER = 24.dp
private val GROUP_INNER = 6.dp

/** A section's title in a list of the profile or the settings. */
internal fun LazyListScope.sectionTitle(title: Int) {
    item(key = "title-$title") {
        Text(
            text = stringResource(title),
            style = MaterialTheme.typography.titleMediumEmphasized,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .padding(start = HitzSpacing.xl, end = HitzSpacing.xl, top = HitzSpacing.lg)
                .semantics { heading() },
        )
    }
}

/** Rows of a group: tonal, 2 dp apart, large outer corners and small inner ones. */
@Composable
internal fun <T> Group(items: List<T>, row: @Composable (T, Shape) -> Unit) {
    Column(
        modifier = Modifier.padding(horizontal = HitzSpacing.lg),
        verticalArrangement = Arrangement.spacedBy(GROUP_GAP),
    ) {
        items.forEachIndexed { index, item -> row(item, groupShape(index, items.size)) }
    }
}

private fun groupShape(index: Int, count: Int) = RoundedCornerShape(
    topStart = if (index == 0) GROUP_OUTER else GROUP_INNER,
    topEnd = if (index == 0) GROUP_OUTER else GROUP_INNER,
    bottomStart = if (index == count - 1) GROUP_OUTER else GROUP_INNER,
    bottomEnd = if (index == count - 1) GROUP_OUTER else GROUP_INNER,
)

/** A switch with what it does: the whole row toggles it. */
@Composable
internal fun SettingRow(
    title: String,
    description: String,
    checked: Boolean,
    onChange: (Boolean) -> Unit,
    shape: Shape,
) {
    ListItem(
        modifier = Modifier
            .clip(shape)
            .toggleable(value = checked, role = Role.Switch, onValueChange = onChange),
        headlineContent = { Text(title) },
        supportingContent = { Text(description) },
        trailingContent = { Switch(checked = checked, onCheckedChange = null) },
        colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
    )
}

/** A row that only informs (a version, a licence), or opens something when [onClick] is set. */
@Composable
internal fun InfoRow(
    title: String,
    description: String,
    shape: Shape,
    onClick: (() -> Unit)? = null,
    trailing: @Composable (() -> Unit)? = null,
) {
    ListItem(
        modifier = Modifier
            .clip(shape)
            .then(if (onClick != null) Modifier.clickableRow(onClick) else Modifier),
        headlineContent = { Text(title) },
        supportingContent = { Text(description) },
        trailingContent = trailing,
        colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
    )
}

private fun Modifier.clickableRow(onClick: () -> Unit) = clickable(role = Role.Button, onClick = onClick)
