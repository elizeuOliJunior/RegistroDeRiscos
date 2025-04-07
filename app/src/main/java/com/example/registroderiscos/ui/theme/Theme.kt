// ui/theme/Theme.kt
package com.example.registroderiscos.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat


private val DarkColorScheme = darkColorScheme(
    primary = PrimaryGreen,
    secondary = PrimaryDarkGreen,
    tertiary = PrimaryGreen,
    background = Color.Black, // Adapte conforme necessário
    surface = Color.Black, // Adapte conforme necessário
    onPrimary = White,
    onSecondary = White,
    onTertiary = White,
    onBackground = White,
    onSurface = White,
    error = ErrorRed,
    onError = White
)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryGreen,
    secondary = PrimaryDarkGreen,
    tertiary = PrimaryGreen,
    background = BackgroundLightGreen,
    surface = BackgroundLightGreen,
    onPrimary = White,
    onSecondary = White,
    onTertiary = White,
    onBackground = TextColorPrimary,
    onSurface = TextColorPrimary,
    error = ErrorRed,
    onError = White
)

@Composable
fun RegistroDeRiscosTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme.not()
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}