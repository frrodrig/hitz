package eus.hitz.feature.shop.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import eus.hitz.feature.shop.ShopScreen
import kotlinx.serialization.Serializable

@Serializable
data object ShopRoute

fun NavController.navigateToShop(navOptions: NavOptions? = null) = navigate(ShopRoute, navOptions)

fun NavGraphBuilder.shopScreen() {
    composable<ShopRoute> { ShopScreen() }
}
