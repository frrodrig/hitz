package eus.hitz.core.designsystem.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import eus.hitz.core.designsystem.R

private fun fredoka(weight: FontWeight) = Font(
    resId = R.font.core_designsystem_fredoka,
    weight = weight,
    variationSettings = FontVariation.Settings(FontVariation.weight(weight.weight)),
)

/** Fredoka: rounded display face for headings, buttons and numbers. */
val FredokaFamily = FontFamily(
    fredoka(FontWeight.Normal),
    fredoka(FontWeight.Medium),
    fredoka(FontWeight.SemiBold),
    fredoka(FontWeight.Bold),
)

/** Atkinson Hyperlegible: body face designed for legibility (clear l/I/1, open shapes). */
val AtkinsonFamily = FontFamily(
    Font(R.font.core_designsystem_atkinson_regular, FontWeight.Normal),
    Font(R.font.core_designsystem_atkinson_italic, FontWeight.Normal, FontStyle.Italic),
    Font(R.font.core_designsystem_atkinson_bold, FontWeight.Bold),
)

private fun display(size: Int, line: Int, weight: FontWeight = FontWeight.SemiBold) = TextStyle(
    fontFamily = FredokaFamily,
    fontWeight = weight,
    fontSize = size.sp,
    lineHeight = line.sp,
)

private fun body(size: Int, line: Int, weight: FontWeight = FontWeight.Normal, tracking: Double = 0.0) = TextStyle(
    fontFamily = AtkinsonFamily,
    fontWeight = weight,
    fontSize = size.sp,
    lineHeight = line.sp,
    letterSpacing = tracking.sp,
)

/** Type scale: Material 3 sizes with Hitz font families. */
val HitzTypography = Typography(
    displayLarge = display(57, 64),
    displayMedium = display(45, 52),
    displaySmall = display(36, 44),
    headlineLarge = display(32, 40),
    headlineMedium = display(28, 36),
    headlineSmall = display(24, 32),
    titleLarge = display(22, 28),
    titleMedium = display(18, 24, FontWeight.Medium),
    titleSmall = display(15, 20, FontWeight.Medium),
    bodyLarge = body(17, 26, tracking = 0.2),
    bodyMedium = body(15, 22, tracking = 0.2),
    bodySmall = body(13, 18, tracking = 0.3),
    labelLarge = display(16, 20, FontWeight.SemiBold),
    labelMedium = body(13, 16, FontWeight.Bold, tracking = 0.4),
    labelSmall = body(11, 16, FontWeight.Bold, tracking = 0.4),
)
