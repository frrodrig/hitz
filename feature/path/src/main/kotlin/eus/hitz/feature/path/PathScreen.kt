@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package eus.hitz.feature.path

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LinearWavyProgressIndicator
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import eus.hitz.core.designsystem.component.HitzDailyGoalChip
import eus.hitz.core.designsystem.component.HitzMascot
import eus.hitz.core.designsystem.component.MascotPose
import eus.hitz.core.designsystem.icon.HitzIcons
import eus.hitz.core.designsystem.theme.HitzSpacing
import eus.hitz.core.domain.path.LearningPath
import eus.hitz.core.domain.path.NodeState
import eus.hitz.core.domain.path.NodeType
import eus.hitz.core.domain.path.PathNode
import eus.hitz.core.domain.path.PathSection
import eus.hitz.core.domain.path.PathUnit
import eus.hitz.core.domain.score.ScoreRules

/** Horizontal offsets that make the path wind gently down the screen. */
private val Winding = listOf(0.dp, 44.dp, 64.dp, 44.dp, 0.dp, (-44).dp, (-64).dp, (-44).dp)
private val MASCOT_SIZE = 96.dp
private val GUIDE_BUTTON = 56.dp
private val CHIP_ICON = 18.dp
private const val UNIT_COLORS = 3
private const val SECONDARY_ALPHA = 0.8f
private const val TRACK_ALPHA = 0.3f

@Composable
fun PathScreen(
    onStartLevel: (unitId: String, levelId: String, lessonId: String?) -> Unit,
    onOpenUnitIntro: (unitId: String, levelId: String, lessonId: String?) -> Unit,
    onOpenGuidebook: (unitId: String) -> Unit,
    onStartStory: (unitId: String, levelId: String) -> Unit,
    onOpenProfile: () -> Unit,
    viewModel: PathViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    PathContent(
        state = state,
        onStartLevel = onStartLevel,
        onOpenUnitIntro = onOpenUnitIntro,
        onOpenGuidebook = onOpenGuidebook,
        onStartStory = onStartStory,
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
    onOpenProfile: () -> Unit = {},
    onOpenUnitIntro: (unitId: String, levelId: String, lessonId: String?) -> Unit = onStartLevel,
    listState: LazyListState = rememberLazyListState(),
) {
    when (state) {
        PathUiState.Loading -> Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) { LoadingIndicator() }
        PathUiState.Error -> Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(stringResource(R.string.feature_path_error), Modifier.padding(HitzSpacing.xl))
        }
        is PathUiState.Ready -> {
            val rows = remember(state.path) { state.path.rows() }
            // The bar names the section being read: the one of the first visible row.
            val section by remember(rows) {
                derivedStateOf { rows.getOrNull(listState.firstVisibleItemIndex)?.sectionNumber ?: 1 }
            }
            Column(modifier.fillMaxSize()) {
                PathTopBar(section, state.score, state.dailyGoal, onOpenProfile = onOpenProfile)
                LearningPathList(
                    rows = rows,
                    onNodeClick = { node ->
                        when {
                            node.type == NodeType.Story -> onStartStory(node.unitId, node.levelId)
                            // A unit is introduced before its first lesson.
                            node.type == NodeType.Lesson && node.unitId !in state.seenUnitIntros ->
                                onOpenUnitIntro(node.unitId, node.levelId, node.nextLessonId)
                            else -> onStartLevel(node.unitId, node.levelId, node.nextLessonId)
                        }
                    },
                    onOpenGuidebook = onOpenGuidebook,
                    listState = listState,
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

/** The section being read, the score and today's goal; both chips open the profile. */
@Composable
private fun PathTopBar(section: Int, score: Int, dailyGoal: Float, onOpenProfile: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = HitzSpacing.lg, end = HitzSpacing.lg, top = HitzSpacing.sm, bottom = HitzSpacing.xs),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(HitzSpacing.sm),
    ) {
        Column(Modifier.weight(1f)) {
            Text(
                text = stringResource(R.string.feature_path_section, section),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = stringResource(R.string.feature_path_learn),
                style = MaterialTheme.typography.titleLargeEmphasized,
                modifier = Modifier.semantics { heading() },
            )
        }
        ScoreChip(score, onClick = onOpenProfile)
        HitzDailyGoalChip(dailyGoal, onClick = onOpenProfile)
    }
}

@Composable
private fun ScoreChip(score: Int, onClick: () -> Unit) {
    val description = stringResource(R.string.feature_path_score, score, ScoreRules.MAX)
    Surface(
        onClick = onClick,
        shape = MaterialTheme.shapes.extraLarge,
        color = MaterialTheme.colorScheme.tertiaryContainer,
        contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
        modifier = Modifier.clearAndSetSemantics {
            contentDescription = description
            role = Role.Button
            this.onClick(label = null) {
                onClick()
                true
            }
        },
    ) {
        Row(
            modifier = Modifier.padding(horizontal = HitzSpacing.md, vertical = HitzSpacing.xs),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(HitzSpacing.xs),
        ) {
            Icon(painterResource(HitzIcons.Star), contentDescription = null, modifier = Modifier.size(CHIP_ICON))
            Text(score.toString(), style = MaterialTheme.typography.labelLargeEmphasized)
        }
    }
}

private sealed interface PathRow {
    val sectionNumber: Int

    data class SectionHeader(val section: PathSection, override val sectionNumber: Int) : PathRow

    /** [index]: the unit's position in the whole course, which picks its colour. */
    data class UnitHeader(val unit: PathUnit, val index: Int, override val sectionNumber: Int) : PathRow

    data class Node(val node: PathNode, val index: Int, override val sectionNumber: Int) : PathRow
}

private fun LearningPath.rows(): List<PathRow> = buildList {
    var unitIndex = 0
    sections.forEachIndexed { sectionIndex, section ->
        val number = sectionIndex + 1
        add(PathRow.SectionHeader(section, number))
        section.units.forEach { unit ->
            add(PathRow.UnitHeader(unit, unitIndex++, number))
            unit.nodes.forEachIndexed { index, node -> add(PathRow.Node(node, index, number)) }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun LearningPathList(
    rows: List<PathRow>,
    onNodeClick: (PathNode) -> Unit,
    onOpenGuidebook: (unitId: String) -> Unit,
    listState: LazyListState,
    modifier: Modifier = Modifier,
) {
    val currentIndex = rows.indexOfFirst { it is PathRow.Node && it.node.isCurrent }
    LaunchedEffect(currentIndex) {
        if (currentIndex > 0) listState.scrollToItem((currentIndex - 2).coerceAtLeast(0))
    }
    LazyColumn(
        state = listState,
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = HitzSpacing.xl),
        verticalArrangement = Arrangement.spacedBy(HitzSpacing.lg),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        rows.forEach { row ->
            when (row) {
                is PathRow.SectionHeader -> item(key = row.key) { SectionHeader(row.section, row.sectionNumber) }
                // The unit stays on top while its levels scroll under it.
                is PathRow.UnitHeader -> stickyHeader(key = row.key) {
                    UnitHeader(row.unit, row.index, onOpenGuidebook = { onOpenGuidebook(row.unit.id) })
                }
                is PathRow.Node -> item(key = row.key) {
                    NodeRow(row.node, Winding[row.index % Winding.size]) { onNodeClick(row.node) }
                }
            }
        }
    }
}

/** A node on its winding offset; the Galtzagorri waits beside the current one. */
@Composable
private fun NodeRow(node: PathNode, offset: Dp, onClick: () -> Unit) {
    Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        PathNodeButton(node = node, onClick = onClick, modifier = Modifier.offset(x = offset))
        if (node.isCurrent) {
            HitzMascot(
                pose = MascotPose.Hola,
                size = MASCOT_SIZE,
                contentDescription = null,
                modifier = Modifier
                    .align(if (offset > 0.dp) Alignment.CenterStart else Alignment.CenterEnd)
                    .padding(horizontal = HitzSpacing.xl),
            )
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
            .padding(start = HitzSpacing.lg, end = HitzSpacing.lg, top = HitzSpacing.lg),
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

/** Units take the system's three colours in turn; locked ones stay neutral. */
@Composable
private fun unitColors(unit: PathUnit, index: Int): Triple<Color, Color, Color> = with(MaterialTheme.colorScheme) {
    when {
        !unit.isUnlocked -> Triple(surfaceContainerHigh, onSurfaceVariant, surfaceContainerHighest)
        index % UNIT_COLORS == 0 -> Triple(primary, onPrimary, primaryContainer)
        index % UNIT_COLORS == 1 -> Triple(secondary, onSecondary, secondaryContainer)
        else -> Triple(tertiary, onTertiary, tertiaryContainer)
    }
}

@Composable
private fun UnitHeader(unit: PathUnit, index: Int, onOpenGuidebook: () -> Unit) {
    val (container, content, button) = unitColors(unit, index)
    val done = unit.nodes.count { it.state == NodeState.Completed }
    val number = unit.id.unitNumber()
    // An opaque strip behind the card, so levels scrolling under the sticky header don't show.
    Box(
        Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = HitzSpacing.lg, vertical = HitzSpacing.xs),
    ) {
        Surface(shape = MaterialTheme.shapes.extraLarge, color = container, contentColor = content) {
            Row(
                modifier = Modifier.padding(
                    start = HitzSpacing.xl,
                    end = HitzSpacing.lg,
                    top = HitzSpacing.lg,
                    bottom = HitzSpacing.lg
                ),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(HitzSpacing.md),
            ) {
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(HitzSpacing.xs)) {
                    Text(
                        text = pluralStringResource(
                            R.plurals.feature_path_unit_levels,
                            unit.nodes.size,
                            number,
                            done,
                            unit.nodes.size
                        ),
                        style = MaterialTheme.typography.labelLarge,
                        color = content.copy(alpha = SECONDARY_ALPHA),
                    )
                    Text(
                        text = unit.title.resolve(),
                        style = MaterialTheme.typography.headlineSmallEmphasized,
                        modifier = Modifier.semantics { heading() },
                    )
                    if (unit.isUnlocked) {
                        LinearWavyProgressIndicator(
                            progress = { if (unit.nodes.isEmpty()) 0f else done.toFloat() / unit.nodes.size },
                            color = content,
                            trackColor = content.copy(alpha = TRACK_ALPHA),
                            modifier = Modifier.fillMaxWidth().padding(top = HitzSpacing.xs),
                        )
                    }
                }
                GuideButton(number, button, onOpenGuidebook)
            }
        }
    }
}

/** The unit's guide: a large tonal icon button in the unit's container colour. */
@Composable
private fun GuideButton(number: String, container: Color, onClick: () -> Unit) {
    FilledTonalIconButton(
        onClick = onClick,
        shape = MaterialTheme.shapes.large,
        colors = IconButtonDefaults.filledTonalIconButtonColors(
            containerColor = container,
            contentColor = contentColorFor(container),
        ),
        modifier = Modifier.size(GUIDE_BUTTON),
    ) {
        Icon(
            painterResource(HitzIcons.Book),
            contentDescription = stringResource(R.string.feature_path_guidebook_title, number),
        )
    }
}

private val UnitIdPattern = Regex("^u(\\d+)-(\\d+)$")

/** "u03-2" → "3.2". */
internal fun String.unitNumber(): String {
    val match = UnitIdPattern.matchEntire(this) ?: return this
    val (book, half) = match.destructured
    return "${book.toInt()}.$half"
}
