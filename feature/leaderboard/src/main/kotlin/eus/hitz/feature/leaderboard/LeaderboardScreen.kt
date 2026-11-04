@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package eus.hitz.feature.leaderboard

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
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
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import eus.hitz.core.designsystem.preview.HitzPreviews
import eus.hitz.core.designsystem.theme.HitzSpacing
import eus.hitz.core.designsystem.theme.HitzTheme
import eus.hitz.core.domain.gamification.LeagueOutcome
import eus.hitz.core.domain.gamification.LeagueView
import eus.hitz.core.domain.gamification.Leagues
import eus.hitz.core.domain.gamification.Standing

/** League names: Basque mountains, from the lowest tier to the highest. */
private val TierNames = listOf(
    R.string.feature_leaderboard_tier_0,
    R.string.feature_leaderboard_tier_1,
    R.string.feature_leaderboard_tier_2,
    R.string.feature_leaderboard_tier_3,
    R.string.feature_leaderboard_tier_4,
    R.string.feature_leaderboard_tier_5,
)

@StringRes
internal fun tierName(tier: Int): Int = TierNames[tier.coerceIn(TierNames.indices)]

private val TierShapes = listOf(
    MaterialShapes.Circle,
    MaterialShapes.Pill,
    MaterialShapes.Cookie4Sided,
    MaterialShapes.Cookie9Sided,
    MaterialShapes.Sunny,
    MaterialShapes.SoftBurst,
)

@Composable
fun LeaderboardScreen(
    modifier: Modifier = Modifier,
    viewModel: LeaderboardViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    LeaderboardContent(state, modifier)
}

/** Weekly league: tier, time left, and the ranking with its promotion and demotion zones. */
@Composable
internal fun LeaderboardContent(state: LeaderboardUiState, modifier: Modifier = Modifier) {
    Surface(modifier.fillMaxSize()) {
        when (state) {
            LeaderboardUiState.Loading -> Box(contentAlignment = Alignment.Center) { LoadingIndicator() }
            is LeaderboardUiState.Ready -> League(state.league)
        }
    }
}

@Composable
private fun League(league: LeagueView) {
    val promotes = league.tier < Leagues.TIERS - 1
    val demotes = league.tier > 0
    LazyColumn(contentPadding = PaddingValues(vertical = HitzSpacing.lg)) {
        item(key = "header") { LeagueHeader(league) }
        items(league.standings, key = { it.rank }) { standing ->
            StandingRow(standing)
            when {
                promotes && standing.rank == Leagues.PROMOTED -> ZoneDivider(
                    R.string.feature_leaderboard_promotion_zone
                )
                demotes && standing.rank == Leagues.GROUP_SIZE - Leagues.DEMOTED ->
                    ZoneDivider(R.string.feature_leaderboard_demotion_zone)
            }
        }
    }
}

@Composable
private fun LeagueHeader(league: LeagueView) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = HitzSpacing.xl),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(HitzSpacing.sm),
    ) {
        TierBadges(league.tier)
        Text(
            text = stringResource(R.string.feature_leaderboard_league, stringResource(tierName(league.tier))),
            style = MaterialTheme.typography.headlineMediumEmphasized,
            modifier = Modifier.semantics { heading() },
        )
        Text(
            text = pluralStringResource(R.plurals.feature_leaderboard_days_left, league.daysLeft, league.daysLeft),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        val rank = league.lastRank
        val outcome = league.lastOutcome
        if (rank != null && outcome != null) {
            Text(
                text = stringResource(
                    when (outcome) {
                        LeagueOutcome.Promoted -> R.string.feature_leaderboard_last_promoted
                        LeagueOutcome.Stayed -> R.string.feature_leaderboard_last_stayed
                        LeagueOutcome.Demoted -> R.string.feature_leaderboard_last_demoted
                    },
                    rank,
                ),
                style = MaterialTheme.typography.bodyMedium,
            )
        }
        Text(
            text = stringResource(R.string.feature_leaderboard_rules, Leagues.PROMOTED),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = HitzSpacing.md),
        )
    }
}

/** One expressive shape per tier; reached tiers are filled. */
@Composable
private fun TierBadges(current: Int) {
    val description = stringResource(R.string.feature_leaderboard_tier_of, current + 1, Leagues.TIERS)
    Row(
        modifier = Modifier.clearAndSetSemantics { contentDescription = description },
        horizontalArrangement = Arrangement.spacedBy(HitzSpacing.sm),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        TierShapes.forEachIndexed { tier, shape ->
            val size = if (tier == current) 56.dp else 32.dp
            Surface(
                modifier = Modifier.size(size),
                shape = shape.toShape(),
                color = when {
                    tier == current -> MaterialTheme.colorScheme.primary
                    tier < current -> MaterialTheme.colorScheme.primaryContainer
                    else -> MaterialTheme.colorScheme.surfaceContainerHighest
                },
            ) {}
        }
    }
}

@Composable
private fun StandingRow(standing: Standing) {
    val name = if (standing.isLearner) stringResource(R.string.feature_leaderboard_you) else standing.name
    val xp = stringResource(R.string.feature_leaderboard_xp, standing.xp)
    val scheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography
    ListItem(
        modifier = Modifier
            .padding(horizontal = HitzSpacing.sm)
            .semantics(mergeDescendants = true) {},
        headlineContent = {
            Text(
                name,
                style = if (standing.isLearner) typography.titleMediumEmphasized else typography.titleMedium,
            )
        },
        leadingContent = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = standing.rank.toString(),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.width(32.dp),
                )
                Surface(
                    modifier = Modifier.size(40.dp),
                    shape = CircleShape,
                    color = if (standing.isLearner) scheme.primary else scheme.secondaryContainer,
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(name.take(1), style = MaterialTheme.typography.titleMedium)
                    }
                }
            }
        },
        trailingContent = { Text(xp, style = MaterialTheme.typography.labelLarge) },
        colors = ListItemDefaults.colors(
            containerColor = if (standing.isLearner) scheme.secondaryContainer else Color.Transparent,
        ),
    )
}

@Composable
private fun ZoneDivider(@StringRes label: Int) {
    Row(
        modifier = Modifier.padding(horizontal = HitzSpacing.lg, vertical = HitzSpacing.sm),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(HitzSpacing.sm),
    ) {
        HorizontalDivider(Modifier.weight(1f))
        Text(
            stringResource(label),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary
        )
        HorizontalDivider(Modifier.weight(1f))
    }
}

internal object LeaderboardPreviewData {
    val league = LeagueView(
        tier = 1,
        daysLeft = 3,
        standings = Leagues.standings(weekStart = 20_394, tier = 1, learnerXp = 45, elapsed = 0.6),
        lastRank = 2,
        lastOutcome = LeagueOutcome.Promoted,
    )
}

@HitzPreviews
@Composable
private fun LeaderboardPreview() {
    HitzTheme { LeaderboardContent(LeaderboardUiState.Ready(LeaderboardPreviewData.league)) }
}
