@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package eus.hitz.app.ui

import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ShortNavigationBar
import androidx.compose.material3.ShortNavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import eus.hitz.app.navigation.TopLevelDestination
import eus.hitz.core.designsystem.theme.HitzTheme
import eus.hitz.feature.leaderboard.navigation.leaderboardScreen
import eus.hitz.feature.lesson.navigation.LessonRoute
import eus.hitz.feature.lesson.navigation.StoryRoute
import eus.hitz.feature.lesson.navigation.lessonScreen
import eus.hitz.feature.lesson.navigation.navigateToLesson
import eus.hitz.feature.lesson.navigation.navigateToStory
import eus.hitz.feature.lesson.navigation.storyScreen
import eus.hitz.feature.path.navigation.GuidebookRoute
import eus.hitz.feature.path.navigation.PathRoute
import eus.hitz.feature.path.navigation.guidebookScreen
import eus.hitz.feature.path.navigation.navigateToGuidebook
import eus.hitz.feature.path.navigation.pathScreen
import eus.hitz.feature.profile.navigation.profileScreen
import eus.hitz.feature.shop.navigation.shopScreen

@Composable
fun HitzApp(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination

    // Lessons, stories and guidebooks are full screen: no bottom navigation.
    val showNavigationBar = currentDestination?.let {
        it.hasRoute(LessonRoute::class) || it.hasRoute(StoryRoute::class) || it.hasRoute(GuidebookRoute::class)
    } != true

    Scaffold(
        modifier = modifier,
        bottomBar = {
            if (showNavigationBar) {
                TopLevelNavigationBar(currentDestination, navController)
            }
        },
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = PathRoute,
            modifier = Modifier
                .padding(padding)
                .consumeWindowInsets(padding),
        ) {
            pathScreen(
                onStartLevel = { unitId, levelId, lessonId ->
                    navController.navigateToLesson(unitId, levelId, lessonId)
                },
                onOpenGuidebook = { unitId -> navController.navigateToGuidebook(unitId) },
                onStartStory = { unitId, levelId -> navController.navigateToStory(unitId, levelId) },
            )
            guidebookScreen(onBack = { navController.popBackStack() })
            lessonScreen(onClose = { navController.popBackStack() })
            storyScreen(onClose = { navController.popBackStack() })
            leaderboardScreen()
            profileScreen()
            shopScreen()
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
                onClick = {
                    navController.navigate(
                        destination.route,
                        navOptions {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        },
                    )
                },
                icon = { Icon(painterResource(destination.icon), contentDescription = null) },
                label = { Text(stringResource(destination.label)) },
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun HitzAppPreview() {
    HitzTheme { HitzApp() }
}
