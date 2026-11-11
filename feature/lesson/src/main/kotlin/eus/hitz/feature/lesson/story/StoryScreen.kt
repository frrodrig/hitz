@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package eus.hitz.feature.lesson.story

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import eus.hitz.core.designsystem.component.ChoiceState
import eus.hitz.core.designsystem.component.GroupPosition
import eus.hitz.core.designsystem.component.HitzButton
import eus.hitz.core.designsystem.component.HitzChoiceCard
import eus.hitz.core.designsystem.component.HitzChoiceGroup
import eus.hitz.core.designsystem.icon.HitzIcons
import eus.hitz.core.designsystem.preview.HitzPreviews
import eus.hitz.core.designsystem.theme.HitzSpacing
import eus.hitz.core.designsystem.theme.HitzTheme
import eus.hitz.core.model.content.Language
import eus.hitz.feature.lesson.AudioClip
import eus.hitz.feature.lesson.LessonMessage
import eus.hitz.feature.lesson.LessonTopBar
import eus.hitz.feature.lesson.R
import eus.hitz.feature.lesson.ui.LanguageText
import eus.hitz.feature.lesson.ui.LocalGlossary
import kotlin.math.roundToInt

private const val PERCENT = 100

@Composable
fun StoryScreen(
    onClose: () -> Unit,
    viewModel: StoryViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                StoryEvent.Close -> onClose()
            }
        }
    }
    StoryContent(state = state, onAction = viewModel::onAction, onClose = onClose)
}

/** Stateless story UI, driven by [state]. */
@Composable
internal fun StoryContent(
    state: StoryUiState,
    onAction: (StoryAction) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
) {
    when (state) {
        StoryUiState.Loading -> Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) { LoadingIndicator() }
        StoryUiState.Error -> LessonMessage(
            title = stringResource(R.string.feature_lesson_story_error),
            body = null,
            action = stringResource(R.string.feature_lesson_finish),
            onAction = onClose,
            modifier = modifier,
        )
        is StoryUiState.Finished -> LessonMessage(
            title = stringResource(R.string.feature_lesson_story_finished_title),
            body = stringResource(
                R.string.feature_lesson_story_finished_accuracy,
                (state.accuracy * PERCENT).roundToInt(),
            ),
            action = stringResource(R.string.feature_lesson_continue),
            onAction = onClose,
            modifier = modifier,
            reward = state.reward,
        )
        is StoryUiState.Active -> CompositionLocalProvider(LocalGlossary provides state.glossary) {
            ActiveStory(state, onAction, modifier)
        }
    }
}

@Composable
private fun ActiveStory(
    state: StoryUiState.Active,
    onAction: (StoryAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val listState = rememberLazyListState()
    val itemCount = state.lines.size + 1 + if (state.question != null) 1 else 0
    LaunchedEffect(itemCount) { listState.animateScrollToItem(itemCount - 1) }
    Scaffold(
        modifier = modifier,
        topBar = {
            LessonTopBar(
                progress = state.progress,
                mistakesLeft = null,
                onClose = { onAction(StoryAction.Close) },
                closeLabel = stringResource(R.string.feature_lesson_story_close),
            )
        },
        bottomBar = {
            HitzButton(
                text = stringResource(R.string.feature_lesson_continue),
                onClick = { onAction(StoryAction.Continue) },
                enabled = state.canContinue,
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(HitzSpacing.lg),
            )
        },
    ) { padding ->
        LazyColumn(
            state = listState,
            modifier = Modifier.padding(padding).fillMaxSize(),
            contentPadding = PaddingValues(HitzSpacing.lg),
            verticalArrangement = Arrangement.spacedBy(HitzSpacing.lg),
        ) {
            item(key = "title") {
                LanguageText(
                    text = state.title,
                    language = Language.Basque,
                    style = MaterialTheme.typography.headlineMediumEmphasized,
                    modifier = Modifier.semantics { heading() },
                )
            }
            items(state.lines, key = { it.index }) { line ->
                StoryLine(line, translated = line.index in state.translated, onAction = onAction)
            }
            state.question?.let { question ->
                item(key = "question") { StoryQuestion(question, onAction) }
            }
        }
    }
}

/** A line of the story. Tapping the text shows its translation; the speaker button reads it. */
@Composable
private fun StoryLine(line: StoryLineUi, translated: Boolean, onAction: (StoryAction) -> Unit) {
    val toggleLabel = stringResource(R.string.feature_lesson_story_translation)
    Row(verticalAlignment = Alignment.Top) {
        Column(
            modifier = Modifier
                .weight(1f)
                .clickable(onClickLabel = toggleLabel) { onAction(StoryAction.ToggleTranslation(line.index)) },
            verticalArrangement = Arrangement.spacedBy(HitzSpacing.xs),
        ) {
            Text(line.speaker, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
            LanguageText(line.text, Language.Basque, style = MaterialTheme.typography.titleLarge, hints = true)
            if (translated) {
                LanguageText(
                    text = line.translation,
                    language = Language.Spanish,
                    style = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                )
            }
        }
        IconButton(onClick = { onAction(StoryAction.PlayLine(line.index)) }) {
            Icon(
                painterResource(HitzIcons.Speaker),
                contentDescription = stringResource(R.string.feature_lesson_listen)
            )
        }
    }
}

@Composable
private fun StoryQuestion(question: StoryQuestionUi, onAction: (StoryAction) -> Unit) {
    Surface(shape = MaterialTheme.shapes.large, color = MaterialTheme.colorScheme.surfaceContainer) {
        Column(Modifier.padding(HitzSpacing.lg), verticalArrangement = Arrangement.spacedBy(HitzSpacing.md)) {
            Text(question.prompt, style = MaterialTheme.typography.titleMedium)
            HitzChoiceGroup(Modifier.fillMaxWidth()) {
                question.options.forEachIndexed { index, option ->
                    val state = when {
                        index == question.correct -> ChoiceState.Correct
                        index in question.wrong -> ChoiceState.Incorrect
                        question.correct != null -> ChoiceState.Disabled
                        else -> ChoiceState.Default
                    }
                    HitzChoiceCard(
                        text = option,
                        state = state,
                        onClick = { onAction(StoryAction.Answer(index)) },
                        position = GroupPosition.of(index, question.options.size),
                    )
                }
            }
        }
    }
}

internal object StoryPreviewData {
    private val lines = listOf(
        Triple("Ane", "Egun on!", "¡Buenos días!"),
        Triple("Iker", "Kaixo! Zer moduz?", "¡Hola! ¿Qué tal?"),
        Triple("Ane", "Oso ondo, eskerrik asko.", "Muy bien, gracias."),
    ).mapIndexed { index, (speaker, eu, es) -> StoryLineUi(index, speaker, eu, es, AudioClip("story.$index", eu)) }

    val reading = StoryUiState.Active(
        title = "Lehen eguna",
        progress = 0.3f,
        lines = lines.take(2),
        translated = setOf(1)
    )

    val question = StoryUiState.Active(
        title = "Lehen eguna",
        progress = 0.5f,
        lines = lines,
        question = StoryQuestionUi("¿Cómo está Ane?", listOf("Regular", "Muy bien", "No lo dice"), wrong = setOf(0)),
    )
}

@HitzPreviews
@Composable
private fun StoryReadingPreview() {
    HitzTheme { StoryContent(StoryPreviewData.reading, onAction = {}, onClose = {}) }
}

@HitzPreviews
@Composable
private fun StoryQuestionPreview() {
    HitzTheme { StoryContent(StoryPreviewData.question, onAction = {}, onClose = {}) }
}
