package eus.hitz.core.designsystem.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

// Temporary M0 palette. The final theme (colours, typography and shapes) arrives in M1.
private val GaltzagorriRed = Color(0xFFC8102E)
private val GaltzagorriRedDark = Color(0xFFFFB3B0)

private val LightColors = lightColorScheme(primary = GaltzagorriRed)
private val DarkColors = darkColorScheme(primary = GaltzagorriRedDark)

/**
 * Hitz theme.
 *
 * @param darkTheme use the dark colour scheme.
 * @param dynamicColor use the system dynamic colours (Android 12+); off by default to keep the
 * brand identity.
 */
@Composable
fun HitzTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColors
        else -> LightColors
    }
    MaterialTheme(colorScheme = colorScheme, content = content)
}
