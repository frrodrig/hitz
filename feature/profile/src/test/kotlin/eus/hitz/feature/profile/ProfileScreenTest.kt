package eus.hitz.feature.profile

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import eus.hitz.core.designsystem.theme.HitzTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [35])
class ProfileScreenTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun showsTitle() {
        composeRule.setContent { HitzTheme { ProfileScreen() } }
        composeRule.onNodeWithText("Perfil").assertIsDisplayed()
    }
}
