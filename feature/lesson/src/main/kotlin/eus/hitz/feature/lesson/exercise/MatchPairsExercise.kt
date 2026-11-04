@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package eus.hitz.feature.lesson.exercise

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.dp
import eus.hitz.core.designsystem.preview.HitzPreviews
import eus.hitz.core.designsystem.theme.HitzSpacing
import eus.hitz.core.designsystem.theme.HitzTheme
import eus.hitz.core.domain.lesson.MatchPairsState
import eus.hitz.core.domain.lesson.PairSide
import eus.hitz.core.model.content.Language
import eus.hitz.feature.lesson.ExerciseUi
import eus.hitz.feature.lesson.LessonAction
import eus.hitz.feature.lesson.R
import eus.hitz.feature.lesson.ui.LanguageText
import kotlin.random.Random

private enum class PairItemState { Idle, Selected, Matched, Mismatched }

/** Match Basque words (left) with their Spanish meaning (right). */
@Composable
internal fun MatchPairsExercise(
    exercise: ExerciseUi.MatchPairs,
    onAction: (LessonAction) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val state = exercise.state
    Row(modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(HitzSpacing.md)) {
        PairColumn(
            items = state.leftOrder.map { it to state.left[it] },
            language = Language.Basque,
            stateOf = { pair -> state.itemState(PairSide.Left, pair) },
            onSelect = { pair -> if (enabled) onAction(LessonAction.SelectPair(PairSide.Left, pair)) },
            modifier = Modifier.weight(1f),
        )
        PairColumn(
            items = state.rightOrder.map { it to state.right[it] },
            language = Language.Spanish,
            stateOf = { pair -> state.itemState(PairSide.Right, pair) },
            onSelect = { pair -> if (enabled) onAction(LessonAction.SelectPair(PairSide.Right, pair)) },
            modifier = Modifier.weight(1f),
        )
    }
}

private fun MatchPairsState.itemState(side: PairSide, pair: Int): PairItemState {
    val selected = if (side == PairSide.Left) selectedLeft else selectedRight
    val mismatched = lastMismatch?.let { (l, r) -> if (side == PairSide.Left) l == pair else r == pair } == true
    return when {
        pair in matched -> PairItemState.Matched
        selected == pair -> PairItemState.Selected
        mismatched -> PairItemState.Mismatched
        else -> PairItemState.Idle
    }
}

@Composable
private fun PairColumn(
    items: List<Pair<Int, String>>,
    language: Language,
    stateOf: (Int) -> PairItemState,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val matchedDescription = stringResource(R.string.feature_lesson_pair_matched)
    val selectedDescription = stringResource(R.string.feature_lesson_pair_selected)
    Column(modifier, verticalArrangement = Arrangement.spacedBy(HitzSpacing.sm)) {
        items.forEach { (pair, text) ->
            val itemState = stateOf(pair)
            val itemModifier = Modifier
                .fillMaxWidth()
                .heightIn(min = ButtonDefaults.MediumContainerHeight)
                .semantics {
                    when (itemState) {
                        PairItemState.Matched -> stateDescription = matchedDescription
                        PairItemState.Selected -> stateDescription = selectedDescription
                        else -> Unit
                    }
                }
            val label: @Composable () -> Unit = {
                LanguageText(text, language, style = MaterialTheme.typography.bodyLarge)
            }
            if (itemState == PairItemState.Selected) {
                FilledTonalButton(
                    onClick = { onSelect(pair) },
                    shapes = ButtonDefaults.shapes(),
                    modifier = itemModifier
                ) {
                    label()
                }
            } else {
                OutlinedButton(
                    onClick = { onSelect(pair) },
                    shapes = ButtonDefaults.shapes(),
                    modifier = itemModifier,
                    enabled = itemState != PairItemState.Matched,
                    border = if (itemState == PairItemState.Mismatched) {
                        BorderStroke(2.dp, MaterialTheme.colorScheme.error)
                    } else {
                        ButtonDefaults.outlinedButtonBorder(enabled = itemState != PairItemState.Matched)
                    },
                ) { label() }
            }
        }
    }
}

@HitzPreviews
@Composable
private fun MatchPairsExercisePreview() {
    val state = MatchPairsState
        .start(listOf("kaixo", "agur", "mesedez"), listOf("hola", "adiós", "por favor"), Random(1))
        .select(PairSide.Left, 0)
        .select(PairSide.Right, 0)
        .select(PairSide.Left, 1)
    HitzTheme {
        Surface {
            MatchPairsExercise(
                ExerciseUi.MatchPairs("preview", state),
                onAction = {},
                modifier = Modifier.padding(HitzSpacing.lg),
            )
        }
    }
}
