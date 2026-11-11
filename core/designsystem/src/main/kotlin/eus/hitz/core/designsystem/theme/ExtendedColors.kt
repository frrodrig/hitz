package eus.hitz.core.designsystem.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/** A colour with its content colour and a container pair, following Material 3 roles. */
@Immutable
data class ColorFamily(
    val color: Color,
    val onColor: Color,
    val container: Color,
    val onContainer: Color,
)

/**
 * Colours that Material 3 does not define: answer feedback (right, almost right) and the
 * daily goal once met.
 *
 * Read them with `HitzTheme.extendedColors`.
 */
@Immutable
data class HitzExtendedColors(
    val success: ColorFamily,
    val warning: ColorFamily,
)

internal val LocalHitzExtendedColors = staticCompositionLocalOf { LightExtendedColors }
