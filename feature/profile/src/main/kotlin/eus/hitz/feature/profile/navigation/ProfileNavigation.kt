package eus.hitz.feature.profile.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import eus.hitz.feature.profile.ProfileScreen
import eus.hitz.feature.profile.score.ScoreScreen
import eus.hitz.feature.profile.settings.SettingsScreen
import kotlinx.serialization.Serializable

@Serializable
data object ProfileRoute

/** The score in detail: its history and what the learner can do. */
@Serializable
data object ScoreRoute

/** The learner's settings, reached from the gear in the profile. */
@Serializable
data object SettingsRoute

fun NavController.navigateToProfile(navOptions: NavOptions? = null) = navigate(ProfileRoute, navOptions)

fun NavController.navigateToScore() = navigate(ScoreRoute)

fun NavController.navigateToSettings() = navigate(SettingsRoute)

fun NavGraphBuilder.profileScreen(onOpenScore: () -> Unit, onOpenSettings: () -> Unit) {
    composable<ProfileRoute> { ProfileScreen(onOpenScore = onOpenScore, onOpenSettings = onOpenSettings) }
}

fun NavGraphBuilder.scoreScreen(onBack: () -> Unit) {
    composable<ScoreRoute> { ScoreScreen(onBack = onBack) }
}

fun NavGraphBuilder.settingsScreen(onBack: () -> Unit) {
    composable<SettingsRoute> { SettingsScreen(onBack = onBack) }
}
