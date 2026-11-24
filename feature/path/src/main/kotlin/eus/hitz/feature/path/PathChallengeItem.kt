package eus.hitz.feature.path

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import eus.hitz.core.designsystem.component.CastMember
import eus.hitz.core.designsystem.component.HitzMascot
import eus.hitz.core.designsystem.component.HitzStars
import eus.hitz.core.designsystem.component.MAX_STARS
import eus.hitz.core.designsystem.component.MascotPose
import eus.hitz.core.designsystem.preview.HitzPreviews
import eus.hitz.core.designsystem.theme.HitzSpacing
import eus.hitz.core.designsystem.theme.HitzTheme
import eus.hitz.core.domain.path.PathChallenge
import eus.hitz.core.model.content.LocalizedText

private val PedestalSize = 104.dp
private val HostSize = 112.dp
private val BorderWidth = 3.dp
private val LabelWidth = 136.dp
private const val LOCKED_ALPHA = 0.6f

/**
 * A unit's star challenge, beside the path: its host on a round pedestal, the best stars below and
 * a label. Locked, the host stands still in grey and the label says what unlocks it; available,
 * the host plays their greeting; with stars, they celebrate, on a golden pedestal for all three.
 */
@Composable
internal fun PathChallengeItem(challenge: PathChallenge, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val scheme = MaterialTheme.colorScheme
    val host = CastMember.fromName(challenge.host)
    val perfect = challenge.stars == MAX_STARS
    val (container, border) = when {
        !challenge.isAvailable -> scheme.surfaceContainerHighest to Color.Transparent
        perfect -> scheme.tertiaryContainer to scheme.tertiary
        else -> scheme.primaryContainer to scheme.primary
    }
    val description = challengeDescription(challenge)
    Column(
        modifier = modifier
            .widthIn(max = LabelWidth)
            .clearAndSetSemantics {
                contentDescription = description
                role = Role.Button
                if (challenge.isAvailable) {
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
        Surface(
            onClick = onClick,
            enabled = challenge.isAvailable,
            shape = CircleShape,
            color = container,
            border = BorderStroke(BorderWidth, border),
            modifier = Modifier.size(PedestalSize),
        ) {
            Box(contentAlignment = Alignment.BottomCenter) {
                HitzMascot(
                    pose = if (challenge.stars > 0) MascotPose.Bien else MascotPose.Hola,
                    member = host,
                    size = HostSize,
                    contentDescription = null,
                    animated = challenge.isAvailable,
                    modifier = if (challenge.isAvailable) Modifier else Modifier.grayscale(),
                )
            }
        }
        HitzStars(earned = challenge.stars)
        Text(
            text = stringResource(
                if (challenge.isAvailable) R.string.feature_path_challenge else R.string.feature_path_challenge_locked,
            ),
            style = MaterialTheme.typography.labelLarge,
            textAlign = TextAlign.Center,
            color = if (challenge.isAvailable) scheme.onSurface else scheme.onSurfaceVariant,
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

/** Draws the content in shades of grey, dimmed: a host waiting for the unit to be completed. */
private fun Modifier.grayscale(): Modifier = graphicsLayer { alpha = LOCKED_ALPHA }.drawWithCache {
    val paint = Paint().apply { colorFilter = ColorFilter.colorMatrix(ColorMatrix().apply { setToSaturation(0f) }) }
    onDrawWithContent {
        drawIntoCanvas { canvas ->
            canvas.saveLayer(Rect(Offset.Zero, size), paint)
            drawContent()
            canvas.restore()
        }
    }
}

@HitzPreviews
@Composable
private fun PathChallengeItemPreview() {
    val title = LocalizedText("Reto de estrellas", "Izarren erronka")
    fun challenge(host: String, available: Boolean, stars: Int) =
        PathChallenge("u01-1", "u01-1.l6", title, host, available, stars)
    HitzTheme {
        Surface {
            Row(Modifier.padding(HitzSpacing.lg), horizontalArrangement = Arrangement.spacedBy(HitzSpacing.md)) {
                PathChallengeItem(challenge("ane", available = false, stars = 0), {})
                PathChallengeItem(challenge("mikel", available = true, stars = 0), {})
                PathChallengeItem(challenge("maite", available = true, stars = 2), {})
                PathChallengeItem(challenge("unai", available = true, stars = 3), {})
            }
        }
    }
}
