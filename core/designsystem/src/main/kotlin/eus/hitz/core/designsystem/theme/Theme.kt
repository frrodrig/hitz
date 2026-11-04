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

// Paleta provisional de M0. El tema definitivo (colores, tipografía y shapes) llega en M1.
private val GaltzagorriRed = Color(0xFFC8102E)
private val GaltzagorriRedDark = Color(0xFFFFB3B0)

private val LightColors = lightColorScheme(primary = GaltzagorriRed)
private val DarkColors = darkColorScheme(primary = GaltzagorriRedDark)

/**
 * Tema de Hitz.
 *
 * @param darkTheme usa el esquema oscuro.
 * @param dynamicColor usa el color dinámico del sistema (Android 12+); desactivado por defecto
 * para mantener la identidad de marca.
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
