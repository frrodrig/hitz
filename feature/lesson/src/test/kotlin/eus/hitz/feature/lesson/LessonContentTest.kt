package eus.hitz.feature.lesson

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.google.common.truth.Truth.assertThat
import eus.hitz.core.designsystem.theme.HitzTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [35], qualifiers = RobolectricDeviceQualifiers.Pixel7)
class LessonContentTest {
    @get:Rule
    val composeRule = createComposeRule()

    private val actions = mutableListOf<LessonAction>()

    private fun show(state: LessonUiState) {
        composeRule.setContent { HitzTheme { LessonContent(state, onAction = { actions += it }, onClose = {}) } }
    }

    @Test
    fun tappingAWordSelectsIt() {
        show(LessonPreviewData.active.copy(exercise = LessonPreviewData.wordBank.copy(selected = emptyList())))
        composeRule.onNodeWithText("Traduce esta frase").assertIsDisplayed()
        composeRule.onNodeWithText("Hola").performScrollTo().performClick()
        assertThat(actions).containsExactly(LessonAction.SelectTile(2))
    }

    @Test
    fun checkIsDisabledWithoutAnAnswer() {
        show(LessonPreviewData.active.copy(exercise = LessonPreviewData.wordBank.copy(selected = emptyList())))
        composeRule.onNodeWithText("Comprobar").assertIsNotEnabled()
    }

    @Test
    fun incorrectFeedbackShowsTheAnswerAndContinues() {
        show(LessonPreviewData.incorrect)
        composeRule.onNodeWithText("¡Hola! Soy Ane.", substring = true).assertIsDisplayed()
        composeRule.onNodeWithText("Continuar").performClick()
        assertThat(actions).containsExactly(LessonAction.Continue)
    }

    @Test
    fun quitDialogConfirms() {
        show(LessonPreviewData.active.copy(showQuitDialog = true))
        composeRule.onNodeWithText("¿Salir de la lección?").assertIsDisplayed()
        composeRule.onNodeWithText("Salir").performClick()
        assertThat(actions).containsExactly(LessonAction.ConfirmQuit)
    }

    @Test
    fun finishedShowsAccuracy() {
        show(LessonPreviewData.finished)
        composeRule.onNodeWithText("¡Lección completada!").assertIsDisplayed()
        composeRule.onNodeWithText("A la primera").assertIsDisplayed()
        composeRule.onNodeWithText("80 %").assertIsDisplayed()
        composeRule.onNodeWithText("4 min").assertIsDisplayed()
        composeRule.onNodeWithText("105 %").assertIsDisplayed()
        composeRule.onNodeWithText("Has hecho lo necesario por hoy. ¡Bien hecho!").assertIsDisplayed()
        composeRule.onNodeWithText("Has conseguido 1 logro nuevo.").assertIsDisplayed()
    }

    @Test
    fun finishedOnTheWayShowsTheMinutesLeft() {
        show(LessonPreviewData.finishedOnTheWay)
        composeRule.onNodeWithText("45 %").assertIsDisplayed()
        composeRule.onNodeWithText("Te faltan 6 minutos para tu objetivo de hoy.").assertIsDisplayed()
    }

    @Test
    fun legendaryLevelsShowTheMistakesLeft() {
        show(LessonPreviewData.active.copy(mistakesLeft = 2))
        composeRule.onNodeWithContentDescription("Te quedan 2 fallos permitidos").assertIsDisplayed()
    }

    @Test
    fun matchPairsHasNoCheckButton() {
        show(LessonPreviewData.active.copy(exercise = LessonPreviewData.pairs))
        composeRule.onNodeWithText("Toca las parejas").assertIsDisplayed()
        assertThat(composeRule.onAllNodesWithText("Comprobar").fetchSemanticsNodes()).isEmpty()
        composeRule.onAllNodesWithText("kaixo").onFirst().performClick()
        assertThat(actions).hasSize(1)
    }

    @Test
    fun listenButtonPlaysTheClip() {
        show(LessonPreviewData.active.copy(exercise = LessonPreviewData.listenTap))
        composeRule.onNodeWithText("Toca lo que oyes").assertIsDisplayed()
        composeRule.onNodeWithText("Escuchar").performClick()
        composeRule.onNodeWithContentDescription("Escuchar despacio").performClick()
        assertThat(actions).containsExactly(LessonAction.PlayAudio(), LessonAction.PlayAudio(slow = true)).inOrder()
    }

    @Test
    fun choosingAMeaningSelectsIt() {
        show(LessonPreviewData.active.copy(exercise = LessonPreviewData.meaning.copy(selected = null)))
        composeRule.onNodeWithText("¡De nada!").performScrollTo().performClick()
        assertThat(actions).containsExactly(LessonAction.SelectOption(2))
    }

    @Test
    fun trueFalseIncorrectNeedsNoCorrection() {
        show(LessonPreviewData.trueFalseIncorrect)
        composeRule.onNodeWithText("No es correcto").assertIsDisplayed()
    }

    @Test
    fun chatShowsTheChosenReplyAsTheLearners() {
        show(LessonPreviewData.active.copy(exercise = LessonPreviewData.chat))
        composeRule.onNodeWithText("Tú").assertIsDisplayed()
        assertThat(composeRule.onAllNodesWithText("Ni Itziar naiz.").fetchSemanticsNodes()).hasSize(2)
    }

    @Test
    fun speakingCanBeSkippedWithoutARecognizer() {
        show(LessonPreviewData.active.copy(exercise = LessonPreviewData.speak))
        assertThat(composeRule.onAllNodesWithText("Comprobar").fetchSemanticsNodes()).isEmpty()
        composeRule.onNodeWithText("reconocimiento de voz", substring = true).assertIsDisplayed()
        composeRule.onNodeWithText("Ahora no puedo hablar").performScrollTo().performClick()
        assertThat(actions).containsExactly(LessonAction.SkipSpeaking)
    }
}
