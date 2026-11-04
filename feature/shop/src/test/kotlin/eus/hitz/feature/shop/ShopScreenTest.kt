package eus.hitz.feature.shop

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import com.google.common.truth.Truth.assertThat
import eus.hitz.core.designsystem.theme.HitzTheme
import eus.hitz.core.domain.gamification.ShopItem
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(AndroidJUnit4::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [35], qualifiers = RobolectricDeviceQualifiers.Pixel7)
class ShopScreenTest {
    @get:Rule
    val composeRule = createComposeRule()

    private val bought = mutableListOf<ShopItem>()

    private fun show(state: ShopUiState) {
        composeRule.setContent { HitzTheme { ShopContent(state, onBuy = { bought += it }) } }
    }

    @Test
    fun buyingAStreakFreeze() {
        show(ShopUiState(gems = 45, hearts = 5, streakFreezes = 0))
        composeRule.onNodeWithText("Tienda").assertIsDisplayed()
        composeRule.onNodeWithText("Comprar por 20 gemas").performClick()
        assertThat(bought).containsExactly(ShopItem.StreakFreeze)
    }

    @Test
    fun heartsCannotBeRefilledWhenFull() {
        show(ShopUiState(gems = 45, hearts = 5, streakFreezes = 0))
        composeRule.onNodeWithText("Tienes todas las vidas.").assertIsDisplayed()
        composeRule.onNodeWithText("Comprar por 30 gemas").assertIsNotEnabled()
    }

    @Test
    fun screenshot() {
        show(ShopUiState(gems = 45, hearts = 3, nextHeartAt = 0, streakFreezes = 1))
        composeRule.onRoot().captureRoboImage("src/test/screenshots/shop.png")
    }
}
