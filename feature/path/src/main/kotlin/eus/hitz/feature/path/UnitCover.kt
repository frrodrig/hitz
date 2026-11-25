@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package eus.hitz.feature.path

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import eus.hitz.core.designsystem.component.CastMember
import eus.hitz.core.designsystem.component.HitzScene
import eus.hitz.core.designsystem.icon.HitzIcons
import eus.hitz.core.designsystem.preview.HitzPreviews
import eus.hitz.core.designsystem.theme.HitzSpacing
import eus.hitz.core.designsystem.theme.HitzTheme
import eus.hitz.core.designsystem.theme.raisedSurface
import eus.hitz.core.domain.path.NodeState
import eus.hitz.core.domain.path.PathUnit

private val CoverScene = 120.dp
private val SegmentWidth = 18.dp
private val SegmentHeight = 8.dp
private val SegmentGap = 3.dp
private val BarSegmentWidth = 10.dp
private val BarSegmentHeight = 6.dp
private const val LOCKED_ALPHA = 0.6f

/** Titles up to this length are set in display type; longer ones in a headline, to stay on two lines. */
private const val DISPLAY_TITLE_LENGTH = 12

/**
 * A unit's cover at the top of its stretch of the path: the unit's scene with its host (the one
 * who also presents its star challenge), its title, first goal, levels done and the guide. The
 * current unit's scene plays; locked units wait in grey on a neutral surface.
 */
@Composable
internal fun UnitCover(
    unit: PathUnit,
    isCurrentUnit: Boolean,
    onOpenGuidebook: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val scheme = MaterialTheme.colorScheme
    val unlocked = unit.isUnlocked
    Surface(
        shape = MaterialTheme.shapes.extraLarge,
        color = if (unlocked) scheme.secondaryContainer else scheme.surfaceContainer,
        contentColor = if (unlocked) scheme.onSecondaryContainer else scheme.onSurfaceVariant,
        modifier = modifier,
    ) {
        Row(
            modifier = Modifier.padding(
                start = HitzSpacing.xl,
                end = HitzSpacing.md,
                top = HitzSpacing.xl,
                bottom = HitzSpacing.xl,
            ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(HitzSpacing.sm),
        ) {
            CoverText(unit, onOpenGuidebook, Modifier.weight(1f))
            HitzScene(
                unitId = unit.id,
                host = CastMember.fromName(unit.challenge?.host.orEmpty()),
                size = CoverScene,
                animated = isCurrentUnit,
                modifier = if (unlocked) Modifier else Modifier.grayscale(),
            )
        }
    }
}

/** Levels done, title, first goal, and the guide beside the levels as segments. */
@Composable
private fun CoverText(unit: PathUnit, onOpenGuidebook: () -> Unit, modifier: Modifier = Modifier) {
    val done = unit.nodes.count { it.state == NodeState.Completed }
    val number = unit.id.unitNumber()
    val title = unit.title.resolve()
    Column(modifier, verticalArrangement = Arrangement.spacedBy(HitzSpacing.xs)) {
        Text(
            text = pluralStringResource(
                R.plurals.feature_path_unit_levels,
                unit.nodes.size,
                number,
                done,
                unit.nodes.size,
            ),
            style = MaterialTheme.typography.labelLarge,
        )
        Text(
            text = title,
            style = if (title.length <= DISPLAY_TITLE_LENGTH) {
                MaterialTheme.typography.displaySmall
            } else {
                MaterialTheme.typography.headlineMediumEmphasized
            },
            modifier = Modifier.semantics { heading() },
        )
        unit.goals.firstOrNull()?.let { goal ->
            Text(
                text = goal.resolve(),
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Spacer(Modifier.height(HitzSpacing.sm))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(HitzSpacing.md),
        ) {
            GuideButton(number, onOpenGuidebook)
            if (unit.isUnlocked) LevelSegments(done, unit.nodes.size)
        }
    }
}

/** The guide, a small white button on the cover: icon and "Guía", described with the unit. */
@Composable
private fun GuideButton(number: String, onClick: () -> Unit) {
    val description = stringResource(R.string.feature_path_guidebook_title, number)
    Button(
        onClick = onClick,
        shapes = ButtonDefaults.shapes(),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.raisedSurface,
            contentColor = MaterialTheme.colorScheme.onSurface,
        ),
        contentPadding = ButtonDefaults.ButtonWithIconContentPadding,
        modifier = Modifier.semantics { contentDescription = description },
    ) {
        Icon(painterResource(HitzIcons.Book), contentDescription = null, Modifier.size(ButtonDefaults.IconSize))
        Spacer(Modifier.width(ButtonDefaults.IconSpacing))
        Text(stringResource(R.string.feature_path_guidebook), Modifier.clearAndSetSemantics {})
    }
}

/** The unit's levels as segments, done ones in the path's walked colour. Decorative: the label says it. */
@Composable
internal fun LevelSegments(done: Int, total: Int, compact: Boolean = false) {
    val walked = MaterialTheme.colorScheme.secondary
    val ahead = MaterialTheme.colorScheme.raisedSurface
    val width = if (compact) BarSegmentWidth else SegmentWidth
    val height = if (compact) BarSegmentHeight else SegmentHeight
    Row(Modifier.clearAndSetSemantics {}, horizontalArrangement = Arrangement.spacedBy(SegmentGap)) {
        repeat(total) { index ->
            Box(
                Modifier
                    .size(width, height)
                    .background(if (index < done) walked else ahead, RoundedCornerShape(height / 2)),
            )
        }
    }
}

/**
 * The bar that takes the cover's place at the top of the screen once the cover has scrolled
 * away: unit number and title, levels done and the guide.
 */
@Composable
internal fun UnitBar(unit: PathUnit, onOpenGuidebook: () -> Unit, modifier: Modifier = Modifier) {
    val number = unit.id.unitNumber()
    val done = unit.nodes.count { it.state == NodeState.Completed }
    Surface(color = MaterialTheme.colorScheme.surface, shadowElevation = HitzSpacing.xxs, modifier = modifier) {
        Row(
            modifier = Modifier.padding(
                start = HitzSpacing.xl,
                end = HitzSpacing.lg,
                top = HitzSpacing.xs,
                bottom = HitzSpacing.xs,
            ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(HitzSpacing.md),
        ) {
            Column(Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.feature_path_unit, number),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text = unit.title.resolve(),
                    style = MaterialTheme.typography.titleMediumEmphasized,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            if (unit.isUnlocked) LevelSegments(done, unit.nodes.size, compact = true)
            FilledTonalIconButton(
                onClick = onOpenGuidebook,
                colors = IconButtonDefaults.filledTonalIconButtonColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                ),
            ) {
                Icon(
                    painterResource(HitzIcons.Book),
                    contentDescription = stringResource(R.string.feature_path_guidebook_title, number),
                )
            }
        }
    }
}

/** Draws the content in shades of grey, dimmed: a place still to be reached. */
internal fun Modifier.grayscale(): Modifier = graphicsLayer { alpha = LOCKED_ALPHA }.drawWithCache {
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
private fun UnitCoverPreview() {
    val units = PathPreviewData.path.sections.first().units
    HitzTheme {
        Surface(color = Color.Transparent) {
            Column(Modifier.padding(HitzSpacing.lg), verticalArrangement = Arrangement.spacedBy(HitzSpacing.lg)) {
                UnitCover(units[0], isCurrentUnit = true, onOpenGuidebook = {})
                UnitCover(units[1], isCurrentUnit = false, onOpenGuidebook = {})
                UnitBar(units[0], onOpenGuidebook = {})
            }
        }
    }
}
