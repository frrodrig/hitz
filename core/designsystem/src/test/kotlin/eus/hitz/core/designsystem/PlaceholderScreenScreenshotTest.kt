package eus.hitz.core.designsystem

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import eus.hitz.core.designsystem.component.PlaceholderScreen
import eus.hitz.core.designsystem.theme.HitzTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(AndroidJUnit4::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [35], qualifiers = RobolectricDeviceQualifiers.Pixel7)
class PlaceholderScreenScreenshotTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun placeholderLight() = capture(darkTheme = false, name = "placeholder_light")

    @Test
    fun placeholderDark() = capture(darkTheme = true, name = "placeholder_dark")

    private fun capture(darkTheme: Boolean, name: String) {
        composeRule.setContent {
            HitzTheme(darkTheme = darkTheme) {
                PlaceholderScreen(title = "Hitz", description = "Laster hemen!")
            }
        }
        composeRule.onRoot().captureRoboImage("src/test/screenshots/$name.png")
    }
}
