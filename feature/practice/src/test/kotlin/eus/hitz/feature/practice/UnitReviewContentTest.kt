package eus.hitz.feature.practice

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
import eus.hitz.core.domain.practice.ReviewWord
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(AndroidJUnit4::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [35], qualifiers = RobolectricDeviceQualifiers.Pixel7)
class UnitReviewContentTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun aUnitShowsItsWordsGuidePhrasesAndStory() {
        val played = mutableListOf<ReviewWord>()
        var practised: String? = null
        var story: Pair<String, String>? = null
        var back = false
        composeRule.setContent {
            HitzTheme {
                UnitReviewContent(
                    UnitReviewUiState.Ready(UnitReviewPreviewData.review),
                    onBack = { back = true },
                    onPractice = { practised = it },
                    onReadStory = { unit, level -> story = unit to level },
                    onPlay = { played += it },
                )
            }
        }
        composeRule.onNodeWithText("Unidad 1.1").assertExists()
        composeRule.onNodeWithText("arratsalde on").assertExists()
        composeRule.onRoot().captureRoboImage("src/test/screenshots/unit_review_words.png")

        composeRule.onNodeWithContentDescription("Escuchar «gabon»").performClick()
        assertThat(played.map { it.clipId }).containsExactly("u01-1.l1.s1.e06")

        composeRule.onNodeWithText("Guía").performClick()
        composeRule.onNodeWithText("Saludar según la hora").assertExists()
        composeRule.onRoot().captureRoboImage("src/test/screenshots/unit_review_guide.png")

        composeRule.onNodeWithText("Historia").performClick()
        composeRule.onNodeWithText("Leer la historia").performClick()
        assertThat(story).isEqualTo("u01-1" to "u01-1.l4")

        composeRule.onNodeWithText("Practicar esta unidad").performClick()
        assertThat(practised).isEqualTo("u01-1")
        composeRule.onNodeWithContentDescription("Atrás").performClick()
        assertThat(back).isTrue()
    }
}
