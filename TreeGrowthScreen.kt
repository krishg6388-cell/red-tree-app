package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.outlined.Spa
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.FloatingLeavesAtmosphere
import com.example.ui.components.GlassCard
import com.example.ui.components.LivingTreeGrowthViewer
import com.example.ui.components.TreeGrowthStage
import com.example.ui.theme.BlackBase
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonGreenBright
import com.example.ui.theme.NeonYellow
import com.example.ui.theme.OrangeYellowGradient
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary

@Composable
fun TreeGrowthScreen(
    onBack: () -> Unit = {},
    onNavigateToStudy: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val outerAuraBrush = remember {
        Brush.radialGradient(
            listOf(
                NeonGreen,
                NeonYellow,
                Color.Transparent
            )
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BlackBase)
    ) {
        FloatingLeavesAtmosphere()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Spacer(modifier = Modifier.height(4.dp))

                // Header: Back arrow + Tree Growth
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = "Back",
                            tint = NeonGreen,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Text(
                        text = "Tree Growth",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Spectacular Glowing Tree Art Container (Screen 10 reference)
                Box(
                    modifier = Modifier.size(260.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Outer aura glow
                    Box(
                        modifier = Modifier
                            .size(250.dp)
                            .clip(CircleShape)
                            .drawBehind {
                                drawCircle(
                                    brush = outerAuraBrush,
                                    alpha = 0.22f
                                )
                            }
                    )

                    LivingTreeGrowthViewer(
                        targetStage = TreeGrowthStage.BABY_PLANT,
                        isTimerRunning = false,
                        modifier = Modifier
                            .size(230.dp)
                            .clip(CircleShape)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Level Tag: "Level 3 • Young Tree"
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Level 3",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeonYellow,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "•", color = TextMuted, fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Young Tree",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeonGreenBright
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // XP Bar: 320 / 500 XP
                Column(
                    modifier = Modifier.fillMaxWidth(0.85f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color.White.copy(alpha = 0.1f))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(320f / 500f)
                                .height(8.dp)
                                .background(OrangeYellowGradient)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "320 / 500 XP",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = NeonYellow
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Next Level Card (Screen 10 reference)
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                borderColor = NeonGreen.copy(alpha = 0.5f),
                onClick = onNavigateToStudy
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(NeonGreen.copy(alpha = 0.15f))
                                .border(1.dp, NeonGreen.copy(alpha = 0.6f), RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Spa,
                                contentDescription = "Next Level",
                                tint = NeonGreenBright,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column {
                            Text(
                                text = "Next Level",
                                fontSize = 11.sp,
                                color = TextMuted,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                text = "Level 4 - Sapling",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }
                    }

                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.ArrowForward,
                        contentDescription = "Go",
                        tint = NeonGreen,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}
