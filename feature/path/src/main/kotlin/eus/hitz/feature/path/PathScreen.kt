@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package eus.hitz.feature.path

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import eus.hitz.core.designsystem.component.HitzButton
import eus.hitz.core.designsystem.component.HitzButtonSize
import eus.hitz.core.designsystem.component.HitzButtonStyle
import eus.hitz.core.designsystem.component.HitzStatBar
import eus.hitz.core.designsystem.component.StatKind
import eus.hitz.core.designsystem.icon.HitzIcons
import eus.hitz.core.designsystem.theme.HitzSpacing
import eus.hitz.core.domain.path.LearningPath
import eus.hitz.core.domain.path.NodeType
import eus.hitz.core.domain.path.PathNode
import eus.hitz.core.domain.path.PathSection
import eus.hitz.core.domain.path.PathUnit

/** Horizontal offsets that make the path wind gently down the screen. */
private val Winding = listOf(0.dp, 44.dp, 64.dp, 44.dp, 0.dp, (-44).dp, (-64).dp, (-44).dp)

@Composable
fun PathScreen(
    onStartLevel: (unitId: String, levelId: String, lessonId: String?) -> Unit,
    onOpenGuidebook: (unitId: String) -> Unit,
    onStartStory: (unitId: String, levelId: String) -> Unit,
    onOpenShop: () -> Unit,
    onOpenProfile: () -> Unit,
    viewModel: PathViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    PathContent(
        state = state,
        onStartLevel = onStartLevel,
        onOpenGuidebook = onOpenGuidebook,
        onStartStory = onStartStory,
        onOpenShop = onOpenShop,
        onOpenProfile = onOpenProfile,
    )
}

@Composable
internal fun PathContent(
    state: PathUiState,
    onStartLevel: (unitId: String, levelId: String, lessonId: String?) -> Unit,
    onOpenGuidebook: (unitId: String) -> Unit,
    modifier: Modifier = Modifier,
    onStartStory: (unitId: String, levelId: String) -> Unit = { _, _ -> },
    onOpenShop: () -> Unit = {},
    onOpenProfile: () -> Unit = {},
    listState: LazyListState = rememberLazyListState(),
) {
    var showNoHearts by rememberSaveable { mutableStateOf(false) }
    when (state) {
        PathUiState.Loading -> Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) { LoadingIndicator() }
        PathUiState.Error -> Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(stringResource(R.string.feature_path_error), Modifier.padding(HitzSpacing.xl))
        }
        is PathUiState.Ready -> Column(modifier.fillMaxSize()) {
            StatsRow(state.stats, onOpenShop = onOpenShop, onOpenProfile = onOpenProfile)
            LearningPathList(
                path = state.path,
                onNodeClick = { node ->
                    when {
                        node.type == NodeType.Story -> onStartStory(node.unitId, node.levelId)
                        // Lessons need hearts; reviews are free practice and legendary levels bring their own.
                        node.type == NodeType.Lesson && state.stats.hearts == 0 -> showNoHearts = true
                        else -> onStartLevel(node.unitId, node.levelId, node.nextLessonId)
                    }
                },
                onOpenGuidebook = onOpenGuidebook,
                listState = listState,
                modifier = Modifier.weight(1f),
            )
        }
    }
    if (showNoHearts) {
        AlertDialog(
            onDismissRequest = { showNoHearts = false },
            title = { Text(stringResource(R.string.feature_path_no_hearts_title)) },
            text = { Text(stringResource(R.string.feature_path_no_hearts_body)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showNoHearts = false
                        onOpenShop()
                    },
                ) { Text(stringResource(R.string.feature_path_no_hearts_shop)) }
            },
            dismissButton = {
                TextButton(onClick = { showNoHearts = false }) {
                    Text(stringResource(R.string.feature_path_no_hearts_dismiss))
                }
            },
        )
    }
}

/** Streak, gems and hearts; tapping them opens the profile or the shop. */
@Composable
private fun StatsRow(stats: PathStats, onOpenShop: () -> Unit, onOpenProfile: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = HitzSpacing.lg, vertical = HitzSpacing.sm),
        horizontalArrangement = Arrangement.End,
    ) {
        HitzStatBar(
            stats = listOf(
                StatKind.Streak to stats.streak,
                StatKind.Gems to stats.gems,
                StatKind.Hearts to stats.hearts
            ),
            onStatClick = { kind -> if (kind == StatKind.Streak) onOpenProfile() else onOpenShop() },
        )
    }
}

private sealed interface PathRow {
    data class SectionHeader(val section: PathSection, val number: Int) : PathRow

    data class UnitHeader(val unit: PathUnit) : PathRow

    data class Node(val node: PathNode, val index: Int) : PathRow
}

private fun LearningPath.rows(): List<PathRow> = buildList {
    sections.forEachIndexed { sectionIndex, section ->
        add(PathRow.SectionHeader(section, sectionIndex + 1))
        section.units.forEach { unit ->
            add(PathRow.UnitHeader(unit))
            unit.nodes.forEachIndexed { index, node -> add(PathRow.Node(node, index)) }
        }
    }
}

@Composable
private fun LearningPathList(
    path: LearningPath,
    onNodeClick: (PathNode) -> Unit,
    onOpenGuidebook: (unitId: String) -> Unit,
    listState: LazyListState,
    modifier: Modifier = Modifier,
) {
    val rows = path.rows()
    val currentIndex = rows.indexOfFirst { it is PathRow.Node && it.node.isCurrent }
    LaunchedEffect(currentIndex) {
        if (currentIndex > 0) listState.scrollToItem((currentIndex - 1).coerceAtLeast(0))
    }
    LazyColumn(
        state = listState,
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = HitzSpacing.lg),
        verticalArrangement = Arrangement.spacedBy(HitzSpacing.lg),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        itemsIndexed(rows, key = { _, row -> row.key }) { _, row ->
            when (row) {
                is PathRow.SectionHeader -> SectionHeader(row.section, row.number)
                is PathRow.UnitHeader -> UnitHeader(row.unit, onOpenGuidebook = { onOpenGuidebook(row.unit.id) })
                is PathRow.Node -> PathNodeButton(
                    node = row.node,
                    onClick = { onNodeClick(row.node) },
                    modifier = Modifier.offset(x = Winding[row.index % Winding.size]),
                )
            }
        }
    }
}

private val PathRow.key: String
    get() = when (this) {
        is PathRow.SectionHeader -> "section-${section.id}"
        is PathRow.UnitHeader -> "unit-${unit.id}"
        is PathRow.Node -> "node-${node.levelId}"
    }

@Composable
private fun SectionHeader(section: PathSection, number: Int) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = HitzSpacing.lg),
        verticalArrangement = Arrangement.spacedBy(HitzSpacing.xs),
    ) {
        Text(
            text = stringResource(R.string.feature_path_section, number),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
        )
        Text(
            text = section.title.resolve(),
            style = MaterialTheme.typography.headlineSmallEmphasized,
            modifier = Modifier.semantics { heading() },
        )
        Text(section.description.resolve(), style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun UnitHeader(unit: PathUnit, onOpenGuidebook: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = HitzSpacing.lg),
        shape = MaterialTheme.shapes.extraLarge,
        color = with(MaterialTheme.colorScheme) {
            if (unit.isUnlocked) primaryContainer else surfaceContainerHigh
        },
        contentColor = with(MaterialTheme.colorScheme) {
            if (unit.isUnlocked) onPrimaryContainer else onSurfaceVariant
        },
    ) {
        Row(
            modifier = Modifier.padding(HitzSpacing.lg),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(HitzSpacing.md),
        ) {
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(HitzSpacing.xxs)) {
                Text(
                    text = stringResource(R.string.feature_path_unit, unit.id.unitNumber()),
                    style = MaterialTheme.typography.labelLarge,
                )
                Text(
                    text = unit.title.resolve(),
                    style = MaterialTheme.typography.titleLargeEmphasized,
                    modifier = Modifier.semantics { heading() },
                )
            }
            HitzButton(
                text = stringResource(R.string.feature_path_guidebook),
                onClick = onOpenGuidebook,
                style = HitzButtonStyle.Tonal,
                size = HitzButtonSize.Small,
                icon = HitzIcons.Book,
            )
        }
    }
}

private val UnitIdPattern = Regex("^u(\\d+)-(\\d+)$")

/** "u03-2" → "3.2". */
internal fun String.unitNumber(): String {
    val match = UnitIdPattern.matchEntire(this) ?: return this
    val (book, half) = match.destructured
    return "${book.toInt()}.$half"
}
