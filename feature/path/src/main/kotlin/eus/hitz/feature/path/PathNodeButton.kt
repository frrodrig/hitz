@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package eus.hitz.feature.path

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import eus.hitz.core.designsystem.icon.HitzIcons
import eus.hitz.core.designsystem.motion.rememberReducedMotion
import eus.hitz.core.designsystem.preview.HitzPreviews
import eus.hitz.core.designsystem.theme.HitzSpacing
import eus.hitz.core.designsystem.theme.HitzTheme
import eus.hitz.core.domain.path.NodeState
import eus.hitz.core.domain.path.NodeType
import eus.hitz.core.domain.path.PathNode
import eus.hitz.core.model.content.LocalizedText

private val NodeSize = 72.dp
private val CurrentNodeSize = 96.dp
private const val IconScale = 0.38f
private const val FULL_TURN = 360f

/** One slow turn of the current node's shape, like the expressive loading indicator. */
private const val TURN_MILLIS = 16_000

/** Each level type has its own expressive shape, so the path reads at a glance. */
private val NodeType.shape
    get() = when (this) {
        NodeType.Lesson -> MaterialShapes.Cookie9Sided
        NodeType.Story -> MaterialShapes.Flower
        NodeType.Review -> MaterialShapes.Clover4Leaf
        NodeType.Legendary -> MaterialShapes.SoftBurst
    }

private val NodeType.icon: Int
    get() = when (this) {
        NodeType.Lesson -> HitzIcons.Star
        NodeType.Story -> HitzIcons.Book
        NodeType.Review -> HitzIcons.Bolt
        NodeType.Legendary -> HitzIcons.Trophy
    }

@Composable
private fun NodeType.label(): String = stringResource(
    when (this) {
        NodeType.Lesson -> R.string.feature_path_node_lesson
        NodeType.Story -> R.string.feature_path_node_story
        NodeType.Review -> R.string.feature_path_node_review
        NodeType.Legendary -> R.string.feature_path_node_legendary
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
private fun nodeColors(node: PathNode): Pair<Color, Color> {
    val scheme = MaterialTheme.colorScheme
    return when {
        node.state == NodeState.Locked -> scheme.surfaceContainerHighest to scheme.onSurfaceVariant
        node.state == NodeState.Completed -> scheme.secondaryContainer to scheme.onSecondaryContainer
        node.type == NodeType.Legendary || node.type == NodeType.Story -> scheme.tertiary to scheme.onTertiary
        else -> scheme.primary to scheme.onPrimary
    }
}

private fun nodeIcon(node: PathNode): Int = when (node.state) {
    NodeState.Locked -> HitzIcons.Lock
    NodeState.Completed -> HitzIcons.Check
    NodeState.Available -> node.type.icon
}

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

/**
 * A level of the path. Locked nodes are announced as disabled; the current node is larger and
 * labelled "Start".
 */
@Composable
internal fun PathNodeButton(
    node: PathNode,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val (container, content) = nodeColors(node)
    val size: Dp = if (node.isCurrent) CurrentNodeSize else NodeSize
    val title = node.title.resolve()
    val description = nodeDescription(node, title)
    val enabled = node.state != NodeState.Locked

    Column(
        modifier = modifier.clearAndSetSemantics {
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
        },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(HitzSpacing.xs),
    ) {
        if (node.isCurrent) StartLabel()
        Box(contentAlignment = Alignment.Center) {
            if (node.totalLessons > 1 && node.state == NodeState.Available) {
                CircularProgressIndicator(
                    progress = { node.completedLessons.toFloat() / node.totalLessons },
                    modifier = Modifier.size(size + HitzSpacing.md),
                    trackColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                )
            }
            val turn = if (node.isCurrent && !rememberReducedMotion()) currentNodeTurn() else null
            // The shape turns, the icon stays upright on top of it.
            Surface(
                onClick = onClick,
                enabled = enabled,
                shape = node.type.shape.toShape(),
                color = container,
                modifier = Modifier
                    .size(size)
                    .graphicsLayer { rotationZ = turn?.value ?: 0f },
            ) {}
            Icon(
                painter = painterResource(nodeIcon(node)),
                contentDescription = null,
                tint = content,
                modifier = Modifier.size(size * IconScale),
            )
        }
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            color = if (enabled) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant,
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

@Composable
private fun StartLabel() {
    Surface(shape = MaterialTheme.shapes.extraLarge, color = MaterialTheme.colorScheme.inverseSurface) {
        Text(
            text = stringResource(R.string.feature_path_start),
            style = MaterialTheme.typography.labelLargeEmphasized,
            color = MaterialTheme.colorScheme.inverseOnSurface,
            modifier = Modifier.padding(horizontal = HitzSpacing.md, vertical = HitzSpacing.xs),
        )
    }
}

@HitzPreviews
@Composable
private fun PathNodeButtonPreview() {
    val text = LocalizedText("Saludos", "Agurrak")
    fun node(type: NodeType, state: NodeState, current: Boolean = false) =
        PathNode("u", "l", type, text, state, current, completedLessons = 1, totalLessons = 2, nextLessonId = null)
    HitzTheme {
        Surface(color = Color.Transparent) {
            Row(Modifier.padding(HitzSpacing.lg), horizontalArrangement = Arrangement.spacedBy(HitzSpacing.lg)) {
                PathNodeButton(node(NodeType.Lesson, NodeState.Completed), {})
                PathNodeButton(node(NodeType.Lesson, NodeState.Available, current = true), {})
                PathNodeButton(node(NodeType.Story, NodeState.Locked), {})
                PathNodeButton(node(NodeType.Review, NodeState.Locked), {})
            }
        }
    }
}
