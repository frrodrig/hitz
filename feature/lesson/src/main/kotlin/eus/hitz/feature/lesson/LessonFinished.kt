@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package eus.hitz.feature.lesson

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import eus.hitz.core.designsystem.component.HitzButton
import eus.hitz.core.designsystem.component.HitzCelebration
import eus.hitz.core.designsystem.component.HitzMascot
import eus.hitz.core.designsystem.component.MascotPose
import eus.hitz.core.designsystem.icon.HitzIcons
import eus.hitz.core.designsystem.preview.HitzPreviews
import eus.hitz.core.designsystem.theme.HitzSpacing
import eus.hitz.core.designsystem.theme.HitzTheme
import kotlin.math.roundToInt

private const val PERCENT = 100
private val HERO_CORNER = 48.dp
private val MASCOT_SIZE = 200.dp
private val TILE_HEIGHT = 120.dp
private val TILE_OUTER = 28.dp
private val TILE_INNER = 12.dp
private val BADGE_SIZE = 28.dp
private val BADGE_ICON = 16.dp

/**
 * The end of a lesson: a hero with the Galtzagorri on the tertiary container, three result tiles
 * (first-try accuracy, active time and today's goal) and what the session added.
 */
@Composable
internal fun LessonFinished(state: LessonUiState.Finished, onContinue: () -> Unit, modifier: Modifier = Modifier) {
    val reward = state.reward
    Column(modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(HitzSpacing.xl),
        ) {
            Hero(goalReached = reward?.goalReached == true)
            Column(
                modifier = Modifier.padding(horizontal = HitzSpacing.lg),
                verticalArrangement = Arrangement.spacedBy(HitzSpacing.lg),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                ResultTiles(state)
                if (reward != null) RewardMessage(reward)
            }
        }
        HitzButton(
            text = stringResource(R.string.feature_lesson_continue),
            onClick = onContinue,
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(HitzSpacing.lg),
        )
    }
}

@Composable
private fun Hero(goalReached: Boolean) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(bottomStart = HERO_CORNER, bottomEnd = HERO_CORNER),
        color = MaterialTheme.colorScheme.tertiaryContainer,
        contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
    ) {
        Column(
            modifier = Modifier
                .statusBarsPadding()
                .padding(start = HitzSpacing.xl, end = HitzSpacing.xl, top = HitzSpacing.xl, bottom = HitzSpacing.xxl),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(HitzSpacing.lg),
        ) {
            Box(contentAlignment = Alignment.Center) {
                HitzMascot(pose = MascotPose.Bien, size = MASCOT_SIZE, contentDescription = null)
                // Completing the daily goal is celebrated around the mascot, without taking layout space.
                if (goalReached) HitzCelebration(Modifier.matchParentSize().wrapContentSize(unbounded = true))
            }
            Text(
                text = stringResource(R.string.feature_lesson_finished_title),
                style = MaterialTheme.typography.headlineLargeEmphasized,
                textAlign = TextAlign.Center,
                modifier = Modifier.semantics { heading() },
            )
        }
    }
}

/** Three tiles that lean into each other: each has one small corner towards the next. */
@Composable
private fun ResultTiles(state: LessonUiState.Finished) {
    val reward = state.reward
    val scheme = MaterialTheme.colorScheme
    val success = HitzTheme.extendedColors.success
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(HitzSpacing.sm)) {
        ResultTile(
            label = stringResource(R.string.feature_lesson_stat_first_try),
            value = stringResource(
                R.string.feature_lesson_stat_percent,
                (state.result.accuracy * PERCENT).roundToInt()
            ),
            shape = tileShape(bottomEnd = TILE_INNER),
            container = scheme.surfaceContainerHigh,
            content = scheme.onSurface,
            modifier = Modifier.weight(1f),
        )
        ResultTile(
            label = stringResource(R.string.feature_lesson_stat_time),
            value = stringResource(R.string.feature_lesson_stat_minutes, state.activeMinutes),
            shape = tileShape(topEnd = TILE_INNER),
            container = scheme.surfaceContainerHigh,
            content = scheme.onSurface,
            modifier = Modifier.weight(1f),
        )
        if (reward != null) {
            val met = reward.after >= PERCENT
            ResultTile(
                label = stringResource(R.string.feature_lesson_stat_goal),
                value = stringResource(R.string.feature_lesson_stat_percent, reward.after),
                shape = tileShape(topStart = TILE_INNER),
                container = if (met) success.container else scheme.secondaryContainer,
                content = if (met) success.onContainer else scheme.onSecondaryContainer,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

private fun tileShape(
    topStart: Dp = TILE_OUTER,
    topEnd: Dp = TILE_OUTER,
    bottomEnd: Dp = TILE_OUTER,
    bottomStart: Dp = TILE_OUTER,
) = RoundedCornerShape(topStart = topStart, topEnd = topEnd, bottomEnd = bottomEnd, bottomStart = bottomStart)

@Composable
private fun ResultTile(
    label: String,
    value: String,
    shape: Shape,
    container: Color,
    content: Color,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .heightIn(min = TILE_HEIGHT)
            .semantics(mergeDescendants = true) {},
        shape = shape,
        color = container,
        contentColor = content,
    ) {
        Column(
            modifier = Modifier.padding(HitzSpacing.md),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(label, style = MaterialTheme.typography.labelLarge)
            Text(
                value,
                style = MaterialTheme.typography.headlineMediumEmphasized,
                modifier = Modifier.padding(top = HitzSpacing.lg),
            )
        }
    }
}

/** How today's goal stands now, and any achievement the session unlocked. */
@Composable
private fun RewardMessage(reward: RewardUi) {
    val message = when {
        reward.goalReached -> stringResource(R.string.feature_lesson_goal_reached)
        reward.after >= PERCENT -> stringResource(R.string.feature_lesson_goal_already_met)
        else -> pluralStringResource(R.plurals.feature_lesson_goal_left, reward.minutesLeft, reward.minutesLeft)
    }
    Text(message, style = MaterialTheme.typography.titleMedium, textAlign = TextAlign.Center)
    if (reward.newAchievements > 0) {
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
        ) {
            Row(
                modifier = Modifier.padding(
                    start = HitzSpacing.xs,
                    end = HitzSpacing.md,
                    top = HitzSpacing.xs,
                    bottom = HitzSpacing.xs
                ),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(HitzSpacing.sm),
            ) {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(BADGE_SIZE)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            painterResource(HitzIcons.Trophy),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSecondary,
                            modifier = Modifier.size(BADGE_ICON),
                        )
                    }
                }
                Text(
                    text = pluralStringResource(
                        R.plurals.feature_lesson_new_achievements,
                        reward.newAchievements,
                        reward.newAchievements,
                    ),
                    style = MaterialTheme.typography.labelLarge,
                )
            }
        }
    }
}

@HitzPreviews
@Composable
private fun LessonFinishedPreview() {
    HitzTheme { LessonFinished(LessonPreviewData.finished, onContinue = {}) }
}
