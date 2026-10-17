package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Spa
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BlackSurface
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonRed
import com.example.ui.theme.NeonYellow
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

/**
 * High-tech glass placeholder representing the future study companion.
 * Explicitly designed as a reserved placeholder in accordance with project scope.
 */
@Composable
fun CompanionPlaceholderCard(
    modifier: Modifier = Modifier
) {
    val borderGlow = remember {
        Brush.horizontalGradient(
            colors = listOf(
                NeonGreen.copy(alpha = 0.45f),
                NeonYellow.copy(alpha = 0.35f),
                NeonRed.copy(alpha = 0.25f)
            )
        )
    }

    GlassCard(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        borderGlowBrush = borderGlow
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Header Tag
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(NeonYellow)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "FUTURE COMPANION SLOT",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = NeonYellow,
                        letterSpacing = 1.sp
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color.White.copy(alpha = 0.08f))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "RESERVED",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp,
                        color = TextMuted,
                        letterSpacing = 0.8.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Interactive/Graphic Hologram Placeholder
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Ethereal Pulse Sphere (rendered statically without continuous 60fps loop)
                Box(
                    modifier = Modifier.size(68.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.size(68.dp)) {
                        val center = Offset(size.width / 2, size.height / 2)
                        val strokeW1 = 1.5.dp.toPx()
                        val strokeW2 = 1.dp.toPx()
                        // Outer pulse ring
                        drawCircle(
                            color = NeonGreen,
                            alpha = 0.14f,
                            radius = (size.minDimension / 2) * 0.96f,
                            style = Stroke(width = strokeW1)
                        )
                        // Inner secondary ring
                        drawCircle(
                            color = NeonYellow,
                            alpha = 0.22f,
                            radius = (size.minDimension / 2.8f),
                            style = Stroke(width = strokeW2)
                        )
                        // Core seed glow
                        drawCircle(
                            color = NeonGreen,
                            alpha = 0.28f,
                            radius = size.minDimension / 3.2f,
                            center = center
                        )
                    }

                    Icon(
                        imageVector = Icons.Outlined.Spa,
                        contentDescription = "Companion Seed Placeholder",
                        tint = TextPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Neural Focus Companion",
                        color = TextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "A living digital spirit that evolves as you Resist distractions and Engage deep focus.",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Sub-status strip
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .border(
                        1.dp,
                        Color.White.copy(alpha = 0.08f),
                        RoundedCornerShape(10.dp)
                    )
                    .background(Color.Black.copy(alpha = 0.35f))
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "// Companion Incubator • Standby",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                    Text(
                        text = "Phase 2",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = NeonGreen.copy(alpha = 0.8f)
                    )
                }
            }
        }
    }
}

/**
 * Compact companion status pill for secondary screens.
 */
@Composable
fun MiniCompanionDock(
    modifier: Modifier = Modifier
) {
    val dockBorderBrush = remember {
        Brush.horizontalGradient(
            listOf(NeonGreen.copy(alpha = 0.5f), NeonYellow.copy(alpha = 0.5f))
        )
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .border(
                1.dp,
                dockBorderBrush,
                RoundedCornerShape(20.dp)
            )
            .background(BlackSurface.copy(alpha = 0.85f))
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(NeonGreen)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "COMPANION: INCUBATING",
                fontFamily = FontFamily.Monospace,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextSecondary,
                letterSpacing = 0.8.sp
            )
        }
    }
}
