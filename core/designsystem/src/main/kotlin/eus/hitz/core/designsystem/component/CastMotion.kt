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

/**
 * One drawing (a member in a pose, or part of a scene): the whole [still] drawable and its animated
 * layer tree, on a [width] × [height] canvas.
 */
internal class CastArt(
    @param:DrawableRes val still: Int,
    val nodes: List<CastNode>,
    val width: Float = CAST_WIDTH,
    val height: Float = CAST_HEIGHT,
)

/** A node of the layer tree that `tools/design/cast_to_vector.py` writes in `CastArt.kt`. */
internal sealed interface CastNode

/**
 * One vector covering the whole canvas. With a [motion], it moves around ([pivotX], [pivotY]), in
 * the drawing's coordinates.
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

private const val CAST_WIDTH = 240f
private const val CAST_HEIGHT = 280f

/**
 * A member alive: the layers stacked in order, each moved by its motion around its pivot. Every
 * motion reads one clock while drawing, so the animation never recomposes; a paused clock
 * (screenshot tests) draws the pose at rest.
 */
@Composable
internal fun AnimatedCast(art: CastArt, modifier: Modifier = Modifier) {
    val clock = rememberCastClock()
    Box(modifier) {
        art.nodes.forEach { CastNodeView(it, art, clock) }
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
private fun BoxScope.CastNodeView(node: CastNode, art: CastArt, clock: LongState) {
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
                        .graphicsLayer { move(motion, node.pivotX, node.pivotY, art, clock.longValue) }
                },
            )
        }
        is CastGroup -> Box(
            Modifier
                .matchParentSize()
                .graphicsLayer { move(node.motion, node.pivotX, node.pivotY, art, clock.longValue) },
        ) {
            node.children.forEach { CastNodeView(it, art, clock) }
        }
    }
}

private fun GraphicsLayerScope.move(motion: Motion, pivotX: Float, pivotY: Float, art: CastArt, timeMillis: Long) {
    val unit = size.width / art.width
    val fraction = motion.fractionAt(timeMillis)
    transformOrigin = TransformOrigin(pivotX / art.width, pivotY / art.height)
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

/** What a [Track] animates. Translations are in the drawing's units (a member's canvas is 240 wide). */
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
                1f to 0f,
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

    // ----- Scenes of the star challenges (tools/design/art/scenes/)
    /** Bunting or curtains stirring in the breeze. */
    Flutter(2_400, 0, listOf(track(Prop.Rotation, 0f to 0f, 0.25f to 3f, 0.75f to -3f, 1f to 0f))),

    /** Something hanging (a bell, a sign, a frame) swings. */
    Pendulum(2_000, 0, listOf(track(Prop.Rotation, 0f to 0f, 0.25f to 10f, 0.75f to -10f, 1f to 0f))),

    /** Something floats gently up and down. */
    Drift(2_400, 0, listOf(track(Prop.Y, 0f to 0f, 0.5f to -5f, 1f to 0f))),

    /** A sign beats like a heart. */
    Pulse(1_600, 0, listOf(track(Prop.Scale, 0f to 1f, 0.2f to 1.12f, 0.4f to 1f, 1f to 1f))),

    /** A speech bubble, a heart or confetti pops in, stays and goes. */
    Pop(2_400, 0, pop()),

    /** The same, half a loop later. */
    PopLate(2_400, 1_200, pop()),

    /** A rubber stamp lifts and comes down on the card. */
    Stamp(
        2_000,
        0,
        listOf(track(Prop.Y, 0f to 0f, 0.3f to -12f, 0.45f to 4f, 0.55f to 4f, 0.8f to 0f, 1f to 0f)),
    ),

    /** A pin or an apple hops and lands with a squash. */
    Bounce(
        1_400,
        0,
        listOf(
            Track(Prop.Y, listOf(key(0f, 0f), key(0.35f, -16f, EaseOut), key(0.7f, 0f, EaseIn), key(1f, 0f))),
            track(Prop.ScaleY, 0f to 1f, 0.7f to 1f, 0.76f to 0.86f, 0.84f to 1f, 1f to 1f, easing = LinearEasing),
        ),
    ),

    /** Smoke from a chimney rises, grows and fades. */
    Smoke(3_000, 0, rise(24f, grow = true)),

    /** A second puff, half a loop later. */
    SmokeLate(3_000, 1_500, rise(24f, grow = true)),

    /** Steam from a pot curls up and fades. */
    Steam(2_400, 0, rise(18f)),

    /** More steam, a third of a loop later… */
    SteamLate(2_400, 800, rise(18f)),

    /** …and the last wisp. */
    SteamLater(2_400, 1_600, rise(18f)),

    /** A pot lid rattles now and then. */
    Rattle(
        1_600,
        0,
        listOf(
            track(Prop.Rotation, 0f to 0f, 0.6f to 0f, 0.7f to -4f, 0.8f to 3f, 0.9f to -2f, 1f to 0f),
            track(Prop.Y, 0f to 0f, 0.6f to 0f, 0.7f to -2f, 0.9f to 0f, 1f to 0f),
        ),
    ),

    /** A tortilla flies up from the pan, turns over and lands. */
    Flip(
        1_800,
        0,
        listOf(
            Track(
                Prop.Y,
                listOf(key(0f, 0f), key(0.15f, 0f), key(0.4f, -34f, EaseOut), key(0.65f, 0f, EaseIn), key(1f, 0f))
            ),
            track(Prop.ScaleY, 0f to 1f, 0.15f to 1f, 0.4f to -1f, 0.65f to 1f, 1f to 1f, easing = LinearEasing),
        ),
    ),

    /** A gift's lid hops off and drops back. */
    Hop(
        2_400,
        0,
        listOf(
            track(Prop.Y, 0f to 0f, 0.2f to -14f, 0.6f to -14f, 0.75f to 0f, 1f to 0f),
            track(Prop.Rotation, 0f to 0f, 0.2f to -8f, 0.6f to -8f, 0.75f to 0f, 1f to 0f),
        ),
    ),

    /** A calendar page lifts and falls back. */
    PageLift(
        2_400,
        0,
        listOf(track(Prop.ScaleY, 0f to 1f, 0.3f to 1f, 0.5f to -1f, 0.7f to -1f, 0.9f to 1f, 1f to 1f)),
    ),

    /** A star circles a sore head: right, front, left, behind. */
    Orbit(2_000, 0, orbit()),

    /** A second star, opposite the first. */
    OrbitLate(2_000, 1_000, orbit()),

    /** A bottle flies in an arc into the bin and is gone. */
    Toss(
        2_600,
        0,
        listOf(
            track(Prop.X, 0f to 0f, 0.15f to 0f, 0.7f to 74f, 1f to 74f, easing = LinearEasing),
            Track(
                Prop.Y,
                listOf(key(0f, 0f), key(0.15f, 0f), key(0.42f, -50f, EaseOut), key(0.7f, -18f, EaseIn), key(1f, -18f)),
            ),
            track(Prop.Rotation, 0f to 0f, 0.15f to 0f, 0.7f to 200f, 1f to 200f, easing = LinearEasing),
            track(Prop.Alpha, 0f to 1f, 0.7f to 1f, 0.72f to 0f, 0.95f to 0f, 1f to 1f, easing = LinearEasing),
        ),
    ),

    /** A sprout grows and rests. */
    Grow(2_400, 0, listOf(track(Prop.Scale, 0f to 0.75f, 0.5f to 1f, 0.85f to 1f, 1f to 0.75f))),

    /** A drop falls from the watering can. */
    Drip(1_200, 0, drip()),

    /** Another drop, half a loop later. */
    DripLate(1_200, 600, drip()),

    /** A bus drives across the scene. */
    Drive(5_000, 0, listOf(track(Prop.X, 0f to 0f, 0.5f to 110f, 0.5001f to -220f, 1f to 0f, easing = LinearEasing))),

    /** A paper plane glides across, rising and dipping. */
    Glide(
        5_000,
        0,
        listOf(
            track(Prop.X, 0f to 0f, 0.4f to 120f, 0.4001f to -120f, 1f to 0f, easing = LinearEasing),
            track(Prop.Y, 0f to 0f, 0.2f to -8f, 0.4f to 0f, 0.7f to 6f, 1f to 0f),
        ),
    ),

    /** A hammer swings up and strikes. */
    Hammer(
        1_200,
        0,
        listOf(track(Prop.Rotation, 0f to 0f, 0.4f to -32f, 0.5f to 6f, 0.6f to 0f, 1f to 0f)),
    ),

    /** A magnifying glass looks over the page. */
    Search(
        4_000,
        0,
        listOf(
            track(Prop.X, 0f to 0f, 0.3f to -14f, 0.6f to 4f, 1f to 0f),
            track(Prop.Y, 0f to 0f, 0.3f to 8f, 0.6f to 18f, 1f to 0f),
        ),
    ),

    /** A pelota flies to the frontón's wall, hits the floor and comes back. */
    Rally(
        1_800,
        0,
        listOf(
            track(Prop.X, 0f to 0f, 0.33f to 60f, 0.66f to 30f, 1f to 0f, easing = LinearEasing),
            Track(
                Prop.Y,
                listOf(key(0f, 0f), key(0.33f, -14f, EaseOut), key(0.66f, 84f, EaseIn), key(1f, 0f, EaseOut))
            ),
        ),
    ),
    ;

    /** How far into its loop (0–1) the motion is at [timeMillis]. */
    fun fractionAt(timeMillis: Long): Float =
        Math.floorMod(timeMillis - delayMillis, periodMillis.toLong()).toFloat() / periodMillis
}

private fun pop() = listOf(
    track(Prop.Scale, 0f to 0.4f, 0.15f to 1.1f, 0.25f to 1f, 0.8f to 1f, 1f to 0.4f),
    track(Prop.Alpha, 0f to 0f, 0.12f to 1f, 0.8f to 1f, 1f to 0f),
)

/** Rises [height] and fades; with [grow], it swells as it goes. */
private fun rise(height: Float, grow: Boolean = false) = listOfNotNull(
    track(Prop.Alpha, 0f to 0f, 0.25f to 0.9f, 1f to 0f),
    track(Prop.Y, 0f to 6f, 1f to -height, easing = EaseOut),
    if (grow) track(Prop.Scale, 0f to 0.6f, 1f to 1.4f) else null,
)

private fun orbit() = listOf(
    track(Prop.X, 0f to 30f, 0.25f to 0f, 0.5f to -30f, 0.75f to 0f, 1f to 30f),
    track(Prop.Y, 0f to 0f, 0.25f to 7f, 0.5f to 0f, 0.75f to -7f, 1f to 0f),
    track(Prop.Scale, 0f to 1f, 0.25f to 1.25f, 0.5f to 1f, 0.75f to 0.7f, 1f to 1f),
)

private fun drip() = listOf(
    track(Prop.Y, 0f to 0f, 0.8f to 26f, 1f to 26f, easing = EaseIn),
    track(Prop.Alpha, 0f to 1f, 0.7f to 1f, 0.8f to 0f, 1f to 0f, easing = LinearEasing),
)

private fun bubble() = listOf(
    track(Prop.Alpha, 0f to 0f, 0.08f to 0f, 0.22f to 1f, 0.78f to 1f, 0.92f to 0f, 1f to 0f),
    track(Prop.Y, 0f to 6f, 0.08f to 6f, 0.22f to 0f, 0.5f to -3f, 0.78f to 0f, 0.92f to -6f, 1f to -6f),
)
