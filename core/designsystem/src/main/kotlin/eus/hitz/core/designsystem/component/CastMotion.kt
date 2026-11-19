package eus.hitz.core.designsystem.component

import androidx.annotation.DrawableRes
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.withInfiniteAnimationFrameMillis
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.LongState
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.GraphicsLayerScope
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource

/** One drawing of a member in a pose: the whole [still] drawable and its animated layer tree. */
internal class CastArt(@param:DrawableRes val still: Int, val nodes: List<CastNode>)

/** A node of the layer tree that `tools/design/cast_to_vector.py` writes in `CastArt.kt`. */
internal sealed interface CastNode

/**
 * One vector covering the whole 240 × 280 canvas. With a [motion], it moves around
 * ([pivotX], [pivotY]), in the drawing's coordinates.
 */
internal class CastLayer(
    @param:DrawableRes val drawable: Int,
    val motion: Motion? = null,
    val pivotX: Float = 0f,
    val pivotY: Float = 0f,
) : CastNode

/** Layers that move together with [motion] (a body that jumps), and may also move on their own. */
internal class CastGroup(
    val motion: Motion,
    val pivotX: Float,
    val pivotY: Float,
    val children: List<CastNode>,
) : CastNode

private const val VIEW_WIDTH = 240f
private const val VIEW_HEIGHT = 280f

/**
 * A member alive: the layers stacked in order, each moved by its motion around its pivot. Every
 * motion reads one clock while drawing, so the animation never recomposes; a paused clock
 * (screenshot tests) draws the pose at rest.
 */
@Composable
internal fun AnimatedCast(art: CastArt, modifier: Modifier = Modifier) {
    val clock = rememberCastClock()
    Box(modifier) {
        art.nodes.forEach { CastNodeView(it, clock) }
    }
}

@Composable
private fun rememberCastClock(): LongState {
    val time = remember { mutableLongStateOf(0L) }
    LaunchedEffect(Unit) {
        // Infinite-animation frames do not keep tests from going idle.
        val start = withInfiniteAnimationFrameMillis { it }
        while (true) {
            withInfiniteAnimationFrameMillis { time.longValue = it - start }
        }
    }
    return time
}

@Composable
private fun BoxScope.CastNodeView(node: CastNode, clock: LongState) {
    when (node) {
        is CastLayer -> {
            val motion = node.motion
            Image(
                painter = painterResource(node.drawable),
                contentDescription = null,
                modifier = if (motion == null) {
                    Modifier.matchParentSize()
                } else {
                    Modifier
                        .matchParentSize()
                        .graphicsLayer { move(motion, node.pivotX, node.pivotY, clock.longValue) }
                },
            )
        }
        is CastGroup -> Box(
            Modifier
                .matchParentSize()
                .graphicsLayer { move(node.motion, node.pivotX, node.pivotY, clock.longValue) },
        ) {
            node.children.forEach { CastNodeView(it, clock) }
        }
    }
}

private fun GraphicsLayerScope.move(motion: Motion, pivotX: Float, pivotY: Float, timeMillis: Long) {
    val unit = size.width / VIEW_WIDTH
    val fraction = motion.fractionAt(timeMillis)
    transformOrigin = TransformOrigin(pivotX / VIEW_WIDTH, pivotY / VIEW_HEIGHT)
    motion.tracks.forEach { track ->
        val value = track.valueAt(fraction)
        when (track.prop) {
            Prop.X -> translationX = value * unit
            Prop.Y -> translationY = value * unit
            Prop.Rotation -> rotationZ = value
            Prop.ScaleX -> scaleX = value
            Prop.ScaleY -> scaleY = value
            Prop.Scale -> {
                scaleX = value
                scaleY = value
            }
            Prop.Alpha -> alpha = value
        }
    }
}

/** What a [Track] animates. Translations are in the drawing's units (the canvas is 240 wide). */
internal enum class Prop { X, Y, Rotation, ScaleX, ScaleY, Scale, Alpha }

/** [value] at [at] (0–1 of the loop), reached from the previous key with [easing]. */
internal class Key(val at: Float, val value: Float, val easing: Easing)

internal class Track(val prop: Prop, val keys: List<Key>) {
    fun valueAt(fraction: Float): Float {
        val next = keys.indexOfFirst { it.at >= fraction }
        return when (next) {
            -1 -> keys.last().value
            0 -> keys.first().value
            else -> {
                val from = keys[next - 1]
                val to = keys[next]
                val span = to.at - from.at
                val progress = if (span <= 0f) 1f else (fraction - from.at) / span
                from.value + (to.value - from.value) * to.easing.transform(progress)
            }
        }
    }
}

private val EaseInOut: Easing = CubicBezierEasing(0.42f, 0f, 0.58f, 1f)
private val EaseIn: Easing = CubicBezierEasing(0.5f, 0f, 1f, 1f)
private val EaseOut: Easing = CubicBezierEasing(0f, 0f, 0.5f, 1f)

private fun track(prop: Prop, vararg keys: Pair<Float, Float>, easing: Easing = EaseInOut) =
    Track(prop, keys.map { Key(it.first, it.second, easing) })

private fun key(at: Float, value: Float, easing: Easing = EaseInOut) = Key(at, value, easing)

/**
 * Every gesture of the cast, by name, as the drawings in `tools/design/art/cast/` use them in
 * `data-motion`. A motion loops every [periodMillis], [delayMillis] late, and almost all of them
 * start at rest. The design canvas runs the same keyframes as CSS.
 */
internal enum class Motion(val periodMillis: Int, val delayMillis: Int, val tracks: List<Track>) {
    // ----- Hola: everyone's own gesture
    /** Ane's body bobs to the music. */
    Bob(1_600, 0, listOf(track(Prop.Y, 0f to 0f, 0.5f to 3f, 1f to 0f))),

    /** Ane's head tilts from side to side. */
    Tilt(3_200, 0, listOf(track(Prop.Rotation, 0f to 0f, 0.25f to 4f, 0.75f to -4f, 1f to 0f))),

    /** Ane's ponytail swings. */
    TailSwing(1_600, 0, listOf(track(Prop.Rotation, 0f to 0f, 0.25f to 7f, 0.75f to -5f, 1f to 0f))),

    /** The trikitixa's bellows close and open… */
    Squeeze(1_600, 0, listOf(track(Prop.ScaleX, 0f to 1f, 0.5f to 0.7f, 1f to 1f))),

    /** …while its left board and hand… */
    PushRight(1_600, 0, listOf(track(Prop.X, 0f to 0f, 0.5f to 7f, 1f to 0f))),

    /** …and its right board and hand come together. */
    PushLeft(1_600, 0, listOf(track(Prop.X, 0f to 0f, 0.5f to -7f, 1f to 0f))),

    /** A note rises from the trikitixa and fades. */
    NoteRise(
        3_200,
        0,
        listOf(
            track(Prop.Alpha, 0f to 0f, 0.2f to 1f, 0.7f to 0f, 1f to 0f),
            track(Prop.Y, 0f to 8f, 0.7f to -34f, 1f to -34f, easing = EaseOut),
        ),
    ),

    /** The second note, half a loop later. */
    NoteRiseLate(
        3_200,
        1_600,
        listOf(
            track(Prop.Alpha, 0f to 0f, 0.2f to 1f, 0.7f to 0f, 1f to 0f),
            track(Prop.Y, 0f to 8f, 0.7f to -34f, 1f to -34f, easing = EaseOut),
        ),
    ),

    /** Mikel waves from the shoulder, then rests. */
    Wave(
        3_000,
        0,
        listOf(
            track(
                Prop.Rotation,
                0f to 0f,
                0.08f to -16f,
                0.16f to 4f,
                0.24f to -16f,
                0.32f to 4f,
                0.4f to -16f,
                0.5f to 0f,
                1f to 0f,
            ),
        ),
    ),

    /** Mikel nods while he waves. */
    Nod(3_000, 0, listOf(track(Prop.Rotation, 0f to 0f, 0.12f to -4f, 0.28f to 3f, 0.45f to 0f, 1f to 0f))),

    /** Mikel's txapela hops. */
    CapHop(3_000, 0, listOf(track(Prop.Y, 0f to 0f, 0.14f to -3f, 0.24f to 0f, 1f to 0f))),

    /** A slow breath. */
    Breathe(3_000, 0, listOf(track(Prop.ScaleY, 0f to 1f, 0.5f to 1.015f, 1f to 1f))),

    /** Maite looks down at her book and back up. */
    ReadTilt(
        4_000,
        0,
        listOf(
            track(Prop.Rotation, 0f to 0f, 0.3f to 0f, 0.4f to -6f, 0.58f to -6f, 0.7f to 3f, 0.9f to 0f, 1f to 0f),
            track(Prop.Y, 0f to 0f, 0.3f to 0f, 0.4f to 2f, 0.58f to 2f, 0.7f to 0f, 1f to 0f),
        ),
    ),

    /** A page of Maite's book turns over the spine, then hides on the left page. */
    PageFlip(
        4_000,
        0,
        listOf(
            track(Prop.ScaleX, 0f to 1f, 0.38f to 1f, 0.58f to -1f, 1f to -1f),
            track(Prop.Alpha, 0f to 1f, 0.58f to 1f, 0.6f to 0f, 1f to 0f, easing = LinearEasing),
        ),
    ),

    /** Maite's glasses glint when she looks up. */
    Glint(4_000, 0, listOf(track(Prop.Alpha, 0f to 0f, 0.72f to 0f, 0.8f to 1f, 0.92f to 0f, 1f to 0f))),

    /** Unai bends his knees with each bounce. */
    Knees(1_000, 0, listOf(track(Prop.Y, 0f to 0f, 0.15f to 3f, 0.4f to 0f, 1f to 0f))),

    /** Unai's pelota falls, squashes on the ground and comes back to his hand. */
    Dribble(
        1_000,
        0,
        listOf(
            Track(
                Prop.Y,
                listOf(
                    key(0f, 0f),
                    key(0.45f, 38f, EaseIn),
                    key(0.5f, 40f, LinearEasing),
                    key(0.55f, 38f, LinearEasing),
                    key(1f, 0f, EaseOut),
                ),
            ),
            track(Prop.ScaleX, 0f to 1f, 0.45f to 1f, 0.5f to 1.15f, 0.55f to 1f, 1f to 1f, easing = LinearEasing),
            track(Prop.ScaleY, 0f to 1f, 0.45f to 1f, 0.5f to 0.82f, 0.55f to 1f, 1f to 1f, easing = LinearEasing),
        ),
    ),

    /** The pelota's shadow grows as it nears the ground. */
    BallShadow(
        1_000,
        0,
        listOf(
            track(Prop.Scale, 0f to 0.5f, 0.5f to 1.1f, 1f to 0.5f),
            track(Prop.Alpha, 0f to 0.4f, 0.5f to 1f, 1f to 0.4f),
        ),
    ),

    /** Unai's hand pushes the pelota down. */
    HandPush(1_000, 0, listOf(track(Prop.Y, 0f to 0f, 0.12f to 8f, 0.35f to 0f, 1f to 0f))),

    /** Unai's txistera arm sways. */
    Swing(2_000, 0, listOf(track(Prop.Rotation, 0f to 0f, 0.25f to 4f, 0.75f to -3f, 1f to 0f))),

    /** Unai's head follows the pelota. */
    HeadBob(1_000, 0, listOf(track(Prop.Rotation, 0f to 0f, 0.15f to -3f, 0.4f to 0f, 1f to 0f))),

    // ----- In every pose
    /** Eyes close for a moment every few seconds. */
    Blink(4_000, 0, listOf(track(Prop.ScaleY, 0f to 1f, 0.93f to 1f, 0.96f to 0.1f, 1f to 1f, easing = LinearEasing))),

    // ----- Bien
    /** A jump, squashing before take-off and on landing. */
    Jump(
        1_400,
        0,
        listOf(
            Track(
                Prop.Y,
                listOf(key(0f, 0f), key(0.2f, 0f), key(0.45f, -22f, EaseOut), key(0.68f, 0f, EaseIn), key(1f, 0f)),
            ),
            track(
                Prop.ScaleX,
                0f to 1f,
                0.1f to 1.05f,
                0.2f to 0.97f,
                0.45f to 1f,
                0.68f to 0.98f,
                0.76f to 1.05f,
                0.88f to 1f,
                1f to 1f,
            ),
            track(
                Prop.ScaleY,
                0f to 1f,
                0.1f to 0.93f,
                0.2f to 1.04f,
                0.45f to 1f,
                0.68f to 1.02f,
                0.76f to 0.93f,
                0.88f to 1f,
                1f to 1f,
            ),
        ),
    ),

    /** The shadow shrinks while the jump is up. */
    JumpShadow(
        1_400,
        0,
        listOf(
            track(Prop.Scale, 0f to 1f, 0.2f to 1f, 0.45f to 0.7f, 0.68f to 1f, 1f to 1f),
            track(Prop.Alpha, 0f to 1f, 0.2f to 1f, 0.45f to 0.5f, 0.68f to 1f, 1f to 1f),
        ),
    ),

    /** The raised arm wiggles from the shoulder. */
    Wiggle(700, 0, listOf(track(Prop.Rotation, 0f to 0f, 0.25f to 7f, 0.75f to -7f, 1f to 0f))),

    /** A sparkle grows, turns and fades. */
    Twinkle(
        1_400,
        0,
        listOf(
            track(Prop.Scale, 0f to 0.4f, 0.5f to 1.15f, 1f to 0.4f),
            track(Prop.Alpha, 0f to 0.2f, 0.5f to 1f, 1f to 0.2f),
            track(Prop.Rotation, 0f to 0f, 0.5f to 20f, 1f to 0f),
        ),
    ),

    /** The other sparkles, half a loop later. */
    TwinkleLate(
        1_400,
        700,
        listOf(
            track(Prop.Scale, 0f to 0.4f, 0.5f to 1.15f, 1f to 0.4f),
            track(Prop.Alpha, 0f to 0.2f, 0.5f to 1f, 1f to 0.2f),
            track(Prop.Rotation, 0f to 0f, 0.5f to 20f, 1f to 0f),
        ),
    ),

    // ----- Ups
    /** The head shakes, no, no. */
    Shake(
        2_400,
        0,
        listOf(
            track(
                Prop.Rotation,
                0f to 0f,
                0.08f to -7f,
                0.16f to 6f,
                0.24f to -5f,
                0.32f to 3f,
                0.4f to 0f,
                1f to 0f),
            ),
        ),
    ),

    /** The body flinches as the head shakes. */
    Flinch(
        2_400,
        0,
        listOf(
            track(Prop.Y, 0f to 0f, 0.06f to 3f, 0.2f to 0f, 1f to 0f),
            track(Prop.ScaleY, 0f to 1f, 0.06f to 0.98f, 0.2f to 1f, 1f to 1f),
        ),
    ),

    /** A drop of sweat slides down and fades. */
    Sweat(
        2_400,
        0,
        listOf(
            track(Prop.Alpha, 0f to 0f, 0.1f to 0f, 0.2f to 1f, 0.7f to 1f, 0.85f to 0f, 1f to 0f),
            track(Prop.Y, 0f to 0f, 0.1f to 0f, 0.7f to 14f, 0.85f to 18f, 1f to 18f, easing = EaseIn),
        ),
    ),

    /** Unai's dropped pelota rolls to his feet. */
    Roll(
        2_400,
        0,
        listOf(
            track(Prop.X, 0f to 16f, 0.3f to 0f, 1f to 0f, easing = EaseOut),
            track(Prop.Rotation, 0f to 80f, 0.3f to 0f, 1f to 0f, easing = EaseOut),
        ),
    ),

    // ----- Piensa
    /** A slow sway from the feet. */
    Sway(4_000, 0, listOf(track(Prop.Rotation, 0f to 0f, 0.25f to 2.5f, 0.75f to -2.5f, 1f to 0f))),

    /** The eyes look aside, thinking. */
    Look(4_000, 0, listOf(track(Prop.X, 0f to 0f, 0.2f to 0f, 0.35f to -5f, 0.6f to -5f, 0.75f to 0f, 1f to 0f))),

    /** The smallest thought bubble appears, floats and fades… */
    Bubble(4_000, 0, bubble()),

    /** …then the middle one… */
    Bubble2(4_000, 250, bubble()),

    /** …and the big one, with the question mark. */
    Bubble3(4_000, 500, bubble()),
    ;

    /** How far into its loop (0–1) the motion is at [timeMillis]. */
    fun fractionAt(timeMillis: Long): Float =
        Math.floorMod(timeMillis - delayMillis, periodMillis.toLong()).toFloat() / periodMillis
}

private fun bubble() = listOf(
    track(Prop.Alpha, 0f to 0f, 0.08f to 0f, 0.22f to 1f, 0.78f to 1f, 0.92f to 0f, 1f to 0f),
    track(Prop.Y, 0f to 6f, 0.08f to 6f, 0.22f to 0f, 0.5f to -3f, 0.78f to 0f, 0.92f to -6f, 1f to -6f),
)
