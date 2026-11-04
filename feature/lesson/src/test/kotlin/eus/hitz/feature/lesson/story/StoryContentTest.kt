package eus.hitz.feature.lesson.story

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
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(AndroidJUnit4::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [35], qualifiers = RobolectricDeviceQualifiers.Pixel7)
class StoryContentTest {
    @get:Rule
    val composeRule = createComposeRule()

    private val actions = mutableListOf<StoryAction>()

    private fun show(state: StoryUiState, darkTheme: Boolean = false) {
        composeRule.setContent {
            HitzTheme(darkTheme = darkTheme) { StoryContent(state, onAction = { actions += it }, onClose = {}) }
        }
    }

    @Test
    fun tappingALineTogglesItsTranslation() {
        show(StoryPreviewData.reading)
        composeRule.onNodeWithText("¡Hola! ¿Qué tal?").assertIsDisplayed()
        composeRule.onNodeWithText("Egun on!").performClick()
        assertThat(actions).containsExactly(StoryAction.ToggleTranslation(0))
    }

    @Test
    fun continueWaitsForTheAnswer() {
        show(StoryPreviewData.question)
        composeRule.onNodeWithText("Continuar").assertIsNotEnabled()
        composeRule.onNodeWithText("Muy bien").performClick()
        assertThat(actions).containsExactly(StoryAction.Answer(1))
    }

    @Test
    fun finishedShowsAccuracy() {
        show(StoryUiState.Finished(accuracy = 0.5f))
        composeRule.onNodeWithText("¡Historia completada!").assertIsDisplayed()
        composeRule.onNodeWithText("Has acertado el 50 % de las preguntas a la primera.").assertIsDisplayed()
    }

    @Test
    fun readingScreenshot() {
        show(StoryPreviewData.reading)
        composeRule.onRoot().captureRoboImage("src/test/screenshots/story_reading.png")
    }

    @Test
    fun questionScreenshotDark() {
        show(StoryPreviewData.question, darkTheme = true)
        composeRule.onRoot().captureRoboImage("src/test/screenshots/story_question_dark.png")
    }
}
