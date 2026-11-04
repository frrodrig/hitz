package eus.hitz.feature.lesson.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import eus.hitz.feature.lesson.LessonScreen
import kotlinx.serialization.Serializable

@Serializable
data object LessonRoute

fun NavController.navigateToLesson(navOptions: NavOptions? = null) = navigate(LessonRoute, navOptions)

fun NavGraphBuilder.lessonScreen() {
    composable<LessonRoute> { LessonScreen() }
}
