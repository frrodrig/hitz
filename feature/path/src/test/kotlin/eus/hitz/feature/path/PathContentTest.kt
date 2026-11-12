package eus.hitz.feature.path

import androidx.compose.material3.Surface
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
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
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(AndroidJUnit4::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [35], qualifiers = RobolectricDeviceQualifiers.Pixel7)
class PathContentTest {
    @get:Rule
    val composeRule = createComposeRule()

    private var started: Triple<String, String, String?>? = null
    private var guidebook: String? = null
    private var storyTapped = false
    private var profileOpened = false
    private var intro: Triple<String, String, String?>? = null

    private fun show(
        darkTheme: Boolean = false,
        dailyGoal: Float = 0.6f,
        seenUnitIntros: Set<String> = setOf("u01-1"),
    ) {
        composeRule.setContent {
            HitzTheme(darkTheme = darkTheme) {
                Surface {
                    PathContent(
                        state = PathUiState.Ready(PathTestData.path, dailyGoal, seenUnitIntros, score = 7),
                        onOpenProfile = { profileOpened = true },
                        onStartLevel = { u, l, s -> started = Triple(u, l, s) },
                        onOpenGuidebook = { guidebook = it },
                        onStartStory = { _, _ -> storyTapped = true },
                        onOpenUnitIntro = { u, l, s -> intro = Triple(u, l, s) },
                    )
                }
            }
        }
    }

    @Test
    fun currentNodeStartsItsLesson() {
        show()
        composeRule.onNodeWithText("Empezar", useUnmergedTree = true).assertExists()
        composeRule.onNodeWithContentDescription("Lección: ¿Quién eres?, disponible").performClick()
        assertThat(started).isEqualTo(Triple("u01-1", "u01-1.l2", "u01-1.l2.s1"))
    }

    @Test
    fun aUnitIsIntroducedBeforeItsFirstLesson() {
        show(seenUnitIntros = emptySet())
        composeRule.onNodeWithContentDescription("Lección: ¿Quién eres?, disponible").performClick()
        assertThat(intro).isEqualTo(Triple("u01-1", "u01-1.l2", "u01-1.l2.s1"))
        assertThat(started).isNull()
    }

    @Test
    fun theDailyGoalShowsTodayAndOpensTheProfile() {
        show(dailyGoal = 0.6f)
        composeRule.onNodeWithContentDescription("Objetivo diario: 60 %").performClick()
        assertThat(profileOpened).isTrue()
    }

    @Test
    fun theScoreOpensTheProfile() {
        show()
        composeRule.onNodeWithContentDescription("Puntuación: 7 de 29").performClick()
        assertThat(profileOpened).isTrue()
    }

    @Test
    fun lockedNodesAreDisabled() {
        show()
        composeRule.onNodeWithContentDescription("Repaso: Repaso, bloqueado").assertIsNotEnabled()
    }

    @Test
    fun unitHeaderOpensTheGuidebook() {
        show()
        composeRule.onNodeWithText("Unidad 1.1 · ", substring = true).assertExists()
        composeRule.onNodeWithContentDescription("Guía de la unidad 1.1").performClick()
        assertThat(guidebook).isEqualTo("u01-1")
    }

    @Test
    fun screenshotLight() {
        show()
        composeRule.onRoot().captureRoboImage("src/test/screenshots/path_light.png")
    }

    @Test
    fun screenshotDark() {
        show(darkTheme = true)
        composeRule.onRoot().captureRoboImage("src/test/screenshots/path_dark.png")
    }

    @Test
    fun screenshotFontScale200() {
        RuntimeEnvironment.setFontScale(2f)
        show()
        composeRule.onRoot().captureRoboImage("src/test/screenshots/path_font_200.png")
    }
}
