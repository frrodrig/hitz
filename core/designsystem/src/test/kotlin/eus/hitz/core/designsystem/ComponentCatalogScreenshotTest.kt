package eus.hitz.core.designsystem

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.unit.Density
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import eus.hitz.core.designsystem.catalog.ComponentCatalog
import eus.hitz.core.designsystem.theme.HitzTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(AndroidJUnit4::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [35], qualifiers = RobolectricDeviceQualifiers.Pixel7)
class ComponentCatalogScreenshotTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun catalogLight() = capture("catalog_light")

    @Test
    fun catalogDark() = capture("catalog_dark", darkTheme = true)

    @Test
    fun catalogHighContrast() = capture("catalog_high_contrast", highContrast = true)

    @Test
    fun catalogFontScale200() = capture("catalog_font_200", fontScale = 2f)

    private fun capture(
        name: String,
        darkTheme: Boolean = false,
        highContrast: Boolean = false,
        fontScale: Float = 1f,
    ) {
        composeRule.setContent {
            val density = LocalDensity.current
            CompositionLocalProvider(LocalDensity provides Density(density.density, fontScale)) {
                HitzTheme(darkTheme = darkTheme, highContrast = highContrast) {
                    ComponentCatalog(scrollable = false)
                }
            }
        }
        composeRule.onRoot().captureRoboImage("src/test/screenshots/$name.png")
    }
}
