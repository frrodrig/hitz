package eus.hitz.feature.lesson.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import eus.hitz.feature.lesson.LessonScreen
import kotlinx.serialization.Serializable

/**
 * Full-screen session for a level of the path. [lessonId] is set for lesson levels and null for
 * generated levels (review, legendary). Argument names are read by
 * [eus.hitz.feature.lesson.LessonViewModel].
 */
@Serializable
data class LessonRoute(val unitId: String, val levelId: String, val lessonId: String? = null)

fun NavController.navigateToLesson(unitId: String, levelId: String, lessonId: String?, navOptions: NavOptions? = null) =
    navigate(LessonRoute(unitId, levelId, lessonId), navOptions)

fun NavGraphBuilder.lessonScreen(onClose: () -> Unit) {
    composable<LessonRoute> { LessonScreen(onClose = onClose) }
}
