package eus.hitz.app

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/** End-to-end on the JVM: real content, real database (Robolectric), real navigation. */
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(application = HiltTestApplication::class, sdk = [35], qualifiers = RobolectricDeviceQualifiers.Pixel7)
class HitzAppTest {
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    private fun waitForText(text: String) = composeRule.waitUntil(TIMEOUT) {
        composeRule.onAllNodesWithText(text).fetchSemanticsNodes().isNotEmpty()
    }

    /** A fresh install starts with the introduction. */
    private fun skipOnboarding() {
        waitForText("Saltar")
        composeRule.onNodeWithText("Saltar").performClick()
    }

    @Test
    fun pathShowsUnitOneAndOpensTheCurrentLesson() {
        skipOnboarding()
        waitForText("¡Hola!")
        composeRule.onNodeWithText("Aprender").assertIsSelected()
        composeRule.onRoot().captureRoboImage("src/test/screenshots/app_path.png")

        val current = "Lección: Saludos, disponible, 0 de 3 lecciones"
        composeRule.waitUntil(TIMEOUT) {
            composeRule.onAllNodesWithContentDescription(current).fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onNodeWithContentDescription(current).performClick()
        // The unit is introduced first, once.
        waitForText("Palabras clave")
        composeRule.onNodeWithText("Empezar").performClick()
        // A beginner meets each word before using it.
        waitForText("Palabra nueva")
        composeRule.onNodeWithText("egun on").assertIsDisplayed()
        composeRule.onNodeWithText("Continuar").performClick()
        waitForText("Traduce esta frase")
    }

    @Test
    fun theFirstRunIntroducesTheAppAndOffersAPlacementTest() {
        waitForText("Kaixo! Te damos la bienvenida a Hitz")
        composeRule.onRoot().captureRoboImage("src/test/screenshots/app_onboarding.png")
        repeat(3) { composeRule.onNodeWithText("Siguiente").performClick() }
        waitForText("¿Has estudiado euskera antes?")
        composeRule.onNodeWithText("Ya sé algo de euskera").performClick()
        composeRule.onNodeWithText("Empezar").performClick()
        // The placement test of the first section: no hearts, a check button.
        waitForText("Comprobar")
    }

    @Test
    @Config(qualifiers = RobolectricDeviceQualifiers.MediumTablet)
    fun wideScreensUseANavigationRail() {
        skipOnboarding()
        waitForText("¡Hola!")
        composeRule.onNodeWithText("Aprender").assertIsSelected()
        composeRule.onRoot().captureRoboImage("src/test/screenshots/app_path_tablet.png")
        composeRule.onNodeWithText("Ligas").performClick()
        waitForText("Liga Jaizkibel")
    }

    @Test
    fun navigatesToLeagues() {
        skipOnboarding()
        waitForText("¡Hola!")
        composeRule.onNodeWithText("Ligas").performClick()
        waitForText("Liga Jaizkibel")
    }

    private companion object {
        const val TIMEOUT = 5_000L
    }
}
