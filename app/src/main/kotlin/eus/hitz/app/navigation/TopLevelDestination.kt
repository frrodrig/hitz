package eus.hitz.app.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import eus.hitz.app.R
import eus.hitz.feature.leaderboard.navigation.LeaderboardRoute
import eus.hitz.feature.path.navigation.PathRoute
import eus.hitz.feature.profile.navigation.ProfileRoute
import eus.hitz.feature.shop.navigation.ShopRoute
import kotlin.reflect.KClass
import eus.hitz.core.designsystem.R as DesignR

/** Destinos de la barra de navegación inferior. */
enum class TopLevelDestination(
    val route: Any,
    val routeClass: KClass<*>,
    @StringRes val label: Int,
    @DrawableRes val icon: Int,
) {
    PATH(PathRoute, PathRoute::class, R.string.nav_path, DesignR.drawable.core_designsystem_ic_path),
    LEADERBOARD(
        LeaderboardRoute,
        LeaderboardRoute::class,
        R.string.nav_leaderboard,
        DesignR.drawable.core_designsystem_ic_trophy,
    ),
    PROFILE(ProfileRoute, ProfileRoute::class, R.string.nav_profile, DesignR.drawable.core_designsystem_ic_profile),
    SHOP(ShopRoute, ShopRoute::class, R.string.nav_shop, DesignR.drawable.core_designsystem_ic_shop),
}
