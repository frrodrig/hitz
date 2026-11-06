package eus.hitz.feature.path.intro

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
class UnitIntroContentTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun introductionShowsWhatTheUnitTeachesAndStarts() {
        var played: KeyWordUi? = null
        var started = false
        composeRule.setContent {
            HitzTheme {
                UnitIntroContent(
                    state = UnitIntroPreviewData.ready,
                    onPlay = { played = it },
                    onStart = { started = true },
                    onOpenGuidebook = {},
                    onBack = {},
                )
            }
        }
        composeRule.onRoot().captureRoboImage("src/test/screenshots/unit_intro.png")

        composeRule.onNodeWithText("Palabras clave").assertExists()
        composeRule.onNodeWithText("ogi").performClick()
        assertThat(played?.clipId).isEqualTo("u05-1.kw0")
        composeRule.onNodeWithText("Empezar").performClick()
        assertThat(started).isTrue()
    }
}
