@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package eus.hitz.core.designsystem.theme

import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import eus.hitz.core.designsystem.R

private fun sansFlex(weight: FontWeight) = Font(
    resId = R.font.core_designsystem_sans_flex,
    weight = weight,
    variationSettings = FontVariation.Settings(FontVariation.weight(weight.weight)),
)

/**
 * Google Sans Flex, the Material 3 Expressive typeface (SIL OFL 1.1), subset to Latin and
 * instanced to its weight axis.
 */
val SansFlexFamily = FontFamily(
    sansFlex(FontWeight.Normal),
    sansFlex(FontWeight.Medium),
    sansFlex(FontWeight.SemiBold),
    sansFlex(FontWeight.Bold),
    sansFlex(FontWeight.ExtraBold),
)

private fun TextStyle.withFamily() = copy(fontFamily = SansFlexFamily)

private val Baseline = Typography()

/** The Material 3 Expressive type scale (including emphasized styles) set in Google Sans Flex. */
val HitzTypography = Typography(
    displayLarge = Baseline.displayLarge.withFamily(),
    displayMedium = Baseline.displayMedium.withFamily(),
    displaySmall = Baseline.displaySmall.withFamily(),
    headlineLarge = Baseline.headlineLarge.withFamily(),
    headlineMedium = Baseline.headlineMedium.withFamily(),
    headlineSmall = Baseline.headlineSmall.withFamily(),
    titleLarge = Baseline.titleLarge.withFamily(),
    titleMedium = Baseline.titleMedium.withFamily(),
    titleSmall = Baseline.titleSmall.withFamily(),
    bodyLarge = Baseline.bodyLarge.withFamily(),
    bodyMedium = Baseline.bodyMedium.withFamily(),
    bodySmall = Baseline.bodySmall.withFamily(),
    labelLarge = Baseline.labelLarge.withFamily(),
    labelMedium = Baseline.labelMedium.withFamily(),
    labelSmall = Baseline.labelSmall.withFamily(),
    displayLargeEmphasized = Baseline.displayLargeEmphasized.withFamily(),
    displayMediumEmphasized = Baseline.displayMediumEmphasized.withFamily(),
    displaySmallEmphasized = Baseline.displaySmallEmphasized.withFamily(),
    headlineLargeEmphasized = Baseline.headlineLargeEmphasized.withFamily(),
    headlineMediumEmphasized = Baseline.headlineMediumEmphasized.withFamily(),
    headlineSmallEmphasized = Baseline.headlineSmallEmphasized.withFamily(),
    titleLargeEmphasized = Baseline.titleLargeEmphasized.withFamily(),
    titleMediumEmphasized = Baseline.titleMediumEmphasized.withFamily(),
    titleSmallEmphasized = Baseline.titleSmallEmphasized.withFamily(),
    bodyLargeEmphasized = Baseline.bodyLargeEmphasized.withFamily(),
    bodyMediumEmphasized = Baseline.bodyMediumEmphasized.withFamily(),
    bodySmallEmphasized = Baseline.bodySmallEmphasized.withFamily(),
    labelLargeEmphasized = Baseline.labelLargeEmphasized.withFamily(),
    labelMediumEmphasized = Baseline.labelMediumEmphasized.withFamily(),
    labelSmallEmphasized = Baseline.labelSmallEmphasized.withFamily(),
)
