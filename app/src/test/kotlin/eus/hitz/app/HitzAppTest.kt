package eus.hitz.app

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import eus.hitz.app.ui.HitzApp
import eus.hitz.core.designsystem.theme.HitzTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(AndroidJUnit4::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [35], qualifiers = RobolectricDeviceQualifiers.Pixel7, application = android.app.Application::class)
class HitzAppTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun navigatesBetweenTopLevelDestinations() {
        composeRule.setContent { HitzTheme(darkTheme = false) { HitzApp() } }
        composeRule.onNodeWithText("Aprender").assertIsSelected()
        composeRule.onRoot().captureRoboImage("src/test/screenshots/app_path.png")

        composeRule.onNodeWithText("Ligas").performClick()
        composeRule.onNodeWithText("Las ligas llegarán en M6.").assertIsDisplayed()
    }
}
