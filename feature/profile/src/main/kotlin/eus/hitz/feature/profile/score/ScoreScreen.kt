@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)

package eus.hitz.feature.profile.score

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import eus.hitz.core.designsystem.icon.HitzIcons
import eus.hitz.core.designsystem.preview.HitzPreviews
import eus.hitz.core.designsystem.theme.HitzSpacing
import eus.hitz.core.designsystem.theme.HitzTheme
import eus.hitz.core.domain.score.CanDo
import eus.hitz.core.domain.score.ObserveScoreUseCase
import eus.hitz.core.domain.score.Score
import eus.hitz.core.domain.score.ScorePoint
import eus.hitz.core.domain.score.ScoreRules
import eus.hitz.core.model.content.LocalizedText
import eus.hitz.feature.profile.R
import eus.hitz.feature.profile.resolve
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

private const val STOP_TIMEOUT_MILLIS = 5_000L

@HiltViewModel
class ScoreViewModel @Inject constructor(observeScore: ObserveScoreUseCase) : ViewModel() {
    /** Null while loading. */
    val score: StateFlow<Score?> =
        observeScore().stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS), null)
}

@Composable
fun ScoreScreen(onBack: () -> Unit, viewModel: ScoreViewModel = hiltViewModel()) {
    val score by viewModel.score.collectAsStateWithLifecycle()
    ScoreContent(score, onBack)
}

/** The score in detail: how it has grown, what it means and what the learner can already do. */
@Composable
internal fun ScoreContent(score: Score?, onBack: () -> Unit, modifier: Modifier = Modifier) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.feature_profile_score_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(painterResource(HitzIcons.Back), stringResource(R.string.feature_profile_back))
                    }
                },
            )
        },
    ) { padding ->
        if (score == null) {
            Box(Modifier.padding(padding).fillMaxSize(), Alignment.Center) { LoadingIndicator() }
        } else {
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(HitzSpacing.lg),
                verticalArrangement = Arrangement.spacedBy(HitzSpacing.xl),
            ) {
                ScoreSummary(score)
                Text(stringResource(score.band.description), style = MaterialTheme.typography.bodyLarge)
                Section(stringResource(R.string.feature_profile_score_history)) { ScoreChart(score.history) }
                Section(stringResource(R.string.feature_profile_score_can_do)) { CanDoList(score.canDo) }
                Section(stringResource(R.string.feature_profile_score_how)) {
                    Text(
                        text = stringResource(R.string.feature_profile_score_how_body, ScoreRules.MAX),
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
        }
    }
}

@Composable
private fun Section(title: String, content: @Composable () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(HitzSpacing.sm)) {
        Text(title, style = MaterialTheme.typography.titleLarge, modifier = Modifier.semantics { heading() })
        content()
    }
}

@Composable
private fun CanDoList(canDo: List<CanDo>) {
    if (canDo.isEmpty()) {
        Text(
            text = stringResource(R.string.feature_profile_score_can_do_empty),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        return
    }
    canDo.forEach { unit ->
        Column(verticalArrangement = Arrangement.spacedBy(HitzSpacing.xs)) {
            Text(unit.unitTitle.resolve(), style = MaterialTheme.typography.titleSmall)
            unit.goals.forEach { goal ->
                Row(horizontalArrangement = Arrangement.spacedBy(HitzSpacing.sm)) {
                    Icon(
                        painter = painterResource(HitzIcons.Check),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                    )
                    Text(goal.resolve(), style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}

internal object ScorePreviewData {
    private const val TODAY = 20_761L

    val score = Score(
        value = 7,
        progressToNext = 0.4f,
        gainedThisWeek = 3,
        history = listOf(
            ScorePoint(TODAY - 12, 1),
            ScorePoint(TODAY - 9, 2),
            ScorePoint(TODAY - 6, 4),
            ScorePoint(TODAY - 2, 6),
            ScorePoint(TODAY, 7),
        ),
        canDo = listOf(
            CanDo(
                "u01-1",
                LocalizedText("Saludos", "Agurrak"),
                listOf(
                    LocalizedText("Saludar y despedirte.", "Agurtu eta agur esan."),
                    LocalizedText("Decir tu nombre.", "Zure izena esan."),
                ),
            ),
        ),
    )

    val newLearner = Score(0, 0f, 0, listOf(ScorePoint(TODAY, 0)), emptyList())
}

@HitzPreviews
@Composable
private fun ScorePreview() {
    HitzTheme { ScoreContent(ScorePreviewData.score, onBack = {}) }
}
