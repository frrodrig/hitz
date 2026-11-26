package eus.hitz.feature.path.sections

import androidx.compose.ui.test.hasScrollAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
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
import eus.hitz.core.domain.path.NodeState
import eus.hitz.core.model.content.CefrLevel
import eus.hitz.feature.path.PathPreviewData
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(AndroidJUnit4::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [35], qualifiers = RobolectricDeviceQualifiers.Pixel7)
class SectionsContentTest {
    @get:Rule
    val composeRule = createComposeRule()

    private var selected: String? = null
    private var jumpedTo: String? = null

    /** The preview path, with a finished first section, one under way and a locked A2 one. */
    private val path: LearningPath = PathPreviewData.path.let { preview ->
        val first = preview.sections[0].let { section ->
            section.copy(
                units = section.units.map { unit ->
                    unit.copy(nodes = unit.nodes.map { it.copy(state = NodeState.Completed, isCurrent = false) })
                },
            )
        }
        val second = preview.sections[1].let { section ->
            val unit = section.units.single()
            section.copy(
                units = listOf(
                    unit.copy(nodes = unit.nodes.map { it.copy(state = NodeState.Available, isCurrent = true) })
                )
            )
        }
        val third = preview.sections[1].copy(
            id = "s5",
            title = preview.sections[1].title.copy(es = "Entre amigos", eu = "Lagunartean"),
            level = CefrLevel.A2,
        )
        LearningPath(listOf(first, second, third))
    }

    private fun show(darkTheme: Boolean = false) {
        composeRule.setContent {
            HitzTheme(darkTheme = darkTheme) {
                SectionsContent(
                    SectionsUiState.Ready(path),
                    onBack = {},
                    onSelect = { selected = it },
                    onJump = { jumpedTo = it }
                )
            }
        }
    }

    @Test
    fun sectionsAreGroupedByLevelWithTheirPoints() {
        show()
        composeRule.onNodeWithText("Nivel A1").assertExists()
        composeRule.onNodeWithText("Puntuación de 0 a 29").assertExists()
        composeRule.onNodeWithText("Nivel A2").assertExists()
    }

    @Test
    fun theCurrentSectionContinuesAndAFinishedOneCanBeReviewed() {
        show()
        composeRule.onNodeWithText("Continuar").performClick()
        assertThat(selected).isEqualTo("s2")
        composeRule.onNodeWithText("Repasar").performClick()
        assertThat(selected).isEqualTo("s1")
    }

    @Test
    fun aLockedSectionOffersAJump() {
        show()
        composeRule.onNode(hasScrollAction()).performScrollToNode(hasText("Saltar aquí"))
        composeRule.onNodeWithText("Saltar aquí").performClick()
        assertThat(jumpedTo).isEqualTo("s5")
    }

    @Test
    fun screenshot() {
        show()
        composeRule.onRoot().captureRoboImage("src/test/screenshots/sections.png")
    }

    @Test
    fun screenshotDark() {
        show(darkTheme = true)
        composeRule.onRoot().captureRoboImage("src/test/screenshots/sections_dark.png")
    }
}
