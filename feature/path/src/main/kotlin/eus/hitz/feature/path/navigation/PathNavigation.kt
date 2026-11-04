package eus.hitz.feature.path.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import eus.hitz.feature.path.PathScreen
import kotlinx.serialization.Serializable

@Serializable
data object PathRoute

fun NavController.navigateToPath(navOptions: NavOptions? = null) = navigate(PathRoute, navOptions)

/** [onStartLesson] receives the unit id and the lesson id. */
fun NavGraphBuilder.pathScreen(onStartLesson: (unitId: String, lessonId: String) -> Unit) {
    composable<PathRoute> { PathScreen(onStartLesson = onStartLesson) }
}
