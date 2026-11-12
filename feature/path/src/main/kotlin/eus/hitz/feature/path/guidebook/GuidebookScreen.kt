@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)

package eus.hitz.feature.path.guidebook

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.intl.LocaleList
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import eus.hitz.core.designsystem.icon.HitzIcons
import eus.hitz.core.designsystem.theme.HitzSpacing
import eus.hitz.core.model.content.CourseUnit
import eus.hitz.core.model.content.GuidebookSection
import eus.hitz.feature.path.R
import eus.hitz.feature.path.resolve
import eus.hitz.feature.path.unitNumber

@Composable
fun GuidebookScreen(
    onBack: () -> Unit,
    viewModel: GuidebookViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    GuidebookContent(state, onBack)
}

@Composable
internal fun GuidebookContent(
    state: GuidebookUiState,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val unit = (state as? GuidebookUiState.Ready)?.unit
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (unit != null) {
                            stringResource(R.string.feature_path_guidebook_title, unit.id.unitNumber())
                        } else {
                            stringResource(R.string.feature_path_guidebook)
                        },
                    )
                },
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
            GuidebookUiState.Loading -> Box(Modifier.padding(padding).fillMaxSize(), Alignment.Center) {
                LoadingIndicator()
            }
            GuidebookUiState.Error -> Box(Modifier.padding(padding).fillMaxSize(), Alignment.Center) {
                Text(stringResource(R.string.feature_path_error))
            }
            is GuidebookUiState.Ready ->
                GuidebookList(state.unit, PaddingValues(HitzSpacing.lg), Modifier.padding(padding))
        }
    }
}

@Composable
private fun GuidebookList(unit: CourseUnit, contentPadding: PaddingValues, modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = contentPadding,
        verticalArrangement = Arrangement.spacedBy(HitzSpacing.lg),
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(HitzSpacing.sm)) {
                Text(
                    text = unit.title.resolve(),
                    style = MaterialTheme.typography.headlineMediumEmphasized,
                    modifier = Modifier.semantics { heading() },
                )
                Text(stringResource(R.string.feature_path_goals), style = MaterialTheme.typography.titleMedium)
                unit.goals.forEach { goal -> Text("• ${goal.resolve()}", style = MaterialTheme.typography.bodyLarge) }
            }
        }
        items(unit.guidebook.sections) { section -> GuidebookCard(section) }
    }
}

@Composable
private fun GuidebookCard(section: GuidebookSection) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surfaceContainer,
    ) {
        Column(Modifier.padding(HitzSpacing.lg), verticalArrangement = Arrangement.spacedBy(HitzSpacing.sm)) {
            Text(
                text = section.title.resolve(),
                style = MaterialTheme.typography.titleLargeEmphasized,
                modifier = Modifier.semantics { heading() },
            )
            Text(section.body.resolve(), style = MaterialTheme.typography.bodyLarge)
            section.examples.forEach { example ->
                ListItem(
                    headlineContent = {
                        Text(
                            text = example.eu,
                            style = MaterialTheme.typography.titleMedium.copy(localeList = LocaleList("eu")),
                        )
                    },
                    supportingContent = {
                        Text(
                            text = example.es,
                            style = MaterialTheme.typography.bodyMedium.copy(localeList = LocaleList("es")),
                        )
                    },
                    colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                )
            }
        }
    }
}
