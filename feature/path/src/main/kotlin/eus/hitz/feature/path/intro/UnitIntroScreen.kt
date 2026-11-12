@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)

package eus.hitz.feature.path.intro

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.intl.LocaleList
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import eus.hitz.core.designsystem.component.HitzButton
import eus.hitz.core.designsystem.component.HitzButtonStyle
import eus.hitz.core.designsystem.component.HitzMascot
import eus.hitz.core.designsystem.icon.HitzIcons
import eus.hitz.core.designsystem.preview.HitzPreviews
import eus.hitz.core.designsystem.theme.HitzSpacing
import eus.hitz.core.designsystem.theme.HitzTheme
import eus.hitz.core.model.content.LocalizedText
import eus.hitz.feature.path.R
import eus.hitz.feature.path.resolve
import eus.hitz.feature.path.unitNumber

private val WORD_IMAGE = 48.dp
private val WORD_CARD = 156.dp

@Composable
fun UnitIntroScreen(
    onStart: () -> Unit,
    onOpenGuidebook: (unitId: String) -> Unit,
    onBack: () -> Unit,
    viewModel: UnitIntroViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    UnitIntroContent(
        state,
        onPlay = viewModel::play,
        onStart = onStart,
        onOpenGuidebook = onOpenGuidebook,
        onBack = onBack
    )
}

@Composable
internal fun UnitIntroContent(
    state: UnitIntroUiState,
    onPlay: (KeyWordUi) -> Unit,
    onStart: () -> Unit,
    onOpenGuidebook: (unitId: String) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    if (state is UnitIntroUiState.Ready) {
                        Text(stringResource(R.string.feature_path_unit, state.unitId.unitNumber()))
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(painterResource(HitzIcons.Close), stringResource(R.string.feature_path_back))
                    }
                },
            )
        },
        bottomBar = {
            if (state is UnitIntroUiState.Ready) {
                HitzButton(
                    text = stringResource(R.string.feature_path_start),
                    onClick = onStart,
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(HitzSpacing.lg),
                )
            }
        },
    ) { padding ->
        when (state) {
            UnitIntroUiState.Loading -> Box(Modifier.padding(padding).fillMaxSize(), Alignment.Center) {
                LoadingIndicator()
            }
            UnitIntroUiState.Error -> Box(Modifier.padding(padding).fillMaxSize(), Alignment.Center) {
                Text(stringResource(R.string.feature_path_error))
            }
            is UnitIntroUiState.Ready -> UnitIntroBody(
                state = state,
                onPlay = onPlay,
                onOpenGuidebook = { onOpenGuidebook(state.unitId) },
                modifier = Modifier.padding(padding),
            )
        }
    }
}

@Composable
private fun UnitIntroBody(
    state: UnitIntroUiState.Ready,
    onPlay: (KeyWordUi) -> Unit,
    onOpenGuidebook: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(HitzSpacing.lg),
        verticalArrangement = Arrangement.spacedBy(HitzSpacing.xl),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(HitzSpacing.lg)
        ) {
            HitzMascot(size = 72.dp, framed = true, contentDescription = null)
            Text(
                text = state.title.resolve(),
                style = MaterialTheme.typography.headlineMediumEmphasized,
                modifier = Modifier.weight(1f).semantics { heading() },
            )
        }
        GoalsSection(state.goals)
        if (state.keyWords.isNotEmpty()) {
            IntroSection(stringResource(R.string.feature_path_intro_words)) {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(HitzSpacing.sm),
                    verticalArrangement = Arrangement.spacedBy(HitzSpacing.sm),
                ) {
                    state.keyWords.forEach { word -> KeyWordCard(word, onPlay = { onPlay(word) }) }
                }
            }
        }
        if (state.grammar.isNotEmpty()) {
            IntroSection(stringResource(R.string.feature_path_intro_grammar)) {
                state.grammar.forEach { title ->
                    Text(
                        "• ${title.resolve()}",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                Spacer(Modifier.size(HitzSpacing.xs))
                HitzButton(
                    text = stringResource(R.string.feature_path_guidebook),
                    onClick = onOpenGuidebook,
                    style = HitzButtonStyle.Tonal,
                    icon = HitzIcons.Book,
                )
            }
        }
    }
}

@Composable
private fun GoalsSection(goals: List<LocalizedText>) {
    IntroSection(stringResource(R.string.feature_path_goals)) {
        goals.forEach { goal ->
            Row(horizontalArrangement = Arrangement.spacedBy(HitzSpacing.sm)) {
                Icon(
                    painter = painterResource(HitzIcons.Check),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                )
                Text(goal.resolve(), style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}

@Composable
private fun IntroSection(title: String, content: @Composable () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(HitzSpacing.sm)) {
        Text(title, style = MaterialTheme.typography.titleLarge, modifier = Modifier.semantics { heading() })
        content()
    }
}

@Composable
private fun KeyWordCard(word: KeyWordUi, onPlay: () -> Unit) {
    OutlinedCard(onClick = onPlay, modifier = Modifier.width(WORD_CARD)) {
        Row(
            modifier = Modifier.padding(HitzSpacing.sm),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(HitzSpacing.sm),
        ) {
            if (word.imageUri != null) {
                AsyncImage(
                    model = word.imageUri,
                    contentDescription = null,
                    modifier = Modifier
                        .size(WORD_IMAGE)
                        .clip(MaterialTheme.shapes.small),
                )
            } else {
                Surface(
                    modifier = Modifier.size(WORD_IMAGE),
                    shape = MaterialTheme.shapes.small,
                    color = MaterialTheme.colorScheme.secondaryContainer,
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(painterResource(HitzIcons.Speaker), contentDescription = null)
                    }
                }
            }
            Column(Modifier.weight(1f)) {
                Text(
                    text = word.eu,
                    style = MaterialTheme.typography.titleMedium.copy(localeList = LocaleList("eu")),
                )
                Text(
                    text = word.es,
                    style = MaterialTheme.typography.bodyMedium.copy(localeList = LocaleList("es")),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

internal object UnitIntroPreviewData {
    val ready = UnitIntroUiState.Ready(
        unitId = "u05-1",
        title = LocalizedText("Comida favorita", "Janari gogokoena"),
        goals = listOf(
            LocalizedText("Hablar de comidas y bebidas.", "Janari eta edariez hitz egin."),
            LocalizedText(
                "Decir lo que te gusta y lo que prefieres.",
                "Zer gustatzen zaizun eta zer nahiago duzun esan."
            ),
        ),
        keyWords = listOf("ogi" to "pan", "ur" to "agua", "esne" to "leche", "kafe" to "café", "janari" to "comida")
            .mapIndexed { index, (eu, es) -> KeyWordUi(eu, es, null, "u05-1.kw$index") },
        grammar = listOf(
            LocalizedText("Gustatu: me gusta, me gustan", "Gustatu: gustatzen zait"),
            LocalizedText("Nahi dut / nahiago dut", "Nahi dut / nahiago dut"),
        ),
    )
}

@HitzPreviews
@Composable
private fun UnitIntroPreview() {
    HitzTheme {
        UnitIntroContent(UnitIntroPreviewData.ready, onPlay = {}, onStart = {}, onOpenGuidebook = {}, onBack = {})
    }
}
