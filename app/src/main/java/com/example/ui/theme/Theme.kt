package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val FFVoiceChangerColorScheme = darkColorScheme(
    primary = CyberCyan,
    onPrimary = Color.Black,
    primaryContainer = CyberCardBg,
    onPrimaryContainer = CyberCyan,
    secondary = CyberPink,
    onSecondary = Color.White,
    secondaryContainer = CyberCardBg,
    onSecondaryContainer = CyberPink,
    tertiary = CyberPurple,
    onTertiary = Color.White,
    background = CyberBg,
    onBackground = TextPrimary,
    surface = CyberSurface,
    onSurface = TextPrimary,
    surfaceVariant = CyberCardBg,
    onSurfaceVariant = TextSecondary,
    outline = CyberCardBorder,
    error = CyberRed,
    onError = Color.White
)

@Composable
fun FFVoiceChangerTheme(
    darkTheme: Boolean = true, // Default to gamer dark mode
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = FFVoiceChangerColorScheme,
        typography = Typography,
        content = content
    )
}
