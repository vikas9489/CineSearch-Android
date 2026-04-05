package com.movieapp.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

// Black & Green dark theme
private val MovieColorScheme = darkColorScheme(
    primary = Green600,
    onPrimary = Black,
    primaryContainer = Green900,
    onPrimaryContainer = Green300,
    secondary = Green400,
    onSecondary = Black,
    tertiary = Green300,
    onTertiary = Black,
    background = Black,
    onBackground = White,
    surface = DarkSurface,
    onSurface = White,
    surfaceVariant = DarkCard,
    onSurfaceVariant = Gray400,
    outline = Gray700,
    error = ErrorRed,
    onError = Black
)

@Composable
fun MovieAppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = MovieColorScheme,
        typography = Typography,
        content = content
    )
}
