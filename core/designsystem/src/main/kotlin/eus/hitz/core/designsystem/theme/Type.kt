@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package eus.hitz.core.designsystem.theme

import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import eus.hitz.core.designsystem.R

/** Widths of Google Sans Flex's wdth axis that the type scale uses. */
private const val WIDTH_NORMAL = 100f
private const val WIDTH_WIDE = 112f
private const val WIDTH_WIDEST = 125f

/** Every weight the scale uses; each needs its own instance of the variable font. */
private val Weights = listOf(
    FontWeight.Normal,
    FontWeight.Medium,
    FontWeight.SemiBold,
    FontWeight.Bold,
    FontWeight.ExtraBold,
)

private fun sansFlex(weight: FontWeight, width: Float) = Font(
    resId = R.font.core_designsystem_sans_flex,
    weight = weight,
    variationSettings = FontVariation.Settings(
        FontVariation.weight(weight.weight),
        FontVariation.width(width),
    ),
)

private fun sansFlexFamily(width: Float) = FontFamily(Weights.map { sansFlex(it, width) })

/**
 * Google Sans Flex, the Material 3 Expressive typeface (SIL OFL 1.1), subset to Latin with its
 * weight and width axes (`tools/design/subset_font.py`). Reading text uses the normal width.
 */
val SansFlexFamily = sansFlexFamily(WIDTH_NORMAL)

/** The wide cut, for headlines and display text that carry a screen. */
val SansFlexWideFamily = sansFlexFamily(WIDTH_WIDE)

/** The widest cut, kept for hero numerals and emphasized display text. */
val SansFlexWidestFamily = sansFlexFamily(WIDTH_WIDEST)

private val Baseline = Typography()

private fun TextStyle.styled(weight: FontWeight, family: FontFamily = SansFlexFamily) =
    copy(fontFamily = family, fontWeight = weight)

/**
 * The Hitz type scale: Material 3 sizes and line heights, with weights and widths that make the
 * levels distinct. Display text is wide and bold; headlines are semibold, and their emphasized
 * styles bold and wide; titles are semibold; body text stays regular and normal width so long
 * Basque and Spanish sentences read easily. Emphasized styles are one clear step heavier, not a
 * nuance: they mark the one thing to look at on a screen.
 */
val HitzTypography = Typography(
    displayLarge = Baseline.displayLarge.styled(FontWeight.Bold, SansFlexWideFamily),
    displayMedium = Baseline.displayMedium.styled(FontWeight.Bold, SansFlexWideFamily),
    displaySmall = Baseline.displaySmall.styled(FontWeight.Bold, SansFlexWideFamily),
    headlineLarge = Baseline.headlineLarge.styled(FontWeight.SemiBold),
    headlineMedium = Baseline.headlineMedium.styled(FontWeight.SemiBold),
    headlineSmall = Baseline.headlineSmall.styled(FontWeight.SemiBold),
    titleLarge = Baseline.titleLarge.styled(FontWeight.SemiBold),
    titleMedium = Baseline.titleMedium.styled(FontWeight.SemiBold),
    titleSmall = Baseline.titleSmall.styled(FontWeight.SemiBold),
    bodyLarge = Baseline.bodyLarge.styled(FontWeight.Normal),
    bodyMedium = Baseline.bodyMedium.styled(FontWeight.Normal),
    bodySmall = Baseline.bodySmall.styled(FontWeight.Normal),
    labelLarge = Baseline.labelLarge.styled(FontWeight.Medium),
    labelMedium = Baseline.labelMedium.styled(FontWeight.Medium),
    labelSmall = Baseline.labelSmall.styled(FontWeight.Medium),
    displayLargeEmphasized = Baseline.displayLargeEmphasized.styled(FontWeight.ExtraBold, SansFlexWidestFamily),
    displayMediumEmphasized = Baseline.displayMediumEmphasized.styled(FontWeight.ExtraBold, SansFlexWidestFamily),
    displaySmallEmphasized = Baseline.displaySmallEmphasized.styled(FontWeight.ExtraBold, SansFlexWidestFamily),
    headlineLargeEmphasized = Baseline.headlineLargeEmphasized.styled(FontWeight.Bold, SansFlexWideFamily),
    headlineMediumEmphasized = Baseline.headlineMediumEmphasized.styled(FontWeight.Bold, SansFlexWideFamily),
    headlineSmallEmphasized = Baseline.headlineSmallEmphasized.styled(FontWeight.Bold, SansFlexWideFamily),
    titleLargeEmphasized = Baseline.titleLargeEmphasized.styled(FontWeight.Bold),
    titleMediumEmphasized = Baseline.titleMediumEmphasized.styled(FontWeight.Bold),
    titleSmallEmphasized = Baseline.titleSmallEmphasized.styled(FontWeight.Bold),
    bodyLargeEmphasized = Baseline.bodyLargeEmphasized.styled(FontWeight.SemiBold),
    bodyMediumEmphasized = Baseline.bodyMediumEmphasized.styled(FontWeight.SemiBold),
    bodySmallEmphasized = Baseline.bodySmallEmphasized.styled(FontWeight.SemiBold),
    labelLargeEmphasized = Baseline.labelLargeEmphasized.styled(FontWeight.Bold),
    labelMediumEmphasized = Baseline.labelMediumEmphasized.styled(FontWeight.Bold),
    labelSmallEmphasized = Baseline.labelSmallEmphasized.styled(FontWeight.Bold),
)

private val HeroNumeralSize = 88.sp
private val HeroNumeralTracking = (-1).sp
private val LessonPhraseSize = 30.sp
private val LessonPhraseLineHeight = 38.sp

/**
 * Text styles outside the Material scale, for the two places where Hitz breaks it on purpose.
 * Colours come from the surrounding content colour, as with the Material styles.
 */
object HitzTextStyles {
    /** The figure of a hero moment, e.g. first-try accuracy at the end of a lesson. */
    val heroNumeral = TextStyle(
        fontFamily = SansFlexWidestFamily,
        fontWeight = FontWeight.ExtraBold,
        fontSize = HeroNumeralSize,
        lineHeight = HeroNumeralSize,
        letterSpacing = HeroNumeralTracking,
    )

    /** The Basque (or Spanish) sentence an exercise is about: large, but normal width to read. */
    val lessonPhrase = TextStyle(
        fontFamily = SansFlexFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = LessonPhraseSize,
        lineHeight = LessonPhraseLineHeight,
    )
}
