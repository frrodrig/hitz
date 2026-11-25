@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package eus.hitz.core.designsystem.theme

import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import com.google.common.truth.Truth.assertWithMessage
import org.junit.Test

/** The scale only works if its levels stay clearly apart. */
class TypographyTest {
    private val regularAndEmphasized: Map<String, Pair<TextStyle, TextStyle>> = with(HitzTypography) {
        mapOf(
            "displayLarge" to (displayLarge to displayLargeEmphasized),
            "displaySmall" to (displaySmall to displaySmallEmphasized),
            "headlineLarge" to (headlineLarge to headlineLargeEmphasized),
            "headlineSmall" to (headlineSmall to headlineSmallEmphasized),
            "titleLarge" to (titleLarge to titleLargeEmphasized),
            "titleSmall" to (titleSmall to titleSmallEmphasized),
            "bodyLarge" to (bodyLarge to bodyLargeEmphasized),
            "bodySmall" to (bodySmall to bodySmallEmphasized),
            "labelLarge" to (labelLarge to labelLargeEmphasized),
            "labelSmall" to (labelSmall to labelSmallEmphasized),
        )
    }

    @Test
    fun `emphasized styles are at least one full weight step heavier`() {
        regularAndEmphasized.forEach { (name, styles) ->
            val (regular, emphasized) = styles
            val step = emphasized.weight() - regular.weight()
            assertWithMessage("$name: emphasized weight step").that(step).isAtLeast(WEIGHT_STEP)
        }
    }

    @Test
    fun `reading text keeps the normal width`() {
        with(HitzTypography) {
            listOf(bodyLarge, bodyMedium, bodySmall, labelLarge, titleMedium).forEach {
                assertWithMessage("${it.fontSize}").that(it.fontFamily).isEqualTo(SansFlexFamily)
            }
        }
        assertWithMessage("lessonPhrase").that(HitzTextStyles.lessonPhrase.fontFamily).isEqualTo(SansFlexFamily)
    }

    @Test
    fun `display text and hero numerals are wide`() {
        with(HitzTypography) {
            listOf(displayLarge, displayMedium, displaySmall).forEach {
                assertWithMessage("${it.fontSize}").that(it.fontFamily).isEqualTo(SansFlexWideFamily)
            }
        }
        assertWithMessage("heroNumeral").that(HitzTextStyles.heroNumeral.fontFamily).isEqualTo(SansFlexWidestFamily)
    }

    private fun TextStyle.weight() = (fontWeight ?: FontWeight.Normal).weight

    private companion object {
        const val WEIGHT_STEP = 100
    }
}
