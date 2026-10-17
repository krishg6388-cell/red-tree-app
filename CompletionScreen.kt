package com.example.ui.screens

import androidx.activity.compose.BackHandler
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.FloatingLeavesAtmosphere
import com.example.ui.components.GlassCard
import com.example.ui.components.GlowButton
import com.example.ui.theme.BlackBase
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonGreenBright
import com.example.ui.theme.NeonOrange
import com.example.ui.theme.NeonYellow
import com.example.ui.theme.RedOrangeGradient
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun CompletionScreen(
    onContinue: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val subtleQueue = AchievementManager.subtleBannerQueue.value
    val fullCelebrationQueue = AchievementManager.activeFullCelebrationQueue.value

    BackHandler(onBack = onContinue)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BlackBase)
    ) {
        FloatingLeavesAtmosphere()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Spacer(modifier = Modifier.height(4.dp))

                // Header: Check In + Calendar Icon
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.CalendarMonth,
                        contentDescription = "Check In",
                        tint = NeonGreen,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Check In",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }

                Spacer(modifier = Modifier.height(36.dp))

                // Huge Glowing Checkmark Circle (Screen 11 reference)
                Box(
                    modifier = Modifier.size(130.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Outer glow halo
                    Box(
                        modifier = Modifier
                            .size(126.dp)
                            .clip(CircleShape)
                            .drawBehind {
                                drawCircle(
                                    brush = Brush.radialGradient(
                                        listOf(NeonGreen, Color.Transparent)
                                    ),
                                    alpha = 0.28f
                                )
                            }
                    )

                    // Checkmark circle container
                    Box(
                        modifier = Modifier
                            .size(96.dp)
                            .clip(CircleShape)
                            .border(3.dp, NeonGreen, CircleShape)
                            .background(NeonGreen.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Success",
                            tint = NeonGreenBright,
                            modifier = Modifier.size(48.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                // Congratulations text
                Text(
                    text = "You did it!",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = NeonYellow
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Study session completed!",
                    fontSize = 15.sp,
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.height(32.dp))

                // 3 Metrics Row: Focus Time (1.5h), XP Gained (+20), Streak (3d)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    CompletionStatBox(
                        title = "Focus Time",
                        value = "1.5h",
                        accentColor = TextPrimary,
                        modifier = Modifier.weight(1f)
                    )
                    CompletionStatBox(
                        title = "XP Gained",
                        value = "+20",
                        accentColor = NeonGreenBright,
                        modifier = Modifier.weight(1f)
                    )
                    CompletionStatBox(
                        title = "Streak",
                        value = "3d",
                        accentColor = NeonOrange,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(28.dp))

                // Motivational quote
                Text(
                    text = "“Discipline today\nbuilds the freedom tomorrow.”",
                    fontSize = 13.sp,
                    color = TextSecondary,
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Radiant Continue Button
            GlowButton(
                text = "Continue",
                onClick = onContinue,
                gradient = RedOrangeGradient,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("btn_completion_continue")
            )
        }

        val currentSubtle = subtleQueue.firstOrNull()
        if (currentSubtle != null) {
            SubtleFocusAchievementBanner(
                achievement = currentSubtle,
                onDismiss = { AchievementManager.dismissSubtleBanner() },
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }

        val currentFull = fullCelebrationQueue.firstOrNull()
        if (currentFull != null) {
            FullAchievementCelebrationOverlay(
                achievement = currentFull,
                queuePosition = 1,
                queueTotal = fullCelebrationQueue.size,
                onContinue = {
                    AchievementManager.markCelebrationViewed(context, currentFull.id)
                }
            )
        }
    }
}

@Composable
private fun CompletionStatBox(
    title: String,
    value: String,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    GlassCard(
        modifier = modifier,
        borderColor = Color.White.copy(alpha = 0.12f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                fontSize = 10.sp,
                color = TextMuted,
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                fontFamily = FontFamily.Monospace,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = accentColor
            )
        }
    }
}
