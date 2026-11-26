@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package eus.hitz.feature.lesson

import androidx.compose.animation.core.Animatable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.text
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import eus.hitz.core.designsystem.component.HitzButton
import eus.hitz.core.designsystem.component.HitzCelebration
import eus.hitz.core.designsystem.component.HitzMascot
import eus.hitz.core.designsystem.component.HitzStars
import eus.hitz.core.designsystem.component.MascotPose
import eus.hitz.core.designsystem.icon.HitzIcons
import eus.hitz.core.designsystem.motion.HitzReveal
import eus.hitz.core.designsystem.motion.rememberReducedMotion
import eus.hitz.core.designsystem.preview.HitzPreviews
import eus.hitz.core.designsystem.theme.HitzSpacing
import eus.hitz.core.designsystem.theme.HitzTextStyles
import eus.hitz.core.designsystem.theme.HitzTheme
import eus.hitz.core.designsystem.theme.raisedSurface
import kotlin.math.roundToInt

private const val PERCENT = 100
private val HERO_CORNER = 48.dp
private val MASCOT_SIZE = 168.dp
private val TILE_HEIGHT = 112.dp
private val TILE_OUTER = 28.dp
private val TILE_INNER = 8.dp
private val TILE_GAP = 3.dp
private val STAR_SIZE = 44.dp
private val GOAL_ICON = 24.dp
private val BADGE_SIZE = 48.dp
private val BADGE_ICON = 24.dp
private val CARD_ELEVATION = 1.dp

/**
 * The end of a lesson, one of the app's two hero moments. Mikel celebrates on the tertiary
 * container and first-try accuracy is the hero numeral, counting up; then time and today's goal as
 * a connected pair, and what the session added. The pieces come in one after the other
 * ([HitzReveal]); with animations removed they are simply there.
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
            Hero(
                accuracy = (state.result.accuracy * PERCENT).roundToInt(),
                goalReached = reward?.goalReached == true,
                stars = state.stars,
            )
            Column(
                modifier = Modifier.padding(horizontal = HitzSpacing.lg),
                verticalArrangement = Arrangement.spacedBy(HitzSpacing.lg),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                HitzReveal(order = 3) { ResultTiles(state) }
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
private fun Hero(accuracy: Int, goalReached: Boolean, stars: Int?) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(bottomStart = HERO_CORNER, bottomEnd = HERO_CORNER),
        color = MaterialTheme.colorScheme.tertiaryContainer,
        contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
    ) {
        Column(
            modifier = Modifier
                .statusBarsPadding()
                .padding(start = HitzSpacing.xl, end = HitzSpacing.xl, top = HitzSpacing.lg, bottom = HitzSpacing.xxl),
            verticalArrangement = Arrangement.spacedBy(HitzSpacing.md),
        ) {
            Row(verticalAlignment = Alignment.Bottom) {
                HitzReveal(order = 1, modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (stars != null) {
                            pluralStringResource(R.plurals.feature_lesson_challenge_stars, stars, stars)
                        } else {
                            stringResource(R.string.feature_lesson_finished_title)
                        },
                        style = MaterialTheme.typography.titleLargeEmphasized,
                        modifier = Modifier.semantics { heading() },
                    )
                }
                HitzReveal(order = 0) {
                    Box(contentAlignment = Alignment.Center) {
                        HitzMascot(pose = MascotPose.Bien, size = MASCOT_SIZE, contentDescription = null)
                        // Completing the daily goal is celebrated around the mascot, without taking layout space.
                        if (goalReached) HitzCelebration(Modifier.matchParentSize().wrapContentSize(unbounded = true))
                    }
                }
            }
            if (stars != null) HitzReveal(order = 2) { HitzStars(earned = stars, size = STAR_SIZE) }
            HitzReveal(order = 2) {
                Column(Modifier.semantics(mergeDescendants = true) {}) {
                    CountUp(target = accuracy, style = HitzTextStyles.heroNumeral)
                    Text(
                        text = stringResource(R.string.feature_lesson_stat_first_try),
                        style = MaterialTheme.typography.titleMedium,
                    )
                }
            }
        }
    }
}

/**
 * A percentage that counts up to [target] with a slow effects spring. Screen readers and tests
 * get the final figure at once.
 */
@Composable
private fun CountUp(target: Int, style: TextStyle) {
    val still = rememberReducedMotion() || LocalInspectionMode.current
    val value = remember { Animatable(if (still) target.toFloat() else 0f) }
    val spec = MaterialTheme.motionScheme.slowEffectsSpec<Float>()
    LaunchedEffect(target) { value.animateTo(target.toFloat(), spec) }
    val final = stringResource(R.string.feature_lesson_stat_percent, target)
    Text(
        text = stringResource(R.string.feature_lesson_stat_percent, value.value.roundToInt()),
        style = style,
        modifier = Modifier.clearAndSetSemantics { text = AnnotatedString(final) },
    )
}

/** From this font scale on, the result tiles are stacked, each as wide as the screen. */
private const val STACKED_FONT_SCALE = 1.5f

/**
 * Active time and today's goal, a connected pair: their facing corners are small. Side by side
 * normally; stacked with large fonts, where "Cumplido" does not fit in half the width.
 */
@Composable
private fun ResultTiles(state: LessonUiState.Finished) {
    val reward = state.reward
    val paired = reward != null
    if (LocalDensity.current.fontScale >= STACKED_FONT_SCALE) {
        Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(TILE_GAP)) {
            TimeTile(
                state = state,
                shape = tileShape(
                    bottomStart = if (paired) TILE_INNER else TILE_OUTER,
                    bottomEnd = if (paired) TILE_INNER else TILE_OUTER,
                ),
                modifier = Modifier.fillMaxWidth(),
            )
            if (reward != null) {
                GoalTile(reward, tileShape(topStart = TILE_INNER, topEnd = TILE_INNER), Modifier.fillMaxWidth())
            }
        }
    } else {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(TILE_GAP)) {
            TimeTile(
                state = state,
                shape = tileShape(
                    topEnd = if (paired) TILE_INNER else TILE_OUTER,
                    bottomEnd = if (paired) TILE_INNER else TILE_OUTER,
                ),
                modifier = Modifier.weight(1f),
            )
            if (reward != null) {
                GoalTile(reward, tileShape(topStart = TILE_INNER, bottomStart = TILE_INNER), Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun TimeTile(state: LessonUiState.Finished, shape: Shape, modifier: Modifier) {
    ResultTile(
        label = stringResource(R.string.feature_lesson_stat_time),
        shape = shape,
        container = MaterialTheme.colorScheme.surfaceContainerHigh,
        content = MaterialTheme.colorScheme.onSurface,
        modifier = modifier,
    ) {
        Text(
            text = stringResource(R.string.feature_lesson_stat_minutes, state.activeMinutes),
            style = MaterialTheme.typography.headlineMediumEmphasized,
        )
    }
}

@Composable
private fun GoalTile(reward: RewardUi, shape: Shape, modifier: Modifier) {
    val met = reward.after >= PERCENT
    val success = HitzTheme.extendedColors.success
    ResultTile(
        label = stringResource(R.string.feature_lesson_stat_goal),
        shape = shape,
        container = if (met) success.container else MaterialTheme.colorScheme.secondaryContainer,
        content = if (met) success.onContainer else MaterialTheme.colorScheme.onSecondaryContainer,
        modifier = modifier,
    ) {
        // Over 100 % reads as a mistake: a met goal is simply met.
        if (met) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(HitzSpacing.xs),
            ) {
                Icon(painterResource(HitzIcons.Check), contentDescription = null, Modifier.size(GOAL_ICON))
                Text(
                    text = stringResource(R.string.feature_lesson_stat_goal_met),
                    style = MaterialTheme.typography.titleLargeEmphasized,
                )
            }
        } else {
            Text(
                text = stringResource(R.string.feature_lesson_stat_percent, reward.after),
                style = MaterialTheme.typography.headlineMediumEmphasized,
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
    shape: Shape,
    container: Color,
    content: Color,
    modifier: Modifier = Modifier,
    value: @Composable () -> Unit,
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
            modifier = Modifier.padding(HitzSpacing.lg),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(label, style = MaterialTheme.typography.labelLarge)
            Box(Modifier.padding(top = HitzSpacing.lg)) { value() }
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
    HitzReveal(order = 4) {
        Text(message, style = MaterialTheme.typography.titleMedium, textAlign = TextAlign.Center)
    }
    if (reward.newAchievements > 0) {
        HitzReveal(order = 5) { AchievementCard(reward.newAchievements) }
    }
}

/** A new achievement, as a card with its badge: the one expressive shape on this screen below the hero. */
@Composable
private fun AchievementCard(count: Int) {
    Surface(
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.raisedSurface,
        shadowElevation = CARD_ELEVATION,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.padding(HitzSpacing.md),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(HitzSpacing.md),
        ) {
            Surface(
                shape = MaterialShapes.Cookie4Sided.toShape(),
                color = MaterialTheme.colorScheme.tertiary,
                contentColor = MaterialTheme.colorScheme.onTertiary,
                modifier = Modifier.size(BADGE_SIZE),
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(painterResource(HitzIcons.Trophy), contentDescription = null, Modifier.size(BADGE_ICON))
                }
            }
            Text(
                text = pluralStringResource(R.plurals.feature_lesson_new_achievements, count, count),
                style = MaterialTheme.typography.titleMediumEmphasized,
            )
        }
    }
}

@HitzPreviews
@Composable
private fun LessonFinishedPreview() {
    HitzTheme { LessonFinished(LessonPreviewData.finished, onContinue = {}) }
}

@HitzPreviews
@Composable
private fun ChallengeFinishedPreview() {
    HitzTheme { LessonFinished(LessonPreviewData.finished.copy(stars = 2), onContinue = {}) }
}
