package eus.hitz.feature.profile

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import eus.hitz.core.designsystem.theme.HitzTheme
import eus.hitz.core.domain.settings.UserSettings
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [35])
class ProfileScreenTest {
    @get:Rule
    val composeRule = createComposeRule()

    private var speaking: Boolean? = null

    private fun show() {
        composeRule.setContent {
            HitzTheme { ProfileContent(UserSettings(speakingEnabled = true), onSpeakingChange = { speaking = it }) }
        }
    }

    @Test
    fun showsTitle() {
        show()
        composeRule.onNodeWithText("Perfil").assertIsDisplayed()
    }

    @Test
    fun speakingCanBeTurnedOff() {
        show()
        composeRule.onNodeWithText("Ejercicios de hablar").performScrollTo().assertIsOn().performClick()
        assertThat(speaking).isFalse()
    }
}
