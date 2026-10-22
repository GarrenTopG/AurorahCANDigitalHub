package com.aurorah.can.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

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
