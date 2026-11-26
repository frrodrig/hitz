package eus.hitz.feature.profile.settings

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.hasScrollAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import com.google.common.truth.Truth.assertThat
import eus.hitz.core.designsystem.theme.HitzTheme
import eus.hitz.core.domain.settings.UserSettings
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(AndroidJUnit4::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [35], qualifiers = RobolectricDeviceQualifiers.Pixel7)
class SettingsScreenTest {
    @get:Rule
    val composeRule = createComposeRule()

    private val changes = mutableListOf<Pair<Setting, Boolean>>()
    private var back = false
    private var languageOpened = false

    private fun show(
        systemColorsAvailable: Boolean = true,
        language: Boolean = true,
        darkTheme: Boolean = false,
    ) {
        composeRule.setContent {
            HitzTheme(darkTheme = darkTheme) {
                SettingsContent(
                    settings = UserSettings(),
                    onSettingChange = { setting, enabled -> changes += setting to enabled },
                    onBack = { back = true },
                    version = "1.3.0",
                    systemColorsAvailable = systemColorsAvailable,
                    onOpenLanguage = if (language) ({ languageOpened = true }) else null,
                )
            }
        }
    }

    private fun scrollTo(text: String) {
        composeRule.onNode(hasScrollAction()).performScrollToNode(hasText(text))
    }

    @Test
    fun settingsCanBeToggled() {
        show()
        composeRule.onNodeWithText("Ejercicios de hablar").assertIsOn().performClick()
        scrollTo("Efectos de sonido")
        composeRule.onNodeWithText("Efectos de sonido").assertIsOn().performClick()
        scrollTo("Recordatorio diario")
        composeRule.onNodeWithText("Recordatorio diario").assertIsOff().performClick()
        scrollTo("Usar los colores del sistema")
        composeRule.onNodeWithText("Usar los colores del sistema").assertIsOn().performClick()
        scrollTo("Buscar actualizaciones")
        composeRule.onNodeWithText("Buscar actualizaciones").assertIsOn().performClick()
        assertThat(changes).containsExactly(
            Setting.Speaking to false,
            Setting.SoundEffects to false,
            Setting.Reminder to true,
            Setting.SystemColors to false,
            Setting.UpdateChecks to false,
        ).inOrder()
    }

    @Test
    fun theLanguageOpensAndroidsSetting() {
        show()
        scrollTo("Idioma de la app")
        composeRule.onNodeWithText("Idioma de la app").performClick()
        assertThat(languageOpened).isTrue()
    }

    @Test
    fun optionsTheSystemCannotOfferAreHidden() {
        show(systemColorsAvailable = false, language = false)
        composeRule.onNodeWithText("Usar los colores del sistema").assertDoesNotExist()
        composeRule.onNodeWithText("Idioma de la app").assertDoesNotExist()
    }

    @Test
    fun theVersionIsShownAndBackReturns() {
        show()
        scrollTo("1.3.0")
        composeRule.onNodeWithText("1.3.0").assertIsDisplayed()
        composeRule.onNodeWithContentDescription("Volver").performClick()
        assertThat(back).isTrue()
    }

    @Test
    fun screenshot() {
        show()
        composeRule.onRoot().captureRoboImage("src/test/screenshots/settings.png")
    }

    @Test
    fun screenshotDark() {
        show(darkTheme = true)
        composeRule.onRoot().captureRoboImage("src/test/screenshots/settings_dark.png")
    }

    @Test
    fun screenshotFontScale200() {
        RuntimeEnvironment.setFontScale(2f)
        show()
        composeRule.onRoot().captureRoboImage("src/test/screenshots/settings_font_200.png")
    }
}
