package com.example.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Deep black canvas & surface hierarchy
val BlackBase = Color(0xFF040507)
val BlackCanvas = Color(0xFF07090D)
val BlackSurface = Color(0xFF0E1219)
val BlackSurfaceElevated = Color(0xFF141A24)
val BlackSurfaceHighlight = Color(0xFF1C2330)

// Glass surfaces & glowing borders
val GlassBackground = Color(0xCC0D121B)
val GlassCardBackground = Color(0x99131924)
val GlassCardBorder = Color(0x3300E676)
val GlassSubtle = Color(0x0EFFFFFF)

// Primary Accents: R.E.D (Red, Green, Yellow)
// RED: Remove / Resist
val NeonRed = Color(0xFFFF2A55)
val NeonRedBright = Color(0xFFFF334B)
val NeonRedGlow = Color(0x4DFF2A55)
val NeonRedDark = Color(0xFF4A0A19)
val NeonRedSubtle = Color(0x1FFF2A55)

// GREEN: Educate / Engage
val NeonGreen = Color(0xFF00E676)
val NeonGreenBright = Color(0xFF10F284)
val NeonGreenGlow = Color(0x4D00E676)
val NeonGreenDark = Color(0xFF004D27)
val NeonGreenSubtle = Color(0x1F00E676)

// YELLOW & ORANGE: Distraction / Develop / Solar
val NeonYellow = Color(0xFFFFD600)
val NeonYellowBright = Color(0xFFFFEA00)
val NeonYellowGlow = Color(0x4DFFD600)
val NeonYellowDark = Color(0xFF524400)
val NeonYellowSubtle = Color(0x1FFFD600)

val NeonOrange = Color(0xFFFF6D00)
val NeonOrangeGlow = Color(0x4DFF6D00)

// Cyan & Blue Accents for Shields and Charts
val CyberCyan = Color(0xFF00E5FF)
val CyberBlue = Color(0xFF2979FF)

// Text tokens
val TextPrimary = Color(0xFFF3F5FA)
val TextSecondary = Color(0xFF9BA1B6)
val TextMuted = Color(0xFF5A6075)

// Signature Neon Gradients
val RedOrangeGradient = Brush.horizontalGradient(listOf(NeonRed, NeonOrange))
val OrangeYellowGradient = Brush.horizontalGradient(listOf(NeonOrange, NeonYellow))
val GreenCyanGradient = Brush.horizontalGradient(listOf(NeonGreen, CyberCyan))
val RedGreenYellowGradient = Brush.horizontalGradient(listOf(NeonRed, NeonGreen, NeonYellow))
val NeonBorderGradient = Brush.linearGradient(
    listOf(
        NeonGreen.copy(alpha = 0.8f),
        NeonYellow.copy(alpha = 0.5f),
        NeonRed.copy(alpha = 0.6f)
    )
)


