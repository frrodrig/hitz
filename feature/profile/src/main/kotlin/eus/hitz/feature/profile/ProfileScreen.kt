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
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import eus.hitz.core.designsystem.component.HitzMascot
import eus.hitz.core.designsystem.icon.HitzIcons
import eus.hitz.core.designsystem.preview.HitzPreviews
import eus.hitz.core.designsystem.theme.HitzSpacing
import eus.hitz.core.designsystem.theme.HitzTheme
import eus.hitz.core.domain.gamification.AchievementProgress
import eus.hitz.core.domain.gamification.AchievementType
import eus.hitz.core.domain.gamification.LeagueState
import eus.hitz.core.domain.gamification.PlayerSummary
import eus.hitz.core.domain.gamification.Quest
import eus.hitz.core.domain.gamification.QuestProgress
import eus.hitz.core.domain.gamification.QuestType
import eus.hitz.core.domain.settings.UserSettings

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val permission = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) viewModel.setReminderEnabled(true)
    }
    ProfileContent(
        state = state,
        onSpeakingChange = viewModel::setSpeakingEnabled,
        onReminderChange = { enabled ->
            val needsPermission = enabled && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) !=
                PackageManager.PERMISSION_GRANTED
            if (needsPermission) {
                permission.launch(Manifest.permission.POST_NOTIFICATIONS)
            } else {
                viewModel.setReminderEnabled(enabled)
            }
        },
        modifier = modifier,
    )
}

/** Stats, today's quests, achievements and settings. */
@Composable
internal fun ProfileContent(
    state: ProfileUiState,
    onSpeakingChange: (Boolean) -> Unit,
    onReminderChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(modifier.fillMaxSize()) {
        when (state) {
            ProfileUiState.Loading -> Box(contentAlignment = Alignment.Center) { LoadingIndicator() }
            is ProfileUiState.Ready -> LazyColumn(
                contentPadding = PaddingValues(vertical = HitzSpacing.xl),
                verticalArrangement = Arrangement.spacedBy(HitzSpacing.sm),
            ) {
                item(key = "header") { Header(state) }
                sectionTitle(R.string.feature_profile_quests)
                items(state.player.quests, key = { "quest-${it.quest.id}" }) { QuestRow(it) }
                sectionTitle(R.string.feature_profile_achievements)
                items(state.player.achievements, key = { "achievement-${it.type}" }) { AchievementRow(it) }
                sectionTitle(R.string.feature_profile_settings)
                item(key = "speaking") {
                    SettingRow(
                        title = stringResource(R.string.feature_profile_speaking),
                        description = stringResource(R.string.feature_profile_speaking_description),
                        checked = state.settings.speakingEnabled,
                        onChange = onSpeakingChange,
                    )
                }
                item(key = "reminder") {
                    SettingRow(
                        title = stringResource(R.string.feature_profile_reminder),
                        description = stringResource(R.string.feature_profile_reminder_description),
                        checked = state.settings.reminderEnabled,
                        onChange = onReminderChange,
                    )
                }
            }
        }
    }
}

@Composable
private fun Header(state: ProfileUiState.Ready) {
    val player = state.player
    Column(
        modifier = Modifier.padding(horizontal = HitzSpacing.xl),
        verticalArrangement = Arrangement.spacedBy(HitzSpacing.md),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(HitzSpacing.lg)
        ) {
            HitzMascot(contentDescription = null)
            Text(
                text = stringResource(R.string.feature_profile_title),
                style = MaterialTheme.typography.headlineMediumEmphasized,
                modifier = Modifier.semantics { heading() },
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(HitzSpacing.sm)) {
            StatCard(
                icon = HitzIcons.Streak,
                value = player.streak.toString(),
                label = stringResource(R.string.feature_profile_stat_streak),
                modifier = Modifier.weight(1f),
            )
            StatCard(
                icon = HitzIcons.Star,
                value = player.longestStreak.toString(),
                label = stringResource(R.string.feature_profile_stat_longest),
                modifier = Modifier.weight(1f),
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(HitzSpacing.sm)) {
            StatCard(
                icon = HitzIcons.Xp,
                value = player.totalXp.toString(),
                label = stringResource(R.string.feature_profile_stat_xp),
                modifier = Modifier.weight(1f),
            )
            StatCard(
                icon = HitzIcons.Gem,
                value = player.gems.toString(),
                label = stringResource(R.string.feature_profile_stat_gems),
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun StatCard(icon: Int, value: String, label: String, modifier: Modifier = Modifier) {
    OutlinedCard(modifier.semantics(mergeDescendants = true) {}) {
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
private fun QuestRow(progress: QuestProgress) {
    val quest = progress.quest
    val title = when (quest.type) {
        QuestType.EarnXp -> stringResource(R.string.feature_profile_quest_xp, quest.target)
        QuestType.CompleteSessions -> pluralStringResource(
            R.plurals.feature_profile_quest_sessions,
            quest.target,
            quest.target
        )
        QuestType.PerfectSessions -> pluralStringResource(
            R.plurals.feature_profile_quest_perfect,
            quest.target,
            quest.target
        )
        QuestType.CompleteStory -> stringResource(R.string.feature_profile_quest_story)
        QuestType.CompleteReview -> stringResource(R.string.feature_profile_quest_review)
    }
    val supporting = if (progress.isDone) {
        stringResource(R.string.feature_profile_quest_done, quest.gems)
    } else {
        stringResource(R.string.feature_profile_quest_progress, progress.progress, quest.target, quest.gems)
    }
    ProgressRow(title, supporting, progress.progress.toFloat() / quest.target, done = progress.isDone)
}

@Composable
private fun AchievementRow(progress: AchievementProgress) {
    val name = stringResource(
        when (progress.type) {
            AchievementType.Streak -> R.string.feature_profile_achievement_streak
            AchievementType.Xp -> R.string.feature_profile_achievement_xp
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
            onSpeakingChange = {},
            onReminderChange = {},
        )
    }
}

internal object ProfilePreviewData {
    val ready = ProfileUiState.Ready(
        player = PlayerSummary(
            streak = 4,
            practicedToday = true,
            longestStreak = 9,
            totalXp = 640,
            weeklyXp = 85,
            gems = 75,
            hearts = 5,
            nextHeartAt = null,
            streakFreezes = 1,
            league = LeagueState(tier = 1),
            quests = listOf(
                QuestProgress(
                    Quest(QuestType.EarnXp, 30, 10),
                    progress = 30,
                    claimed = true
                ),
                QuestProgress(
                    Quest(QuestType.PerfectSessions, 2, 10),
                    progress = 1,
                    claimed = false
                ),
                QuestProgress(
                    Quest(QuestType.CompleteStory, 1, 10),
                    progress = 0,
                    claimed = false
                ),
            ),
            achievements = listOf(
                AchievementProgress(AchievementType.Streak, 9),
                AchievementProgress(AchievementType.Xp, 640),
                AchievementProgress(AchievementType.Lessons, 31),
                AchievementProgress(AchievementType.Perfect, 12),
                AchievementProgress(AchievementType.Stories, 3),
            ),
        ),
        settings = UserSettings(speakingEnabled = true, reminderEnabled = false),
    )
}
