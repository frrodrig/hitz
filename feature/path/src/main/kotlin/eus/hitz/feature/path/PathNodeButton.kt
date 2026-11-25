@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package eus.hitz.feature.path

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.role
import androidx.compose.ui.text.style.Hyphens
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import eus.hitz.core.designsystem.component.HitzStars
import eus.hitz.core.designsystem.component.MAX_STARS
import eus.hitz.core.designsystem.icon.HitzIcons
import eus.hitz.core.designsystem.motion.rememberReducedMotion
import eus.hitz.core.designsystem.preview.HitzPreviews
import eus.hitz.core.designsystem.theme.HitzSpacing
import eus.hitz.core.designsystem.theme.HitzTheme
import eus.hitz.core.domain.path.NodeState
import eus.hitz.core.domain.path.NodeType
import eus.hitz.core.domain.path.PathChallenge
import eus.hitz.core.domain.path.PathNode
import eus.hitz.core.model.content.LocalizedText

// Size follows priority: the current step is the largest thing on the path, locked ones the smallest.
private val CurrentNodeSize = 96.dp
private val CompletedNodeSize = 56.dp
private val LockedNodeSize = 52.dp
private val OtherNodeSize = 64.dp
private val ChallengeNodeSize = 56.dp
private const val ICON_SCALE = 0.4f

private val RowPadding = 14.dp
private val LabelWidth = 120.dp
private val LabelGap = HitzSpacing.md
private val LabelLineHeight = 20.dp
private val StartCardWidth = 200.dp
private val TrackWidth = 8.dp
private val ChallengeStars = 16.dp

/** The current node and its card grow from their top when they pop in. */
private val PopOrigin = TransformOrigin(0.5f, 0f)

private const val FULL_TURN = 360f

/** One slow turn of the current node's shape, like the expressive loading indicator. */
private const val TURN_MILLIS = 16_000

/**
 * How the path runs through one row, as horizontal offsets from the centre: the row's node sits
 * at [offset]; the track comes from halfway to [previousOffset] (none at the start of a unit) and
 * goes on to halfway to [nextOffset] (none at its end). The part up to the node is walked once
 * the node is [reached]; the part after it once the next step is reached ([walkedOn]).
 */
@Immutable
internal data class Track(
    val offset: Dp,
    val previousOffset: Dp?,
    val nextOffset: Dp?,
    val reached: Boolean,
    val walkedOn: Boolean,
)

/**
 * Draws the path behind a row: one stroke from the top edge to the node's centre and one from
 * there to the bottom edge, meeting the neighbouring rows' strokes halfway between the nodes.
 * [above] and [below] are the walked fractions of each half, read while drawing so that an
 * unlock can fill them in without recomposing.
 */
private fun Modifier.track(
    track: Track,
    nodeCenterY: Dp,
    walked: Color,
    ahead: Color,
    above: () -> Float,
    below: () -> Float,
): Modifier = drawBehind {
    val width = TrackWidth.toPx()
    val centerX = size.width / 2 + track.offset.toPx()
    val centerY = nodeCenterY.toPx()
    track.previousOffset?.let { previous ->
        val startX = size.width / 2 + ((previous + track.offset) / 2).toPx()
        val path = Path().apply {
            moveTo(startX, 0f)
            cubicTo(startX, centerY / 2, centerX, centerY / 2, centerX, centerY)
        }
        drawTrackPart(path, above(), walked, ahead, width)
    }
    track.nextOffset?.let { next ->
        val endX = size.width / 2 + ((track.offset + next) / 2).toPx()
        val middle = centerY + (size.height - centerY) / 2
        val path = Path().apply {
            moveTo(centerX, centerY)
            cubicTo(centerX, middle, endX, middle, endX, size.height)
        }
        drawTrackPart(path, below(), walked, ahead, width)
    }
}

private fun DrawScope.drawTrackPart(path: Path, walkedFraction: Float, walked: Color, ahead: Color, width: Float) {
    val stroke = Stroke(width = width, cap = StrokeCap.Round)
    if (walkedFraction >= 1f) {
        drawPath(path, walked, style = stroke)
        return
    }
    drawPath(path, ahead, style = stroke)
    if (walkedFraction > 0f) {
        val measure = PathMeasure().apply { setPath(path, false) }
        val part = Path()
        measure.getSegment(0f, measure.length * walkedFraction, part, true)
        drawPath(part, walked, style = stroke)
    }
}

private val NodeType.icon: Int
    get() = when (this) {
        NodeType.Lesson -> HitzIcons.Star
        NodeType.Story -> HitzIcons.Book
        NodeType.Review -> HitzIcons.Bolt
    }

@Composable
private fun NodeType.label(): String = stringResource(
    when (this) {
        NodeType.Lesson -> R.string.feature_path_node_lesson
        NodeType.Story -> R.string.feature_path_node_story
        NodeType.Review -> R.string.feature_path_node_review
    },
)

@Composable
private fun NodeState.label(): String = stringResource(
    when (this) {
        NodeState.Locked -> R.string.feature_path_state_locked
        NodeState.Available -> R.string.feature_path_state_available
        NodeState.Completed -> R.string.feature_path_state_completed
    },
)

@Composable
private fun nodeDescription(node: PathNode, title: String): String {
    val type = node.type.label()
    val state = node.state.label()
    val lessons = if (node.totalLessons > 1) {
        stringResource(R.string.feature_path_lessons_done, node.completedLessons, node.totalLessons)
    } else {
        null
    }
    return listOfNotNull("$type: $title", state, lessons).joinToString(", ")
}

private val PathNode.size: Dp
    get() = when {
        isCurrent -> CurrentNodeSize
        state == NodeState.Completed -> CompletedNodeSize
        state == NodeState.Locked -> LockedNodeSize
        else -> OtherNodeSize
    }

/** The current node shows its lessons as a ring around it when the level has several. */
private val PathNode.showsLessons: Boolean get() = isCurrent && totalLessons > 1

/** Room the node takes in its row, ring included. */
private val PathNode.boxSize: Dp get() = if (showsLessons) size + HitzSpacing.md else size

/** Semantics for a node and its label: one button, described with its type, title and state. */
private fun Modifier.nodeSemantics(description: String, enabled: Boolean, onClick: () -> Unit) = clearAndSetSemantics {
    contentDescription = description
    role = Role.Button
    if (enabled) {
        this.onClick(label = null) {
            onClick()
            true
        }
    } else {
        disabled()
    }
}

/**
 * A level on the path, at its place on the [track]. Completed and locked levels are small, with
 * their title beside them; the current one is large, with a card under it that starts it.
 * [pop] scales the current node and its card when they have just been unlocked.
 */
@Composable
internal fun PathNodeRow(
    node: PathNode,
    track: Track,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    above: () -> Float = { if (track.reached) 1f else 0f },
    below: () -> Float = { if (track.walkedOn) 1f else 0f },
    pop: () -> Float = { 1f },
) {
    val title = node.title.resolve()
    val description = nodeDescription(node, title)
    val enabled = node.state != NodeState.Locked
    Box(
        modifier
            .fillMaxWidth()
            .track(
                track = track,
                nodeCenterY = RowPadding + node.boxSize / 2,
                walked = MaterialTheme.colorScheme.secondary,
                ahead = MaterialTheme.colorScheme.surfaceContainerHighest,
                above = above,
                below = below,
            )
            .padding(vertical = RowPadding),
    ) {
        if (node.isCurrent) {
            Column(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset(x = track.offset)
                    .graphicsLayer {
                        val scale = pop()
                        scaleX = scale
                        scaleY = scale
                        transformOrigin = PopOrigin
                    },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(HitzSpacing.sm),
            ) {
                NodeShape(node, onClick, Modifier.nodeSemantics(description, enabled, onClick))
                StartCard(node, title, onClick)
            }
        } else {
            SideLabelled(
                offset = track.offset,
                nodeSize = node.boxSize,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .nodeSemantics(description, enabled, onClick),
                node = { NodeShape(node, onClick) },
                label = { align ->
                    Text(
                        text = title,
                        style = MaterialTheme.typography.labelLarge.copy(hyphens = Hyphens.Auto),
                        textAlign = align,
                        color = if (node.state == NodeState.Completed) {
                            MaterialTheme.colorScheme.onSurface
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        },
                    )
                },
            )
        }
    }
}

/**
 * A node with its label beside it, on the side with more room: the label goes on the left of a
 * node right of the centre and on the right otherwise. The node's centre stays at [offset].
 */
@Composable
private fun SideLabelled(
    offset: Dp,
    nodeSize: Dp,
    modifier: Modifier,
    node: @Composable () -> Unit,
    label: @Composable (TextAlign) -> Unit,
) {
    val labelOnStart = offset > 0.dp
    val shift = (LabelWidth + LabelGap) / 2
    Row(
        modifier = modifier.offset(x = if (labelOnStart) offset - shift else offset + shift),
        horizontalArrangement = Arrangement.spacedBy(LabelGap),
    ) {
        val labelModifier = Modifier
            .width(LabelWidth)
            .padding(top = ((nodeSize - LabelLineHeight) / 2).coerceAtLeast(0.dp))
        if (labelOnStart) {
            Column(labelModifier, horizontalAlignment = Alignment.End) { label(TextAlign.End) }
        }
        node()
        if (!labelOnStart) {
            Column(labelModifier, horizontalAlignment = Alignment.Start) { label(TextAlign.Start) }
        }
    }
}

/**
 * The node itself. The current step is the one expressive shape on the path, in the primary
 * colour, turning slowly; the others are plain circles: walked ones in the path's colour with a
 * check, locked ones neutral with a lock.
 */
@Composable
private fun NodeShape(node: PathNode, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val scheme = MaterialTheme.colorScheme
    val (container, content) = when {
        node.isCurrent -> scheme.primary to scheme.onPrimary
        node.state == NodeState.Completed -> scheme.secondary to scheme.onSecondary
        node.state == NodeState.Locked -> scheme.surfaceContainerHighest to scheme.onSurfaceVariant
        else -> scheme.primaryContainer to scheme.onPrimaryContainer
    }
    val containerColor by animateColorAsState(
        targetValue = container,
        animationSpec = MaterialTheme.motionScheme.defaultEffectsSpec(),
        label = "node",
    )
    val icon = when (node.state) {
        NodeState.Locked -> HitzIcons.Lock
        NodeState.Completed -> HitzIcons.Check
        NodeState.Available -> node.type.icon
    }
    val shape: Shape = if (node.isCurrent) MaterialShapes.Cookie9Sided.toShape() else CircleShape
    Box(modifier.size(node.boxSize), contentAlignment = Alignment.Center) {
        if (node.showsLessons) {
            CircularProgressIndicator(
                progress = { node.completedLessons.toFloat() / node.totalLessons },
                modifier = Modifier.size(node.boxSize),
                trackColor = scheme.surfaceContainerHighest,
            )
        }
        val turn = if (node.isCurrent && !rememberReducedMotion()) currentNodeTurn() else null
        // The shape turns, the icon stays upright on top of it.
        Surface(
            onClick = onClick,
            enabled = node.state != NodeState.Locked,
            shape = shape,
            color = containerColor,
            modifier = Modifier
                .size(node.size)
                .graphicsLayer { rotationZ = turn?.value ?: 0f },
        ) {}
        Icon(
            painter = painterResource(icon),
            contentDescription = null,
            tint = content,
            modifier = Modifier.size(node.size * ICON_SCALE),
        )
    }
}

@Composable
private fun currentNodeTurn(): State<Float> = rememberInfiniteTransition(label = "current node").animateFloat(
    initialValue = 0f,
    targetValue = FULL_TURN,
    animationSpec = infiniteRepeatable(tween(TURN_MILLIS, easing = LinearEasing)),
    label = "turn",
)

/** Under the current node: its title, its lessons when there are several, and Start. */
@Composable
private fun StartCard(node: PathNode, title: String, onClick: () -> Unit) {
    Surface(
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surfaceContainerLowest,
        shadowElevation = HitzSpacing.xxs,
        modifier = Modifier.width(StartCardWidth),
    ) {
        Column(
            modifier = Modifier.padding(horizontal = HitzSpacing.lg, vertical = HitzSpacing.md),
            verticalArrangement = Arrangement.spacedBy(HitzSpacing.sm),
        ) {
            // The node already says all this to a screen reader.
            Column(Modifier.clearAndSetSemantics {}) {
                Text(title, style = MaterialTheme.typography.titleMediumEmphasized)
                if (node.totalLessons > 1) {
                    Text(
                        text = stringResource(
                            R.string.feature_path_lessons_done,
                            node.completedLessons,
                            node.totalLessons,
                        ),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            Button(onClick = onClick, shapes = ButtonDefaults.shapes(), modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.feature_path_start))
            }
        }
    }
}

/**
 * A unit's star challenge, the last step of its stretch of path: a node in the achievement colour
 * once the unit is done (filled when all its stars are earned), with its stars beside it.
 */
@Composable
internal fun ChallengeNodeRow(
    challenge: PathChallenge,
    track: Track,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    above: () -> Float = { if (track.reached) 1f else 0f },
) {
    val scheme = MaterialTheme.colorScheme
    val (container, content) = when {
        !challenge.isAvailable -> scheme.surfaceContainerHighest to scheme.onSurfaceVariant
        challenge.stars == MAX_STARS -> scheme.tertiary to scheme.onTertiary
        else -> scheme.tertiaryContainer to scheme.onTertiaryContainer
    }
    val description = challengeDescription(challenge)
    Box(
        modifier
            .fillMaxWidth()
            .track(
                track = track,
                nodeCenterY = RowPadding + ChallengeNodeSize / 2,
                walked = scheme.secondary,
                ahead = scheme.surfaceContainerHighest,
                above = above,
                below = { 0f },
            )
            .padding(vertical = RowPadding),
    ) {
        SideLabelled(
            offset = track.offset,
            nodeSize = ChallengeNodeSize,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .nodeSemantics(description, challenge.isAvailable, onClick),
            node = { ChallengeNode(challenge, container, content, onClick) },
            label = { align -> ChallengeLabel(challenge, align) },
        )
    }
}

@Composable
private fun ChallengeNode(challenge: PathChallenge, container: Color, content: Color, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        enabled = challenge.isAvailable,
        shape = CircleShape,
        color = container,
        modifier = Modifier.size(ChallengeNodeSize),
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                painterResource(HitzIcons.Star),
                contentDescription = null,
                tint = content,
                modifier = Modifier.size(ChallengeNodeSize * ICON_SCALE),
            )
        }
    }
}

/** Its name, then the stars earned, or what opens it while locked. */
@Composable
private fun ChallengeLabel(challenge: PathChallenge, align: TextAlign) {
    val scheme = MaterialTheme.colorScheme
    Text(
        text = stringResource(R.string.feature_path_challenge),
        style = MaterialTheme.typography.labelLarge.copy(hyphens = Hyphens.Auto),
        textAlign = align,
        color = if (challenge.isAvailable) scheme.onSurface else scheme.onSurfaceVariant,
    )
    if (challenge.isAvailable) {
        HitzStars(earned = challenge.stars, size = ChallengeStars)
    } else {
        Text(
            text = stringResource(R.string.feature_path_challenge_locked),
            style = MaterialTheme.typography.labelMedium.copy(hyphens = Hyphens.Auto),
            textAlign = align,
            color = scheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun challengeDescription(challenge: PathChallenge): String {
    val stars = stringResource(
        R.string.feature_path_challenge_description,
        challenge.unitId.unitNumber(),
        challenge.stars,
        MAX_STARS,
    )
    return if (challenge.isAvailable) {
        stars
    } else {
        "$stars, ${stringResource(R.string.feature_path_state_locked)}: " +
            stringResource(R.string.feature_path_challenge_locked)
    }
}

@HitzPreviews
@Composable
private fun PathNodeRowPreview() {
    val text = LocalizedText("Saludos", "Agurrak")
    fun node(type: NodeType, state: NodeState, current: Boolean = false) =
        PathNode("u", "l", type, text, state, current, completedLessons = 1, totalLessons = 2, nextLessonId = null)
    val offsets = listOf(0.dp, 44.dp, 64.dp, 44.dp)
    fun track(index: Int, reached: Boolean, walkedOn: Boolean) = Track(
        offset = offsets[index],
        previousOffset = offsets.getOrNull(index - 1),
        nextOffset = offsets.getOrNull(index + 1),
        reached = reached,
        walkedOn = walkedOn,
    )
    HitzTheme {
        Surface(color = Color.Transparent) {
            Column {
                PathNodeRow(node(NodeType.Lesson, NodeState.Completed), track(0, true, true), {})
                PathNodeRow(node(NodeType.Lesson, NodeState.Available, current = true), track(1, true, false), {})
                PathNodeRow(node(NodeType.Story, NodeState.Locked), track(2, false, false), {})
                ChallengeNodeRow(
                    challenge = PathChallenge("u01-1", "u01-1.l6", text, "ane", isAvailable = true, stars = 2),
                    track = track(3, false, false),
                    onClick = {},
                )
            }
        }
    }
}
