@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)

package eus.hitz.feature.profile.settings

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import eus.hitz.core.designsystem.icon.HitzIcons
import eus.hitz.core.designsystem.preview.HitzPreviews
import eus.hitz.core.designsystem.theme.HitzSpacing
import eus.hitz.core.designsystem.theme.HitzTheme
import eus.hitz.core.domain.settings.UserSettings
import eus.hitz.feature.profile.Group
import eus.hitz.feature.profile.InfoRow
import eus.hitz.feature.profile.R
import eus.hitz.feature.profile.SettingRow
import eus.hitz.feature.profile.sectionTitle

/** The back arrow, turned to point forward. */
private const val CHEVRON_TURN = 180f

@Composable
fun SettingsScreen(onBack: () -> Unit, viewModel: SettingsViewModel = hiltViewModel()) {
    val settings by viewModel.settings.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val onReminderChange = rememberReminderToggle { viewModel.set(Setting.Reminder, it) }
    SettingsContent(
        settings = settings,
        onSettingChange = { setting, enabled ->
            if (setting == Setting.Reminder) onReminderChange(enabled) else viewModel.set(setting, enabled)
        },
        onBack = onBack,
        version = remember(context) { context.appVersion() },
        systemColorsAvailable = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S,
        onOpenLanguage = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            { context.openAppLanguageSettings() }
        } else {
            null
        },
    )
}

/** Turning the reminder on asks for notifications first, on Android 13 and later. */
@Composable
private fun rememberReminderToggle(setReminder: (Boolean) -> Unit): (Boolean) -> Unit {
    val context = LocalContext.current
    val permission = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) setReminder(true)
    }
    return { enabled ->
        val needsPermission = enabled && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) !=
            PackageManager.PERMISSION_GRANTED
        if (needsPermission) {
            permission.launch(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            setReminder(enabled)
        }
    }
}

private fun Context.appVersion(): String? = runCatching {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        packageManager.getPackageInfo(packageName, PackageManager.PackageInfoFlags.of(0)).versionName
    } else {
        @Suppress("DEPRECATION")
        packageManager.getPackageInfo(packageName, 0).versionName
    }
}.getOrNull()

/** Android's own screen for this app's language (Android 13+), where Spanish or Basque is chosen. */
private fun Context.openAppLanguageSettings() {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return
    runCatching {
        startActivity(Intent(Settings.ACTION_APP_LOCALE_SETTINGS, Uri.fromParts("package", packageName, null)))
    }
}

private data class SwitchRow(val setting: Setting, @StringRes val title: Int, @StringRes val description: Int)

private val ExerciseRows = listOf(
    SwitchRow(Setting.Speaking, R.string.feature_profile_speaking, R.string.feature_profile_speaking_description),
    SwitchRow(Setting.Listening, R.string.feature_profile_listening, R.string.feature_profile_listening_description),
)

private val SoundRows = listOf(
    SwitchRow(
        Setting.SoundEffects,
        R.string.feature_profile_sound_effects,
        R.string.feature_profile_sound_effects_description,
    ),
    SwitchRow(Setting.Reminder, R.string.feature_profile_reminder, R.string.feature_profile_reminder_description),
)

private val ColorRows = listOf(
    SwitchRow(
        Setting.SystemColors,
        R.string.feature_profile_system_colors,
        R.string.feature_profile_system_colors_description,
    ),
)

private fun UserSettings.isOn(setting: Setting) = when (setting) {
    Setting.Speaking -> speakingEnabled
    Setting.Listening -> listeningEnabled
    Setting.SoundEffects -> soundEffectsEnabled
    Setting.Reminder -> reminderEnabled
    Setting.SystemColors -> systemColors
    Setting.UpdateChecks -> updateChecksEnabled
}

/**
 * The settings, grouped by what they change: exercises, sound and reminders, appearance, the
 * app's language and the app itself. Colours appear from Android 12 and the language from Android
 * 13, the versions that support them.
 */
@Composable
internal fun SettingsContent(
    settings: UserSettings?,
    onSettingChange: (Setting, Boolean) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    version: String? = null,
    systemColorsAvailable: Boolean = true,
    onOpenLanguage: (() -> Unit)? = null,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.feature_profile_settings)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(painterResource(HitzIcons.Back), stringResource(R.string.feature_profile_back))
                    }
                },
            )
        },
    ) { padding ->
        if (settings == null) {
            Box(Modifier.padding(padding).fillMaxSize(), Alignment.Center) { LoadingIndicator() }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(top = padding.calculateTopPadding(), bottom = HitzSpacing.xl),
                verticalArrangement = Arrangement.spacedBy(HitzSpacing.sm),
            ) {
                switches(R.string.feature_profile_settings_exercises, ExerciseRows, settings, onSettingChange)
                switches(R.string.feature_profile_settings_sound, SoundRows, settings, onSettingChange)
                if (systemColorsAvailable) {
                    switches(R.string.feature_profile_settings_appearance, ColorRows, settings, onSettingChange)
                }
                if (onOpenLanguage != null) {
                    sectionTitle(R.string.feature_profile_settings_language)
                    item(key = "language") {
                        Group(listOf(Unit)) { _, shape ->
                            InfoRow(
                                title = stringResource(R.string.feature_profile_app_language),
                                description = stringResource(R.string.feature_profile_app_language_description),
                                shape = shape,
                                onClick = onOpenLanguage,
                                // It opens another screen: a chevron says so.
                                trailing = {
                                    Icon(
                                        painter = painterResource(HitzIcons.Back),
                                        contentDescription = null,
                                        modifier = Modifier.rotate(CHEVRON_TURN),
                                    )
                                },
                            )
                        }
                    }
                }
                sectionTitle(R.string.feature_profile_settings_app)
                item(key = "app") { AppGroup(settings, onSettingChange, version) }
            }
        }
    }
}

private fun LazyListScope.switches(
    @StringRes title: Int,
    rows: List<SwitchRow>,
    settings: UserSettings,
    onSettingChange: (Setting, Boolean) -> Unit,
) {
    sectionTitle(title)
    item(key = "group-$title") {
        Group(rows) { row, shape ->
            SettingRow(
                title = stringResource(row.title),
                description = stringResource(row.description),
                checked = settings.isOn(row.setting),
                onChange = { onSettingChange(row.setting, it) },
                shape = shape,
            )
        }
    }
}

/** Update checks, the version installed and the licences of the typeface and the icons. */
@Composable
private fun AppGroup(settings: UserSettings, onSettingChange: (Setting, Boolean) -> Unit, version: String?) {
    val rows = listOfNotNull(
        AppRow.UpdateChecks,
        AppRow.Version.takeIf { version != null },
        AppRow.Typeface,
        AppRow.Icons,
    )
    Group(rows) { row, shape ->
        when (row) {
            AppRow.UpdateChecks -> SettingRow(
                title = stringResource(R.string.feature_profile_update_checks),
                description = stringResource(R.string.feature_profile_update_checks_description),
                checked = settings.updateChecksEnabled,
                onChange = { onSettingChange(Setting.UpdateChecks, it) },
                shape = shape,
            )
            AppRow.Version -> InfoRow(
                title = stringResource(R.string.feature_profile_version),
                description = version.orEmpty(),
                shape = shape,
            )
            AppRow.Typeface -> InfoRow(
                title = stringResource(R.string.feature_profile_typeface),
                description = stringResource(R.string.feature_profile_typeface_licence),
                shape = shape,
            )
            AppRow.Icons -> InfoRow(
                title = stringResource(R.string.feature_profile_icons),
                description = stringResource(R.string.feature_profile_icons_licence),
                shape = shape,
            )
        }
    }
}

private enum class AppRow { UpdateChecks, Version, Typeface, Icons }

@HitzPreviews
@Composable
private fun SettingsContentPreview() {
    HitzTheme {
        SettingsContent(
            settings = UserSettings(reminderEnabled = true),
            onSettingChange = { _, _ -> },
            onBack = {},
            version = "1.3.0",
            onOpenLanguage = {},
        )
    }
}
