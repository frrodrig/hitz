@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package eus.hitz.app.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ShortNavigationBar
import androidx.compose.material3.ShortNavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.material3.WideNavigationRail
import androidx.compose.material3.WideNavigationRailItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import eus.hitz.app.navigation.TopLevelDestination
import eus.hitz.core.designsystem.motion.HitzTransitions
import eus.hitz.core.designsystem.theme.HitzTheme
import eus.hitz.core.domain.practice.PracticeMode
import eus.hitz.feature.lesson.navigation.LessonRoute
import eus.hitz.feature.lesson.navigation.PlacementRoute
import eus.hitz.feature.lesson.navigation.PracticeSessionRoute
import eus.hitz.feature.lesson.navigation.StoryRoute
import eus.hitz.feature.lesson.navigation.lessonScreen
import eus.hitz.feature.lesson.navigation.navigateToLesson
import eus.hitz.feature.lesson.navigation.navigateToPlacement
import eus.hitz.feature.lesson.navigation.navigateToPractice
import eus.hitz.feature.lesson.navigation.navigateToStory
import eus.hitz.feature.lesson.navigation.placementScreen
import eus.hitz.feature.lesson.navigation.practiceSessionScreen
import eus.hitz.feature.lesson.navigation.storyScreen
import eus.hitz.feature.onboarding.StartingLevel
import eus.hitz.feature.onboarding.navigation.OnboardingRoute
import eus.hitz.feature.onboarding.navigation.onboardingScreen
import eus.hitz.feature.path.navigation.GuidebookRoute
import eus.hitz.feature.path.navigation.PathRoute
import eus.hitz.feature.path.navigation.UnitIntroRoute
import eus.hitz.feature.path.navigation.guidebookScreen
import eus.hitz.feature.path.navigation.navigateToGuidebook
import eus.hitz.feature.path.navigation.navigateToUnitIntro
import eus.hitz.feature.path.navigation.pathScreen
import eus.hitz.feature.path.navigation.unitIntroScreen
import eus.hitz.feature.practice.navigation.UnitReviewRoute
import eus.hitz.feature.practice.navigation.navigateToUnitReview
import eus.hitz.feature.practice.navigation.practiceScreen
import eus.hitz.feature.practice.navigation.unitReviewScreen
import eus.hitz.feature.profile.navigation.ScoreRoute
import eus.hitz.feature.profile.navigation.navigateToScore
import eus.hitz.feature.profile.navigation.profileScreen
import eus.hitz.feature.profile.navigation.scoreScreen

/** Destinations shown full screen, without the top-level navigation. */
private val fullScreenRoutes = listOf(
    LessonRoute::class,
    PlacementRoute::class,
    PracticeSessionRoute::class,
    StoryRoute::class,
    GuidebookRoute::class,
    UnitIntroRoute::class,
    UnitReviewRoute::class,
    OnboardingRoute::class,
    ScoreRoute::class,
)

/** Sessions rise over the path; details move forward along the horizontal axis. */
private val sessionRoutes = listOf(
    LessonRoute::class,
    PlacementRoute::class,
    PracticeSessionRoute::class,
    StoryRoute::class,
)
private val detailRoutes = listOf(
    GuidebookRoute::class,
    UnitIntroRoute::class,
    UnitReviewRoute::class,
    ScoreRoute::class,
)

/** How a destination moves in and out; see [HitzTransitions]. */
private enum class DestinationKind { TopLevel, Detail, Session }

private val NavDestination.kind: DestinationKind
    get() = when {
        sessionRoutes.any { hasRoute(it) } -> DestinationKind.Session
        detailRoutes.any { hasRoute(it) } -> DestinationKind.Detail
        else -> DestinationKind.TopLevel
    }

/** The course starts at its first section; placement tests go through the sections from there. */
private const val FIRST_SECTION = "s1"

/** [startWithOnboarding]: the first run starts with the introduction instead of the path. */
@Composable
fun HitzApp(
    modifier: Modifier = Modifier,
    startWithOnboarding: Boolean = false,
    navController: NavHostController = rememberNavController(),
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination

    val showNavigationBar = currentDestination?.let { destination ->
        fullScreenRoutes.none { destination.hasRoute(it) }
    } ?: !startWithOnboarding

    // Wide windows (tablets, foldables, landscape) use a navigation rail instead of the bottom bar.
    val wide = with(LocalDensity.current) { LocalWindowInfo.current.containerSize.width.toDp() } >= RAIL_MIN_WIDTH

    Row(modifier.fillMaxSize()) {
        AnimatedVisibility(
            visible = wide && showNavigationBar,
            enter = HitzTransitions.navigationRailEnter,
            exit = HitzTransitions.navigationRailExit,
        ) {
            TopLevelNavigationRail(currentDestination, navController)
        }
        HitzScaffold(
            navController = navController,
            startWithOnboarding = startWithOnboarding,
            bottomBar = {
                AnimatedVisibility(
                    visible = !wide && showNavigationBar,
                    enter = HitzTransitions.navigationBarEnter,
                    exit = HitzTransitions.navigationBarExit,
                ) {
                    TopLevelNavigationBar(currentDestination, navController)
                }
            },
            modifier = Modifier.weight(1f),
        )
    }
}

private val RAIL_MIN_WIDTH = 600.dp

/** Every destination of the app and how they lead to each other. */
private fun NavGraphBuilder.hitzGraph(navController: NavHostController) {
    pathScreen(
        onStartLevel = { unitId, levelId, lessonId ->
            navController.navigateToLesson(unitId, levelId, lessonId)
        },
        onOpenGuidebook = { unitId -> navController.navigateToGuidebook(unitId) },
        onStartStory = { unitId, levelId -> navController.navigateToStory(unitId, levelId) },
        onOpenProfile = { navController.navigateToTopLevel(TopLevelDestination.PROFILE) },
        onOpenUnitIntro = { unitId, levelId, lessonId ->
            navController.navigateToUnitIntro(unitId, levelId, lessonId)
        },
    )
    onboardingScreen(
        onFinish = { level ->
            navController.navigate(PathRoute) { popUpTo<OnboardingRoute> { inclusive = true } }
            if (level == StartingLevel.SomeBasque) navController.navigateToPlacement(FIRST_SECTION)
        },
    )
    placementScreen(
        onClose = { navController.popBackStack() },
        onNext = { sectionId ->
            navController.navigateToPlacement(
                sectionId,
                navOptions { popUpTo<PlacementRoute> { inclusive = true } }
            )
        },
    )
    guidebookScreen(onBack = { navController.popBackStack() })
    unitIntroScreen(
        onStart = { unitId, levelId, lessonId ->
            // The lesson replaces the introduction: closing it goes back to the path.
            navController.navigateToLesson(
                unitId,
                levelId,
                lessonId,
                navOptions { popUpTo<UnitIntroRoute> { inclusive = true } },
            )
        },
        onOpenGuidebook = { unitId -> navController.navigateToGuidebook(unitId) },
        onBack = { navController.popBackStack() },
    )
    lessonScreen(onClose = { navController.popBackStack() })
    storyScreen(onClose = { navController.popBackStack() })
    practiceScreen(
        onPractice = { mode -> navController.navigateToPractice(mode) },
        onOpenUnit = { unitId -> navController.navigateToUnitReview(unitId) },
    )
    unitReviewScreen(
        onBack = { navController.popBackStack() },
        onPractice = { unitId -> navController.navigateToPractice(PracticeMode.Mistakes, unitId) },
        onReadStory = { unitId, levelId -> navController.navigateToStory(unitId, levelId) },
    )
    practiceSessionScreen(onClose = { navController.popBackStack() })
    profileScreen(onOpenScore = { navController.navigateToScore() })
    scoreScreen(onBack = { navController.popBackStack() })
}

@Composable
private fun HitzScaffold(
    navController: NavHostController,
    startWithOnboarding: Boolean,
    bottomBar: @Composable () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        bottomBar = bottomBar,
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = if (startWithOnboarding) OnboardingRoute else PathRoute,
            modifier = Modifier
                .padding(padding)
                .consumeWindowInsets(padding),
            enterTransition = {
                when (targetState.destination.kind) {
                    DestinationKind.Session -> HitzTransitions.sessionEnter
                    DestinationKind.Detail -> HitzTransitions.forwardEnter
                    DestinationKind.TopLevel -> HitzTransitions.topLevelEnter
                }
            },
            exitTransition = {
                when (targetState.destination.kind) {
                    DestinationKind.Session -> HitzTransitions.underSessionExit
                    DestinationKind.Detail -> HitzTransitions.forwardExit
                    DestinationKind.TopLevel -> HitzTransitions.topLevelExit
                }
            },
            popEnterTransition = {
                when (initialState.destination.kind) {
                    DestinationKind.Session -> HitzTransitions.underSessionEnter
                    DestinationKind.Detail -> HitzTransitions.backwardEnter
                    DestinationKind.TopLevel -> HitzTransitions.topLevelEnter
                }
            },
            popExitTransition = {
                when (initialState.destination.kind) {
                    DestinationKind.Session -> HitzTransitions.sessionExit
                    DestinationKind.Detail -> HitzTransitions.backwardExit
                    DestinationKind.TopLevel -> HitzTransitions.topLevelExit
                }
            },
        ) {
            hitzGraph(navController)
        }
    }
}

@Composable
private fun TopLevelNavigationRail(currentDestination: NavDestination?, navController: NavHostController) {
    WideNavigationRail {
        TopLevelDestination.entries.forEach { destination ->
            val selected = currentDestination?.hierarchy
                ?.any { it.hasRoute(destination.routeClass) } == true
            WideNavigationRailItem(
                selected = selected,
                onClick = { navController.navigateToTopLevel(destination) },
                icon = {
                    Icon(
                        painterResource(if (selected) destination.selectedIcon else destination.icon),
                        contentDescription = null,
                    )
                },
                label = { Text(stringResource(destination.label)) },
                railExpanded = false,
            )
        }
    }
}

@Composable
private fun TopLevelNavigationBar(currentDestination: NavDestination?, navController: NavHostController) {
    ShortNavigationBar {
        TopLevelDestination.entries.forEach { destination ->
            val selected = currentDestination?.hierarchy
                ?.any { it.hasRoute(destination.routeClass) } == true
            ShortNavigationBarItem(
                selected = selected,
                onClick = { navController.navigateToTopLevel(destination) },
                icon = {
                    Icon(
                        painterResource(if (selected) destination.selectedIcon else destination.icon),
                        contentDescription = null,
                    )
                },
                label = { Text(stringResource(destination.label)) },
            )
        }
    }
}

/** Top-level navigation keeps one copy of each destination and restores its state. */
private fun NavHostController.navigateToTopLevel(destination: TopLevelDestination) = navigate(
    destination.route,
    navOptions {
        popUpTo(graph.findStartDestination().id) { saveState = true }
        launchSingleTop = true
        restoreState = true
    },
)

@PreviewLightDark
@Composable
private fun HitzAppPreview() {
    HitzTheme { HitzApp() }
}
