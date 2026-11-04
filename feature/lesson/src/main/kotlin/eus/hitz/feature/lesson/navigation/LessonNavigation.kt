package eus.hitz.feature.lesson.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import eus.hitz.feature.lesson.LessonScreen
import kotlinx.serialization.Serializable

/** Full-screen lesson. Argument names are read by [eus.hitz.feature.lesson.LessonViewModel]. */
@Serializable
data class LessonRoute(val unitId: String, val lessonId: String)

fun NavController.navigateToLesson(unitId: String, lessonId: String, navOptions: NavOptions? = null) =
    navigate(LessonRoute(unitId, lessonId), navOptions)

fun NavGraphBuilder.lessonScreen(onClose: () -> Unit) {
    composable<LessonRoute> { LessonScreen(onClose = onClose) }
}
