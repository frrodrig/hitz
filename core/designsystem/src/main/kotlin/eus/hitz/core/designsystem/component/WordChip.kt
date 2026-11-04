package eus.hitz.core.designsystem.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
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

/**
 * A tappable word for word-bank exercises. When [used] is true the chip keeps its size as an
 * empty slot, so the word bank does not reflow while the learner builds the answer.
 */
@Composable
fun HitzWordChip(
    word: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    used: Boolean = false,
) {
    val scheme = MaterialTheme.colorScheme
    if (used) {
        val usedDescription = stringResource(R.string.core_designsystem_word_used, word)
        Surface(
            modifier = modifier
                .heightIn(min = 48.dp)
                .clearAndSetSemantics { contentDescription = usedDescription },
            shape = MaterialTheme.shapes.small,
            color = scheme.surfaceContainerHigh,
        ) {
            Text(word, Modifier.padding(horizontal = 14.dp, vertical = 12.dp).alpha(0f))
        }
    } else {
        Surface(
            onClick = onClick,
            modifier = modifier.heightIn(min = 48.dp),
            shape = MaterialTheme.shapes.small,
            color = scheme.surface,
            contentColor = scheme.onSurface,
            border = BorderStroke(2.dp, scheme.outlineVariant),
            shadowElevation = 1.dp,
        ) {
            Text(
                text = word,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            )
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
