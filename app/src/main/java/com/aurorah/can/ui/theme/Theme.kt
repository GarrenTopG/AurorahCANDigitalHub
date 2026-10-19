package com.aurorah.can.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Aurorah CAN Brand Colors
val BrandPrimary = Color(0xFF752D42)
val BrandMid = Color(0xFF943D56)
val PageBackground = Color(0xFFFAF8F9)
val TextInk = Color(0xFF28252A)
val TextMuted = Color(0xFF625D62)
val BrandPale = Color(0xFFFDF2F8)
val LineColor = Color(0xFFE5DDE1)

@Composable
fun AurorahTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = BrandPrimary,
            onPrimary = Color.White,
            secondary = BrandMid,
            background = PageBackground,
            surface = Color.White,
            onSurface = TextInk
        ),
        content = content
    )
}
