package com.example.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.Spa
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.DailyTasksManager
import com.example.ui.components.FloatingLeavesAtmosphere
import com.example.ui.components.GlassCard
import com.example.ui.navigation.ScreenDestination
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DailyTasksScreen(
    onBack: () -> Unit = {},
    onNavigateToStudy: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val tasksManager = remember { DailyTasksManager.getInstance(context) }

    LaunchedEffect(Unit) {
        tasksManager.refreshTasks()
    }

    val tasks = tasksManager.tasksState.value
    val completedCount = tasks.count { it.isCompleted }
    val totalCount = tasks.size
    val totalXpEarned = tasks.filter { it.isCompleted }.sumOf { it.rewardXp }
    val overallProgress = if (totalCount > 0) completedCount.toFloat() / totalCount.toFloat() else 0f

    val formattedDate = remember {
        val sdf = SimpleDateFormat("EEEE, MMMM d", Locale.getDefault())
        sdf.format(Date())
    }

    androidx.activity.compose.BackHandler(onBack = onBack)

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
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(4.dp))

                // Top Header: Back Arrow + Title
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = onBack,
                            modifier = Modifier.padding(end = 2.dp).testTag("btn_daily_tasks_back")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                                contentDescription = "Back",
                                tint = NeonGreen,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "Daily Tasks",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = formattedDate,
                                fontSize = 11.5.sp,
                                color = TextSecondary
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFF0F2618))
                            .border(1.dp, NeonGreen.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "+$totalXpEarned XP",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = NeonGreen
                        )
                    }
                }
            }

            // Summary Progress Card
            item {
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    borderColor = NeonGreen.copy(alpha = 0.4f)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "TODAY'S PROGRESS",
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NeonGreen,
                                    letterSpacing = 1.1.sp
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "$completedCount of $totalCount Tasks Completed",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            }

                            val pct = (overallProgress * 100).toInt()
                            Text(
                                text = "$pct%",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (pct == 100) NeonGreen else NeonYellow
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        val animatedFraction by animateFloatAsState(
                            targetValue = overallProgress,
                            animationSpec = tween(600, easing = FastOutSlowInEasing),
                            label = "task_progress_anim"
                        )

                        LinearProgressIndicator(
                            progress = { animatedFraction },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = NeonGreen,
                            trackColor = Color.White.copy(alpha = 0.08f)
                        )
                    }
                }
            }

            // Tasks List
            items(tasks) { task ->
                val cardBorder = if (task.isCompleted) NeonGreen.copy(alpha = 0.7f) else Color.White.copy(alpha = 0.1f)
                val cardBg = if (task.isCompleted) BlackSurface.copy(alpha = 0.92f) else BlackSurface.copy(alpha = 0.7f)

                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    borderColor = cardBorder,
                    backgroundColor = cardBg
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Checkbox indicator
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(
                                    if (task.isCompleted) NeonGreen.copy(alpha = 0.2f) else Color.Transparent
                                )
                                .border(
                                    1.2.dp,
                                    if (task.isCompleted) NeonGreen else Color.White.copy(alpha = 0.25f),
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (task.isCompleted) {
                                Icon(
                                    imageVector = Icons.Outlined.Check,
                                    contentDescription = "Completed",
                                    tint = NeonGreen,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = task.title,
                                fontSize = 14.sp,
                                fontWeight = if (task.isCompleted) FontWeight.Bold else FontWeight.SemiBold,
                                color = if (task.isCompleted) TextPrimary else Color(0xFFE2E8F0)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = task.description,
                                fontSize = 11.sp,
                                color = TextSecondary,
                                maxLines = 2
                            )
                            Spacer(modifier = Modifier.height(6.dp))

                            // Mini progress bar & counter
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                val taskFraction = if (task.targetProgress > 0) {
                                    (task.currentProgress.toFloat() / task.targetProgress.toFloat()).coerceIn(0f, 1f)
                                } else 0f

                                LinearProgressIndicator(
                                    progress = { taskFraction },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(4.dp)
                                        .clip(RoundedCornerShape(2.dp)),
                                    color = if (task.isCompleted) NeonGreen else CyberCyan,
                                    trackColor = Color.White.copy(alpha = 0.08f)
                                )

                                Spacer(modifier = Modifier.width(10.dp))

                                Text(
                                    text = "${task.currentProgress}/${task.targetProgress} ${task.unit}",
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = if (task.isCompleted) NeonGreen else TextMuted
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        // Reward chip
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    if (task.isCompleted) Color(0xFF132F1A) else Color(0xFF1C2230)
                                )
                                .border(
                                    1.dp,
                                    if (task.isCompleted) NeonGreen.copy(alpha = 0.6f) else Color.White.copy(alpha = 0.15f),
                                    RoundedCornerShape(12.dp)
                                )
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "+${task.rewardXp} XP",
                                fontSize = 10.5.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = if (task.isCompleted) NeonGreen else NeonYellow
                            )
                        }
                    }
                }
            }

            // Bottom Motivational Notice
            item {
                Spacer(modifier = Modifier.height(4.dp))
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Tasks automatically refresh every midnight.",
                        fontSize = 11.5.sp,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Complete study sessions to advance all goals. 🍃",
                        fontSize = 11.5.sp,
                        color = NeonGreen,
                        fontWeight = FontWeight.Medium
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
