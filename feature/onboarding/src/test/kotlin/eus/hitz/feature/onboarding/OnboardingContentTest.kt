package eus.hitz.feature.onboarding

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
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(AndroidJUnit4::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [35], qualifiers = RobolectricDeviceQualifiers.Pixel7)
class OnboardingContentTest {
    @get:Rule
    val composeRule = createComposeRule()

    private var finished: StartingLevel? = null

    private fun show() = composeRule.setContent {
        HitzTheme { OnboardingContent(OnboardingPreviewData.sections, onFinish = { finished = it }) }
    }

    @Test
    fun everyPageExplainsTheAppAndTheLastAsksForTheLevel() {
        show()
        composeRule.onRoot().captureRoboImage("src/test/screenshots/onboarding_welcome.png")
        composeRule.onNodeWithText("Siguiente").performClick()
        composeRule.onNodeWithText("Sección 1 · Primeros pasos").assertExists()
        composeRule.onRoot().captureRoboImage("src/test/screenshots/onboarding_learn.png")
        composeRule.onNodeWithText("Siguiente").performClick()
        composeRule.onNodeWithText("Palabras nuevas").assertExists()
        composeRule.onRoot().captureRoboImage("src/test/screenshots/onboarding_how.png")
        composeRule.onNodeWithText("Siguiente").performClick()

        composeRule.onNodeWithText("Empezar").assertIsNotEnabled()
        composeRule.onNodeWithText("Ya sé algo de euskera").performClick()
        composeRule.onRoot().captureRoboImage("src/test/screenshots/onboarding_level.png")
        composeRule.onNodeWithText("Empezar").performClick()
        assertThat(finished).isEqualTo(StartingLevel.SomeBasque)
    }

    @Test
    fun skippingStartsFromScratch() {
        show()
        composeRule.onNodeWithText("Saltar").performClick()
        assertThat(finished).isEqualTo(StartingLevel.Beginner)
    }
}
