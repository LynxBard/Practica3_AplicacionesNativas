package com.example.p3_aplicacionesnativas.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40
)

@Composable
fun P3_AplicacionesNativasTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

// Tema Guinda con soporte para modo claro/oscuro
private val GuindaDarkColorScheme = darkColorScheme(
    primary = GuindaIPNDark,
    onPrimary = Color(0xFF2A0F27),
    primaryContainer = GuindaIPNDarkContainer,
    onPrimaryContainer = GuindaIPNDarkOnContainer,
    secondary = GuindaIPNDarkLight,
    onSecondary = Color(0xFF3D1A37),
    secondaryContainer = Color(0xFF5A3154),
    onSecondaryContainer = Color(0xFFFBE4F8),
    tertiary = Pink80,
    onTertiary = Color(0xFF4D1F2E),
    tertiaryContainer = Color(0xFF6A2E45),
    onTertiaryContainer = Color(0xFFFFD9E2),
    background = Color(0xFF1C1B1E),
    onBackground = Color(0xFFE6E1E5),
    surface = Color(0xFF1C1B1E),
    onSurface = Color(0xFFE6E1E5),
    surfaceVariant = Color(0xFF49454F),
    onSurfaceVariant = Color(0xFFCAC4D0),
    outline = Color(0xFF938F99),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005)
)

private val GuindaLightColorScheme = lightColorScheme(
    primary = GuindaIPN,
    onPrimary = Color.White,
    primaryContainer = GuindaIPNContainer,
    onPrimaryContainer = GuindaIPNOnContainer,
    secondary = GuindaIPNLight,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFF5E0F1),
    onSecondaryContainer = Color(0xFF2D1429),
    tertiary = Pink40,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFFFD9E2),
    onTertiaryContainer = Color(0xFF3E0021),
    background = Color(0xFFFFFBFE),
    onBackground = Color(0xFF1C1B1E),
    surface = Color(0xFFFFFBFE),
    onSurface = Color(0xFF1C1B1E),
    surfaceVariant = Color(0xFFE7E0EC),
    onSurfaceVariant = Color(0xFF49454F),
    outline = Color(0xFF79747E),
    error = Color(0xFFBA1A1A),
    onError = Color.White
)

@Composable
fun GuindaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) GuindaDarkColorScheme else GuindaLightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

// Tema Azul con soporte para modo claro/oscuro
private val AzulDarkColorScheme = darkColorScheme(
    primary = AzulESCOMDark,
    onPrimary = Color(0xFF00233D),
    primaryContainer = AzulESCOMDarkContainer,
    onPrimaryContainer = AzulESCOMDarkOnContainer,
    secondary = AzulESCOMDarkLight,
    onSecondary = Color(0xFF001F3D),
    secondaryContainer = Color(0xFF003D6D),
    onSecondaryContainer = Color(0xFFE0F0FF),
    tertiary = Pink80,
    onTertiary = Color(0xFF4D1F2E),
    tertiaryContainer = Color(0xFF6A2E45),
    onTertiaryContainer = Color(0xFFFFD9E2),
    background = Color(0xFF1C1B1E),
    onBackground = Color(0xFFE6E1E5),
    surface = Color(0xFF1C1B1E),
    onSurface = Color(0xFFE6E1E5),
    surfaceVariant = Color(0xFF49454F),
    onSurfaceVariant = Color(0xFFCAC4D0),
    outline = Color(0xFF938F99),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005)
)

private val AzulLightColorScheme = lightColorScheme(
    primary = AzulESCOM,
    onPrimary = Color.White,
    primaryContainer = AzulESCOMContainer,
    onPrimaryContainer = AzulESCOMOnContainer,
    secondary = AzulESCOMLight,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFD6EAFF),
    onSecondaryContainer = Color(0xFF001A33),
    tertiary = Pink40,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFFFD9E2),
    onTertiaryContainer = Color(0xFF3E0021),
    background = Color(0xFFFFFBFE),
    onBackground = Color(0xFF1C1B1E),
    surface = Color(0xFFFFFBFE),
    onSurface = Color(0xFF1C1B1E),
    surfaceVariant = Color(0xFFE7E0EC),
    onSurfaceVariant = Color(0xFF49454F),
    outline = Color(0xFF79747E),
    error = Color(0xFFBA1A1A),
    onError = Color.White
)

@Composable
fun AzulTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) AzulDarkColorScheme else AzulLightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}