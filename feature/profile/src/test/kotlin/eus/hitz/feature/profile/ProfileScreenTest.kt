package eus.hitz.feature.profile

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.hasScrollAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
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
class ProfileScreenTest {
    @get:Rule
    val composeRule = createComposeRule()

    private var speaking: Boolean? = null
    private var reminder: Boolean? = null

    private fun show(darkTheme: Boolean = false) {
        composeRule.setContent {
            HitzTheme(darkTheme = darkTheme) {
                ProfileContent(
                    state = ProfilePreviewData.ready,
                    onSpeakingChange = { speaking = it },
                    onReminderChange = { reminder = it },
                )
            }
        }
    }

    private fun scrollTo(text: String) {
        composeRule.onNode(hasScrollAction()).performScrollToNode(hasText(text))
    }

    @Test
    fun showsStatsAndQuests() {
        show()
        composeRule.onNodeWithText("Perfil").assertIsDisplayed()
        composeRule.onNodeWithText("Mejor racha").assertIsDisplayed()
        scrollTo("Gana 30 XP")
        composeRule.onNodeWithText("¡Hecho! +10 gemas").assertIsDisplayed()
    }

    @Test
    fun achievementsShowTheirNextGoal() {
        show()
        scrollTo("Estudio: lecciones completadas")
        composeRule.onNodeWithText("Nivel 2 de 4 · 31 de 50").assertIsDisplayed()
    }

    @Test
    fun settingsCanBeToggled() {
        show()
        scrollTo("Ejercicios de hablar")
        composeRule.onNodeWithText("Ejercicios de hablar").assertIsOn().performClick()
        assertThat(speaking).isFalse()
        scrollTo("Recordatorio diario")
        composeRule.onNodeWithText("Recordatorio diario").assertIsOff().performClick()
        assertThat(reminder).isTrue()
    }

    @Test
    fun screenshot() {
        show()
        composeRule.onRoot().captureRoboImage("src/test/screenshots/profile.png")
    }
}
