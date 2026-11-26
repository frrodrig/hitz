@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)

package eus.hitz.feature.path.sections

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import eus.hitz.core.designsystem.icon.HitzIcons
import eus.hitz.core.designsystem.preview.HitzPreviews
import eus.hitz.core.designsystem.theme.HitzSpacing
import eus.hitz.core.designsystem.theme.HitzTheme
import eus.hitz.core.domain.path.LearningPath
import eus.hitz.core.domain.path.PathSection
import eus.hitz.core.domain.score.ScoreRules
import eus.hitz.feature.path.PathPreviewData
import eus.hitz.feature.path.R
import eus.hitz.feature.path.resolve

private val StateIcon = 18.dp

/** The list keeps a readable width on tablets. */
private val ListMaxWidth = 640.dp

/**
 * Every section of the course, grouped by CEFR level, with what it covers and the learner's
 * progress. The current section continues where the learner is; finished ones can be reviewed; a
 * locked one can be reached with a jump test.
 */
@Composable
fun SectionsScreen(
    onBack: () -> Unit,
    onSelect: (sectionId: String) -> Unit,
    onJump: (sectionId: String) -> Unit,
    viewModel: SectionsViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    SectionsContent(state, onBack, onSelect, onJump)
}

@Composable
internal fun SectionsContent(
    state: SectionsUiState,
    onBack: () -> Unit,
    onSelect: (sectionId: String) -> Unit,
    onJump: (sectionId: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.feature_path_sections_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            painter = painterResource(HitzIcons.Back),
                            contentDescription = stringResource(R.string.feature_path_back),
                        )
                    }
                },
            )
        },
    ) { padding ->
        when (state) {
            SectionsUiState.Loading -> Box(Modifier.padding(padding).fillMaxSize(), Alignment.Center) {
                LoadingIndicator()
            }
            SectionsUiState.Error -> Box(Modifier.padding(padding).fillMaxSize(), Alignment.Center) {
                Text(stringResource(R.string.feature_path_error))
            }
            is SectionsUiState.Ready -> Box(Modifier.padding(padding).fillMaxSize(), Alignment.TopCenter) {
                SectionList(state.path, onSelect, onJump, Modifier.widthIn(max = ListMaxWidth))
            }
        }
    }
}

@Composable
private fun SectionList(
    path: LearningPath,
    onSelect: (sectionId: String) -> Unit,
    onJump: (sectionId: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val numbered = path.sections.withIndex().toList()
    val current = path.currentSection
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(HitzSpacing.lg),
        verticalArrangement = Arrangement.spacedBy(HitzSpacing.md),
    ) {
        numbered.groupBy { it.value.level }.forEach { (level, sections) ->
            item(key = "level-$level") { LevelHeader(level) }
            itemsIndexed(sections, key = { _, (_, section) -> section.id }) { _, (index, section) ->
                SectionCard(
                    section = section,
                    number = index + 1,
                    isCurrent = section == current && !section.isCompleted,
                    onSelect = { onSelect(section.id) },
                    onJump = { onJump(section.id) },
                )
            }
        }
    }
}

/** A CEFR level and the stretch of the score it covers. */
@Composable
private fun LevelHeader(level: String) {
    Column(
        modifier = Modifier.padding(top = HitzSpacing.md, bottom = HitzSpacing.xs),
        verticalArrangement = Arrangement.spacedBy(HitzSpacing.xxs),
    ) {
        Text(
            text = stringResource(R.string.feature_path_sections_level, level),
            style = MaterialTheme.typography.titleLargeEmphasized,
            modifier = Modifier.semantics { heading() },
        )
        Text(
            text = stringResource(
                R.string.feature_path_sections_points,
                ScoreRules.bottom(level),
                ScoreRules.top(level)
            ),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun SectionCard(
    section: PathSection,
    number: Int,
    isCurrent: Boolean,
    onSelect: () -> Unit,
    onJump: () -> Unit,
) {
    val scheme = MaterialTheme.colorScheme
    val container = when {
        isCurrent -> scheme.primaryContainer
        section.isUnlocked -> scheme.surfaceContainer
        else -> scheme.surfaceContainerLow
    }
    Surface(shape = MaterialTheme.shapes.extraLarge, color = container, modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(HitzSpacing.xl), verticalArrangement = Arrangement.spacedBy(HitzSpacing.sm)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = stringResource(R.string.feature_path_section, number),
                    style = MaterialTheme.typography.labelLarge,
                    color = if (isCurrent) scheme.onPrimaryContainer else scheme.primary,
                    modifier = Modifier.weight(1f),
                )
                SectionState(section, isCurrent)
            }
            Text(
                text = section.title.resolve(),
                style = MaterialTheme.typography.headlineSmallEmphasized,
                modifier = Modifier.semantics { heading() },
            )
            Text(section.description.resolve(), style = MaterialTheme.typography.bodyMedium)
            val units = section.units.size
            Text(
                text = pluralStringResource(
                    R.plurals.feature_path_sections_units,
                    units,
                    section.completedUnits,
                    units,
                ),
                style = MaterialTheme.typography.labelLarge,
                color = scheme.onSurfaceVariant,
                modifier = Modifier.padding(top = HitzSpacing.xs),
            )
            LinearProgressIndicator(
                progress = { if (units == 0) 0f else section.completedUnits.toFloat() / units },
                modifier = Modifier.fillMaxWidth(),
            )
            SectionAction(section, isCurrent, onSelect, onJump)
        }
    }
}

/** Continue the current section, review a finished one or jump to a locked one. */
@Composable
private fun SectionAction(section: PathSection, isCurrent: Boolean, onSelect: () -> Unit, onJump: () -> Unit) {
    val modifier = Modifier.fillMaxWidth().padding(top = HitzSpacing.sm)
    when {
        isCurrent -> Button(onClick = onSelect, shapes = ButtonDefaults.shapes(), modifier = modifier) {
            Text(stringResource(R.string.feature_path_sections_continue))
        }
        section.isUnlocked -> OutlinedButton(
            onClick = onSelect,
            shapes = ButtonDefaults.shapes(),
            modifier = modifier
        ) {
            Text(stringResource(R.string.feature_path_sections_review))
        }
        else -> FilledTonalButton(onClick = onJump, shapes = ButtonDefaults.shapes(), modifier = modifier) {
            Text(stringResource(R.string.feature_path_jump))
        }
    }
}

/** Completed, under way or locked: an icon and a word, so the state is never told by colour alone. */
@Composable
private fun SectionState(section: PathSection, isCurrent: Boolean) {
    val (icon, label) = when {
        section.isCompleted -> HitzIcons.Check to R.string.feature_path_sections_completed
        isCurrent || section.isUnlocked -> HitzIcons.Bolt to R.string.feature_path_sections_current
        else -> HitzIcons.Lock to R.string.feature_path_sections_locked
    }
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(HitzSpacing.xs)) {
        Icon(painterResource(icon), contentDescription = null, modifier = Modifier.size(StateIcon))
        Text(stringResource(label), style = MaterialTheme.typography.labelLarge)
    }
}

@HitzPreviews
@Composable
private fun SectionsPreview() {
    HitzTheme { SectionsContent(SectionsUiState.Ready(PathPreviewData.path), onBack = {}, onSelect = {}, onJump = {}) }
}
