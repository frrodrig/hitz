package eus.hitz.core.designsystem.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import com.google.common.truth.Truth.assertWithMessage
import org.junit.Test
import kotlin.math.max
import kotlin.math.min

/** Every text/background pair must meet WCAG AA (4.5:1) in every scheme. */
class ColorContrastTest {
    private fun contrast(a: Color, b: Color): Double {
        val l1 = a.luminance().toDouble()
        val l2 = b.luminance().toDouble()
        return (max(l1, l2) + 0.05) / (min(l1, l2) + 0.05)
    }

    private fun ColorScheme.pairs() = mapOf(
        "primary" to (primary to onPrimary),
        "primaryContainer" to (primaryContainer to onPrimaryContainer),
        "secondary" to (secondary to onSecondary),
        "secondaryContainer" to (secondaryContainer to onSecondaryContainer),
        "tertiary" to (tertiary to onTertiary),
        "tertiaryContainer" to (tertiaryContainer to onTertiaryContainer),
        "error" to (error to onError),
        "errorContainer" to (errorContainer to onErrorContainer),
        "surface" to (surface to onSurface),
        "surfaceVariant" to (surface to onSurfaceVariant),
        "surfaceContainerHighest" to (surfaceContainerHighest to onSurface),
    )

    private fun HitzExtendedColors.pairs() = listOf(success, warning)
        .flatMapIndexed { index, family ->
            listOf(
                "family$index" to (family.color to family.onColor),
                "family${index}Container" to (family.container to family.onContainer),
            )
        }.toMap()

    @Test
    fun `colour schemes meet WCAG AA`() {
        mapOf(
            "light" to LightColorScheme,
            "dark" to DarkColorScheme,
            "lightHighContrast" to LightHighContrastColorScheme,
            "darkHighContrast" to DarkHighContrastColorScheme,
        ).forEach { (schemeName, scheme) ->
            scheme.pairs().forEach { (pair, colors) ->
                assertWithMessage("$schemeName.$pair")
                    .that(contrast(colors.first, colors.second))
                    .isAtLeast(4.5)
            }
        }
    }

    @Test
    fun `extended colours meet WCAG AA`() {
        mapOf("light" to LightExtendedColors, "dark" to DarkExtendedColors).forEach { (name, colors) ->
            colors.pairs().forEach { (pair, values) ->
                assertWithMessage("$name.$pair").that(contrast(values.first, values.second)).isAtLeast(4.5)
            }
        }
    }
}
