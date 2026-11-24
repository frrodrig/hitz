package eus.hitz.core.designsystem.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import eus.hitz.core.designsystem.motion.rememberReducedMotion
import eus.hitz.core.designsystem.preview.HitzPreviews
import eus.hitz.core.designsystem.theme.HitzSpacing
import eus.hitz.core.designsystem.theme.HitzTheme

/** Side of a scene's square canvas, in its drawing's units. */
internal const val SCENE_SIZE = 200f

/**
 * A star challenge's scene, generated from `tools/design/art/scenes/` by
 * `tools/design/cast_to_vector.py`: [back] goes behind the host, [front] before them, and the host
 * stands at ([hostX], [hostY]) with [hostHeight], in the scene's coordinates.
 */
internal class SceneArt(
    val back: CastArt?,
    val front: CastArt?,
    val hostX: Float,
    val hostY: Float,
    val hostHeight: Float,
)

/**
 * The scene of the star challenge of [unitId] in a circle of [size]: the place of the unit's theme,
 * its [host] in [pose] and a prop with its own loop (steam from a pot, a pelota on the frontón…).
 * It is [animated] like the cast, and still with reduced motion. A unit without a scene shows the
 * host alone. Decorative: the caller describes it.
 */
@Composable
fun HitzScene(
    unitId: String,
    host: CastMember,
    modifier: Modifier = Modifier,
    pose: MascotPose = MascotPose.Hola,
    size: Dp = 136.dp,
    animated: Boolean = true,
) {
    val scene = SceneArtwork[unitId]
    val moving = animated && !rememberReducedMotion()
    val unit = size / SCENE_SIZE
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceContainerHighest),
    ) {
        scene?.back?.let { SceneLayer(it, moving) }
        HitzMascot(
            pose = pose,
            member = host,
            size = if (scene == null) size * HOST_ALONE else unit * scene.hostHeight,
            contentDescription = null,
            animated = moving,
            modifier = if (scene == null) {
                Modifier.align(Alignment.BottomCenter)
            } else {
                Modifier.offset(x = unit * scene.hostX, y = unit * scene.hostY)
            },
        )
        scene?.front?.let { SceneLayer(it, moving) }
    }
}

/** How much of the circle a host without a scene fills. */
private const val HOST_ALONE = 0.92f

@Composable
private fun BoxScope.SceneLayer(art: CastArt, moving: Boolean) {
    if (moving) {
        AnimatedCast(art, Modifier.matchParentSize())
    } else {
        Image(painterResource(art.still), contentDescription = null, modifier = Modifier.matchParentSize())
    }
}

@HitzPreviews
@Composable
private fun HitzScenePreview() {
    HitzTheme {
        Row(horizontalArrangement = Arrangement.spacedBy(HitzSpacing.sm)) {
            HitzScene("u05-1", CastMember.Maite, size = 112.dp)
            HitzScene("u09-1", CastMember.Mikel, size = 112.dp, pose = MascotPose.Bien)
            HitzScene("u12-1", CastMember.Unai, size = 112.dp)
            HitzScene("none", CastMember.Ane, size = 112.dp)
        }
    }
}
