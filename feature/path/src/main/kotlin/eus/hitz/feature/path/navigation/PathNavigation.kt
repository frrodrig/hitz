package eus.hitz.feature.path.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import eus.hitz.feature.path.PathScreen
import eus.hitz.feature.path.PathViewModel
import eus.hitz.feature.path.guidebook.GuidebookScreen
import eus.hitz.feature.path.intro.UnitIntroScreen
import eus.hitz.feature.path.sections.SectionsScreen
import kotlinx.serialization.Serializable

@Serializable
data object PathRoute

/** The guidebook of a unit. The argument name is read by the guidebook ViewModel. */
@Serializable
data class GuidebookRoute(val unitId: String)

/** A unit's introduction, then the lesson it was opened for. Argument names are read by the ViewModel. */
@Serializable
data class UnitIntroRoute(val unitId: String, val levelId: String, val lessonId: String?)

/** Every section of the course, opened from the section's name over the path. */
@Serializable
data object SectionsRoute

fun NavController.navigateToPath(navOptions: NavOptions? = null) = navigate(PathRoute, navOptions)

fun NavController.navigateToSections() = navigate(SectionsRoute)

/** Back from the sections overview to the path, which then shows [sectionId]. */
fun NavController.showSection(sectionId: String) {
    previousBackStackEntry?.savedStateHandle?.set(PathViewModel.SELECTED_SECTION, sectionId)
    popBackStack()
}

fun NavController.navigateToGuidebook(unitId: String) = navigate(GuidebookRoute(unitId))

fun NavController.navigateToUnitIntro(unitId: String, levelId: String, lessonId: String?) =
    navigate(UnitIntroRoute(unitId, levelId, lessonId))

fun NavGraphBuilder.pathScreen(
    onStartLevel: (unitId: String, levelId: String, lessonId: String?) -> Unit,
    onOpenGuidebook: (unitId: String) -> Unit,
    onStartStory: (unitId: String, levelId: String) -> Unit,
    onOpenProfile: () -> Unit,
    onOpenUnitIntro: (unitId: String, levelId: String, lessonId: String?) -> Unit,
    onOpenSections: () -> Unit,
    onJump: (sectionId: String) -> Unit,
) {
    composable<PathRoute> {
        PathScreen(
            onStartLevel = onStartLevel,
            onOpenUnitIntro = onOpenUnitIntro,
            onOpenGuidebook = onOpenGuidebook,
            onStartStory = onStartStory,
            onOpenProfile = onOpenProfile,
            onOpenSections = onOpenSections,
            onJump = onJump,
        )
    }
}

/** [onSelect] goes back to the path showing a section; [onJump] starts a test to jump to one. */
fun NavGraphBuilder.sectionsScreen(
    onBack: () -> Unit,
    onSelect: (sectionId: String) -> Unit,
    onJump: (sectionId: String) -> Unit,
) {
    composable<SectionsRoute> { SectionsScreen(onBack = onBack, onSelect = onSelect, onJump = onJump) }
}

fun NavGraphBuilder.guidebookScreen(onBack: () -> Unit) {
    composable<GuidebookRoute> { GuidebookScreen(onBack = onBack) }
}

/** [onStart] plays the lesson the introduction was opened for. */
fun NavGraphBuilder.unitIntroScreen(
    onStart: (unitId: String, levelId: String, lessonId: String?) -> Unit,
    onOpenGuidebook: (unitId: String) -> Unit,
    onBack: () -> Unit,
) {
    composable<UnitIntroRoute> { entry ->
        val route = entry.toRoute<UnitIntroRoute>()
        UnitIntroScreen(
            onStart = { onStart(route.unitId, route.levelId, route.lessonId) },
            onOpenGuidebook = onOpenGuidebook,
            onBack = onBack,
        )
    }
}
