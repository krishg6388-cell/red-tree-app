package com.example.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Spa
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.components.FloatingLeavesAtmosphere
import com.example.ui.components.LivingDashboardTree
import com.example.ui.navigation.ScreenDestination
import com.example.ui.theme.BlackBase
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonRed
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun DashboardScreen(
    onNavigateTo: (ScreenDestination) -> Unit,
    isPaused: Boolean = false,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BlackBase)
    ) {
        if (!isPaused) {
            FloatingLeavesAtmosphere()
        }

        // Main content matching reference image
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 18.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Spacer(modifier = Modifier.height(2.dp))

            // Header: R.E.D TREE Branding Logo + Settings Icon
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Tree Emblem Logo
                    Image(
                        painter = painterResource(id = R.drawable.img_logo_red_tree),
                        contentDescription = "R.E.D Tree Logo",
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Fit
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    Text(
                        text = buildAnnotatedString {
                            withStyle(
                                SpanStyle(
                                    color = Color(0xFFFF334B),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 19.sp,
                                    letterSpacing = 1.sp
                                )
                            ) {
                                append("R.E.D")
                            }
                            withStyle(
                                SpanStyle(
                                    color = Color(0xFF00E676),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 19.sp,
                                    letterSpacing = 1.sp
                                )
                            ) {
                                append(" TREE")
                            }
                        }
                    )
                }

                IconButton(
                    onClick = { onNavigateTo(ScreenDestination.FOCUS_GUARD) },
                    modifier = Modifier.testTag("btn_dashboard_settings")
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Settings,
                        contentDescription = "Settings",
                        tint = Color(0xFFCBD5E1),
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            // Greeting Section: "Good Morning, Krish 🍃" / "Better than yesterday 🍃"
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Good Morning,",
                    color = Color(0xFF94A3B8),
                    fontSize = 15.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Krish",
                        color = TextPrimary,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "🍃", fontSize = 20.sp)
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Better than yesterday 🍃",
                    color = Color(0xFF00E676),
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            // Split Hero: Left = Large Glowing Study Tree; Right = Today's Study & Your Tree cards
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left Column: Highly realistic Living Study Tree with continuous animation
                LivingDashboardTree(
                    modifier = Modifier
                        .weight(1f)
                        .height(216.dp),
                    isPaused = isPaused,
                    onClick = { onNavigateTo(ScreenDestination.TREE_GROWTH) }
                )

                val studyRepo = remember { com.example.data.StudySessionRepository.getInstance(context) }
                val todayMinutes = studyRepo.todayMinutesState.value
                val todayHours = todayMinutes / 60
                val todayMinutesRem = todayMinutes % 60
                val todayProgressFraction = (todayMinutes.toFloat() / 360f).coerceIn(0f, 1f)

                // Right Column: Two stacked dark glass cards with sharp neon borders
                Column(
                    modifier = Modifier
                        .weight(1.08f)
                        .height(216.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Card 1: Today's Study
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .border(
                                1.2.dp,
                                Brush.linearGradient(
                                    listOf(
                                        Color(0xFF00E676),
                                        Color(0xFF00E5FF),
                                        Color(0xFFFF2A4B).copy(alpha = 0.4f)
                                    )
                                ),
                                RoundedCornerShape(16.dp)
                            )
                            .background(Color(0xE6080D14))
                            .clickable { onNavigateTo(ScreenDestination.STATISTICS) }
                            .padding(horizontal = 11.dp, vertical = 9.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Today's Study",
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFFCBD5E1),
                                maxLines = 1
                            )

                            Row(
                                verticalAlignment = Alignment.Bottom
                            ) {
                                Text(
                                    text = "$todayHours h ${todayMinutesRem}m",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    maxLines = 1
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "/ 6 h",
                                    fontSize = 10.5.sp,
                                    color = Color(0xFF8E8E93),
                                    modifier = Modifier.padding(bottom = 1.dp)
                                )
                            }

                            // Glowing Neon Green-Cyan Progress Bar
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(5.dp)
                                    .clip(RoundedCornerShape(2.5.dp))
                                    .background(Color(0xFF14202D))
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(if (todayProgressFraction > 0f) todayProgressFraction else 0.05f)
                                        .fillMaxSize()
                                        .clip(RoundedCornerShape(2.5.dp))
                                        .background(
                                            Brush.horizontalGradient(
                                                listOf(
                                                    Color(0xFF00E676),
                                                    Color(0xFF00E5FF)
                                                )
                                            )
                                        )
                                )
                            }
                        }
                    }

                    // Card 2: Your Tree
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .border(
                                1.2.dp,
                                Brush.linearGradient(
                                    listOf(
                                        Color(0xFF00E5FF),
                                        Color(0xFF00E676),
                                        Color(0xFFFF9100).copy(alpha = 0.5f)
                                    )
                                ),
                                RoundedCornerShape(16.dp)
                            )
                            .background(Color(0xE6080D14))
                            .clickable { onNavigateTo(ScreenDestination.TREE_GROWTH) }
                            .padding(horizontal = 11.dp, vertical = 9.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Your Tree",
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFFCBD5E1),
                                maxLines = 1
                            )

                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Spa,
                                    contentDescription = "Sprout",
                                    tint = Color(0xFF00E676),
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Column {
                                    Text(
                                        text = "Level 1",
                                        fontSize = 10.sp,
                                        color = Color(0xFFCBD5E1),
                                        maxLines = 1
                                    )
                                    Text(
                                        text = "Seedling",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        maxLines = 1
                                    )
                                }
                            }

                            // Glowing Amber-Yellow Mini Progress Bar
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(4.dp)
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(Color(0xFF1B1D25))
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(0.35f)
                                        .fillMaxSize()
                                        .clip(RoundedCornerShape(2.dp))
                                        .background(
                                            Brush.horizontalGradient(
                                                listOf(
                                                    Color(0xFFFF9100),
                                                    Color(0xFFFFD600)
                                                )
                                            )
                                        )
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(2.dp))

            // Large Start Study Button with Vibrant RGB Neon Border
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .clip(RoundedCornerShape(26.dp))
                    .border(
                        2.dp,
                        Brush.horizontalGradient(
                            listOf(
                                Color(0xFFFF2A4B),
                                Color(0xFFFF7A00),
                                Color(0xFFFFD600),
                                Color(0xFF00E676)
                            )
                        ),
                        RoundedCornerShape(30.dp)
                    )
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                Color(0xFF28070F),
                                Color(0xFF071F14)
                            )
                        )
                    )
                    .clickable { onNavigateTo(ScreenDestination.STUDY) }
                    .testTag("btn_start_study"),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    // Circular Glowing Play Indicator
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .border(1.5.dp, Color(0xFFFF2A4B), CircleShape)
                            .background(Color(0xFFFF2A4B).copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Start Study",
                            tint = Color(0xFFFF334B),
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Text(
                        text = "Start Study",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        letterSpacing = 0.5.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(2.dp))

            // Centered Motivational Quote & Luminous Divider Line
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "🍃 “Distractions steal your time,\nbut focus builds your future.” 🍃",
                    color = Color(0xFF67E8F9),
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Luminous horizontal divider line with glowing center bead
                Box(
                    modifier = Modifier
                        .width(180.dp)
                        .height(14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(2.dp)
                    ) {
                        val midY = size.height / 2
                        drawLine(
                            brush = Brush.horizontalGradient(
                                listOf(
                                    Color.Transparent,
                                    Color(0xFF00E676).copy(alpha = 0.85f),
                                    Color.Transparent
                                )
                            ),
                            start = Offset(0f, midY),
                            end = Offset(size.width, midY),
                            strokeWidth = 1.5.dp.toPx()
                        )
                    }

                    // Golden-yellow center bead
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFFD600))
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Compact Professional Study Progress & Focus Milestone Section based on REAL data
            val studyRepo = remember { com.example.data.StudySessionRepository.getInstance(context) }
            val todayMinutes = studyRepo.todayMinutesState.value
            val subjectBreakdown = studyRepo.subjectBreakdownState.value

            DashboardStudyProgressSection(
                todayMinutes = todayMinutes,
                subjectBreakdown = subjectBreakdown,
                onNavigateTo = onNavigateTo
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun DashboardStudyProgressSection(
    todayMinutes: Int,
    subjectBreakdown: Map<String, Int>,
    onNavigateTo: (ScreenDestination) -> Unit
) {
    val targetMinutes = 360 // 6 hours
    val progressFraction = (todayMinutes.toFloat() / targetMinutes).coerceIn(0f, 1f)
    val remainingMinutes = (targetMinutes - todayMinutes).coerceAtLeast(0)
    val nextMilestoneMins = (40 - (todayMinutes % 40)).coerceAtLeast(1)

    val topSubject = subjectBreakdown.maxByOrNull { it.value }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .border(
                1.2.dp,
                Brush.horizontalGradient(
                    listOf(
                        Color(0xFFFF2A4B).copy(alpha = 0.45f),
                        Color(0xFFFFD600).copy(alpha = 0.55f),
                        Color(0xFF00E676).copy(alpha = 0.55f)
                    )
                ),
                RoundedCornerShape(20.dp)
            )
            .background(Color(0xE6080D14))
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Row 1: Target Header with Live Progress
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF00E676).copy(alpha = 0.16f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Spa,
                        contentDescription = "Target",
                        tint = Color(0xFF00E676),
                        modifier = Modifier.size(16.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "TODAY'S TARGET",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFFD600),
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = if (remainingMinutes > 0) "${todayMinutes}m completed • ${remainingMinutes}m remaining" else "Daily 6h target achieved! 🎉",
                        fontSize = 11.5.sp,
                        color = Color(0xFFCBD5E1)
                    )
                }
            }

            Text(
                text = "${(progressFraction * 100).toInt()}%",
                fontFamily = FontFamily.Monospace,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF00E676)
            )
        }

        // Luminous Progress Bar toward Today's Goal
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(Color(0xFF14202D))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(if (progressFraction > 0f) progressFraction else 0.05f)
                    .fillMaxSize()
                    .clip(RoundedCornerShape(3.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(Color(0xFFFF334B), Color(0xFFFF9100), Color(0xFF00E676))
                        )
                    )
            )
        }

        // Row 2: Subject Progress & Next Milestone Dual Cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Left Card: Subject Focus
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(12.dp))
                    .background(Color(0xFF0D1520))
                    .clickable { onNavigateTo(ScreenDestination.STUDY) }
                    .padding(horizontal = 10.dp, vertical = 8.dp)
            ) {
                Column {
                    Text(
                        text = "SUBJECT FOCUS",
                        fontSize = 9.5.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF67E8F9),
                        letterSpacing = 0.8.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = if (topSubject != null) "📚 ${topSubject.key} (${topSubject.value}m)" else "📚 Mathematics (Active)",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White,
                        maxLines = 1
                    )
                }
            }

            // Right Card: Next Milestone
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(12.dp))
                    .background(Color(0xFF0D1520))
                    .clickable { onNavigateTo(ScreenDestination.ACHIEVEMENTS) }
                    .padding(horizontal = 10.dp, vertical = 8.dp)
            ) {
                Column {
                    Text(
                        text = "NEXT MILESTONE",
                        fontSize = 9.5.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFF9100),
                        letterSpacing = 0.8.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "🏅 ${nextMilestoneMins}m to next badge",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White,
                        maxLines = 1
                    )
                }
            }
        }
    }
}
