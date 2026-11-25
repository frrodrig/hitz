package eus.hitz.feature.path

import androidx.compose.material3.Surface
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasScrollAction
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
import eus.hitz.core.domain.path.LearningPath
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
        path: LearningPath = PathTestData.path,
    ) {
        composeRule.setContent {
            HitzTheme(darkTheme = darkTheme) {
                Surface {
                    PathContent(
                        state = PathUiState.Ready(path, dailyGoal, seenUnitIntros, score = 7),
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

    /** Challenges close their unit, further down the path. */
    private fun scrollToChallenge() {
        composeRule.onNode(hasScrollAction())
            .performScrollToNode(hasContentDescription("Reto de estrellas de la unidad 1.1", substring = true))
    }

    @Test
    fun aLockedChallengeWaitsForTheUnit() {
        show()
        scrollToChallenge()
        composeRule.onNodeWithContentDescription("Reto de estrellas de la unidad 1.1", substring = true)
            .assertIsNotEnabled()
    }

    @Test
    fun anAvailableChallengeShowsItsStarsAndStarts() {
        val unit = PathTestData.path.sections.single().units.first()
        val challenge = checkNotNull(unit.challenge).copy(isAvailable = true, stars = 2)
        val section = PathTestData.path.sections.single()
        val units = listOf(unit.copy(challenge = challenge)) + section.units.drop(1)
        show(path = LearningPath(listOf(section.copy(units = units))))
        scrollToChallenge()
        composeRule.onNodeWithContentDescription("Reto de estrellas de la unidad 1.1: 2 de 3 estrellas").performClick()
        assertThat(started).isEqualTo(Triple("u01-1", "u01-1.l5", null))
    }

    @Test
    fun unitHeaderOpensTheGuidebook() {
        show()
        composeRule.onNodeWithText("Unidad 1.1 · ", substring = true).assertExists()
        composeRule.onNodeWithContentDescription("Guía de la unidad 1.1").performClick()
        assertThat(guidebook).isEqualTo("u01-1")
    }

    @Test
    fun theSectionIsNamedOnceAtTheTop() {
        show()
        composeRule.onNodeWithText("Primeros pasos").assertExists()
        composeRule.onNodeWithText("7 pts").assertExists()
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
