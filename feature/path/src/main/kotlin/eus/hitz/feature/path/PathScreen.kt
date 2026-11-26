@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package eus.hitz.feature.path

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import eus.hitz.core.designsystem.component.HitzDailyGoalChip
import eus.hitz.core.designsystem.icon.HitzIcons
import eus.hitz.core.designsystem.motion.rememberReducedMotion
import eus.hitz.core.designsystem.preview.HitzPreviews
import eus.hitz.core.designsystem.theme.HitzSpacing
import eus.hitz.core.designsystem.theme.HitzTheme
import eus.hitz.core.domain.path.LearningPath
import eus.hitz.core.domain.path.NodeState
import eus.hitz.core.domain.path.NodeType
import eus.hitz.core.domain.path.PathChallenge
import eus.hitz.core.domain.path.PathNode
import eus.hitz.core.domain.path.PathRecall
import eus.hitz.core.domain.path.PathSection
import eus.hitz.core.domain.path.PathUnit
import eus.hitz.core.model.content.LocalizedText
import kotlinx.coroutines.delay

/** Horizontal offsets that make the path wind gently down the screen. */
private val Winding = listOf(0.dp, 44.dp, 64.dp, 44.dp, 0.dp, (-44).dp, (-64).dp, (-44).dp)

private val CHIP_ICON = 18.dp

/** From this window width the current unit gets a pane beside the path. */
private val PaneMinWidth = 840.dp
private val PaneWidth = 360.dp

/** The path never grows wider than this: on tablets it is centred instead of stretched. */
private val PathMaxWidth = 600.dp

/** The unit bar appears once less than this much of the unit's cover is left on screen. */
private val UnitBarThreshold = 72.dp

/** How close to its unit's cover the current node must be for the path to open on the cover. */
private const val COVER_REACH = 3

/** A new step is unlocked once the lesson has sunk away. */
private const val UNLOCK_DELAY_MILLIS = 450L
private const val POP_START = 0.6f
private const val HALF = 0.5f

@Composable
fun PathScreen(
    onStartLevel: (unitId: String, levelId: String, lessonId: String?) -> Unit,
    onOpenUnitIntro: (unitId: String, levelId: String, lessonId: String?) -> Unit,
    onOpenGuidebook: (unitId: String) -> Unit,
    onStartStory: (unitId: String, levelId: String) -> Unit,
    onOpenProfile: () -> Unit,
    onOpenSections: () -> Unit,
    onJump: (sectionId: String) -> Unit,
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
        onOpenSections = onOpenSections,
        onSelectSection = viewModel::selectSection,
        onJump = { sectionId ->
            // Back from a passed jump, the path shows the section jumped to.
            viewModel.selectSection(sectionId)
            onJump(sectionId)
        },
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
    onOpenSections: () -> Unit = {},
    onSelectSection: (sectionId: String) -> Unit = {},
    onJump: (sectionId: String) -> Unit = {},
    listState: LazyListState = rememberLazyListState(),
) {
    when (state) {
        PathUiState.Loading -> Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) { LoadingIndicator() }
        PathUiState.Error -> Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(stringResource(R.string.feature_path_error), Modifier.padding(HitzSpacing.xl))
        }
        is PathUiState.Ready -> {
            val openNode: (PathNode) -> Unit = { node ->
                when {
                    node.type == NodeType.Story -> onStartStory(node.unitId, node.levelId)
                    // A unit is introduced before its first lesson.
                    node.type == NodeType.Lesson && node.unitId !in state.seenUnitIntros ->
                        onOpenUnitIntro(node.unitId, node.levelId, node.nextLessonId)
                    else -> onStartLevel(node.unitId, node.levelId, node.nextLessonId)
                }
            }
            BoxWithConstraints(modifier.fillMaxSize()) {
                val currentUnit = remember(state.path) {
                    state.path.sections.flatMap { it.units }.firstOrNull { unit -> unit.nodes.any { it.isCurrent } }
                }
                // Read here: inside the Row, the Row's scope hides BoxWithConstraints' maxWidth.
                val showPane = maxWidth >= PaneMinWidth && currentUnit != null
                Row(Modifier.fillMaxSize()) {
                    // The path keeps a readable width; on wide windows the current unit stays in view beside it.
                    Box(Modifier.weight(1f).fillMaxHeight(), contentAlignment = Alignment.TopCenter) {
                        LearningPath(
                            state = state,
                            listState = listState,
                            onNodeClick = openNode,
                            onChallengeClick = { onStartLevel(it.unitId, it.levelId, null) },
                            onRecallClick = { onStartLevel(it.unitId, it.levelId, null) },
                            onOpenGuidebook = onOpenGuidebook,
                            onOpenProfile = onOpenProfile,
                            onOpenSections = onOpenSections,
                            onSelectSection = onSelectSection,
                            onJump = onJump,
                            modifier = Modifier.widthIn(max = PathMaxWidth).fillMaxHeight(),
                        )
                    }
                    if (showPane && currentUnit != null) {
                        CurrentUnitPane(
                            unit = currentUnit,
                            onStart = openNode,
                            onOpenGuidebook = { onOpenGuidebook(currentUnit.id) },
                            modifier = Modifier.width(PaneWidth).fillMaxHeight(),
                        )
                    }
                }
            }
        }
    }
}

/**
 * The top bar over the path, the rows of the section on show and the unit bar that replaces a cover
 * once it scrolls away. The path shows one section at a time; its name in the top bar opens the
 * sections overview, and the end of a section leads to the next one.
 */
@Composable
private fun LearningPath(
    state: PathUiState.Ready,
    listState: LazyListState,
    onNodeClick: (PathNode) -> Unit,
    onChallengeClick: (PathChallenge) -> Unit,
    onRecallClick: (PathRecall) -> Unit,
    onOpenGuidebook: (unitId: String) -> Unit,
    onOpenProfile: () -> Unit,
    onOpenSections: () -> Unit,
    onSelectSection: (sectionId: String) -> Unit,
    onJump: (sectionId: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val sections = state.path.sections
    val index = sections.indexOfFirst { it.id == state.sectionId }.takeIf { it >= 0 }
        ?: sections.indexOf(state.path.currentSection).coerceAtLeast(0)
    val section = sections.getOrNull(index)
    val rows = remember(state.path, index) { state.path.rows(index) }
    val units = remember(state.path) { sections.flatMap { it.units }.associateBy { it.id } }
    val unlock = rememberPathUnlock(state.path)
    Column(modifier) {
        PathTopBar(
            section = index + 1,
            title = section?.title,
            level = section?.level,
            score = state.score,
            scoreMax = state.scoreMax,
            dailyGoal = state.dailyGoal,
            onOpenProfile = onOpenProfile,
            onOpenSections = onOpenSections,
        )
        Box(Modifier.weight(1f)) {
            LearningPathList(
                rows = rows,
                sectionId = section?.id,
                unlock = unlock,
                onChallengeClick = onChallengeClick,
                onRecallClick = onRecallClick,
                onNodeClick = onNodeClick,
                onOpenGuidebook = onOpenGuidebook,
                onSelectSection = onSelectSection,
                onJump = onJump,
                listState = listState,
                modifier = Modifier.fillMaxSize(),
            )
            UnitBarOverlay(
                rows = rows,
                units = units,
                listState = listState,
                onOpenGuidebook = onOpenGuidebook,
                modifier = Modifier.align(Alignment.TopCenter),
            )
        }
    }
}

/**
 * Beside the path on wide windows: where the learner is (the current unit's cover), what the unit
 * teaches and a button that continues with its current step, so the next action never scrolls away.
 */
@Composable
private fun CurrentUnitPane(
    unit: PathUnit,
    onStart: (PathNode) -> Unit,
    onOpenGuidebook: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val current = unit.nodes.firstOrNull { it.isCurrent }
    Surface(color = MaterialTheme.colorScheme.surfaceContainerLow, modifier = modifier) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(HitzSpacing.xl),
            verticalArrangement = Arrangement.spacedBy(HitzSpacing.lg),
        ) {
            Text(
                text = stringResource(R.string.feature_path_now),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            // Its goals are listed right below, so the cover leaves out the first one.
            UnitCover(unit, isCurrentUnit = false, onOpenGuidebook = onOpenGuidebook, showGoal = false)
            if (unit.goals.isNotEmpty()) {
                Text(
                    text = stringResource(R.string.feature_path_pane_goals),
                    style = MaterialTheme.typography.titleMediumEmphasized,
                    modifier = Modifier.semantics { heading() },
                )
                unit.goals.forEach { goal ->
                    Row(horizontalArrangement = Arrangement.spacedBy(HitzSpacing.sm)) {
                        Icon(
                            painterResource(HitzIcons.Check),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(CHIP_ICON),
                        )
                        Text(goal.resolve(), style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
            if (current != null) {
                Button(
                    onClick = { onStart(current) },
                    shapes = ButtonDefaults.shapes(),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(stringResource(R.string.feature_path_continue_with, current.title.resolve()))
                }
            }
        }
    }
}

/** From this font scale on, the chips go under the section's name instead of beside it. */
private const val STACKED_FONT_SCALE = 1.5f

/**
 * The section on show, the score and today's goal; both chips open the profile and the section's
 * name opens the sections overview. When the section changes, its name slides in from below. With
 * large fonts the chips move under the name, so it is never cut short.
 */
@Composable
private fun PathTopBar(
    section: Int,
    title: LocalizedText?,
    level: String?,
    score: Int,
    scoreMax: Int,
    dailyGoal: Float,
    onOpenProfile: () -> Unit,
    onOpenSections: () -> Unit,
) {
    val padding = Modifier
        .fillMaxWidth()
        .padding(start = HitzSpacing.xl, end = HitzSpacing.lg, top = HitzSpacing.sm, bottom = HitzSpacing.sm)
    if (LocalDensity.current.fontScale >= STACKED_FONT_SCALE) {
        Column(padding, verticalArrangement = Arrangement.spacedBy(HitzSpacing.sm)) {
            SectionTitle(section, title, level, onOpenSections)
            Row(horizontalArrangement = Arrangement.spacedBy(HitzSpacing.sm)) {
                ScoreChip(score, scoreMax, onClick = onOpenProfile)
                HitzDailyGoalChip(dailyGoal, onClick = onOpenProfile)
            }
        }
    } else {
        Row(
            modifier = padding,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(HitzSpacing.sm),
        ) {
            SectionTitle(section, title, level, onOpenSections, Modifier.weight(1f))
            ScoreChip(score, scoreMax, onClick = onOpenProfile)
            HitzDailyGoalChip(dailyGoal, onClick = onOpenProfile)
        }
    }
}

/** The section's number, level and name, with a chevron: a tap opens the sections overview. */
@Composable
private fun SectionTitle(
    section: Int,
    title: LocalizedText?,
    level: String?,
    onOpenSections: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val slide = MaterialTheme.motionScheme.fastSpatialSpec<IntOffset>()
    val fade = MaterialTheme.motionScheme.defaultEffectsSpec<Float>()
    val action = stringResource(R.string.feature_path_sections_open)
    AnimatedContent(
        targetState = section to title,
        modifier = modifier
            .clip(MaterialTheme.shapes.medium)
            .clickable(onClickLabel = action, role = Role.Button, onClick = onOpenSections),
        transitionSpec = {
            (slideInVertically(slide) { it / 2 } + fadeIn(fade)) togetherWith
                (slideOutVertically(slide) { -it / 2 } + fadeOut(fade))
        },
        label = "section",
    ) { (number, sectionTitle) ->
        Column(Modifier.padding(vertical = HitzSpacing.xs)) {
            Text(
                text = if (level != null) {
                    stringResource(R.string.feature_path_section_level, number, level)
                } else {
                    stringResource(R.string.feature_path_section, number)
                },
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = sectionTitle?.resolve().orEmpty(),
                    style = MaterialTheme.typography.titleLargeEmphasized,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false).semantics { heading() },
                )
                Icon(
                    painterResource(HitzIcons.ExpandMore),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(CHIP_ICON + HitzSpacing.xs),
                )
            }
        }
    }
}

/** The score with its unit, so it reads as points and not as a count of something else. */
@Composable
private fun ScoreChip(score: Int, max: Int, onClick: () -> Unit) {
    val description = stringResource(R.string.feature_path_score, score, max)
    Surface(
        onClick = onClick,
        shape = MaterialTheme.shapes.extraLarge,
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
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
            modifier = Modifier.padding(horizontal = HitzSpacing.md, vertical = HitzSpacing.sm),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(HitzSpacing.xs),
        ) {
            Icon(
                painterResource(HitzIcons.Level),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier.size(CHIP_ICON),
            )
            Text(
                text = stringResource(R.string.feature_path_score_short, score),
                style = MaterialTheme.typography.labelLargeEmphasized,
            )
        }
    }
}

private sealed interface PathRow {
    /** The unit the row belongs to; none for the way to the next section. */
    val unitId: String?

    data class UnitCover(val unit: PathUnit, val isCurrentUnit: Boolean) : PathRow {
        override val unitId: String get() = unit.id
    }

    data class Node(val node: PathNode, val track: Track) : PathRow {
        override val unitId: String get() = node.unitId
    }

    data class Challenge(val challenge: PathChallenge, val track: Track) : PathRow {
        override val unitId: String get() = challenge.unitId
    }

    data class Recall(val recall: PathRecall) : PathRow {
        override val unitId: String get() = recall.unitId
    }

    data class NextSection(val section: PathSection, val number: Int) : PathRow {
        override val unitId: String? get() = null
    }
}

/**
 * One row per step of section [sectionIndex]: each unit opens with its cover, then its levels wind
 * down the screen and its star challenge closes it; a cumulative review follows the units that end
 * a textbook unit. The section ends with the way to the next one.
 */
private fun LearningPath.rows(sectionIndex: Int): List<PathRow> = buildList {
    val section = sections.getOrNull(sectionIndex) ?: return@buildList
    section.units.forEach { unit ->
        add(PathRow.UnitCover(unit, isCurrentUnit = unit.nodes.any { it.isCurrent }))
        val offsets = List(unit.nodes.size + 1) { Winding[it % Winding.size] }
        val challenge = unit.challenge
        val lastIndex = if (challenge == null) unit.nodes.lastIndex else unit.nodes.size
        unit.nodes.forEachIndexed { index, node ->
            val nextReached = unit.nodes.getOrNull(index + 1)?.let { it.state != NodeState.Locked }
                ?: (challenge?.isAvailable == true)
            val track = Track(
                offset = offsets[index],
                previousOffset = offsets.getOrNull(index - 1).takeIf { index > 0 },
                nextOffset = offsets[index + 1].takeIf { index < lastIndex },
                reached = node.state != NodeState.Locked,
                walkedOn = node.state == NodeState.Completed && nextReached,
            )
            add(PathRow.Node(node, track))
        }
        if (challenge != null) {
            val index = unit.nodes.size
            val track = Track(
                offset = offsets[index],
                previousOffset = offsets.getOrNull(index - 1).takeIf { index > 0 },
                nextOffset = null,
                reached = challenge.isAvailable,
                walkedOn = false,
            )
            add(PathRow.Challenge(challenge, track))
        }
        unit.recall?.let { add(PathRow.Recall(it)) }
    }
    sections.getOrNull(sectionIndex + 1)?.let { add(PathRow.NextSection(it, sectionIndex + 2)) }
}

/** The row the path opens on: the current unit's cover when the current step is near it. */
private fun List<PathRow>.openingRow(): Int {
    val current = indexOfFirst { it is PathRow.Node && it.node.isCurrent }
    if (current <= 0) return 0
    val cover = subList(0, current).indexOfLast { it is PathRow.UnitCover }
    return if (cover >= 0 && current - cover <= COVER_REACH) cover else current - 1
}

/**
 * Animates a step that was unlocked while the learner was away (in the lesson that unlocked it):
 * the path walks on from the previous step to the new one, then the new step pops into place.
 * The last step seen survives navigation; nothing moves the first time or with reduced motion.
 */
@Stable
private class PathUnlock(val levelId: String?, val previousLevelId: String?) {
    val fill = Animatable(if (levelId == null) 1f else 0f)
    val pop = Animatable(if (levelId == null) 1f else POP_START)

    /** Walked fraction of the track above [node], when it is the step being unlocked. */
    fun above(node: PathNode): (() -> Float)? =
        if (node.levelId == levelId) ({ ((fill.value - HALF) / HALF).coerceIn(0f, 1f) }) else null

    /** Walked fraction of the track below [node], when the next step is the one being unlocked. */
    fun below(node: PathNode): (() -> Float)? =
        if (node.levelId == previousLevelId) ({ (fill.value / HALF).coerceIn(0f, 1f) }) else null

    fun scale(node: PathNode): (() -> Float)? = if (node.levelId == levelId) ({ pop.value }) else null
}

@Composable
private fun rememberPathUnlock(path: LearningPath): PathUnlock {
    val current = path.currentNode?.levelId
    var lastSeen by rememberSaveable { mutableStateOf<String?>(null) }
    val still = rememberReducedMotion()
    val unlocking = current?.takeIf { lastSeen != null && it != lastSeen && !still }
    val previous = unlocking?.let { id ->
        path.sections.flatMap { it.units }.firstNotNullOfOrNull { unit ->
            val index = unit.nodes.indexOfFirst { it.levelId == id }
            if (index > 0) unit.nodes[index - 1].levelId else null
        }
    }
    val unlock = remember(unlocking, previous) { PathUnlock(unlocking, previous) }
    val fillSpec = MaterialTheme.motionScheme.slowEffectsSpec<Float>()
    val popSpec = MaterialTheme.motionScheme.defaultSpatialSpec<Float>()
    LaunchedEffect(unlock) {
        if (unlock.levelId != null) {
            delay(UNLOCK_DELAY_MILLIS)
            unlock.fill.animateTo(1f, fillSpec)
            unlock.pop.animateTo(1f, popSpec)
        }
        lastSeen = current
    }
    return unlock
}

@Composable
private fun LearningPathList(
    rows: List<PathRow>,
    sectionId: String?,
    unlock: PathUnlock,
    onChallengeClick: (PathChallenge) -> Unit,
    onRecallClick: (PathRecall) -> Unit,
    onNodeClick: (PathNode) -> Unit,
    onOpenGuidebook: (unitId: String) -> Unit,
    onSelectSection: (sectionId: String) -> Unit,
    onJump: (sectionId: String) -> Unit,
    listState: LazyListState,
    modifier: Modifier = Modifier,
) {
    OpenOnTheCurrentStep(rows, sectionId, listState)
    LazyColumn(
        state = listState,
        modifier = modifier,
        contentPadding = PaddingValues(bottom = HitzSpacing.xxl),
    ) {
        rows.forEach { row ->
            when (row) {
                is PathRow.UnitCover -> item(key = row.key) {
                    UnitCover(
                        unit = row.unit,
                        isCurrentUnit = row.isCurrentUnit,
                        onOpenGuidebook = { onOpenGuidebook(row.unit.id) },
                        modifier = Modifier.padding(
                            start = HitzSpacing.md,
                            end = HitzSpacing.md,
                            top = HitzSpacing.xl,
                            bottom = HitzSpacing.sm,
                        ),
                    )
                }
                is PathRow.Node -> item(key = row.key) {
                    val track = row.track
                    PathNodeRow(
                        node = row.node,
                        track = track,
                        onClick = { onNodeClick(row.node) },
                        above = unlock.above(row.node) ?: { if (track.reached) 1f else 0f },
                        below = unlock.below(row.node) ?: { if (track.walkedOn) 1f else 0f },
                        pop = unlock.scale(row.node) ?: { 1f },
                    )
                }
                is PathRow.Challenge -> item(key = row.key) {
                    ChallengeNodeRow(
                        challenge = row.challenge,
                        track = row.track,
                        onClick = { onChallengeClick(row.challenge) },
                    )
                }
                is PathRow.Recall -> item(key = row.key) {
                    RecallCard(row.recall, { onRecallClick(row.recall) }, Modifier.padding(HitzSpacing.lg))
                }
                is PathRow.NextSection -> item(key = row.key) {
                    NextSectionCard(
                        section = row.section,
                        number = row.number,
                        onOpen = { onSelectSection(row.section.id) },
                        onJump = { onJump(row.section.id) },
                        modifier = Modifier.padding(horizontal = HitzSpacing.lg).padding(top = HitzSpacing.xxl),
                    )
                }
            }
        }
    }
}

/** A new section opens at its current step, or at its top; the same section keeps up with its current step. */
@Composable
private fun OpenOnTheCurrentStep(rows: List<PathRow>, sectionId: String?, listState: LazyListState) {
    val opening = remember(rows) { rows.openingRow() }
    var openedSection by rememberSaveable { mutableStateOf<String?>(null) }
    LaunchedEffect(sectionId, opening) {
        if (sectionId != openedSection) {
            listState.scrollToItem(opening)
            openedSection = sectionId
        } else if (opening > 0) {
            listState.scrollToItem(opening)
        }
    }
}

/**
 * Once a unit's cover has scrolled away, a compact bar with the unit stays at the top. It slides
 * down when it appears and back up when the next cover arrives; between units it crossfades.
 */
@Composable
private fun UnitBarOverlay(
    rows: List<PathRow>,
    units: Map<String, PathUnit>,
    listState: LazyListState,
    onOpenGuidebook: (unitId: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val threshold = with(LocalDensity.current) { UnitBarThreshold.roundToPx() }
    val unitId by remember(rows, threshold) {
        derivedStateOf {
            val first = listState.layoutInfo.visibleItemsInfo.firstOrNull() ?: return@derivedStateOf null
            val row = rows.getOrNull(first.index) ?: return@derivedStateOf null
            val coverGone = row !is PathRow.UnitCover || first.offset + first.size < threshold
            row.unitId.takeIf { coverGone }
        }
    }
    val slide = MaterialTheme.motionScheme.defaultSpatialSpec<IntOffset>()
    val fade = MaterialTheme.motionScheme.defaultEffectsSpec<Float>()
    AnimatedContent(
        targetState = unitId,
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.TopCenter,
        transitionSpec = {
            val change = if (initialState == null || targetState == null) {
                (slideInVertically(slide) { -it } + fadeIn(fade)) togetherWith
                    (slideOutVertically(slide) { -it } + fadeOut(fade))
            } else {
                fadeIn(fade) togetherWith fadeOut(fade)
            }
            change using null
        },
        label = "unit bar",
    ) { id ->
        val unit = id?.let(units::get)
        if (unit != null) UnitBar(unit, onOpenGuidebook = { onOpenGuidebook(unit.id) })
    }
}

private val PathRow.key: String
    get() = when (this) {
        is PathRow.UnitCover -> "unit-${unit.id}"
        is PathRow.Node -> "node-${node.levelId}"
        is PathRow.Challenge -> "challenge-${challenge.levelId}"
        is PathRow.Recall -> "recall-${recall.levelId}"
        is PathRow.NextSection -> "next-${section.id}"
    }

@HitzPreviews
@Composable
private fun PathContentPreview() {
    HitzTheme {
        Surface {
            PathContent(
                state = PathUiState.Ready(
                    path = PathPreviewData.path,
                    dailyGoal = 0.6f,
                    seenUnitIntros = emptySet(),
                    score = 7,
                ),
                onStartLevel = { _, _, _ -> },
                onOpenGuidebook = {},
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
