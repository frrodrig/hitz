package eus.hitz.feature.leaderboard.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import eus.hitz.feature.leaderboard.LeaderboardScreen
import kotlinx.serialization.Serializable

@Serializable
data object LeaderboardRoute

fun NavController.navigateToLeaderboard(navOptions: NavOptions? = null) = navigate(LeaderboardRoute, navOptions)

fun NavGraphBuilder.leaderboardScreen() {
    composable<LeaderboardRoute> { LeaderboardScreen() }
}
