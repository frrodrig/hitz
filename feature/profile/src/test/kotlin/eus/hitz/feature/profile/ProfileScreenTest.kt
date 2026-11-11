package eus.hitz.feature.profile

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.assertIsSelected
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
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(AndroidJUnit4::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [35], qualifiers = RobolectricDeviceQualifiers.Pixel7)
class ProfileScreenTest {
    @get:Rule
    val composeRule = createComposeRule()

    private val changes = mutableListOf<Pair<Setting, Boolean>>()
    private var goal: Int? = null
    private var scoreOpened = false

    private fun show(darkTheme: Boolean = false) {
        composeRule.setContent {
            HitzTheme(darkTheme = darkTheme) {
                ProfileContent(
                    state = ProfilePreviewData.ready,
                    onSettingChange = { setting, enabled -> changes += setting to enabled },
                    onGoalChange = { goal = it },
                    onOpenScore = { scoreOpened = true },
                )
            }
        }
    }

    private fun scrollTo(text: String) {
        composeRule.onNode(hasScrollAction()).performScrollToNode(hasText(text))
    }

    @Test
    fun showsTodayAndTheWeek() {
        show()
        composeRule.onNodeWithText("Perfil").assertIsDisplayed()
        scrollTo("Hoy: 6 de 10 min")
        composeRule.onNodeWithText("Hoy: 6 de 10 min").assertIsDisplayed()
        composeRule.onNodeWithText("Te faltan 4 minutos.").assertIsDisplayed()
        composeRule.onNodeWithContentDescription("lunes: 120 %").assertIsDisplayed()
        composeRule.onNodeWithContentDescription("miércoles: 60 %").assertIsDisplayed()
    }

    @Test
    fun theScoreOpensItsDetails() {
        show()
        composeRule.onNodeWithContentDescription("Puntuación: 7 de 29").assertIsDisplayed()
        composeRule.onNodeWithText("Hacia el 8: 40 %").assertIsDisplayed()
        composeRule.onNodeWithText("Ver tu evolución").performClick()
        assertThat(scoreOpened).isTrue()
    }

    @Test
    fun theDailyGoalCanBeChanged() {
        show()
        scrollTo("15 min")
        composeRule.onNodeWithText("10 min").assertIsSelected()
        composeRule.onNodeWithText("15 min").performClick()
        assertThat(goal).isEqualTo(15)
    }

    @Test
    fun achievementsShowTheirNextGoal() {
        show()
        scrollTo("Estudio: lecciones completadas")
        composeRule.onNodeWithText("Nivel 2 de 4 · 31 de 50").assertIsDisplayed()
    }

    @Test
    fun settingsCanBeToggled() {
        show()
        scrollTo("Ejercicios de hablar")
        composeRule.onNodeWithText("Ejercicios de hablar").assertIsOn().performClick()
        scrollTo("Efectos de sonido")
        composeRule.onNodeWithText("Efectos de sonido").assertIsOn().performClick()
        scrollTo("Recordatorio diario")
        composeRule.onNodeWithText("Recordatorio diario").assertIsOff().performClick()
        assertThat(changes).containsExactly(
            Setting.Speaking to false,
            Setting.SoundEffects to false,
            Setting.Reminder to true,
        ).inOrder()
    }

    @Test
    fun screenshot() {
        show()
        composeRule.onRoot().captureRoboImage("src/test/screenshots/profile.png")
    }

    @Test
    fun screenshotFontScale200() {
        RuntimeEnvironment.setFontScale(2f)
        show()
        composeRule.onRoot().captureRoboImage("src/test/screenshots/profile_font_200.png")
    }
}
