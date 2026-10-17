package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.Bolt
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Spa
import androidx.compose.material.icons.outlined.Star
import com.example.ui.components.TreeGrowthStage
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.FloatingLeavesAtmosphere
import com.example.ui.components.GlassCard
import com.example.ui.components.NeonBadge
import com.example.ui.components.NeonGlowCard
import com.example.ui.components.SegmentedNeonTabs
import com.example.ui.theme.BlackBase
import com.example.ui.theme.BlackSurface
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonOrange
import com.example.ui.theme.NeonRed
import com.example.ui.theme.NeonYellow
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun StatisticsScreen(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val studyRepo = remember { com.example.data.StudySessionRepository.getInstance(context) }
    var selectedTimeframe by rememberSaveable { mutableIntStateOf(0) } // 0=Daily, 1=Weekly, 2=Monthly
    val timeframes = remember { listOf("Daily", "Weekly", "Monthly") }

    val palette = remember { listOf(NeonRed, NeonOrange, NeonYellow, NeonGreen, CyberCyan, NeonYellow, NeonGreen) }

    val todayMinutes = studyRepo.getTodayMinutes()
    val thisWeekMinutes = studyRepo.getThisWeekMinutes()
    val thisMonthMinutes = studyRepo.getThisMonthMinutes()
    val totalStudyMinutes = studyRepo.getTotalStudyMinutes()
    val currentStreak = com.example.ui.screens.AchievementManager.getStreakDays(context)
    val longestStreak = com.example.ui.screens.AchievementManager.getLongestStreakDays(context)
    val completedSessions = com.example.ui.screens.AchievementManager.getCompletedStudySessions(context)
    val currentXp = 320 + (totalStudyMinutes * 10)
    val currentTreeStage = TreeGrowthStage.fromMinutes(totalStudyMinutes)

    fun formatDuration(mins: Int): String {
        val h = mins / 60
        val m = mins % 60
        return when {
            h > 0 && m > 0 -> "${h}h ${m}m"
            h > 0 -> "${h}h"
            else -> "${m}m"
        }
    }

    val rawChartData = when (selectedTimeframe) {
        0 -> studyRepo.getDailyData()
        1 -> studyRepo.getWeeklyData()
        else -> studyRepo.getMonthlyData()
    }

    val totalPeriodMinutes = when (selectedTimeframe) {
        0 -> todayMinutes
        1 -> thisWeekMinutes
        else -> thisMonthMinutes
    }

    val formattedStudyTime = formatDuration(totalPeriodMinutes)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BlackBase)
    ) {
        FloatingLeavesAtmosphere()

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(4.dp))

                // Header: Statistics + Settings icon
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Outlined.BarChart,
                            contentDescription = "Statistics",
                            tint = NeonYellow,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Statistics",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }

                    IconButton(onClick = {}) {
                        Icon(
                            imageVector = Icons.Outlined.Settings,
                            contentDescription = "Settings",
                            tint = TextSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // Real Time Summary Overview (Today, Week, Month, Total)
            item {
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    borderColor = NeonGreen.copy(alpha = 0.25f)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp)
                    ) {
                        Text(
                            text = "STUDY TIME OVERVIEW",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = NeonGreen,
                            letterSpacing = 1.1.sp
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Today", fontSize = 11.sp, color = TextSecondary)
                                Text(
                                    text = formatDuration(todayMinutes),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text("This Week", fontSize = 11.sp, color = TextSecondary)
                                Text(
                                    text = formatDuration(thisWeekMinutes),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NeonYellow,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text("This Month", fontSize = 11.sp, color = TextSecondary)
                                Text(
                                    text = formatDuration(thisMonthMinutes),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = CyberCyan,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Total", fontSize = 11.sp, color = TextSecondary)
                                Text(
                                    text = formatDuration(totalStudyMinutes),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NeonRed,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                    }
                }
            }

            // Segmented Filter: [ Daily | Weekly | Monthly ]
            item {
                SegmentedNeonTabs(
                    items = timeframes,
                    selectedIndex = selectedTimeframe,
                    onSelect = { selectedTimeframe = it },
                    activeColor = NeonYellow,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Big Study Time Card with Neon Bar Chart
            item {
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    borderColor = NeonYellow.copy(alpha = 0.35f)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Column {
                                Text(
                                    text = "${timeframes[selectedTimeframe]} Study Time",
                                    color = TextSecondary,
                                    fontSize = 12.sp
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = formattedStudyTime,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            }

                            if (totalPeriodMinutes > 0) {
                                Text(
                                    text = "Active 🔥",
                                    color = NeonGreen,
                                    fontSize = 13.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        AnimatedContent(
                            targetState = selectedTimeframe,
                            transitionSpec = {
                                (fadeIn(tween(300)) + slideInHorizontally(tween(300)) { it / 3 })
                                    .togetherWith(fadeOut(tween(200)))
                            },
                            label = "chart_timeframe_anim"
                        ) { targetTimeframe ->
                            val currentChartData = when (targetTimeframe) {
                                0 -> studyRepo.getDailyData()
                                1 -> studyRepo.getWeeklyData()
                                else -> studyRepo.getMonthlyData()
                            }

                            if (totalPeriodMinutes <= 0 || currentChartData.isEmpty() || currentChartData.all { it.second == 0 }) {
                                // Professional subtle empty state as requested by User Item 4
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(120.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color.White.copy(alpha = 0.02f))
                                        .border(1.dp, Color.White.copy(alpha = 0.06f), RoundedCornerShape(12.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(text = "🌱", fontSize = 22.sp)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "No study data yet",
                                            fontSize = 13.sp,
                                            color = Color(0xFFCBD5E1),
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = "Complete a focus session to see ${timeframes[targetTimeframe].lowercase()} distribution",
                                            fontSize = 10.5.sp,
                                            color = TextMuted
                                        )
                                    }
                                }
                            } else {
                                val maxVal = (currentChartData.maxOfOrNull { it.second } ?: 1).coerceAtLeast(1)
                                val count = currentChartData.size
                                val animFraction by animateFloatAsState(
                                    targetValue = 1f,
                                    animationSpec = tween(550, easing = FastOutSlowInEasing),
                                    label = "bar_rise_anim"
                                )

                                Column(modifier = Modifier.fillMaxWidth()) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(120.dp)
                                    ) {
                                        Canvas(modifier = Modifier.fillMaxSize()) {
                                            val barWidth = (size.width / (count * 2.2f)).coerceIn(12.dp.toPx(), 28.dp.toPx())
                                            val spacing = if (count > 1) (size.width - (barWidth * count)) / (count - 1) else 0f

                                            currentChartData.forEachIndexed { i, item ->
                                                val heightFraction = (item.second.toFloat() / maxVal.toFloat()).coerceIn(0.12f, 1f)
                                                val barHeight = heightFraction * animFraction * (size.height - 10.dp.toPx())
                                                val left = i * (barWidth + spacing)
                                                val top = size.height - barHeight
                                                val color = palette[i % palette.size]

                                                drawRoundRect(
                                                    brush = Brush.verticalGradient(
                                                        listOf(
                                                            color,
                                                            color.copy(alpha = 0.35f)
                                                        )
                                                    ),
                                                    topLeft = Offset(left, top),
                                                    size = Size(barWidth, barHeight),
                                                    cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
                                                )
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    // Dynamic Day/Interval Labels matching the active timeframe
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        currentChartData.forEach { (label, _) ->
                                            Text(
                                                text = label,
                                                fontSize = 10.sp,
                                                fontFamily = FontFamily.Monospace,
                                                color = TextMuted
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Real Metric Cards (Current Streak, Longest Streak, Completed Sessions, Current XP, Tree Stage)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatMetricTile(
                        icon = Icons.Outlined.LocalFireDepartment,
                        iconColor = NeonOrange,
                        title = "Current Streak",
                        value = "${currentStreak}d",
                        modifier = Modifier.weight(1f)
                    )
                    StatMetricTile(
                        icon = Icons.Outlined.Bolt,
                        iconColor = NeonYellow,
                        title = "Longest Streak",
                        value = "${longestStreak}d",
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatMetricTile(
                        icon = Icons.Outlined.CheckCircle,
                        iconColor = NeonGreen,
                        title = "Completed Sessions",
                        value = "$completedSessions",
                        modifier = Modifier.weight(1f)
                    )
                    StatMetricTile(
                        icon = Icons.Outlined.Star,
                        iconColor = NeonYellow,
                        title = "Current XP",
                        value = "$currentXp XP",
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item {
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    borderColor = currentTreeStage.primaryColor.copy(alpha = 0.5f)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(currentTreeStage.primaryColor.copy(alpha = 0.18f))
                                .border(1.dp, currentTreeStage.primaryColor.copy(alpha = 0.6f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Spa,
                                contentDescription = "Tree Stage",
                                tint = currentTreeStage.primaryColor,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = "Current Tree Stage",
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                            Text(
                                text = "${currentTreeStage.stageName} • Stage ${currentTreeStage.stageNumber}",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = currentTreeStage.primaryColor
                            )
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
private fun StatMetricTile(
    icon: ImageVector,
    iconColor: Color,
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    GlassCard(
        modifier = modifier,
        borderColor = iconColor.copy(alpha = 0.35f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(iconColor.copy(alpha = 0.15f))
                    .border(1.dp, iconColor.copy(alpha = 0.5f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = iconColor,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = title,
                    color = TextSecondary,
                    fontSize = 11.sp
                )
                Text(
                    text = value,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }
        }
    }
}
