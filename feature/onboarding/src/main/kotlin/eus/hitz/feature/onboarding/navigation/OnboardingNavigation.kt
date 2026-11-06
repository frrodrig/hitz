package eus.hitz.feature.onboarding.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import eus.hitz.feature.onboarding.OnboardingScreen
import eus.hitz.feature.onboarding.StartingLevel
import kotlinx.serialization.Serializable

/** The first-run introduction, the start destination until it is completed. */
@Serializable
data object OnboardingRoute

fun NavGraphBuilder.onboardingScreen(onFinish: (StartingLevel) -> Unit) {
    composable<OnboardingRoute> { OnboardingScreen(onFinish = onFinish) }
}
