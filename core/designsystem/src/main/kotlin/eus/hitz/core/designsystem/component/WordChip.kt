@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package eus.hitz.core.designsystem.component

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.unit.dp
import eus.hitz.core.designsystem.R
import eus.hitz.core.designsystem.motion.HitzPopIn
import eus.hitz.core.designsystem.preview.HitzPreviews
import eus.hitz.core.designsystem.theme.HitzSpacing
import eus.hitz.core.designsystem.theme.HitzTheme
import eus.hitz.core.designsystem.theme.raisedSurface

/** Word tiles are 48 dp tall: a comfortable touch target and room for 200 % fonts to grow. */
val HitzWordTileHeight = 48.dp
private val TileCorner = 14.dp
private val TileShape = RoundedCornerShape(TileCorner)
private val TilePadding = PaddingValues(horizontal = 18.dp)
private val TileBorder = 1.dp

/** Word tiles are keys: they stand this much above their base and sink onto it when pressed. */
private val KeyDepth = 3.dp

/** The height a word key takes in a row, its base included. */
val HitzWordKeyHeight = HitzWordTileHeight + KeyDepth
private val SlotStroke = 1.5.dp
private val SlotDash = 6.dp
private val SlotGap = 4.dp

/**
 * A tappable word for word-bank exercises, drawn as a key: a tile on the lightest container with
 * a darker base under it, so it reads as something to press and move. Pressed, it sinks onto its
 * base with a fast spatial spring; it springs into place when it appears. When [used] is true it
 * leaves a dashed slot of the same size, so the bank does not reflow while the answer is built.
 */
@Composable
fun HitzWordChip(
    word: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    used: Boolean = false,
) {
    if (used) UsedSlot(word, modifier) else WordKey(word, onClick, modifier)
}

@Composable
private fun WordKey(word: String, onClick: () -> Unit, modifier: Modifier) {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val press by animateFloatAsState(
        targetValue = if (pressed) 1f else 0f,
        animationSpec = MaterialTheme.motionScheme.fastSpatialSpec(),
        label = "press",
    )
    val base = MaterialTheme.colorScheme.outlineVariant
    HitzPopIn(modifier) {
        Box(
            Modifier
                .drawBehind {
                    val depth = KeyDepth.toPx()
                    drawRoundRect(
                        color = base,
                        topLeft = Offset(0f, depth),
                        size = Size(size.width, size.height - depth),
                        cornerRadius = CornerRadius(TileCorner.toPx()),
                    )
                }
                .padding(bottom = KeyDepth),
        ) {
            Button(
                onClick = onClick,
                shape = TileShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.raisedSurface,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                ),
                border = BorderStroke(TileBorder, base),
                contentPadding = TilePadding,
                interactionSource = interaction,
                modifier = Modifier
                    .heightIn(min = HitzWordTileHeight)
                    .graphicsLayer { translationY = press * KeyDepth.toPx() },
            ) {
                Text(word, style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}

/** The place a used word leaves: a dashed outline as wide as the word, read as "used". */
@Composable
private fun UsedSlot(word: String, modifier: Modifier) {
    val usedDescription = stringResource(R.string.core_designsystem_word_used, word)
    val outline = MaterialTheme.colorScheme.outlineVariant
    Box(
        modifier = modifier
            .padding(bottom = KeyDepth)
            .heightIn(min = HitzWordTileHeight)
            .clearAndSetSemantics { contentDescription = usedDescription }
            .drawBehind {
                val stroke = SlotStroke.toPx()
                drawRoundRect(
                    color = outline,
                    topLeft = Offset(stroke / 2, stroke / 2),
                    size = Size(size.width - stroke, size.height - stroke),
                    cornerRadius = CornerRadius(TileCorner.toPx()),
                    style = Stroke(
                        width = stroke,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(SlotDash.toPx(), SlotGap.toPx())),
                    ),
                )
            }
            .padding(TilePadding),
        contentAlignment = Alignment.Center,
    ) {
        // Invisible, so the slot keeps the word's width.
        Text(word, style = MaterialTheme.typography.titleMedium, modifier = Modifier.alpha(0f))
    }
}

/** Lays out word chips in wrapping rows. */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HitzWordBank(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    FlowRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(HitzSpacing.sm, Alignment.CenterHorizontally),
        verticalArrangement = Arrangement.spacedBy(HitzSpacing.sm),
    ) { content() }
}

@HitzPreviews
@Composable
private fun HitzWordChipPreview() {
    HitzTheme {
        Surface {
            HitzWordBank(Modifier.padding(HitzSpacing.lg)) {
                listOf("Ni", "Ane", "naiz", "zu", "zara", "Bilbokoa").forEachIndexed { i, word ->
                    HitzWordChip(word, onClick = {}, used = i == 1)
                }
            }
        }
    }
}
