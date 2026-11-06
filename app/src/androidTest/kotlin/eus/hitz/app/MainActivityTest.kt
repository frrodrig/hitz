package eus.hitz.app

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.accessibility.enableAccessibilityChecks
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.tryPerformAccessibilityChecks
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * End-to-end on a device, with the Accessibility Test Framework checking every screen it visits
 * (touch target size, contrast, labels, …): any issue fails the test.
 */
@RunWith(AndroidJUnit4::class)
class MainActivityTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setUp() {
        composeRule.enableAccessibilityChecks()
    }

    private fun waitForText(text: String) = composeRule.waitUntil(TIMEOUT) {
        composeRule.onAllNodesWithText(text).fetchSemanticsNodes().isNotEmpty()
    }

    /** A fresh install starts with the introduction (once: data persists between these tests). */
    private fun skipOnboarding() {
        composeRule.waitUntil(TIMEOUT) {
            listOf("Saltar", "Aprender").any { composeRule.onAllNodesWithText(it).fetchSemanticsNodes().isNotEmpty() }
        }
        if (composeRule.onAllNodesWithText("Saltar").fetchSemanticsNodes().isNotEmpty()) {
            composeRule.onRoot().tryPerformAccessibilityChecks()
            composeRule.onNodeWithText("Saltar").performClick()
        }
    }

    @Test
    fun everyTopLevelScreenIsAccessible() {
        skipOnboarding()
        waitForText("¡Hola!")
        composeRule.onRoot().tryPerformAccessibilityChecks()
        composeRule.onNodeWithText("Ligas").performClick()
        waitForText("Liga Jaizkibel")
        composeRule.onNodeWithText("Perfil").performClick()
        waitForText("Mejor racha")
        composeRule.onNodeWithText("Tienda").performClick()
        composeRule.onNodeWithText("Recargar vidas").assertIsDisplayed()
        composeRule.onRoot().tryPerformAccessibilityChecks()
    }

    @Test
    fun theFirstLessonIsAccessible() {
        skipOnboarding()
        val current = "Lección: Saludos, disponible, 0 de 3 lecciones"
        composeRule.waitUntil(TIMEOUT) {
            composeRule.onAllNodesWithContentDescription(current).fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onNodeWithContentDescription(current).performClick()
        // The unit is introduced first, once.
        waitForText("Palabras clave")
        composeRule.onRoot().tryPerformAccessibilityChecks()
        composeRule.onNodeWithText("Empezar").performClick()
        // A beginner meets each word before using it.
        waitForText("Palabra nueva")
        composeRule.onNodeWithText("egun on").assertIsDisplayed()
        composeRule.onRoot().tryPerformAccessibilityChecks()
        composeRule.onNodeWithText("Continuar").performClick()
        waitForText("Traduce esta frase")
        composeRule.onRoot().tryPerformAccessibilityChecks()
    }

    private companion object {
        const val TIMEOUT = 10_000L
    }
}
