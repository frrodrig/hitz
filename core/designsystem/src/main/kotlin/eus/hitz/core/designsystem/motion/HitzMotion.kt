@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package eus.hitz.core.designsystem.motion

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MotionScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * The app's motion scheme: Material 3 Expressive springs. Spatial springs (position, size, shape,
 * rotation) overshoot a little and settle; effects springs (colour, opacity) never overshoot.
 * Fast suits small components, default parts of the screen, slow whole screens.
 */
val HitzMotionScheme: MotionScheme = MotionScheme.expressive()

/**
 * The standard scheme eases in without bouncing. Whole screens that slide in from an edge use
 * it, so the overshoot never opens a gap at the opposite edge.
 */
private val CalmMotionScheme: MotionScheme = MotionScheme.standard()

/** A detail screen comes in from this fraction of the width, not from the edge. */
private const val AXIS_FRACTION = 5
private const val TOP_LEVEL_INITIAL_SCALE = 0.96f

/**
 * Navigation transitions. Each kind of destination moves along its own axis, so the learner
 * can tell where they are going:
 *
 * - **Top level** (Aprender, Practicar, Perfil): fade through, the new screen growing slightly.
 * - **Detail** (guide, unit introduction, score, unit review): forward along the horizontal
 *   axis; back reverses it, and predictive back scrubs it.
 * - **Session** (lessons, stories, placement): rises from the bottom over the path, which stays
 *   still underneath, and sinks back when closed.
 */
object HitzTransitions {
    val topLevelEnter: EnterTransition
        get() = fadeIn(HitzMotionScheme.defaultEffectsSpec()) +
            scaleIn(HitzMotionScheme.defaultSpatialSpec(), initialScale = TOP_LEVEL_INITIAL_SCALE)

    val topLevelExit: ExitTransition
        get() = fadeOut(HitzMotionScheme.fastEffectsSpec())

    val forwardEnter: EnterTransition
        get() = slideInHorizontally(HitzMotionScheme.defaultSpatialSpec<IntOffset>()) { it / AXIS_FRACTION } +
            fadeIn(HitzMotionScheme.defaultEffectsSpec())

    val forwardExit: ExitTransition
        get() = slideOutHorizontally(HitzMotionScheme.defaultSpatialSpec<IntOffset>()) { -it / AXIS_FRACTION } +
            fadeOut(HitzMotionScheme.fastEffectsSpec())

    val backwardEnter: EnterTransition
        get() = slideInHorizontally(HitzMotionScheme.defaultSpatialSpec<IntOffset>()) { -it / AXIS_FRACTION } +
            fadeIn(HitzMotionScheme.defaultEffectsSpec())

    val backwardExit: ExitTransition
        get() = slideOutHorizontally(HitzMotionScheme.defaultSpatialSpec<IntOffset>()) { it / AXIS_FRACTION } +
            fadeOut(HitzMotionScheme.fastEffectsSpec())

    val sessionEnter: EnterTransition
        get() = slideInVertically(CalmMotionScheme.slowSpatialSpec<IntOffset>()) { it } +
            fadeIn(HitzMotionScheme.fastEffectsSpec())

    val sessionExit: ExitTransition
        get() = slideOutVertically(CalmMotionScheme.defaultSpatialSpec<IntOffset>()) { it } +
            fadeOut(HitzMotionScheme.defaultEffectsSpec())

    /** The screen under a session keeps still while the session covers it, then disappears. */
    val underSessionExit: ExitTransition
        get() = fadeOut(HitzMotionScheme.slowEffectsSpec())

    /** The screen under a session is already in place when the session sinks away. */
    val underSessionEnter: EnterTransition
        get() = fadeIn(HitzMotionScheme.fastEffectsSpec())

    /*
     * The navigation bar and rail make way for full-screen destinations. Their size uses effects
     * springs: a size that overshot would go below zero on the way out.
     */

    val navigationBarEnter: EnterTransition
        get() = expandVertically(HitzMotionScheme.defaultEffectsSpec()) + fadeIn(HitzMotionScheme.defaultEffectsSpec())

    val navigationBarExit: ExitTransition
        get() = shrinkVertically(HitzMotionScheme.defaultEffectsSpec()) + fadeOut(HitzMotionScheme.fastEffectsSpec())

    val navigationRailEnter: EnterTransition
        get() = expandHorizontally(HitzMotionScheme.defaultEffectsSpec()) +
            fadeIn(HitzMotionScheme.defaultEffectsSpec())

    val navigationRailExit: ExitTransition
        get() = shrinkHorizontally(HitzMotionScheme.defaultEffectsSpec()) +
            fadeOut(HitzMotionScheme.fastEffectsSpec())
}

/** Pause between two items revealed one after the other. */
private const val REVEAL_STAGGER_MILLIS = 70L
private val RevealOffset = 24.dp

/**
 * Shows [content] by lifting it into place and fading it in, [order] steps after the screen
 * appears; a run of these, numbered in reading order, builds a screen up in sequence. Used for
 * hero moments only (the end of a lesson, a unit's cover), not for everyday content.
 *
 * With animations removed, and in previews, the content is simply there.
 */
@Composable
fun HitzReveal(order: Int, modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    val still = rememberReducedMotion() || LocalInspectionMode.current
    val lift = remember { Animatable(if (still) 0f else 1f) }
    val opacity = remember { Animatable(if (still) 1f else 0f) }
    val liftSpec = MaterialTheme.motionScheme.slowSpatialSpec<Float>()
    val opacitySpec = MaterialTheme.motionScheme.defaultEffectsSpec<Float>()
    LaunchedEffect(Unit) {
        if (opacity.value < 1f) {
            delay(order * REVEAL_STAGGER_MILLIS)
            coroutineScope {
                launch { lift.animateTo(0f, liftSpec) }
                launch { opacity.animateTo(1f, opacitySpec) }
            }
        }
    }
    val offset = with(LocalDensity.current) { RevealOffset.toPx() }
    Box(
        modifier.graphicsLayer {
            alpha = opacity.value
            translationY = lift.value * offset
        },
    ) {
        content()
    }
}

private const val POP_IN_SCALE = 0.85f

/**
 * Shows [content] with a small spring, growing from slightly smaller and fading in: for pieces
 * that appear where the learner put them (a word tile landing in the answer, a tile coming back
 * to the bank). Fast spatial spring for the size, fast effects for the opacity. With animations
 * removed, and in previews, the content is simply there.
 */
@Composable
fun HitzPopIn(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    val still = rememberReducedMotion() || LocalInspectionMode.current
    val scale = remember { Animatable(if (still) 1f else POP_IN_SCALE) }
    val opacity = remember { Animatable(if (still) 1f else 0f) }
    val scaleSpec = MaterialTheme.motionScheme.fastSpatialSpec<Float>()
    val opacitySpec = MaterialTheme.motionScheme.fastEffectsSpec<Float>()
    LaunchedEffect(Unit) {
        if (opacity.value < 1f) {
            coroutineScope {
                launch { scale.animateTo(1f, scaleSpec) }
                launch { opacity.animateTo(1f, opacitySpec) }
            }
        }
    }
    Box(
        modifier.graphicsLayer {
            scaleX = scale.value
            scaleY = scale.value
            alpha = opacity.value
        },
    ) {
        content()
    }
}
