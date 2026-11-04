package eus.hitz.feature.path.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import eus.hitz.feature.path.PathScreen
import eus.hitz.feature.path.guidebook.GuidebookScreen
import kotlinx.serialization.Serializable

@Serializable
data object PathRoute

/** The guidebook of a unit. The argument name is read by the guidebook ViewModel. */
@Serializable
data class GuidebookRoute(val unitId: String)

fun NavController.navigateToPath(navOptions: NavOptions? = null) = navigate(PathRoute, navOptions)

fun NavController.navigateToGuidebook(unitId: String) = navigate(GuidebookRoute(unitId))

fun NavGraphBuilder.pathScreen(
    onStartLevel: (unitId: String, levelId: String, lessonId: String?) -> Unit,
    onOpenGuidebook: (unitId: String) -> Unit,
    onStartStory: (unitId: String, levelId: String) -> Unit,
    onOpenShop: () -> Unit,
    onOpenProfile: () -> Unit,
) {
    composable<PathRoute> {
        PathScreen(
            onStartLevel = onStartLevel,
            onOpenGuidebook = onOpenGuidebook,
            onStartStory = onStartStory,
            onOpenShop = onOpenShop,
            onOpenProfile = onOpenProfile,
        )
    }
}

fun NavGraphBuilder.guidebookScreen(onBack: () -> Unit) {
    composable<GuidebookRoute> { GuidebookScreen(onBack = onBack) }
}
