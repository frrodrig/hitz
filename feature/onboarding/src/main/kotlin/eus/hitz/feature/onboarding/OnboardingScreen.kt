@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package eus.hitz.feature.onboarding

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import eus.hitz.core.designsystem.component.HitzButton
import eus.hitz.core.designsystem.component.HitzMascot
import eus.hitz.core.designsystem.icon.HitzIcons
import eus.hitz.core.designsystem.preview.HitzPreviews
import eus.hitz.core.designsystem.theme.HitzSpacing
import eus.hitz.core.designsystem.theme.HitzTheme
import eus.hitz.core.domain.gamification.DailyGoalRules
import eus.hitz.core.model.content.LocalizedText
import kotlinx.coroutines.launch

private const val PAGES = 5
private const val GOAL_PAGE = 3
private const val LEVEL_PAGE = PAGES - 1

/** The learner's answer to "have you studied Basque before?". */
enum class StartingLevel { Beginner, SomeBasque }

/**
 * First-run introduction. [onFinish] gets the learner's level: beginners go to the path, the others
 * to the placement test of the first section.
 */
@Composable
fun OnboardingScreen(
    onFinish: (StartingLevel) -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel(),
) {
    val sections by viewModel.sections.collectAsStateWithLifecycle()
    OnboardingContent(
        sections = sections,
        onFinish = { level, goalMinutes ->
            viewModel.complete(goalMinutes)
            onFinish(level)
        },
    )
}

/** [onFinish] gets the level and the daily goal in minutes (the default one when skipped). */
@Composable
internal fun OnboardingContent(
    sections: List<SectionUi>,
    onFinish: (StartingLevel, goalMinutes: Int) -> Unit,
    modifier: Modifier = Modifier,
    pagerState: PagerState = rememberPagerState { PAGES },
) {
    val scope = rememberCoroutineScope()
    var level by rememberSaveable { mutableStateOf<StartingLevel?>(null) }
    var goal by rememberSaveable { mutableIntStateOf(DailyGoalRules.DEFAULT_GOAL_MINUTES) }
    Column(
        modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .statusBarsPadding()
            .navigationBarsPadding(),
    ) {
        HorizontalPager(state = pagerState, modifier = Modifier.weight(1f)) { page ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(HitzSpacing.xl),
                verticalArrangement = Arrangement.spacedBy(HitzSpacing.lg),
            ) {
                when (page) {
                    0 -> WelcomePage()
                    1 -> LearnPage(sections)
                    2 -> HowPage()
                    GOAL_PAGE -> GoalPage(goal, onSelect = { goal = it })
                    else -> LevelPage(level, onSelect = { level = it })
                }
            }
        }
        PageIndicator(pagerState.currentPage, Modifier.align(Alignment.CenterHorizontally))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(HitzSpacing.lg),
            horizontalArrangement = Arrangement.spacedBy(HitzSpacing.md),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (pagerState.currentPage < LEVEL_PAGE) {
                TextButton(onClick = { onFinish(StartingLevel.Beginner, goal) }) {
                    Text(stringResource(R.string.feature_onboarding_skip))
                }
                HitzButton(
                    text = stringResource(R.string.feature_onboarding_next),
                    onClick = { scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) } },
                    modifier = Modifier.weight(1f),
                )
            } else {
                HitzButton(
                    text = stringResource(R.string.feature_onboarding_start),
                    onClick = { level?.let { onFinish(it, goal) } },
                    enabled = level != null,
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun PageTitle(@StringRes title: Int) {
    Text(
        text = stringResource(title),
        style = MaterialTheme.typography.headlineMediumEmphasized,
        modifier = Modifier.semantics { heading() },
    )
}

@Composable
private fun WelcomePage() {
    Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) { HitzMascot(size = 160.dp) }
    Text(
        text = stringResource(R.string.feature_onboarding_welcome_title),
        style = MaterialTheme.typography.headlineMediumEmphasized,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .fillMaxWidth()
            .semantics { heading() },
    )
    Text(
        text = stringResource(R.string.feature_onboarding_welcome_body),
        style = MaterialTheme.typography.bodyLarge,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth(),
    )
}

@Composable
private fun LearnPage(sections: List<SectionUi>) {
    PageTitle(R.string.feature_onboarding_learn_title)
    Text(stringResource(R.string.feature_onboarding_learn_body), style = MaterialTheme.typography.bodyLarge)
    sections.forEachIndexed { index, section ->
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.large,
            color = MaterialTheme.colorScheme.surfaceContainer,
        ) {
            Column(Modifier.padding(HitzSpacing.lg), verticalArrangement = Arrangement.spacedBy(HitzSpacing.xs)) {
                Text(
                    text = stringResource(R.string.feature_onboarding_section, index + 1, section.title.resolve()),
                    style = MaterialTheme.typography.titleMedium,
                )
                Text(section.description.resolve(), style = MaterialTheme.typography.bodyMedium)
                Text(
                    text = pluralStringResource(R.plurals.feature_onboarding_units, section.units, section.units),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        }
    }
}

private data class Feature(
    @param:DrawableRes val icon: Int,
    @param:StringRes val title: Int,
    @param:StringRes val body: Int,
)

private val features = listOf(
    Feature(
        HitzIcons.Path,
        R.string.feature_onboarding_how_lessons_title,
        R.string.feature_onboarding_how_lessons_body
    ),
    Feature(HitzIcons.Book, R.string.feature_onboarding_how_words_title, R.string.feature_onboarding_how_words_body),
    Feature(HitzIcons.Star, R.string.feature_onboarding_how_goal_title, R.string.feature_onboarding_how_goal_body),
    Feature(
        HitzIcons.Trophy,
        R.string.feature_onboarding_how_review_title,
        R.string.feature_onboarding_how_review_body
    ),
)

@Composable
private fun HowPage() {
    PageTitle(R.string.feature_onboarding_how_title)
    features.forEach { feature ->
        Row(horizontalArrangement = Arrangement.spacedBy(HitzSpacing.lg)) {
            Surface(
                modifier = Modifier.size(48.dp),
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.secondaryContainer,
            ) {
                Box(
                    contentAlignment = Alignment.Center
                ) { Icon(painterResource(feature.icon), contentDescription = null) }
            }
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(HitzSpacing.xs)) {
                Text(stringResource(feature.title), style = MaterialTheme.typography.titleMedium)
                Text(stringResource(feature.body), style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

private val goalLabels = mapOf(
    5 to R.string.feature_onboarding_goal_5,
    10 to R.string.feature_onboarding_goal_10,
    15 to R.string.feature_onboarding_goal_15,
    20 to R.string.feature_onboarding_goal_20,
)

@Composable
private fun GoalPage(selected: Int, onSelect: (Int) -> Unit) {
    PageTitle(R.string.feature_onboarding_goal_title)
    Text(stringResource(R.string.feature_onboarding_goal_body), style = MaterialTheme.typography.bodyLarge)
    Column(Modifier.selectableGroup(), verticalArrangement = Arrangement.spacedBy(HitzSpacing.md)) {
        DailyGoalRules.GOAL_OPTIONS.forEach { minutes ->
            ChoiceCard(
                title = stringResource(R.string.feature_onboarding_goal_minutes, minutes),
                body = stringResource(goalLabels.getValue(minutes)),
                selected = minutes == selected,
                onClick = { onSelect(minutes) },
            )
        }
    }
}

@Composable
private fun LevelPage(selected: StartingLevel?, onSelect: (StartingLevel) -> Unit) {
    PageTitle(R.string.feature_onboarding_level_title)
    Column(Modifier.selectableGroup(), verticalArrangement = Arrangement.spacedBy(HitzSpacing.md)) {
        ChoiceCard(
            title = stringResource(R.string.feature_onboarding_level_beginner),
            body = stringResource(R.string.feature_onboarding_level_beginner_body),
            selected = selected == StartingLevel.Beginner,
            onClick = { onSelect(StartingLevel.Beginner) },
        )
        ChoiceCard(
            title = stringResource(R.string.feature_onboarding_level_some),
            body = stringResource(R.string.feature_onboarding_level_some_body),
            selected = selected == StartingLevel.SomeBasque,
            onClick = { onSelect(StartingLevel.SomeBasque) },
        )
    }
}

@Composable
private fun ChoiceCard(title: String, body: String, selected: Boolean, onClick: () -> Unit) {
    OutlinedCard(
        modifier = Modifier
            .fillMaxWidth()
            .selectable(selected = selected, role = Role.RadioButton, onClick = onClick),
        colors = CardDefaults.outlinedCardColors(
            containerColor = if (selected) {
                MaterialTheme.colorScheme.secondaryContainer
            } else {
                MaterialTheme.colorScheme.surface
            },
        ),
        border = if (selected) {
            BorderStroke(3.dp, MaterialTheme.colorScheme.secondary)
        } else {
            CardDefaults.outlinedCardBorder()
        },
    ) {
        Row(
            modifier = Modifier.padding(HitzSpacing.lg),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(HitzSpacing.md),
        ) {
            RadioButton(selected = selected, onClick = null)
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(HitzSpacing.xs)) {
                Text(title, style = MaterialTheme.typography.titleMedium)
                Text(body, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Composable
private fun PageIndicator(current: Int, modifier: Modifier = Modifier) {
    val description = stringResource(R.string.feature_onboarding_page, current + 1, PAGES)
    Row(
        modifier = modifier.clearAndSetSemantics { contentDescription = description },
        horizontalArrangement = Arrangement.spacedBy(HitzSpacing.sm),
    ) {
        repeat(PAGES) { page ->
            Box(
                Modifier
                    .size(if (page == current) 10.dp else 8.dp)
                    .background(
                        color = if (page == current) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.outlineVariant
                        },
                        shape = CircleShape,
                    ),
            )
        }
    }
}

/** The text in the UI language: Basque when the app runs in Basque, Spanish otherwise. */
@Composable
private fun LocalizedText.resolve(): String =
    if (LocalConfiguration.current.locales[0].language == "eu") eu else es

internal object OnboardingPreviewData {
    val sections = listOf(
        SectionUi(
            LocalizedText("Primeros pasos", "Lehen urratsak"),
            LocalizedText("Saludar y presentarse.", "Agurtu."),
            6
        ),
        SectionUi(LocalizedText("En casa y en la calle", "Etxean eta kalean"), LocalizedText("La casa.", "Etxea."), 6),
    )
}

@HitzPreviews
@Composable
private fun OnboardingPreview() {
    HitzTheme { OnboardingContent(OnboardingPreviewData.sections, onFinish = { _, _ -> }) }
}
