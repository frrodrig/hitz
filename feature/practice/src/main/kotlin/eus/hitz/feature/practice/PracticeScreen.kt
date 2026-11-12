@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package eus.hitz.feature.practice

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import eus.hitz.core.designsystem.component.HitzButton
import eus.hitz.core.designsystem.component.HitzMascot
import eus.hitz.core.designsystem.component.MascotPose
import eus.hitz.core.designsystem.icon.HitzIcons
import eus.hitz.core.designsystem.preview.HitzPreviews
import eus.hitz.core.designsystem.theme.HitzSpacing
import eus.hitz.core.designsystem.theme.HitzTheme
import eus.hitz.core.domain.practice.LearnedUnit
import eus.hitz.core.domain.practice.PracticeHub
import eus.hitz.core.domain.practice.PracticeMode
import eus.hitz.core.model.content.LocalizedText

private const val PERCENT = 100
private val UNIT_BADGE = 48.dp
private val MODE_TILE = 96.dp
private val GROUP_OUTER = 24.dp
private val GROUP_INNER = 6.dp
private val GROUP_GAP = 2.dp
private const val UNIT_COLORS = 3

/** "u01-2" → "1.2". */
internal fun String.unitNumber(): String =
    removePrefix("u").split("-").joinToString(".") { it.toIntOrNull()?.toString() ?: it }

/** Units take the system's three colours in turn, as on the path. */
@Composable
internal fun unitColors(index: Int): Pair<Color, Color> = with(MaterialTheme.colorScheme) {
    when (index % UNIT_COLORS) {
        0 -> primary to onPrimary
        1 -> secondary to onSecondary
        else -> tertiary to onTertiary
    }
}

@Composable
fun PracticeScreen(
    onPractice: (PracticeMode) -> Unit,
    onOpenUnit: (unitId: String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PracticeViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    PracticeContent(state, onPractice, onOpenUnit, modifier)
}

/** The practice hub, organised around what the learner has already seen. */
@Composable
internal fun PracticeContent(
    state: PracticeUiState,
    onPractice: (PracticeMode) -> Unit,
    onOpenUnit: (unitId: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(modifier.fillMaxSize()) {
        when (state) {
            PracticeUiState.Loading -> Box(contentAlignment = Alignment.Center) { LoadingIndicator() }
            is PracticeUiState.Ready -> LazyColumn(
                contentPadding = PaddingValues(horizontal = HitzSpacing.lg, vertical = HitzSpacing.xl),
                verticalArrangement = Arrangement.spacedBy(HitzSpacing.sm),
            ) {
                item(key = "header") { Header() }
                if (state.hub.units.isEmpty()) {
                    item(key = "empty") { EmptyHub() }
                } else {
                    item(key = "personal") { PersonalReview(state.hub.weakItems) { onPractice(PracticeMode.Mistakes) } }
                    if (state.modes.isNotEmpty()) item(key = "modes") { QuickModes(state.modes, onPractice) }
                    item(key = "units-title") { SectionTitle(stringResource(R.string.feature_practice_units)) }
                    item(key = "units") {
                        Column(verticalArrangement = Arrangement.spacedBy(GROUP_GAP)) {
                            state.hub.units.forEachIndexed { index, unit ->
                                UnitRow(unit, index, state.hub.units.size) { onOpenUnit(unit.unitId) }
                            }
                        }
                    }
                    item(key = "library-title") { SectionTitle(stringResource(R.string.feature_practice_library)) }
                    item(key = "library") { Library(state.hub) }
                }
            }
        }
    }
}

@Composable
private fun Header() {
    Column(Modifier.padding(bottom = HitzSpacing.sm), verticalArrangement = Arrangement.spacedBy(HitzSpacing.xs)) {
        Text(
            text = stringResource(R.string.feature_practice_title),
            style = MaterialTheme.typography.headlineLargeEmphasized,
            modifier = Modifier.semantics { heading() },
        )
        Text(
            text = stringResource(R.string.feature_practice_subtitle),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun EmptyHub() {
    Column(
        modifier = Modifier.fillMaxWidth().padding(top = HitzSpacing.xl),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(HitzSpacing.md),
    ) {
        HitzMascot(pose = MascotPose.Piensa, size = 160.dp, contentDescription = null)
        Text(
            text = stringResource(R.string.feature_practice_empty_title),
            style = MaterialTheme.typography.titleLargeEmphasized,
            textAlign = TextAlign.Center,
        )
        Text(
            text = stringResource(R.string.feature_practice_empty_body),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
    }
}

/** The hero of the hub: a session made of what the learner gets wrong most. */
@Composable
private fun PersonalReview(weakItems: Int, onStart: () -> Unit) {
    val scheme = MaterialTheme.colorScheme
    Surface(color = scheme.primary, contentColor = scheme.onPrimary, shape = MaterialTheme.shapes.extraLarge) {
        Box {
            Surface(
                shape = MaterialShapes.Cookie9Sided.toShape(),
                color = scheme.onPrimary.copy(alpha = 0.1f),
                modifier = Modifier.size(170.dp).align(Alignment.TopEnd).offset(x = 48.dp, y = (-48).dp),
            ) {}
            Column(
                modifier = Modifier.padding(HitzSpacing.xl),
                verticalArrangement = Arrangement.spacedBy(HitzSpacing.sm),
            ) {
                Text(
                    stringResource(R.string.feature_practice_personal),
                    style = MaterialTheme.typography.labelLarge,
                    color = scheme.primaryContainer,
                )
                Text(
                    text = if (weakItems > 0) {
                        pluralStringResource(R.plurals.feature_practice_personal_weak, weakItems, weakItems)
                    } else {
                        stringResource(R.string.feature_practice_personal_none)
                    },
                    style = MaterialTheme.typography.headlineSmallEmphasized,
                    modifier = Modifier.padding(end = HitzSpacing.xxl),
                )
                HitzButton(
                    text = stringResource(R.string.feature_practice_start),
                    onClick = onStart,
                    icon = HitzIcons.Bolt,
                    modifier = Modifier.padding(top = HitzSpacing.sm),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = scheme.primaryContainer,
                        contentColor = scheme.onPrimaryContainer,
                    ),
                )
            }
        }
    }
}

@Composable
private fun QuickModes(modes: List<PracticeMode>, onPractice: (PracticeMode) -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(HitzSpacing.sm)) {
        modes.forEach { mode ->
            val scheme = MaterialTheme.colorScheme
            val (container, content) = when (mode) {
                PracticeMode.Speaking -> scheme.tertiaryContainer to scheme.onTertiaryContainer
                else -> scheme.secondaryContainer to scheme.onSecondaryContainer
            }
            Surface(
                onClick = { onPractice(mode) },
                color = container,
                contentColor = content,
                shape = MaterialTheme.shapes.large,
                modifier = Modifier.weight(1f).heightIn(min = MODE_TILE),
            ) {
                Column(
                    Modifier.padding(HitzSpacing.lg),
                    verticalArrangement = Arrangement.spacedBy(HitzSpacing.md),
                ) {
                    Icon(
                        painterResource(if (mode == PracticeMode.Speaking) HitzIcons.Mic else HitzIcons.Speaker),
                        contentDescription = null,
                    )
                    Text(
                        text = stringResource(
                            if (mode == PracticeMode.Speaking) {
                                R.string.feature_practice_speaking
                            } else {
                                R.string.feature_practice_listening
                            },
                        ),
                        style = MaterialTheme.typography.titleMediumEmphasized,
                    )
                }
            }
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMediumEmphasized,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(top = HitzSpacing.lg, start = HitzSpacing.xs).semantics { heading() },
    )
}

/** Rows of a group: large outer corners, small inner ones, 2 dp apart. */
private fun groupShape(index: Int, count: Int) = RoundedCornerShape(
    topStart = if (index == 0) GROUP_OUTER else GROUP_INNER,
    topEnd = if (index == 0) GROUP_OUTER else GROUP_INNER,
    bottomStart = if (index == count - 1) GROUP_OUTER else GROUP_INNER,
    bottomEnd = if (index == count - 1) GROUP_OUTER else GROUP_INNER,
)

@Composable
private fun UnitRow(unit: LearnedUnit, index: Int, count: Int, onClick: () -> Unit) {
    val (badge, onBadge) = unitColors(index)
    val details = buildList {
        add(pluralStringResource(R.plurals.feature_practice_words, unit.words, unit.words))
        if (unit.guides > 0) add(pluralStringResource(R.plurals.feature_practice_guides, unit.guides, unit.guides))
        if (unit.hasStory) add(stringResource(R.string.feature_practice_with_story))
    }.joinToString(stringResource(R.string.feature_practice_separator))
    Surface(
        onClick = onClick,
        shape = groupShape(index, count),
        color = MaterialTheme.colorScheme.surfaceContainer,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = HitzSpacing.lg, vertical = HitzSpacing.md),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(HitzSpacing.md),
        ) {
            Surface(
                shape = MaterialShapes.Cookie9Sided.toShape(),
                color = badge,
                modifier = Modifier.size(UNIT_BADGE)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(unit.unitId.unitNumber(), style = MaterialTheme.typography.labelLarge, color = onBadge)
                }
            }
            Column(Modifier.weight(1f)) {
                Text(unit.title.resolve(), style = MaterialTheme.typography.titleMedium)
                Text(
                    details,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = if (unit.completed) {
                    stringResource(R.string.feature_practice_mastered)
                } else {
                    stringResource(R.string.feature_practice_percent, (unit.progress * PERCENT).toInt())
                },
                style = MaterialTheme.typography.labelLarge,
                color = with(MaterialTheme.colorScheme) { if (unit.completed) primary else onSurfaceVariant },
            )
        }
    }
}

@Composable
private fun Library(hub: PracticeHub) {
    Row(horizontalArrangement = Arrangement.spacedBy(HitzSpacing.sm)) {
        listOf(
            hub.words to R.string.feature_practice_library_words,
            hub.guides to R.string.feature_practice_library_guides,
            hub.stories to R.string.feature_practice_library_stories,
        ).forEach { (count, label) ->
            Surface(
                color = MaterialTheme.colorScheme.surfaceContainerHigh,
                shape = MaterialTheme.shapes.large,
                modifier = Modifier.weight(1f).semantics(mergeDescendants = true) {},
            ) {
                Column(Modifier.padding(HitzSpacing.lg), verticalArrangement = Arrangement.spacedBy(HitzSpacing.xs)) {
                    Text(count.toString(), style = MaterialTheme.typography.headlineMediumEmphasized)
                    Text(stringResource(label), style = MaterialTheme.typography.labelLarge)
                }
            }
        }
    }
}

internal object PracticePreviewData {
    private fun text(es: String, eu: String) = LocalizedText(es, eu)

    val hub = PracticeHub(
        units = listOf(
            LearnedUnit("u01-1", "s1", text("¡Hola!", "Kaixo!"), 1f, words = 8, guides = 2, hasStory = true),
            LearnedUnit(
                "u01-2",
                "s1",
                text("¿Quién eres?", "Nor zara?"),
                0.2f,
                words = 4,
                guides = 1,
                hasStory = false
            ),
        ),
        weakItems = 12,
        available = mapOf(PracticeMode.Mistakes to 40, PracticeMode.Listening to 8, PracticeMode.Speaking to 4),
    )

    val ready = PracticeUiState.Ready(hub, listOf(PracticeMode.Listening, PracticeMode.Speaking))
    val empty = PracticeUiState.Ready(PracticeHub(emptyList(), 0, emptyMap()), emptyList())
}

@HitzPreviews
@Composable
private fun PracticePreview() {
    HitzTheme { PracticeContent(PracticePreviewData.ready, onPractice = {}, onOpenUnit = {}) }
}
