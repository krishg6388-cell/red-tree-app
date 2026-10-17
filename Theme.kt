package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = NeonGreen,
    onPrimary = BlackBase,
    primaryContainer = NeonGreenDark,
    onPrimaryContainer = NeonGreen,

    secondary = NeonRed,
    onSecondary = Color.White,
    secondaryContainer = NeonRedDark,
    onSecondaryContainer = NeonRed,

    tertiary = NeonYellow,
    onTertiary = BlackBase,
    tertiaryContainer = NeonYellowDark,
    onTertiaryContainer = NeonYellow,

    background = BlackBase,
    onBackground = TextPrimary,
    surface = BlackSurface,
    onSurface = TextPrimary,
    surfaceVariant = BlackSurfaceElevated,
    onSurfaceVariant = TextSecondary,
    outline = GlassCardBorder,
    outlineVariant = GlassSubtle
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false, // Always enforce R.E.D Tree's signature cyber aesthetic
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}

