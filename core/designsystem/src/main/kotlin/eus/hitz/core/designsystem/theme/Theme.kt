package eus.hitz.core.designsystem.theme

import android.app.UiModeManager
import android.content.Context
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

/**
 * Hitz theme: brand colour schemes, Fredoka + Atkinson Hyperlegible typography and rounded shapes.
 *
 * @param darkTheme use the dark colour scheme.
 * @param dynamicColor use the wallpaper-based colours on Android 12+. Off by default to keep the
 * brand identity; the user can enable it in the settings.
 * @param highContrast use the high-contrast schemes. Defaults to the system contrast setting
 * (Android 14+).
 */
@Composable
fun HitzTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    highContrast: Boolean = rememberSystemHighContrast(),
    content: @Composable () -> Unit,
) {
    val context = LocalContext.current
    val colorScheme: ColorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ->
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        highContrast -> if (darkTheme) DarkHighContrastColorScheme else LightHighContrastColorScheme
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    val extendedColors = if (darkTheme) DarkExtendedColors else LightExtendedColors

    CompositionLocalProvider(LocalHitzExtendedColors provides extendedColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = HitzTypography,
            shapes = HitzShapes,
            content = content,
        )
    }
}

/** Accessors for the tokens that Material 3 does not cover. */
object HitzTheme {
    val extendedColors: HitzExtendedColors
        @Composable
        @ReadOnlyComposable
        get() = LocalHitzExtendedColors.current
}

private const val HIGH_CONTRAST_THRESHOLD = 0.5f

@Composable
private fun rememberSystemHighContrast(): Boolean {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.UPSIDE_DOWN_CAKE) return false
    val context = LocalContext.current
    return remember(context) {
        val uiModeManager = context.getSystemService(Context.UI_MODE_SERVICE) as? UiModeManager
        (uiModeManager?.contrast ?: 0f) >= HIGH_CONTRAST_THRESHOLD
    }
}
