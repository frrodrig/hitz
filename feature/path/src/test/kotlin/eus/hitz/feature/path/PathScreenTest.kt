package eus.hitz.feature.path

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
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
class PathScreenTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun showsTitleAndStartsTheSampleLesson() {
        var started: Pair<String, String>? = null
        composeRule.setContent { HitzTheme { PathScreen(onStartLesson = { u, l -> started = u to l }) } }
        composeRule.onNodeWithText("Ruta").assertIsDisplayed()
        composeRule.onNodeWithText("Probar la lección 1.1").performClick()
        assertThat(started).isEqualTo("u01-1" to "u01-1.l1.s1")
    }
}
