package eus.hitz.feature.path

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import com.google.common.truth.Truth.assertThat
import eus.hitz.core.designsystem.theme.HitzTheme
import eus.hitz.core.model.content.CourseUnit
import eus.hitz.core.model.content.Example
import eus.hitz.core.model.content.Guidebook
import eus.hitz.core.model.content.GuidebookSection
import eus.hitz.core.model.content.LocalizedText
import eus.hitz.feature.path.guidebook.GuidebookContent
import eus.hitz.feature.path.guidebook.GuidebookUiState
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(AndroidJUnit4::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [35], qualifiers = RobolectricDeviceQualifiers.Pixel7)
class GuidebookContentTest {
    @get:Rule
    val composeRule = createComposeRule()

    private val unit = CourseUnit(
        schemaVersion = 1,
        id = "u01-1",
        section = "s1",
        bookUnit = 1,
        title = LocalizedText("¡Hola!", "Kaixo!"),
        goals = listOf(LocalizedText("Saludar y despedirse.", "Agurtu.")),
        guidebook = Guidebook(
            listOf(
                GuidebookSection(
                    title = LocalizedText("Presentarse", "Aurkeztu"),
                    body = LocalizedText("Ni + nombre + naiz.", "Ni + izena + naiz."),
                    examples = listOf(Example("Ni Jon naiz.", "Soy Jon.")),
                ),
            ),
        ),
        levels = emptyList(),
    )

    @Test
    fun showsGoalsSectionsAndExamples() {
        var back = false
        composeRule.setContent {
            HitzTheme { GuidebookContent(GuidebookUiState.Ready(unit), onBack = { back = true }) }
        }
        composeRule.onNodeWithText("Guía de la unidad 1.1").assertExists()
        composeRule.onNodeWithText("• Saludar y despedirse.").assertExists()
        composeRule.onNodeWithText("Ni Jon naiz.").assertExists()
        composeRule.onRoot().captureRoboImage("src/test/screenshots/guidebook.png")
        composeRule.onNodeWithContentDescription("Volver").performClick()
        assertThat(back).isTrue()
    }
}
