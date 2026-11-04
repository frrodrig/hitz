package eus.hitz.feature.path

import androidx.compose.material3.Surface
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
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
class PathContentTest {
    @get:Rule
    val composeRule = createComposeRule()

    private var started: Triple<String, String, String?>? = null
    private var guidebook: String? = null
    private var storyTapped = false
    private var shopOpened = false

    private fun show(darkTheme: Boolean = false, stats: PathStats = PathStats(streak = 3, gems = 120)) {
        composeRule.setContent {
            HitzTheme(darkTheme = darkTheme) {
                Surface {
                    PathContent(
                        state = PathUiState.Ready(PathTestData.path, stats),
                        onOpenShop = { shopOpened = true },
                        onStartLevel = { u, l, s -> started = Triple(u, l, s) },
                        onOpenGuidebook = { guidebook = it },
                        onStartStory = { _, _ -> storyTapped = true },
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
    fun withoutHeartsALessonOffersTheShop() {
        show(stats = PathStats(hearts = 0))
        composeRule.onNodeWithContentDescription("Lección: ¿Quién eres?, disponible").performClick()
        assertThat(started).isNull()
        composeRule.onNodeWithText("Te has quedado sin vidas").assertExists()
        composeRule.onNodeWithText("Ir a la tienda").performClick()
        assertThat(shopOpened).isTrue()
    }

    @Test
    fun lockedNodesAreDisabled() {
        show()
        composeRule.onNodeWithContentDescription("Repaso: Repaso, bloqueado").assertIsNotEnabled()
    }

    @Test
    fun unitHeaderOpensTheGuidebook() {
        show()
        composeRule.onNodeWithText("Unidad 1.1").assertExists()
        composeRule.onAllNodesWithText("Guía").onFirst().performScrollTo().performClick()
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
}
