package eus.hitz.feature.leaderboard

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import eus.hitz.core.designsystem.theme.HitzTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(AndroidJUnit4::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [35], qualifiers = RobolectricDeviceQualifiers.Pixel7)
class LeaderboardScreenTest {
    @get:Rule
    val composeRule = createComposeRule()

    private fun show(darkTheme: Boolean = false) {
        composeRule.setContent {
            HitzTheme(darkTheme = darkTheme) {
                LeaderboardContent(LeaderboardUiState.Ready(LeaderboardPreviewData.league))
            }
        }
    }

    @Test
    fun showsTheLeagueAndTheLearner() {
        show()
        composeRule.onNodeWithText("Liga Adarra").assertIsDisplayed()
        composeRule.onNodeWithText("Quedan 3 días").assertIsDisplayed()
        composeRule.onNodeWithText("La semana pasada quedaste en el puesto 2 y subiste de liga.").assertIsDisplayed()
        composeRule.onNodeWithText("Zona de ascenso").assertExists()
    }

    @Test
    fun screenshot() {
        show()
        composeRule.onRoot().captureRoboImage("src/test/screenshots/leaderboard.png")
    }

    @Test
    fun screenshotDark() {
        show(darkTheme = true)
        composeRule.onRoot().captureRoboImage("src/test/screenshots/leaderboard_dark.png")
    }
}
