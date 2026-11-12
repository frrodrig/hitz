package eus.hitz.feature.practice

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import com.google.common.truth.Truth.assertThat
import eus.hitz.core.designsystem.theme.HitzTheme
import eus.hitz.core.domain.practice.PracticeMode
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(AndroidJUnit4::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [35], qualifiers = RobolectricDeviceQualifiers.Pixel7)
class PracticeContentTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun theHubOffersThePersonalReviewModesAndUnits() {
        val practised = mutableListOf<PracticeMode>()
        var opened: String? = null
        composeRule.setContent {
            HitzTheme { PracticeContent(PracticePreviewData.ready, { practised += it }, { opened = it }) }
        }
        composeRule.onNodeWithText("12 palabras y estructuras que te cuestan").assertExists()
        composeRule.onRoot().captureRoboImage("src/test/screenshots/practice_hub.png")

        composeRule.onNodeWithText("Empezar").performClick()
        composeRule.onNodeWithText("Escuchar").performClick()
        composeRule.onNodeWithText("¡Hola!").performClick()
        assertThat(practised).containsExactly(PracticeMode.Mistakes, PracticeMode.Listening).inOrder()
        assertThat(opened).isEqualTo("u01-1")
    }

    @Test
    fun beforeTheFirstLessonThereIsNothingToReview() {
        composeRule.setContent { HitzTheme { PracticeContent(PracticePreviewData.empty, {}, {}) } }
        composeRule.onNodeWithText("Aún no hay nada que repasar").assertExists()
        composeRule.onRoot().captureRoboImage("src/test/screenshots/practice_empty.png")
    }

    @Test
    @Config(qualifiers = "+night")
    fun theHubInDarkTheme() {
        composeRule.setContent { HitzTheme { PracticeContent(PracticePreviewData.ready, {}, {}) } }
        composeRule.onRoot().captureRoboImage("src/test/screenshots/practice_hub_dark.png")
    }
}
