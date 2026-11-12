package eus.hitz.feature.profile.score

import androidx.compose.ui.test.assertIsDisplayed
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
import eus.hitz.core.domain.score.Score
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(AndroidJUnit4::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [35], qualifiers = RobolectricDeviceQualifiers.Pixel7)
class ScoreContentTest {
    @get:Rule
    val composeRule = createComposeRule()

    private var closed = false

    private fun show(score: Score?, darkTheme: Boolean = false) = composeRule.setContent {
        HitzTheme(darkTheme = darkTheme) { ScoreContent(score, onBack = { closed = true }) }
    }

    @Test
    fun showsTheScoreItsHistoryAndWhatTheLearnerCanDo() {
        show(ScorePreviewData.score)
        composeRule.onNodeWithContentDescription("Puntuación: 7 de 29").assertIsDisplayed()
        composeRule.onNodeWithText("Primeros pasos").assertIsDisplayed()
        composeRule.onNodeWithText("+3 puntos esta semana").assertIsDisplayed()
        composeRule.onNodeWithContentDescription("Tu puntuación ha pasado de 1 a 7.").assertExists()
        composeRule.onNode(hasScrollAction()).performScrollToNode(hasText("Decir tu nombre."))
        composeRule.onNodeWithText("Decir tu nombre.").assertIsDisplayed()
        composeRule.onNodeWithContentDescription("Volver").performClick()
        assertThat(closed).isTrue()
    }

    @Test
    fun aNewLearnerIsToldHowToFillIt() {
        show(ScorePreviewData.newLearner)
        val empty = "Termina tu primera unidad y aquí verás lo que ya sabes hacer en euskera."
        composeRule.onNode(hasScrollAction()).performScrollToNode(hasText(empty))
        composeRule.onNodeWithText(empty).assertIsDisplayed()
    }

    @Test
    fun screenshot() {
        show(ScorePreviewData.score)
        composeRule.onRoot().captureRoboImage("src/test/screenshots/score.png")
    }

    @Test
    fun screenshotDark() {
        show(ScorePreviewData.score, darkTheme = true)
        composeRule.onRoot().captureRoboImage("src/test/screenshots/score_dark.png")
    }

    @Test
    fun screenshotNewLearner() {
        show(ScorePreviewData.newLearner)
        composeRule.onRoot().captureRoboImage("src/test/screenshots/score_new_learner.png")
    }
}
