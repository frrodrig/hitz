@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package eus.hitz.feature.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import eus.hitz.core.designsystem.component.HitzMascot
import eus.hitz.core.designsystem.preview.HitzPreviews
import eus.hitz.core.designsystem.theme.HitzSpacing
import eus.hitz.core.designsystem.theme.HitzTheme
import eus.hitz.core.domain.settings.UserSettings

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = hiltViewModel(),
) {
    val settings by viewModel.settings.collectAsStateWithLifecycle()
    ProfileContent(settings, onSpeakingChange = viewModel::setSpeakingEnabled, modifier = modifier)
}

/** Profile: stats arrive with gamification (M6); for now, the learner's settings. */
@Composable
internal fun ProfileContent(
    settings: UserSettings,
    onSpeakingChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(vertical = HitzSpacing.xl),
            verticalArrangement = Arrangement.spacedBy(HitzSpacing.md),
        ) {
            Column(
                modifier = Modifier.padding(horizontal = HitzSpacing.xl),
                verticalArrangement = Arrangement.spacedBy(HitzSpacing.md),
            ) {
                HitzMascot(contentDescription = null)
                Text(
                    text = stringResource(R.string.feature_profile_title),
                    style = MaterialTheme.typography.headlineMediumEmphasized,
                    modifier = Modifier.semantics { heading() },
                )
                Text(stringResource(R.string.feature_profile_placeholder), style = MaterialTheme.typography.bodyLarge)
                Text(
                    text = stringResource(R.string.feature_profile_settings),
                    style = MaterialTheme.typography.titleMediumEmphasized,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .padding(top = HitzSpacing.lg)
                        .semantics { heading() },
                )
            }
            ListItem(
                modifier = Modifier.toggleable(
                    value = settings.speakingEnabled,
                    role = Role.Switch,
                    onValueChange = onSpeakingChange,
                ),
                headlineContent = { Text(stringResource(R.string.feature_profile_speaking)) },
                supportingContent = { Text(stringResource(R.string.feature_profile_speaking_description)) },
                trailingContent = { Switch(checked = settings.speakingEnabled, onCheckedChange = null) },
                colors = ListItemDefaults.colors(containerColor = Color.Transparent),
            )
        }
    }
}

@HitzPreviews
@Composable
private fun ProfileContentPreview() {
    HitzTheme { ProfileContent(UserSettings(speakingEnabled = true), onSpeakingChange = {}) }
}
