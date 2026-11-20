package eus.hitz.app

import androidx.compose.ui.test.isDialog
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import com.google.common.truth.Truth.assertThat
import eus.hitz.app.ui.UpdateDialog
import eus.hitz.core.designsystem.theme.HitzTheme
import eus.hitz.core.domain.update.AppRelease
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(AndroidJUnit4::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [35], qualifiers = RobolectricDeviceQualifiers.Pixel7)
class UpdateDialogTest {
    @get:Rule
    val composeRule = createComposeRule()

    private val release = AppRelease(
        version = "1.2.0",
        apkUrl = "https://github.com/frrodrig/hitz/releases/download/v1.2.0/hitz-1.2.0.apk",
        pageUrl = "https://github.com/frrodrig/hitz/releases/tag/v1.2.0",
    )

    @Test
    fun theNewVersionIsOfferedForDownload() {
        var downloads = 0
        var dismissed = 0
        composeRule.setContent {
            HitzTheme { UpdateDialog(release, onDownload = { downloads++ }, onDismiss = { dismissed++ }) }
        }
        composeRule.onNodeWithText("La versión 1.2.0", substring = true).assertExists()
        composeRule.onNode(isDialog()).captureRoboImage("src/test/screenshots/update_dialog.png")

        composeRule.onNodeWithText("Descargar").performClick()
        composeRule.onNodeWithText("Ahora no").performClick()
        assertThat(downloads).isEqualTo(1)
        assertThat(dismissed).isEqualTo(1)
    }
}
