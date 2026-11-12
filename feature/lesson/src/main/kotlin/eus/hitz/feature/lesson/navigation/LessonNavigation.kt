package eus.hitz.feature.lesson.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import eus.hitz.core.domain.practice.PracticeMode
import eus.hitz.feature.lesson.LessonScreen
import eus.hitz.feature.lesson.story.StoryScreen
import kotlinx.serialization.Serializable

/**
 * Full-screen session for a level of the path. [lessonId] is set for lesson levels and null for
 * generated levels (review, legendary). Argument names are read by
 * [eus.hitz.feature.lesson.LessonViewModel].
 */
@Serializable
data class LessonRoute(val unitId: String, val levelId: String, val lessonId: String? = null)

/**
 * A placement test for section [sectionId], played by the lesson screen. Argument name read by
 * [eus.hitz.feature.lesson.LessonViewModel].
 */
@Serializable
data class PlacementRoute(val sectionId: String)

/**
 * A practice session from the practice hub, played by the lesson screen: [mode] is a
 * [eus.hitz.core.domain.practice.PracticeMode] name; with [practiceUnit] it practises that unit
 * only. Argument names read by [eus.hitz.feature.lesson.LessonViewModel].
 */
@Serializable
data class PracticeSessionRoute(val mode: String, val practiceUnit: String? = null)

/** Full-screen story player for a story level, read by [eus.hitz.feature.lesson.story.StoryViewModel]. */
@Serializable
data class StoryRoute(val unitId: String, val levelId: String)

fun NavController.navigateToLesson(unitId: String, levelId: String, lessonId: String?, navOptions: NavOptions? = null) =
    navigate(LessonRoute(unitId, levelId, lessonId), navOptions)

fun NavController.navigateToStory(unitId: String, levelId: String, navOptions: NavOptions? = null) =
    navigate(StoryRoute(unitId, levelId), navOptions)

fun NavController.navigateToPlacement(sectionId: String, navOptions: NavOptions? = null) =
    navigate(PlacementRoute(sectionId), navOptions)

fun NavController.navigateToPractice(mode: PracticeMode, unitId: String? = null, navOptions: NavOptions? = null) =
    navigate(PracticeSessionRoute(mode.name, unitId), navOptions)

fun NavGraphBuilder.lessonScreen(onClose: () -> Unit) {
    composable<LessonRoute> { LessonScreen(onClose = onClose) }
}

/** [onNext] starts the test of the next section after passing one. */
fun NavGraphBuilder.placementScreen(onClose: () -> Unit, onNext: (sectionId: String) -> Unit) {
    composable<PlacementRoute> { LessonScreen(onClose = onClose, onNextPlacement = onNext) }
}

fun NavGraphBuilder.practiceSessionScreen(onClose: () -> Unit) {
    composable<PracticeSessionRoute> { LessonScreen(onClose = onClose) }
}

fun NavGraphBuilder.storyScreen(onClose: () -> Unit) {
    composable<StoryRoute> { StoryScreen(onClose = onClose) }
}
