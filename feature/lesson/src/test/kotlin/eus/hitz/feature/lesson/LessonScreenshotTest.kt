package eus.hitz.feature.lesson

import androidx.compose.ui.test.junit4.createComposeRule
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
class LessonScreenshotTest {
    @get:Rule
    val composeRule = createComposeRule()

    private fun capture(name: String, state: LessonUiState, darkTheme: Boolean = false) {
        composeRule.setContent {
            HitzTheme(darkTheme = darkTheme) { LessonContent(state, onAction = {}, onClose = {}) }
        }
        composeRule.onRoot().captureRoboImage("src/test/screenshots/$name.png")
    }

    @Test
    fun wordBank() = capture("lesson_word_bank", LessonPreviewData.active)

    @Test
    fun wordBankIncorrectDark() = capture(
        "lesson_word_bank_incorrect_dark",
        LessonPreviewData.incorrect,
        darkTheme = true
    )

    @Test
    fun typingAlmost() = capture("lesson_typing_almost", LessonPreviewData.typingAlmost)

    @Test
    fun chooseImage() = capture(
        "lesson_choose_image",
        LessonPreviewData.active.copy(exercise = LessonPreviewData.image)
    )

    @Test
    fun matchPairs() = capture("lesson_match_pairs", LessonPreviewData.active.copy(exercise = LessonPreviewData.pairs))

    @Test
    fun listenTap() = capture(
        "lesson_listen_tap",
        LessonPreviewData.active.copy(exercise = LessonPreviewData.listenTap)
    )

    @Test
    fun fillBlank() = capture(
        "lesson_fill_blank",
        LessonPreviewData.active.copy(exercise = LessonPreviewData.fillBlank)
    )

    @Test
    fun chooseMeaningDark() = capture(
        "lesson_choose_meaning_dark",
        LessonPreviewData.active.copy(exercise = LessonPreviewData.meaning),
        darkTheme = true,
    )

    @Test
    fun completeChatCorrect() = capture("lesson_complete_chat_correct", LessonPreviewData.chatCorrect)

    @Test
    fun trueFalseIncorrect() = capture("lesson_true_false_incorrect", LessonPreviewData.trueFalseIncorrect)

    @Test
    fun speak() = capture("lesson_speak", LessonPreviewData.active.copy(exercise = LessonPreviewData.speak))

    @Test
    fun finished() = capture("lesson_finished", LessonPreviewData.finished)

    @Test
    fun outOfHearts() = capture("lesson_out_of_hearts", LessonUiState.OutOfHearts)
}
