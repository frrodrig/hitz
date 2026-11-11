package eus.hitz.app.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import eus.hitz.app.R
import eus.hitz.core.designsystem.icon.HitzIcons
import eus.hitz.feature.path.navigation.PathRoute
import eus.hitz.feature.profile.navigation.ProfileRoute
import kotlin.reflect.KClass

/** Destinations shown in the bottom navigation bar. */
enum class TopLevelDestination(
    val route: Any,
    val routeClass: KClass<*>,
    @StringRes val label: Int,
    @DrawableRes val icon: Int,
) {
    PATH(PathRoute, PathRoute::class, R.string.nav_path, HitzIcons.Path),
    PROFILE(ProfileRoute, ProfileRoute::class, R.string.nav_profile, HitzIcons.Profile),
}
