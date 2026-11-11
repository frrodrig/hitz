@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package eus.hitz.feature.profile

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearWavyProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import eus.hitz.core.designsystem.component.HitzDailyGoalRing
import eus.hitz.core.designsystem.component.HitzMascot
import eus.hitz.core.designsystem.icon.HitzIcons
import eus.hitz.core.designsystem.preview.HitzPreviews
import eus.hitz.core.designsystem.theme.HitzSpacing
import eus.hitz.core.designsystem.theme.HitzTheme
import eus.hitz.core.domain.gamification.AchievementProgress
import eus.hitz.core.domain.gamification.AchievementType
import eus.hitz.core.domain.gamification.DailyGoalRules
import eus.hitz.core.domain.gamification.DailyProgress
import eus.hitz.core.domain.gamification.PlayerSummary
import eus.hitz.core.domain.gamification.WeekDay
import eus.hitz.core.domain.settings.UserSettings
import java.time.LocalDate
import java.time.format.TextStyle

private const val PERCENT = 100
private val TODAY_RING = 72.dp
private val DAY_RING = 32.dp

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val onReminderChange = rememberReminderToggle(viewModel)
    ProfileContent(
        state = state,
        onSettingChange = { setting, enabled ->
            when (setting) {
                Setting.Speaking -> viewModel.setSpeakingEnabled(enabled)
                Setting.Listening -> viewModel.setListeningEnabled(enabled)
                Setting.SoundEffects -> viewModel.setSoundEffectsEnabled(enabled)
                Setting.Reminder -> onReminderChange(enabled)
            }
        },
        onGoalChange = viewModel::setDailyGoal,
        modifier = modifier,
    )
}

/** The learner's switches in the profile. */
internal enum class Setting { Speaking, Listening, SoundEffects, Reminder }

@Composable
private fun rememberReminderToggle(viewModel: ProfileViewModel): (Boolean) -> Unit {
    val context = LocalContext.current
    val permission = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) viewModel.setReminderEnabled(true)
    }
    return { enabled ->
        val needsPermission = enabled && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) !=
            PackageManager.PERMISSION_GRANTED
        if (needsPermission) {
            permission.launch(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            viewModel.setReminderEnabled(enabled)
        }
    }
}

/** The daily goal and this week, achievements and settings. */
@Composable
internal fun ProfileContent(
    state: ProfileUiState,
    onSettingChange: (Setting, Boolean) -> Unit,
    onGoalChange: (minutes: Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(modifier.fillMaxSize()) {
        when (state) {
            ProfileUiState.Loading -> Box(contentAlignment = Alignment.Center) { LoadingIndicator() }
            is ProfileUiState.Ready -> LazyColumn(
                contentPadding = PaddingValues(vertical = HitzSpacing.xl),
                verticalArrangement = Arrangement.spacedBy(HitzSpacing.sm),
            ) {
                item(key = "header") { Header() }
                sectionTitle(R.string.feature_profile_daily_goal)
                item(key = "daily-goal") { DailyGoalCard(state.player) }
                item(key = "goal-choice") { GoalChoice(state.settings.dailyGoalMinutes, onGoalChange) }
                sectionTitle(R.string.feature_profile_achievements)
                items(state.player.achievements, key = { "achievement-${it.type}" }) { AchievementRow(it) }
                sectionTitle(R.string.feature_profile_settings)
                items(settingRows(state.settings), key = { "setting-${it.setting}" }) { row ->
                    SettingRow(
                        title = stringResource(row.title),
                        description = stringResource(row.description),
                        checked = row.checked,
                        onChange = { onSettingChange(row.setting, it) },
                    )
                }
            }
        }
    }
}

private data class SettingRowUi(val setting: Setting, val title: Int, val description: Int, val checked: Boolean)

private fun settingRows(settings: UserSettings) = listOf(
    SettingRowUi(
        Setting.Speaking,
        R.string.feature_profile_speaking,
        R.string.feature_profile_speaking_description,
        settings.speakingEnabled,
    ),
    SettingRowUi(
        Setting.Listening,
        R.string.feature_profile_listening,
        R.string.feature_profile_listening_description,
        settings.listeningEnabled,
    ),
    SettingRowUi(
        Setting.SoundEffects,
        R.string.feature_profile_sound_effects,
        R.string.feature_profile_sound_effects_description,
        settings.soundEffectsEnabled,
    ),
    SettingRowUi(
        Setting.Reminder,
        R.string.feature_profile_reminder,
        R.string.feature_profile_reminder_description,
        settings.reminderEnabled,
    ),
)

@Composable
private fun Header() {
    Row(
        modifier = Modifier.padding(horizontal = HitzSpacing.xl),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(HitzSpacing.lg),
    ) {
        HitzMascot(contentDescription = null)
        Text(
            text = stringResource(R.string.feature_profile_title),
            style = MaterialTheme.typography.headlineMediumEmphasized,
            modifier = Modifier.semantics { heading() },
        )
    }
}

@Composable
private fun DailyGoalCard(player: PlayerSummary) {
    OutlinedCard(Modifier.fillMaxWidth().padding(horizontal = HitzSpacing.xl)) {
        Column(
            modifier = Modifier.padding(HitzSpacing.lg),
            verticalArrangement = Arrangement.spacedBy(HitzSpacing.lg),
        ) {
            Today(player.today)
            Text(
                text = stringResource(R.string.feature_profile_week),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                player.week.forEach { WeekDayItem(it) }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(HitzSpacing.sm)) {
                StatCard(
                    icon = HitzIcons.Star,
                    value = player.goalDays.toString(),
                    label = stringResource(R.string.feature_profile_goal_days),
                    modifier = Modifier.weight(1f),
                )
                StatCard(
                    icon = HitzIcons.Book,
                    value = player.totalMinutes.toString(),
                    label = stringResource(R.string.feature_profile_study_minutes),
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun Today(today: DailyProgress) {
    Row(
        modifier = Modifier.semantics(mergeDescendants = true) {},
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(HitzSpacing.lg),
    ) {
        HitzDailyGoalRing(today.progress, size = TODAY_RING)
        Column(verticalArrangement = Arrangement.spacedBy(HitzSpacing.xs)) {
            Text(
                text = stringResource(R.string.feature_profile_today, today.minutesDone, today.goalMinutes),
                style = MaterialTheme.typography.titleLargeEmphasized,
            )
            Text(
                text = if (today.goalMet) {
                    stringResource(R.string.feature_profile_goal_met)
                } else {
                    pluralStringResource(R.plurals.feature_profile_goal_left, today.minutesLeft, today.minutesLeft)
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun WeekDayItem(day: WeekDay) {
    val locale = LocalConfiguration.current.locales[0]
    val dayOfWeek = LocalDate.ofEpochDay(day.epochDay).dayOfWeek
    val percent = (day.progress * PERCENT).toInt()
    val description = stringResource(
        R.string.feature_profile_week_day,
        dayOfWeek.getDisplayName(TextStyle.FULL, locale),
        percent,
    )
    Column(
        modifier = Modifier.clearAndSetSemantics { contentDescription = description },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(HitzSpacing.xs),
    ) {
        HitzDailyGoalRing(if (day.isFuture) 0f else day.progress, size = DAY_RING)
        Text(
            text = dayOfWeek.getDisplayName(TextStyle.NARROW, locale),
            style = with(MaterialTheme.typography) { if (day.isToday) labelLargeEmphasized else labelLarge },
            color = if (day.isToday) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun StatCard(icon: Int, value: String, label: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.semantics(mergeDescendants = true) {},
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
    ) {
        Row(
            modifier = Modifier.padding(HitzSpacing.md),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(HitzSpacing.sm),
        ) {
            Icon(painterResource(icon), contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Column {
                Text(value, style = MaterialTheme.typography.titleLargeEmphasized)
                Text(label, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

/** How many minutes a day count as the goal. */
@Composable
private fun GoalChoice(selected: Int, onGoalChange: (Int) -> Unit) {
    Column(
        modifier = Modifier.padding(horizontal = HitzSpacing.xl, vertical = HitzSpacing.sm),
        verticalArrangement = Arrangement.spacedBy(HitzSpacing.sm),
    ) {
        Text(stringResource(R.string.feature_profile_goal_choose), style = MaterialTheme.typography.titleMedium)
        SingleChoiceSegmentedButtonRow(Modifier.fillMaxWidth()) {
            DailyGoalRules.GOAL_OPTIONS.forEachIndexed { index, minutes ->
                SegmentedButton(
                    selected = minutes == selected,
                    onClick = { onGoalChange(minutes) },
                    shape = SegmentedButtonDefaults.itemShape(index, DailyGoalRules.GOAL_OPTIONS.size),
                ) {
                    Text(stringResource(R.string.feature_profile_goal_option, minutes))
                }
            }
        }
        Text(
            text = stringResource(R.string.feature_profile_goal_hint),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

private fun LazyListScope.sectionTitle(title: Int) {
    item(key = "title-$title") {
        Text(
            text = stringResource(title),
            style = MaterialTheme.typography.titleMediumEmphasized,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .padding(start = HitzSpacing.xl, end = HitzSpacing.xl, top = HitzSpacing.lg)
                .semantics { heading() },
        )
    }
}

@Composable
private fun AchievementRow(progress: AchievementProgress) {
    val name = stringResource(
        when (progress.type) {
            AchievementType.GoalDays -> R.string.feature_profile_achievement_goal_days
            AchievementType.StudyMinutes -> R.string.feature_profile_achievement_study_minutes
            AchievementType.Lessons -> R.string.feature_profile_achievement_lessons
            AchievementType.Perfect -> R.string.feature_profile_achievement_perfect
            AchievementType.Stories -> R.string.feature_profile_achievement_stories
        },
    )
    val next = progress.nextTarget
    val tiers = progress.type.thresholds.size
    val supporting = if (next == null) {
        stringResource(R.string.feature_profile_achievement_complete, tiers)
    } else {
        stringResource(R.string.feature_profile_achievement_next, progress.tier, tiers, progress.value, next)
    }
    ProgressRow(name, supporting, if (next == null) 1f else progress.value.toFloat() / next, done = next == null)
}

@Composable
private fun ProgressRow(title: String, supporting: String, fraction: Float, done: Boolean) {
    ListItem(
        headlineContent = { Text(title) },
        supportingContent = {
            Column(verticalArrangement = Arrangement.spacedBy(HitzSpacing.xs)) {
                Text(supporting)
                LinearWavyProgressIndicator(
                    progress = { fraction.coerceIn(0f, 1f) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        trailingContent = if (done) {
            {
                Icon(
                    painterResource(HitzIcons.Check),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        } else {
            null
        },
        colors = ListItemDefaults.colors(containerColor = Color.Transparent),
        modifier = Modifier.padding(horizontal = HitzSpacing.sm),
    )
}

@Composable
private fun SettingRow(title: String, description: String, checked: Boolean, onChange: (Boolean) -> Unit) {
    ListItem(
        modifier = Modifier
            .padding(horizontal = HitzSpacing.sm)
            .toggleable(value = checked, role = Role.Switch, onValueChange = onChange),
        headlineContent = { Text(title) },
        supportingContent = { Text(description) },
        trailingContent = { Switch(checked = checked, onCheckedChange = null) },
        colors = ListItemDefaults.colors(containerColor = Color.Transparent),
    )
}

@HitzPreviews
@Composable
private fun ProfileContentPreview() {
    HitzTheme {
        ProfileContent(
            state = ProfilePreviewData.ready,
            onSettingChange = { _, _ -> },
            onGoalChange = {},
        )
    }
}

internal object ProfilePreviewData {
    /** Wednesday 2026-11-04. */
    private const val TODAY = 20_761L
    private const val MONDAY = TODAY - 2

    val ready = ProfileUiState.Ready(
        player = PlayerSummary(
            today = DailyProgress(activeMillis = 6 * 60_000L, goalMinutes = 10),
            week = listOf(1.2f, 0.4f, 0.6f, 0f, 0f, 0f, 0f).mapIndexed { index, progress ->
                val day = MONDAY + index
                WeekDay(day, progress, isToday = day == TODAY, isFuture = day > TODAY)
            },
            goalDays = 12,
            totalMinutes = 184,
            achievements = listOf(
                AchievementProgress(AchievementType.GoalDays, 12),
                AchievementProgress(AchievementType.StudyMinutes, 184),
                AchievementProgress(AchievementType.Lessons, 31),
                AchievementProgress(AchievementType.Perfect, 12),
                AchievementProgress(AchievementType.Stories, 3),
            ),
        ),
        settings = UserSettings(speakingEnabled = true, reminderEnabled = false, dailyGoalMinutes = 10),
    )
}
