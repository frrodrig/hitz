@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package eus.hitz.core.designsystem.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalButton
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
import eus.hitz.core.designsystem.R
import eus.hitz.core.designsystem.preview.HitzPreviews
import eus.hitz.core.designsystem.theme.HitzSpacing
import eus.hitz.core.designsystem.theme.HitzTheme

/**
 * A tappable word for word-bank exercises: a Material 3 Expressive tonal button. When [used] is
 * true it leaves an empty slot of the same size, so the bank does not reflow while the learner
 * builds the answer.
 */
@Composable
fun HitzWordChip(
    word: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    used: Boolean = false,
) {
    if (used) {
        val usedDescription = stringResource(R.string.core_designsystem_word_used, word)
        Surface(
            modifier = modifier.clearAndSetSemantics { contentDescription = usedDescription },
            shape = ButtonDefaults.shape,
            color = MaterialTheme.colorScheme.surfaceContainerHighest,
        ) {
            Text(
                text = word,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .padding(ButtonDefaults.contentPaddingFor(ButtonDefaults.MinHeight))
                    .alpha(0f),
            )
        }
    } else {
        FilledTonalButton(onClick = onClick, shapes = ButtonDefaults.shapes(), modifier = modifier) {
            Text(word, style = MaterialTheme.typography.bodyLarge)
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
