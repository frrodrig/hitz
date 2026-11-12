@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package eus.hitz.core.designsystem.component

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.unit.dp
import eus.hitz.core.designsystem.R
import eus.hitz.core.designsystem.preview.HitzPreviews
import eus.hitz.core.designsystem.theme.HitzSpacing
import eus.hitz.core.designsystem.theme.HitzTheme

/** Word tiles are 48 dp tall: a comfortable touch target and room for 200 % fonts to grow. */
val HitzWordTileHeight = 48.dp
private val TileShape = RoundedCornerShape(16.dp)
private val TilePadding = PaddingValues(horizontal = 20.dp)
private val TileBorder = 2.dp

/**
 * A tappable word for word-bank exercises: a raised tile on the lightest container with an
 * outline, so words read as pieces to move. When [used] is true it leaves an empty slot of the
 * same size, so the bank does not reflow while the learner builds the answer.
 */
@Composable
fun HitzWordChip(
    word: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    used: Boolean = false,
) {
    val sized = modifier.heightIn(min = HitzWordTileHeight)
    if (used) {
        val usedDescription = stringResource(R.string.core_designsystem_word_used, word)
        Surface(
            modifier = sized.clearAndSetSemantics { contentDescription = usedDescription },
            shape = TileShape,
            color = MaterialTheme.colorScheme.surfaceContainerHighest,
        ) {
            Box(Modifier.heightIn(min = HitzWordTileHeight).padding(TilePadding), contentAlignment = Alignment.Center) {
                Text(word, style = MaterialTheme.typography.titleMedium, modifier = Modifier.alpha(0f))
            }
        }
    } else {
        Button(
            onClick = onClick,
            shape = TileShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                contentColor = MaterialTheme.colorScheme.onSurface,
            ),
            border = BorderStroke(TileBorder, MaterialTheme.colorScheme.outlineVariant),
            contentPadding = TilePadding,
            modifier = sized,
        ) {
            Text(word, style = MaterialTheme.typography.titleMedium)
        }
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
