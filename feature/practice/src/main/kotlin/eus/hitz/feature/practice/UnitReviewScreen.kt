@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)

package eus.hitz.feature.practice

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.intl.LocaleList
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import eus.hitz.core.designsystem.component.HitzButton
import eus.hitz.core.designsystem.component.HitzButtonStyle
import eus.hitz.core.designsystem.icon.HitzIcons
import eus.hitz.core.designsystem.preview.HitzPreviews
import eus.hitz.core.designsystem.theme.HitzSpacing
import eus.hitz.core.designsystem.theme.HitzTheme
import eus.hitz.core.domain.practice.ReviewStory
import eus.hitz.core.domain.practice.ReviewWord
import eus.hitz.core.domain.practice.Strength
import eus.hitz.core.domain.practice.UnitReview
import eus.hitz.core.model.content.Example
import eus.hitz.core.model.content.GuidebookSection
import eus.hitz.core.model.content.LocalizedText

private const val PERCENT = 100
private const val BARS = 3
private val BAR_WIDTH = 12.dp
private val BAR_HEIGHT = 8.dp
private val FAB_SPACE = 96.dp

private enum class ReviewTab(val label: Int) {
    Words(R.string.feature_practice_tab_words),
    Guide(R.string.feature_practice_tab_guide),
    Phrases(R.string.feature_practice_tab_phrases),
    Story(R.string.feature_practice_tab_story),
}

@Composable
fun UnitReviewScreen(
    onBack: () -> Unit,
    onPractice: (unitId: String) -> Unit,
    onReadStory: (unitId: String, levelId: String) -> Unit,
    viewModel: UnitReviewViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    UnitReviewContent(state, onBack, onPractice, onReadStory, onPlay = viewModel::play)
}

/** One unit to review, by tabs: its words, guide, phrases and story. */
@Composable
internal fun UnitReviewContent(
    state: UnitReviewUiState,
    onBack: () -> Unit,
    onPractice: (unitId: String) -> Unit,
    onReadStory: (unitId: String, levelId: String) -> Unit,
    onPlay: (ReviewWord) -> Unit,
    modifier: Modifier = Modifier,
) {
    val review = (state as? UnitReviewUiState.Ready)?.review
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    review?.let {
                        Text(
                            stringResource(R.string.feature_practice_unit, it.unitId.unitNumber())
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(painterResource(HitzIcons.Back), stringResource(R.string.feature_practice_back))
                    }
                },
            )
        },
        floatingActionButton = {
            if (review != null && review.progress > 0f) {
                ExtendedFloatingActionButton(
                    onClick = { onPractice(review.unitId) },
                    icon = { Icon(painterResource(HitzIcons.Bolt), contentDescription = null) },
                    text = { Text(stringResource(R.string.feature_practice_practice_unit)) },
                )
            }
        },
    ) { padding ->
        when (state) {
            UnitReviewUiState.Loading -> Box(
                Modifier.padding(padding).fillMaxSize(),
                Alignment.Center
            ) { LoadingIndicator() }
            UnitReviewUiState.Error -> Box(Modifier.padding(padding).fillMaxSize(), Alignment.Center) {
                Text(stringResource(R.string.feature_practice_error))
            }
            is UnitReviewUiState.Ready -> ReviewBody(state.review, onReadStory, onPlay, Modifier.padding(padding))
        }
    }
}

@Composable
private fun ReviewBody(
    review: UnitReview,
    onReadStory: (unitId: String, levelId: String) -> Unit,
    onPlay: (ReviewWord) -> Unit,
    modifier: Modifier = Modifier,
) {
    val tabs = ReviewTab.entries.filter { it != ReviewTab.Story || review.story != null }
    var selected by rememberSaveable { mutableIntStateOf(0) }
    val tab = tabs[selected.coerceIn(tabs.indices)]
    Column(modifier.fillMaxSize()) {
        UnitHeader(review)
        PrimaryTabRow(selectedTabIndex = tabs.indexOf(tab)) {
            tabs.forEachIndexed { index, item ->
                Tab(selected = item == tab, onClick = { selected = index }, text = { Text(stringResource(item.label)) })
            }
        }
        LazyColumn(
            contentPadding = PaddingValues(
                start = HitzSpacing.lg,
                end = HitzSpacing.lg,
                top = HitzSpacing.lg,
                bottom = FAB_SPACE
            ),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            when (tab) {
                ReviewTab.Words -> words(review.words, onPlay)
                ReviewTab.Guide -> guide(review.guide)
                ReviewTab.Phrases -> phrases(review.phrases)
                ReviewTab.Story -> review.story?.let { story(it, onReadStory) }
            }
        }
    }
}

@Composable
private fun UnitHeader(review: UnitReview) {
    Surface(
        color = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        shape = MaterialTheme.shapes.extraLarge,
        modifier = Modifier.fillMaxWidth().padding(horizontal = HitzSpacing.lg, vertical = HitzSpacing.sm),
    ) {
        Column(Modifier.padding(HitzSpacing.xl), verticalArrangement = Arrangement.spacedBy(HitzSpacing.xs)) {
            Text(
                review.title.resolve(),
                style = MaterialTheme.typography.headlineSmallEmphasized,
                modifier = Modifier.semantics { heading() },
            )
            review.goals.firstOrNull()?.let { Text(it.resolve(), style = MaterialTheme.typography.bodyMedium) }
            Text(
                text = if (review.completed) {
                    stringResource(R.string.feature_practice_mastered)
                } else {
                    stringResource(R.string.feature_practice_unit_progress, (review.progress * PERCENT).toInt())
                },
                style = MaterialTheme.typography.labelLarge,
            )
        }
    }
}

private fun rowShape(index: Int, count: Int): RoundedCornerShape {
    val outer = 20.dp
    val inner = 6.dp
    return RoundedCornerShape(
        topStart = if (index == 0) outer else inner,
        topEnd = if (index == 0) outer else inner,
        bottomStart = if (index == count - 1) outer else inner,
        bottomEnd = if (index == count - 1) outer else inner,
    )
}

private fun LazyListScope.words(words: List<ReviewWord>, onPlay: (ReviewWord) -> Unit) {
    if (words.isEmpty()) {
        item { EmptyText(stringResource(R.string.feature_practice_no_words)) }
        return
    }
    itemsIndexed(words, key = { _, word -> word.clipId }) { index, word ->
        Surface(color = MaterialTheme.colorScheme.surfaceContainer, shape = rowShape(index, words.size)) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(start = HitzSpacing.lg, end = HitzSpacing.xs),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(HitzSpacing.md),
            ) {
                Column(Modifier.weight(1f).padding(vertical = HitzSpacing.md)) {
                    Text(
                        word.eu,
                        style = MaterialTheme.typography.titleMedium.copy(localeList = LocaleList("eu")),
                    )
                    Text(
                        word.es,
                        style = MaterialTheme.typography.bodyMedium.copy(localeList = LocaleList("es")),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                StrengthBars(word.strength)
                IconButton(onClick = { onPlay(word) }) {
                    Icon(
                        painterResource(HitzIcons.Speaker),
                        contentDescription = stringResource(R.string.feature_practice_listen, word.eu),
                        tint = MaterialTheme.colorScheme.secondary,
                    )
                }
            }
        }
    }
}

@Composable
private fun StrengthBars(strength: Strength) {
    val scheme = MaterialTheme.colorScheme
    val (filled, color, label) = when (strength) {
        Strength.Weak -> Triple(1, scheme.error, R.string.feature_practice_strength_weak)
        Strength.Medium -> Triple(2, scheme.tertiary, R.string.feature_practice_strength_medium)
        Strength.Strong -> Triple(BARS, scheme.primary, R.string.feature_practice_strength_strong)
    }
    val description = stringResource(label)
    Row(
        horizontalArrangement = Arrangement.spacedBy(3.dp),
        modifier = Modifier.clearAndSetSemantics { contentDescription = description },
    ) {
        repeat(BARS) { index ->
            Surface(
                color = if (index < filled) color else scheme.outlineVariant,
                shape = MaterialTheme.shapes.extraSmall,
                modifier = Modifier.size(BAR_WIDTH, BAR_HEIGHT),
            ) {}
        }
    }
}

private fun LazyListScope.guide(sections: List<GuidebookSection>) {
    itemsIndexed(sections) { index, section ->
        Surface(color = MaterialTheme.colorScheme.surfaceContainer, shape = rowShape(index, sections.size)) {
            Column(
                Modifier.fillMaxWidth().padding(HitzSpacing.lg),
                verticalArrangement = Arrangement.spacedBy(HitzSpacing.sm)
            ) {
                Text(
                    section.title.resolve(),
                    style = MaterialTheme.typography.titleMediumEmphasized,
                    modifier = Modifier.semantics { heading() },
                )
                Text(section.body.resolve(), style = MaterialTheme.typography.bodyMedium)
                section.examples.forEach { ExampleLine(it) }
            }
        }
    }
}

private fun LazyListScope.phrases(phrases: List<Example>) {
    itemsIndexed(phrases) { index, example ->
        Surface(color = MaterialTheme.colorScheme.surfaceContainer, shape = rowShape(index, phrases.size)) {
            Box(Modifier.fillMaxWidth().padding(HitzSpacing.lg)) { ExampleLine(example) }
        }
    }
}

@Composable
private fun ExampleLine(example: Example) {
    Column {
        Text(example.eu, style = MaterialTheme.typography.titleSmall.copy(localeList = LocaleList("eu")))
        Text(
            example.es,
            style = MaterialTheme.typography.bodyMedium.copy(localeList = LocaleList("es")),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

private fun LazyListScope.story(story: ReviewStory, onReadStory: (unitId: String, levelId: String) -> Unit) {
    item {
        Surface(color = MaterialTheme.colorScheme.surfaceContainer, shape = MaterialTheme.shapes.large) {
            Column(
                Modifier.fillMaxWidth().padding(HitzSpacing.lg),
                verticalArrangement = Arrangement.spacedBy(HitzSpacing.md)
            ) {
                Text(story.title.resolve(), style = MaterialTheme.typography.titleMediumEmphasized)
                if (story.completed) {
                    HitzButton(
                        text = stringResource(R.string.feature_practice_story_read),
                        onClick = { onReadStory(story.unitId, story.levelId) },
                        style = HitzButtonStyle.Tonal,
                        icon = HitzIcons.Book,
                    )
                } else {
                    Text(
                        stringResource(R.string.feature_practice_story_locked),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyText(text: String) {
    Text(
        text,
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(vertical = HitzSpacing.lg),
    )
}

internal object UnitReviewPreviewData {
    private fun text(es: String, eu: String) = LocalizedText(es, eu)

    val review = UnitReview(
        unitId = "u01-1",
        title = text("¡Hola!", "Kaixo!"),
        goals = listOf(text("Saludar y despedirse.", "Agurtu eta agur esan.")),
        progress = 1f,
        words = listOf(
            ReviewWord("kaixo", "hola", null, "u01-1.kw0", Strength.Strong),
            ReviewWord("egun on", "buenos días", null, "u01-1.kw1", Strength.Strong),
            ReviewWord("arratsalde on", "buenas tardes", null, "u01-1.kw2", Strength.Medium),
            ReviewWord("gabon", "buenas noches", null, "u01-1.l1.s1.e06", Strength.Weak),
        ),
        guide = listOf(
            GuidebookSection(
                text("Saludar según la hora", "Orduaren arabera agurtu"),
                text("«Egun on» por la mañana, «gabon» por la noche.", "Goizean «egun on», gauean «gabon»."),
                listOf(Example("Egun on, Mikel!", "¡Buenos días, Mikel!")),
            ),
        ),
        phrases = listOf(Example("Egun on, Mikel!", "¡Buenos días, Mikel!"), Example("Gabon!", "¡Buenas noches!")),
        story = ReviewStory("u01-1", "u01-1.l4", text("Primer día", "Lehen eguna"), completed = true),
    )
}

@HitzPreviews
@Composable
private fun UnitReviewPreview() {
    HitzTheme {
        UnitReviewContent(
            UnitReviewUiState.Ready(UnitReviewPreviewData.review),
            onBack = {},
            onPractice = {},
            onReadStory = { _, _ -> },
            onPlay = {},
        )
    }
}
