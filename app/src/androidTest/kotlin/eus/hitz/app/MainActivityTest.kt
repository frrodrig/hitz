package eus.hitz.app

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun startsOnThePathAndNavigatesToTheShop() {
        composeRule.waitUntil(10_000) { composeRule.onAllNodesWithText("¡Hola!").fetchSemanticsNodes().isNotEmpty() }
        composeRule.onNodeWithText("Tienda").performClick()
        composeRule.onNodeWithText("La tienda de gemas llegará en M6.").assertIsDisplayed()
    }
}
