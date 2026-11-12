package eus.hitz.app.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import eus.hitz.app.R
import eus.hitz.core.designsystem.icon.HitzIcons
import eus.hitz.feature.path.navigation.PathRoute
import eus.hitz.feature.practice.navigation.PracticeRoute
import eus.hitz.feature.profile.navigation.ProfileRoute
import kotlin.reflect.KClass

/** Destinations shown in the navigation bar or rail; the selected one shows its filled [selectedIcon]. */
enum class TopLevelDestination(
    val route: Any,
    val routeClass: KClass<*>,
    @StringRes val label: Int,
    @DrawableRes val icon: Int,
    @DrawableRes val selectedIcon: Int,
) {
    PATH(PathRoute, PathRoute::class, R.string.nav_path, HitzIcons.Path, HitzIcons.PathFilled),
    PRACTICE(PracticeRoute, PracticeRoute::class, R.string.nav_practice, HitzIcons.Practice, HitzIcons.PracticeFilled),
    PROFILE(ProfileRoute, ProfileRoute::class, R.string.nav_profile, HitzIcons.Profile, HitzIcons.ProfileFilled),
}
