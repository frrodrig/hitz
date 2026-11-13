package eus.hitz.core.designsystem.component

import androidx.annotation.DrawableRes
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.InfiniteTransition
import androidx.compose.animation.core.KeyframesSpec
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.GraphicsLayerScope
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource

/**
 * One pose split into layers by `tools/design/mascot_to_vector.py`. Every layer covers the whole
 * 240 × 280 canvas, so stacked in this order they draw the full pose; each one moves on its own.
 */
internal class MascotLayers(
    @param:DrawableRes val shadow: Int,
    /** The makila and the left arm. */
    @param:DrawableRes val back: Int,
    @param:DrawableRes val body: Int,
    @param:DrawableRes val eyes: Int,
    /** Brows, txapela and hands in front of the body. */
    @param:DrawableRes val front: Int,
    /** The right arm, when it moves on its own. */
    @param:DrawableRes val arm: Int? = null,
    @param:DrawableRes val sparkles: Int? = null,
    @param:DrawableRes val thought: Int? = null,
)

private const val VIEW_WIDTH = 240f
private const val VIEW_HEIGHT = 280f

private fun pivot(x: Float, y: Float) = TransformOrigin(x / VIEW_WIDTH, y / VIEW_HEIGHT)

// Pivots, in the drawing's coordinates.
private val Feet = pivot(120f, 262f)
private val Ground = pivot(120f, 266f)
private val LeftShoulder = pivot(80f, 205f)
private val RightShoulder = pivot(160f, 204f)
private val SparklesCentre = pivot(120f, 110f)

// Idle: breathing and blinking, in every pose.
private const val BREATH_MILLIS = 1_800
private const val BREATH_STRETCH = 0.025f
private const val BREATH_SQUEEZE = 0.012f
private const val BLINK_EVERY = 4_200
private const val BLINK_CLOSED = 0.1f
private const val BLINK_MILLIS = 180

// Hola: waves right away, then every few seconds.
private const val WAVE_EVERY = 5_000
private const val WAVE_ANGLE = -18f
private const val WAVE_BACK = 4f

// Bien: jumps when he appears, wiggles his arms; the sparkles twinkle.
private const val JUMP_HEIGHT = 0.08f
private const val JUMP_UP_MILLIS = 200
private const val WIGGLE_MILLIS = 840
private const val WIGGLE_ANGLE = 6f
private const val TWINKLE_MILLIS = 700
private const val TWINKLE_FADE = 0.5f
private const val TWINKLE_SHRINK = 0.12f
private const val SHADOW_SHRINK = 0.25f

// Ups: shakes his head once.
private const val SHAKE_MILLIS = 650

// Piensa: sways slowly while the thought bubbles float.
private const val SWAY_MILLIS = 5_200
private const val SWAY_ANGLE = 2.5f
private const val FLOAT_MILLIS = 1_400
private const val FLOAT_HEIGHT = 0.02f
private const val FLOAT_FADE = 0.3f

/** Everything that moves, as states read while drawing, so the animation never recomposes. */
private class MascotMotion(
    val breath: State<Float>,
    val blink: State<Float>,
    /** The arms' angle: Hola's right arm waves, Bien's arms wiggle in turn. */
    val swing: State<Float>,
    /** 0 → 1 → 0: Bien's sparkles twinkle and Piensa's thoughts float with it. */
    val pulse: State<Float>,
    val sway: State<Float>,
    val lift: Animatable<Float, *>,
    val shake: Animatable<Float, *>,
)

/**
 * The Galtzagorri's motion: he breathes and blinks in every pose, and each pose adds its own
 * gesture. Every value starts at rest, so a paused clock (screenshots) draws the static pose.
 */
@Composable
private fun rememberMascotMotion(pose: MascotPose): MascotMotion {
    val idle = rememberInfiniteTransition(label = "mascot")
    val breath = idle.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(BREATH_MILLIS, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "breath",
    )
    val blink = idle.loop(pose != MascotPose.Bien, BLINK_EVERY, "blink", rest = 1f) {
        1f at BLINK_EVERY - BLINK_MILLIS
        BLINK_CLOSED at BLINK_EVERY - BLINK_MILLIS / 2
    }
    val swing = when (pose) {
        MascotPose.Hola -> idle.loop(enabled = true, WAVE_EVERY, "wave") {
            WAVE_ANGLE at 250
            WAVE_BACK at 500
            WAVE_ANGLE at 750
            WAVE_BACK at 1_000
            0f at 1_250
        }
        else -> idle.loop(pose == MascotPose.Bien, WIGGLE_MILLIS, "wiggle") {
            WIGGLE_ANGLE at WIGGLE_MILLIS / 4
            0f at WIGGLE_MILLIS / 2
            -WIGGLE_ANGLE at WIGGLE_MILLIS * 3 / 4
        }
    }
    val half = if (pose == MascotPose.Bien) TWINKLE_MILLIS else FLOAT_MILLIS
    val pulse = idle.loop(pose == MascotPose.Bien || pose == MascotPose.Piensa, half * 2, "pulse") { 1f at half }
    val sway = idle.loop(pose == MascotPose.Piensa, SWAY_MILLIS, "sway") {
        SWAY_ANGLE at SWAY_MILLIS / 4
        0f at SWAY_MILLIS / 2
        -SWAY_ANGLE at SWAY_MILLIS * 3 / 4
    }
    val lift = remember(pose) { Animatable(0f) }
    val shake = remember(pose) { Animatable(0f) }
    LaunchedEffect(pose) { enter(pose, lift, shake) }
    return MascotMotion(breath, blink, swing, pulse, sway, lift, shake)
}

/** Bien jumps and Ups shakes his head when they appear. */
private suspend fun enter(pose: MascotPose, lift: Animatable<Float, *>, shake: Animatable<Float, *>) {
    when (pose) {
        MascotPose.Bien -> {
            lift.animateTo(1f, tween(JUMP_UP_MILLIS, easing = FastOutSlowInEasing))
            lift.animateTo(0f, spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessMediumLow))
        }
        MascotPose.Ups -> shake.animateTo(
            0f,
            keyframes {
                durationMillis = SHAKE_MILLIS
                -7f at 90
                6f at 220
                -4f at 360
                2f at 500
            },
        )
        else -> Unit
    }
}

/** The Galtzagorri alive: his layers stacked, each moved around its own pivot. */
@Composable
internal fun AnimatedMascot(pose: MascotPose, modifier: Modifier = Modifier) {
    val layers = pose.layers
    val motion = rememberMascotMotion(pose)
    val eyesPivot = pivot(120f, if (pose == MascotPose.Piensa) 122f else 125f)
    Box(modifier) {
        Layer(layers.shadow) {
            transformOrigin = Ground
            scaleX = 1f - SHADOW_SHRINK * motion.lift.value
            scaleY = scaleX
        }
        Box(
            Modifier
                .matchParentSize()
                .graphicsLayer {
                    transformOrigin = Feet
                    scaleY = 1f + BREATH_STRETCH * motion.breath.value
                    scaleX = 1f - BREATH_SQUEEZE * motion.breath.value
                    translationY = -JUMP_HEIGHT * size.height * motion.lift.value
                    rotationZ = motion.sway.value + motion.shake.value
                },
        ) {
            layers.sparkles?.let {
                Layer(it) {
                    transformOrigin = SparklesCentre
                    alpha = 1f - TWINKLE_FADE * motion.pulse.value
                    scaleX = 1f - TWINKLE_SHRINK * motion.pulse.value
                    scaleY = scaleX
                }
            }
            Layer(layers.back) {
                transformOrigin = LeftShoulder
                // Bien wiggles both arms in turn; the makila goes with the left hand.
                if (pose == MascotPose.Bien) rotationZ = -motion.swing.value
            }
            layers.arm?.let {
                Layer(it) {
                    transformOrigin = RightShoulder
                    rotationZ = motion.swing.value
                }
            }
            Layer(layers.body)
            Layer(layers.eyes) {
                transformOrigin = eyesPivot
                scaleY = motion.blink.value
            }
            Layer(layers.front)
            layers.thought?.let {
                Layer(it) {
                    translationY = -FLOAT_HEIGHT * size.height * motion.pulse.value
                    alpha = 1f - FLOAT_FADE * motion.pulse.value
                }
            }
        }
    }
}

/**
 * A looping keyframe animation that starts and ends at [rest]; when [enabled] is false it stays
 * there, so each pose only runs its own gestures.
 */
@Composable
private fun InfiniteTransition.loop(
    enabled: Boolean,
    durationMillis: Int,
    label: String,
    rest: Float = 0f,
    frames: KeyframesSpec.KeyframesSpecConfig<Float>.() -> Unit,
): State<Float> = animateFloat(
    initialValue = rest,
    targetValue = rest,
    animationSpec = if (enabled) {
        infiniteRepeatable(
            keyframes {
                this.durationMillis = durationMillis
                frames()
            },
        )
    } else {
        infiniteRepeatable(tween(durationMillis))
    },
    label = label,
)

@Composable
private fun BoxScope.Layer(
    @DrawableRes drawable: Int,
    transform: GraphicsLayerScope.() -> Unit = {},
) {
    Image(
        painter = painterResource(drawable),
        contentDescription = null,
        modifier = Modifier
            .matchParentSize()
            .graphicsLayer(transform),
    )
}
