package com.aurorah.can.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFE598AC),
    onPrimary = Color(0xFF42001B),
    primaryContainer = Color(0xFF5B1B2E),
    onPrimaryContainer = Color(0xFFFFD9E2),
    secondary = Color(0xFFE57373),
    onSecondary = Color(0xFF28252A),
    background = Color(0xFF181214),
    onBackground = Color(0xFFEDE0E3),
    surface = Color(0xFF221A1D),
    onSurface = Color(0xFFEDE0E3),
    surfaceVariant = Color(0xFF2E2327),
    onSurfaceVariant = Color(0xFFC2B4B8),
    outline = Color(0xFF4A3E43)
)

private val LightColorScheme = lightColorScheme(
    primary = BrandPrimary,
    onPrimary = Color.White,
    primaryContainer = BrandPale,
    onPrimaryContainer = BrandPrimary,
    secondary = BrandMid,
    onSecondary = Color.White,
    background = PageBackground,
    onBackground = TextInk,
    surface = Color.White,
    onSurface = TextInk,
    surfaceVariant = Color(0xFFF5F3F4),
    onSurfaceVariant = TextMuted,
    outline = LineColor
)

@Composable
fun AurorahTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
