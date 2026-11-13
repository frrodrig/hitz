package eus.hitz.core.designsystem.component

import androidx.compose.foundation.layout.Column
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertHasNoClickAction
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import eus.hitz.core.designsystem.theme.HitzTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [35])
class ComponentBehaviourTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun theAnimatedMascotIsOneImageForAccessibility() {
        composeRule.setContent {
            HitzTheme {
                Column {
                    HitzMascot(pose = MascotPose.Bien, contentDescription = "Galtzagorri")
                    HitzMascot(pose = MascotPose.Piensa, contentDescription = "Quieto", animated = false)
                    HitzMascot(pose = MascotPose.Hola, contentDescription = null)
                }
            }
        }
        // Six stacked layers, announced once, as an image.
        composeRule.onNodeWithContentDescription("Galtzagorri")
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Image))
        composeRule.onNodeWithContentDescription("Quieto").assertExists()
        composeRule.onAllNodes(SemanticsMatcher.keyIsDefined(SemanticsProperties.ContentDescription))
            .assertCountEquals(2)
    }

    @Test
    fun choiceCardReportsSelectionAndState() {
        var clicks = 0
        composeRule.setContent {
            HitzTheme { HitzChoiceCard("etxea", ChoiceState.Selected, onClick = { clicks++ }) }
        }
        composeRule.onNodeWithText("etxea")
            .assertIsSelected()
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.StateDescription, "Seleccionada"))
            .performClick()
        assertThat(clicks).isEqualTo(1)
    }

    @Test
    fun disabledChoiceCardIsNotEnabled() {
        composeRule.setContent { HitzTheme { HitzChoiceCard("etxea", ChoiceState.Disabled, onClick = {}) } }
        composeRule.onNodeWithText("etxea").assertIsNotEnabled()
    }

    @Test
    fun usedWordChipIsAnnouncedAndNotClickable() {
        composeRule.setContent { HitzTheme { HitzWordChip("naiz", onClick = {}, used = true) } }
        composeRule.onNodeWithContentDescription("naiz, ya usada").assertHasNoClickAction()
    }

    @Test
    fun wordChipIsClickable() {
        var clicked = false
        composeRule.setContent { HitzTheme { HitzWordChip("naiz", onClick = { clicked = true }) } }
        composeRule.onNodeWithText("naiz").assertHasClickAction().performClick()
        assertThat(clicked).isTrue()
    }

    @Test
    fun dailyGoalChipHasSpokenDescription() {
        var clicked = false
        composeRule.setContent { HitzTheme { HitzDailyGoalChip(0.6f, onClick = { clicked = true }) } }
        composeRule.onNodeWithContentDescription("Objetivo diario: 60 %").assertHasClickAction().performClick()
        assertThat(clicked).isTrue()
    }

    @Test
    fun feedbackPanelShowsAnswerAndContinues() {
        var continued = false
        composeRule.setContent {
            HitzTheme {
                HitzFeedbackPanel(
                    kind = FeedbackKind.Incorrect,
                    title = "Erantzun zuzena:",
                    continueLabel = "Ados",
                    onContinue = { continued = true },
                    message = "Kalean nago.",
                )
            }
        }
        composeRule.onNodeWithText("Kalean nago.", substring = true).assertExists()
        composeRule.onNodeWithText("Ados").performClick()
        assertThat(continued).isTrue()
    }
}
