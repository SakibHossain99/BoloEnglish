package com.example.ui.theme

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

private val LightColorScheme = lightColorScheme(
    primary = PrimaryBlue,
    onPrimary = StitchOnPrimary,
    primaryContainer = HdPrimaryContainer,
    onPrimaryContainer = HdOnPrimaryContainer,
    secondary = StitchSecondary,
    onSecondary = StitchOnSecondary,
    secondaryContainer = StitchSecondaryContainer,
    onSecondaryContainer = StitchOnSecondaryContainer,
    tertiary = StitchTertiary,
    onTertiary = StitchOnTertiary,
    tertiaryContainer = StitchTertiaryContainer,
    onTertiaryContainer = StitchOnTertiaryContainer,
    error = StitchError,
    onError = StitchOnError,
    errorContainer = StitchErrorContainer,
    onErrorContainer = StitchOnErrorContainer,
    background = BackgroundLight,
    surface = CardSurface,
    surfaceVariant = HdSurfaceVariant,
    outline = HdOutline,
    outlineVariant = HdOutlineVariant,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    onSurfaceVariant = TextSecondary
)

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryBlue,
    onPrimary = Color.White,
    primaryContainer = Color(0xFF004493),
    onPrimaryContainer = Color(0xFFD8E2FF),
    secondary = StitchSecondary,
    onSecondary = Color.White,
    tertiary = StitchTertiary,
    onTertiary = Color.White,
    background = Color(0xFF181C23),
    surface = Color(0xFF1C2028),
    onBackground = Color(0xFFE0E2ED),
    onSurface = Color(0xFFE0E2ED),
    surfaceVariant = Color(0xFF2D3039),
    onSurfaceVariant = Color(0xFFC1C6D7)
)

@Composable
fun BoloEnglishTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
