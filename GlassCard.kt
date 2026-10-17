package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BlackSurface
import com.example.ui.theme.GlassCardBackground
import com.example.ui.theme.GlassCardBorder
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonRed
import com.example.ui.theme.NeonYellow
import com.example.ui.theme.TextPrimary

/**
 * Premium glass-style card with subtle dark backing and soft luminous border.
 */
@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(20.dp),
    backgroundColor: Color = BlackSurface.copy(alpha = 0.72f),
    borderColor: Color = GlassCardBorder,
    borderGlowBrush: Brush? = null,
    borderWidth: Dp = 1.dp,
    onClick: (() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit
) {
    val clickModifier = if (onClick != null) {
        Modifier.clickable(onClick = onClick)
    } else Modifier

    val borderModifier = if (borderGlowBrush != null) {
        Modifier.border(BorderStroke(borderWidth, borderGlowBrush), shape)
    } else {
        Modifier.border(BorderStroke(borderWidth, borderColor), shape)
    }

    Box(
        modifier = modifier
            .clip(shape)
            .then(borderModifier)
            .background(backgroundColor)
            .then(clickModifier),
        content = content
    )
}

/**
 * Glass card with active RGB / Neon accent glow (Red, Green or Yellow).
 */
@Composable
fun NeonGlowCard(
    accentColor: Color,
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(20.dp),
    glowIntensity: Float = 0.35f,
    onClick: (() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit
) {
    val borderBrush = remember(accentColor) {
        Brush.linearGradient(
            colors = listOf(
                accentColor.copy(alpha = 0.85f),
                accentColor.copy(alpha = 0.20f),
                Color.White.copy(alpha = 0.12f),
                accentColor.copy(alpha = 0.65f)
            ),
            start = Offset.Zero,
            end = Offset.Infinite
        )
    }

    Box(
        modifier = modifier
            .drawBehind {
                drawRoundRect(
                    color = accentColor,
                    alpha = glowIntensity * 0.25f,
                    cornerRadius = CornerRadius(24.dp.toPx(), 24.dp.toPx()),
                    topLeft = Offset(-2.dp.toPx(), -2.dp.toPx()),
                    size = androidx.compose.ui.geometry.Size(
                        size.width + 4.dp.toPx(),
                        size.height + 4.dp.toPx()
                    )
                )
            }
            .clip(shape)
            .border(BorderStroke(1.2.dp, borderBrush), shape)
            .background(BlackSurface.copy(alpha = 0.78f))
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier),
        content = content
    )
}

/**
 * High-tech badge with glowing neon border and monospaced student indicator.
 */
@Composable
fun NeonBadge(
    text: String,
    accentColor: Color,
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(8.dp)
) {
    Box(
        modifier = modifier
            .clip(shape)
            .border(BorderStroke(1.dp, accentColor.copy(alpha = 0.65f)), shape)
            .background(accentColor.copy(alpha = 0.12f))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = accentColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            fontFamily = FontFamily.Monospace,
            letterSpacing = 0.8.sp
        )
    }
}
