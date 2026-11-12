package eus.hitz.feature.profile.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import eus.hitz.feature.profile.ProfileScreen
import eus.hitz.feature.profile.score.ScoreScreen
import kotlinx.serialization.Serializable

@Serializable
data object ProfileRoute

/** The score in detail: its history and what the learner can do. */
@Serializable
data object ScoreRoute

fun NavController.navigateToProfile(navOptions: NavOptions? = null) = navigate(ProfileRoute, navOptions)

fun NavController.navigateToScore() = navigate(ScoreRoute)

fun NavGraphBuilder.profileScreen(onOpenScore: () -> Unit) {
    composable<ProfileRoute> { ProfileScreen(onOpenScore = onOpenScore) }
}

fun NavGraphBuilder.scoreScreen(onBack: () -> Unit) {
    composable<ScoreRoute> { ScoreScreen(onBack = onBack) }
}
