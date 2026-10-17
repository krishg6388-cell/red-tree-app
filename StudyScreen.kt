package com.example.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.NotificationsActive
import androidx.compose.material.icons.outlined.Science
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Spa
import androidx.compose.material.icons.outlined.Terminal
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material.icons.outlined.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.components.FloatingLeavesAtmosphere
import com.example.ui.components.StudySessionTreeGrowthSection
import com.example.ui.components.TreeGrowthStage
import com.example.ui.navigation.ScreenDestination
import com.example.ui.theme.BlackBase
import com.example.ui.theme.BlackSurface
import com.example.ui.theme.BlackSurfaceElevated
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonGreenBright
import com.example.ui.theme.NeonOrange
import com.example.ui.theme.NeonRed
import com.example.ui.theme.NeonYellow
import com.example.ui.theme.OrangeYellowGradient
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.delay

@Composable
fun StudyScreen(
    onNavigateTo: (ScreenDestination) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val studyRepo = remember { com.example.data.StudySessionRepository.getInstance(context) }
    var isRunning by rememberSaveable { mutableStateOf(false) }
    var secondsRemaining by rememberSaveable { mutableIntStateOf(30 * 60) }
    var totalSeconds by rememberSaveable { mutableIntStateOf(30 * 60) }
    var selectedModeIndex by rememberSaveable { mutableIntStateOf(0) } // 0=Timer, 1=Subjects, 2=Controls
    var quickFocusMode by rememberSaveable { mutableStateOf("Pomodoro") }
    var customMinutes by rememberSaveable { mutableIntStateOf(30) }
    var showCustomDurationDialog by rememberSaveable { mutableStateOf(false) }
    var showAddSubjectDialog by rememberSaveable { mutableStateOf(false) }
    var newSubjectInput by rememberSaveable { mutableStateOf("") }
    var selectedSubject by rememberSaveable { mutableStateOf("Mathematics") }

    // Tree growth state connected to CURRENT study session (starts at Stage 1 - Seed)
    var sessionSecondsStudied by rememberSaveable { mutableIntStateOf(0) }

    // Automatically synchronize with any planned study session started from Study Planner
    val pendingPlannedSubject = studyRepo.pendingPlannedSubject.value
    LaunchedEffect(pendingPlannedSubject) {
        val planned = studyRepo.consumePendingPlannedSession()
        if (planned != null) {
            selectedSubject = planned.first
            val mins = planned.second.coerceIn(1, 180)
            customMinutes = mins
            quickFocusMode = "Custom"
            totalSeconds = mins * 60
            secondsRemaining = mins * 60
            sessionSecondsStudied = 0
            selectedModeIndex = 0
            isRunning = false
        }
    }

    // Live timer countdown when running
    LaunchedEffect(isRunning) {
        while (isRunning && secondsRemaining > 0) {
            delay(1000L)
            secondsRemaining--
            sessionSecondsStudied++
        }
        if (secondsRemaining == 0 && isRunning) {
            isRunning = false
            val completedMins = (totalSeconds / 60).coerceAtLeast(1)
            studyRepo.recordCompletedSession(subject = selectedSubject, durationMinutes = completedMins)
            sessionSecondsStudied = 0
            onNavigateTo(ScreenDestination.COMPLETION)
        }
    }

    androidx.activity.compose.BackHandler {
        onNavigateTo(ScreenDestination.DASHBOARD)
    }

    // A NEW study session starts visual growth from Stage 1 (Seed, 0 XP)
    // and progressively grows through the 9 stages as the session timer runs!
    val sessionFraction = if (totalSeconds > 0) {
        (sessionSecondsStudied.toFloat() / totalSeconds.toFloat()).coerceIn(0f, 1f)
    } else 0f
    val currentXp = (sessionFraction * 2150f).toInt()
    val currentStage = TreeGrowthStage.fromXp(currentXp)

    val progress = if (totalSeconds > 0) secondsRemaining.toFloat() / totalSeconds.toFloat() else 0f
    val minutes = secondsRemaining / 60
    val seconds = secondsRemaining % 60
    val formattedTime = String.format("%02d:%02d", minutes, seconds)

    val dialGlow = if (isRunning) 1.0f else 0.8f
    val sweepGradientBrush = remember {
        Brush.sweepGradient(
            listOf(
                Color(0xFFFF2A4B),
                Color(0xFFFF9100),
                Color(0xFFFFD600),
                Color(0xFF00E676),
                Color(0xFF00E5FF),
                Color(0xFFFF2A4B)
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
                .padding(horizontal = 18.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Spacer(modifier = Modifier.height(2.dp))

            // 1. Top Bar matching Reference: [ ← Back ] [ ⏱ Study Session ] [ ⚙ Settings ]
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { onNavigateTo(ScreenDestination.DASHBOARD) },
                    modifier = Modifier.testTag("btn_study_back")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                        contentDescription = "Back to Home",
                        tint = Color(0xFFCBD5E1),
                        modifier = Modifier.size(22.dp)
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Timer,
                        contentDescription = "Study Session Timer",
                        tint = Color(0xFF00E676),
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Study Session",
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                IconButton(
                    onClick = { onNavigateTo(ScreenDestination.FOCUS_GUARD) },
                    modifier = Modifier.testTag("btn_study_settings")
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Settings,
                        contentDescription = "Settings",
                        tint = Color(0xFFCBD5E1),
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            // 2. Segmented Pill Tabs matching Reference: [ Timer ] [ Subjects ] [ Controls ]
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(26.dp))
                    .background(Color(0xFF0C1017))
                    .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(26.dp))
                    .padding(4.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    listOf("Timer", "Subjects", "Controls").forEachIndexed { index, title ->
                        val isSelected = selectedModeIndex == index
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(38.dp)
                                .clip(RoundedCornerShape(22.dp))
                                .background(
                                    if (isSelected) {
                                        Brush.horizontalGradient(
                                            listOf(Color(0xFF00E676), Color(0xFF00C853))
                                        )
                                    } else {
                                        Brush.linearGradient(listOf(Color.Transparent, Color.Transparent))
                                    }
                                )
                                .clickable { selectedModeIndex = index }
                                .testTag("tab_study_${title.lowercase()}"),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = title,
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color(0xFF01240F) else Color(0xFFCBD5E1)
                            )
                        }
                    }
                }
            }

            // Mode Content: Timer / Subjects / Controls
            when (selectedModeIndex) {
                0 -> {
                    // MAIN TIMER VIEW (Matching Reference Screen 4)
                    Spacer(modifier = Modifier.height(4.dp))

                    // Active Study Subject indicator (Synchronized with Study Timer)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color(0xFF0F172A))
                            .border(1.2.dp, Color(0xFF00E5FF).copy(alpha = 0.55f), RoundedCornerShape(20.dp))
                            .clickable { selectedModeIndex = 1 }
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                            .testTag("tag_active_study_subject")
                    ) {
                        Text(text = "📚", fontSize = 14.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Studying: $selectedSubject",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF67E8F9)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "• Switch",
                            fontSize = 11.sp,
                            color = Color(0xFF94A3B8)
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // 3. Circular Study Timer Dial with Multi-Color RGB Glow Arc
                    Box(
                        modifier = Modifier.size(244.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val strokeW = 11.dp.toPx()
                            val center = Offset(size.width / 2, size.height / 2)
                            val radius = (size.minDimension / 2) - (strokeW / 2) - 4.dp.toPx()

                            // Subtle dark background track
                            drawCircle(
                                color = Color.White.copy(alpha = 0.05f),
                                radius = radius,
                                center = center,
                                style = Stroke(width = strokeW)
                            )

                            // Soft outer neon glow halo
                            drawCircle(
                                color = Color(0xFF00E676).copy(alpha = 0.08f * dialGlow),
                                radius = radius + 16.dp.toPx(),
                                center = center
                            )

                            // Radiant RGB Sweep Gradient Arc (Neon Red -> Orange/Yellow -> Green -> Cyan)
                            val sweepAngle = 360f * progress
                            drawArc(
                                brush = sweepGradientBrush,
                                startAngle = -90f,
                                sweepAngle = sweepAngle,
                                useCenter = false,
                                topLeft = Offset(center.x - radius, center.y - radius),
                                size = androidx.compose.ui.geometry.Size(radius * 2, radius * 2),
                                style = Stroke(width = strokeW, cap = StrokeCap.Round)
                            )

                            // Outer thin neon border ring
                            drawCircle(
                                color = Color(0xFF00E676).copy(alpha = 0.25f),
                                radius = radius + 10.dp.toPx(),
                                center = center,
                                style = Stroke(width = 1.2.dp.toPx())
                            )
                        }

                        // Inner Center Content: Tree Stage Badge + Big Clock + Focus Time
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(currentStage.primaryColor.copy(alpha = 0.16f))
                                    .border(1.dp, currentStage.primaryColor.copy(alpha = 0.45f), RoundedCornerShape(12.dp))
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Spa,
                                    contentDescription = "Tree Stage",
                                    tint = currentStage.primaryColor,
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "${currentStage.stageName} • Stg ${currentStage.stageNumber}",
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    color = currentStage.auraColor
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = formattedTime,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 40.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "Focus Time",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFFCBD5E1)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // 4. Primary Duo Control Buttons: [ Pause / Start / Resume ] and [ Reset ]
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        val isPaused = !isRunning && secondsRemaining < totalSeconds && secondsRemaining > 0
                        val toggleButtonText = when {
                            isRunning -> "Pause"
                            isPaused -> "Resume"
                            else -> "Start"
                        }

                        // Left: Golden-Amber Glowing Pill Capsule
                        Box(
                            modifier = Modifier
                                .weight(1.2f)
                                .height(52.dp)
                                .clip(RoundedCornerShape(26.dp))
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(Color(0xFFFFB300), Color(0xFFFF8F00))
                                    )
                                )
                                .border(1.dp, Color(0xFFFFE082), RoundedCornerShape(26.dp))
                                .clickable {
                                    if (secondsRemaining <= 0) {
                                        secondsRemaining = totalSeconds
                                    }
                                    isRunning = !isRunning
                                }
                                .testTag("btn_timer_toggle"),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(CircleShape)
                                        .border(1.2.dp, Color(0xFF4A2B00), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = if (isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                                        contentDescription = toggleButtonText,
                                        tint = Color(0xFF221100),
                                        modifier = Modifier.size(15.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = toggleButtonText,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1E1000)
                                )
                            }
                        }

                        // Right: Crimson Glass Capsule with Sharp Neon Red Border
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(52.dp)
                                .clip(RoundedCornerShape(26.dp))
                                .border(1.5.dp, Color(0xFFFF2A4B), RoundedCornerShape(26.dp))
                                .background(Color(0xFF20060B))
                                .clickable {
                                    isRunning = false
                                    secondsRemaining = totalSeconds
                                    sessionSecondsStudied = 0 // Completely reset current session tree back to Stage 1 Seed
                                }
                                .testTag("btn_timer_reset"),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFFF2A4B).copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Refresh,
                                        contentDescription = "Reset Timer",
                                        tint = Color(0xFFFF2A4B),
                                        modifier = Modifier.size(15.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Reset",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // 9-Stage Living Tree Growth System (Seed → Seedling → Baby Plant → Plant → Big Plant → Tree → Giant Tree → Old Tree → Legendary R.E.D Tree)
                    StudySessionTreeGrowthSection(
                        currentXp = currentXp,
                        sessionSecondsStudied = sessionSecondsStudied,
                        isTimerRunning = isRunning,
                        totalSessionSeconds = totalSeconds
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // 5. Motivational Quote matching Reference
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "“One focused session\nbrings you closer to your dreams. 🍃”",
                            color = Color(0xFF7DF9FF),
                            fontSize = 13.sp,
                            lineHeight = 18.sp,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // 6. Quick Focus Section matching Reference: [ Pomodoro ] [ Deep Focus ] [ Custom ]
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Quick Focus",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // Pomodoro Card
                            val isPomodoro = quickFocusMode == "Pomodoro"
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(16.dp))
                                    .border(
                                        1.2.dp,
                                        if (isPomodoro) Color(0xFFFF3D00) else Color.White.copy(alpha = 0.08f),
                                        RoundedCornerShape(16.dp)
                                    )
                                    .background(Color(0xE60A0E15))
                                    .clickable {
                                        quickFocusMode = "Pomodoro"
                                        totalSeconds = 30 * 60
                                        secondsRemaining = totalSeconds
                                        sessionSecondsStudied = 0 // New session starts at Stage 1
                                        isRunning = false
                                    }
                                    .padding(vertical = 12.dp)
                                    .testTag("chip_focus_pomodoro"),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFF2B0A0E))
                                            .border(1.dp, Color(0xFFFF3D00), CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Outlined.Timer,
                                            contentDescription = "Pomodoro",
                                            tint = Color(0xFFFF3D00),
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = "Pomodoro",
                                        fontSize = 11.5.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color(0xFF67E8F9)
                                    )
                                }
                            }

                            // Deep Focus Card
                            val isDeep = quickFocusMode == "Deep Focus"
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(16.dp))
                                    .border(
                                        1.2.dp,
                                        if (isDeep) Color(0xFF00E5FF) else Color.White.copy(alpha = 0.08f),
                                        RoundedCornerShape(16.dp)
                                    )
                                    .background(Color(0xE60A0E15))
                                    .clickable {
                                        quickFocusMode = "Deep Focus"
                                        totalSeconds = 50 * 60
                                        secondsRemaining = totalSeconds
                                        sessionSecondsStudied = 0 // New session starts at Stage 1
                                        isRunning = false
                                    }
                                    .padding(vertical = 12.dp)
                                    .testTag("chip_focus_deep_focus"),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFF041E2B))
                                            .border(1.dp, Color(0xFF00E5FF), CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Outlined.Spa,
                                            contentDescription = "Deep Focus",
                                            tint = Color(0xFF00E5FF),
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = "Deep Focus",
                                        fontSize = 11.5.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color(0xFFCBD5E1)
                                    )
                                }
                            }

                            // Custom Timer Card (Retains working custom selector modal!)
                            val isCustom = quickFocusMode == "Custom"
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(16.dp))
                                    .border(
                                        1.2.dp,
                                        if (isCustom) Color(0xFF00E676) else Color.White.copy(alpha = 0.08f),
                                        RoundedCornerShape(16.dp)
                                    )
                                    .background(Color(0xE60A0E15))
                                    .clickable {
                                        showCustomDurationDialog = true
                                    }
                                    .padding(vertical = 12.dp)
                                    .testTag("chip_focus_custom"),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFF062414))
                                            .border(1.dp, Color(0xFF00E676), CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Outlined.Tune,
                                            contentDescription = "Custom",
                                            tint = Color(0xFF00E676),
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = if (isCustom) "Custom (${customMinutes}m)" else "Custom",
                                        fontSize = 11.5.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color(0xFFCBD5E1)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Mark Session Complete Shortcut (Synchronizes subject and completed session)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .border(1.dp, Color(0xFF00E676).copy(alpha = 0.35f), RoundedCornerShape(14.dp))
                            .background(BlackSurface.copy(alpha = 0.4f))
                            .clickable {
                                val completedMins = (totalSeconds / 60).coerceAtLeast(1)
                                studyRepo.recordCompletedSession(subject = selectedSubject, durationMinutes = completedMins)
                                sessionSecondsStudied = 0
                                onNavigateTo(ScreenDestination.COMPLETION)
                            }
                            .padding(12.dp)
                            .testTag("btn_mark_session_complete"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Outlined.CheckCircle,
                                contentDescription = "Complete",
                                tint = Color(0xFF00E676),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Mark Session Complete & Check In ↗",
                                fontSize = 12.sp,
                                color = Color(0xFF00E676),
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                1 -> {
                    // SUBJECTS VIEW
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Choose Study Subject",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )

                        val allSubjects = studyRepo.subjectsState.value
                        val subjectColors = listOf(
                            Color(0xFF00E5FF),
                            Color(0xFFFFD600),
                            Color(0xFF00E676),
                            Color(0xFF69F0AE),
                            Color(0xFFFF4081),
                            Color(0xFFFF9100),
                            Color(0xFF7C4DFF),
                            Color(0xFF00B0FF)
                        )

                        allSubjects.chunked(2).forEachIndexed { rowIndex, rowItems ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                rowItems.forEachIndexed { colIndex, sub ->
                                    val isSubSelected = selectedSubject.equals(sub, ignoreCase = true)
                                    val color = subjectColors[(rowIndex * 2 + colIndex) % subjectColors.size]
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(14.dp))
                                            .border(
                                                1.2.dp,
                                                if (isSubSelected) color else Color.White.copy(alpha = 0.1f),
                                                RoundedCornerShape(14.dp)
                                            )
                                            .background(
                                                if (isSubSelected) color.copy(alpha = 0.18f) else Color(0xE60A0E15)
                                            )
                                            .clickable {
                                                selectedSubject = sub
                                                selectedModeIndex = 0 // Return to timer with subject synchronized
                                            }
                                            .padding(14.dp)
                                            .testTag("subject_card_${sub.lowercase().replace(" ", "_")}")
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = Icons.Outlined.MenuBook,
                                                contentDescription = sub,
                                                tint = color,
                                                modifier = Modifier.size(20.dp)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = sub,
                                                fontSize = 12.sp,
                                                fontWeight = if (isSubSelected) FontWeight.Bold else FontWeight.Normal,
                                                color = Color.White,
                                                maxLines = 1
                                            )
                                        }
                                    }
                                }
                                if (rowItems.size == 1) {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        // + Add More Subject Button (Item 3 requirement)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .border(
                                    1.2.dp,
                                    Brush.horizontalGradient(
                                        listOf(Color(0xFF00E676), Color(0xFF00E5FF))
                                    ),
                                    RoundedCornerShape(14.dp)
                                )
                                .background(Color(0xFF0C1920))
                                .clickable {
                                    newSubjectInput = ""
                                    showAddSubjectDialog = true
                                }
                                .padding(horizontal = 16.dp)
                                .testTag("btn_add_more_subject"),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Add More Subject",
                                    tint = Color(0xFF00E676),
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "+ Add More Subject",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF00E676)
                                )
                            }
                        }
                    }
                }

                2 -> {
                    // CONTROLS VIEW
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Focus Controls & Ambient Audio",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )

                        val controlsList = listOf(
                            Triple("Ambient Rain Audio", Icons.Outlined.VolumeUp, "Enabled (40%)"),
                            Triple("Focus Guard Strict Mode", Icons.Outlined.Settings, "Active (Apps Blocked)"),
                            Triple("Vibration Alert on Finish", Icons.Outlined.NotificationsActive, "Enabled"),
                            Triple("Auto-Start Break Sessions", Icons.Outlined.Timer, "Disabled")
                        )

                        controlsList.forEach { (title, icon, status) ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(14.dp))
                                    .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(14.dp))
                                    .background(Color(0xE60A0E15))
                                    .padding(14.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = icon,
                                            contentDescription = title,
                                            tint = Color(0xFF00E676),
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Text(
                                            text = title,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = Color.White
                                        )
                                    }
                                    Text(
                                        text = status,
                                        fontSize = 11.sp,
                                        color = Color(0xFF00E5FF)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // Custom Duration Selector Dialog
        if (showCustomDurationDialog) {
            CustomDurationDialog(
                initialMinutes = customMinutes,
                onDismiss = { showCustomDurationDialog = false },
                onConfirm = { selectedMinutes: Int ->
                    customMinutes = selectedMinutes
                    quickFocusMode = "Custom"
                    totalSeconds = selectedMinutes * 60
                    secondsRemaining = selectedMinutes * 60
                    sessionSecondsStudied = 0
                    isRunning = false
                    showCustomDurationDialog = false
                }
            )
        }

        // Add Subject Dialog (Item 3 requirement)
        if (showAddSubjectDialog) {
            Dialog(onDismissRequest = { showAddSubjectDialog = false }) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .border(
                            1.5.dp,
                            Brush.horizontalGradient(
                                listOf(Color(0xFF00E676), Color(0xFF00E5FF), Color(0xFFFFD600))
                            ),
                            RoundedCornerShape(20.dp)
                        )
                        .background(Color(0xFF0C1017))
                        .padding(20.dp)
                        .testTag("dialog_add_subject")
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        Text(
                            text = "Add New Subject",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Enter subject name to synchronize with study timer and tracking statistics.",
                            fontSize = 12.sp,
                            color = Color(0xFF94A3B8)
                        )
                        OutlinedTextField(
                            value = newSubjectInput,
                            onValueChange = { newSubjectInput = it },
                            placeholder = { Text("e.g. History, Economics...", color = Color(0xFF64748B)) },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_add_subject"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF00E676),
                                unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            )
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Box(
                                modifier = Modifier
                                    .clickable { showAddSubjectDialog = false }
                                    .padding(horizontal = 14.dp, vertical = 10.dp)
                                    .testTag("btn_cancel_add_subject")
                            ) {
                                Text(text = "Cancel", color = Color(0xFF94A3B8), fontSize = 14.sp)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFF00E676))
                                    .clickable {
                                        val trimmed = newSubjectInput.trim()
                                        if (trimmed.isNotBlank()) {
                                            studyRepo.addSubject(trimmed)
                                            selectedSubject = trimmed
                                            showAddSubjectDialog = false
                                            selectedModeIndex = 0 // return to timer with new subject active!
                                        }
                                    }
                                    .padding(horizontal = 18.dp, vertical = 10.dp)
                                    .testTag("btn_confirm_add_subject")
                            ) {
                                Text(
                                    text = "Add Subject",
                                    color = Color(0xFF031A0B),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // Focus Mode ON: Subtle non-intrusive achievement banner
        val subtleQueue by AchievementManager.subtleBannerQueue
        val currentSubtle = subtleQueue.firstOrNull()
        if (currentSubtle != null) {
            SubtleFocusAchievementBanner(
                achievement = currentSubtle,
                onDismiss = { AchievementManager.dismissSubtleBanner() },
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }

        // Focus Mode OFF: Full achievement celebration overlay
        val fullCelebrationQueue by AchievementManager.activeFullCelebrationQueue
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

/**
 * Clean, dark glassmorphism Duration Selector Dialog for Custom Timer
 */
@Composable
private fun CustomDurationDialog(
    initialMinutes: Int,
    onDismiss: () -> Unit,
    onConfirm: (Int) -> Unit
) {
    val safeInitial = initialMinutes.coerceAtLeast(30)
    var minutesValue by remember { mutableIntStateOf(safeInitial) }
    var textInput by remember { mutableStateOf(safeInitial.toString()) }
    val focusManager = LocalFocusManager.current

    val presetDurations = listOf(30, 45, 60, 90, 120, 180)

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .border(1.5.dp, CyberCyan.copy(alpha = 0.5f), RoundedCornerShape(24.dp))
                .background(BlackSurfaceElevated)
                .padding(22.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(CyberCyan.copy(alpha = 0.15f))
                                .border(1.dp, CyberCyan.copy(alpha = 0.4f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Tune,
                                contentDescription = "Custom Duration",
                                tint = CyberCyan,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Custom Timer",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "Minimum 30 minutes",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }
                    }

                    IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                        Icon(
                            imageVector = Icons.Outlined.Close,
                            contentDescription = "Close",
                            tint = TextMuted,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                // Interactive Counter with Minus / Display / Plus
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Minus 5m Button (Minimum 30m)
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .border(1.dp, Color.White.copy(alpha = 0.15f), CircleShape)
                            .background(BlackSurface)
                            .clickable {
                                val next = (minutesValue - 5).coerceAtLeast(30)
                                minutesValue = next
                                textInput = next.toString()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Remove,
                            contentDescription = "Decrease 5 mins",
                            tint = TextPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    // Number Input Field
                    Box(
                        modifier = Modifier
                            .width(110.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .border(1.5.dp, CyberCyan, RoundedCornerShape(16.dp))
                            .background(BlackBase)
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            OutlinedTextField(
                                value = textInput,
                                onValueChange = { newText ->
                                    val filtered = newText.filter { it.isDigit() }.take(3)
                                    textInput = filtered
                                    filtered.toIntOrNull()?.let {
                                        if (it in 30..720) {
                                            minutesValue = it
                                        }
                                    }
                                },
                                textStyle = TextStyle(
                                    color = TextPrimary,
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace,
                                    textAlign = TextAlign.Center
                                ),
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Number,
                                    imeAction = ImeAction.Done
                                ),
                                keyboardActions = KeyboardActions(
                                    onDone = { focusManager.clearFocus() }
                                ),
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color.Transparent,
                                    unfocusedBorderColor = Color.Transparent,
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent
                                ),
                                modifier = Modifier.fillMaxWidth().testTag("input_custom_minutes")
                            )
                            Text(
                                text = "MINUTES",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = CyberCyan,
                                letterSpacing = 1.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    // Plus 5m Button
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .border(1.dp, Color.White.copy(alpha = 0.15f), CircleShape)
                            .background(BlackSurface)
                            .clickable {
                                val next = (minutesValue + 5).coerceAtMost(720)
                                minutesValue = next
                                textInput = next.toString()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Increase 5 mins",
                            tint = TextPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                // Preset Pills: [ 30m | 45m | 60m | 90m | 120m | 180m ]
                Text(
                    text = "Quick Presets",
                    fontSize = 11.sp,
                    color = TextMuted,
                    fontFamily = FontFamily.Monospace
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    presetDurations.forEach { duration ->
                        val isSelected = minutesValue == duration
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .border(
                                    1.dp,
                                    if (isSelected) CyberCyan else Color.White.copy(alpha = 0.1f),
                                    RoundedCornerShape(12.dp)
                                )
                                .background(
                                    if (isSelected) CyberCyan.copy(alpha = 0.2f) else BlackSurface
                                )
                                .clickable {
                                    minutesValue = duration
                                    textInput = duration.toString()
                                }
                                .padding(horizontal = 8.dp, vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${duration}m",
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) CyberCyan else TextSecondary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Actions: Cancel & Confirm
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Cancel
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .clip(RoundedCornerShape(22.dp))
                            .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(22.dp))
                            .background(BlackSurface)
                            .clickable(onClick = onDismiss),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Cancel",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextSecondary
                        )
                    }

                    // Confirm
                    Box(
                        modifier = Modifier
                            .weight(1.4f)
                            .height(44.dp)
                            .clip(RoundedCornerShape(22.dp))
                            .background(
                                Brush.horizontalGradient(
                                    listOf(CyberCyan, NeonGreen)
                                )
                            )
                            .clickable {
                                val confirmedMins = (textInput.toIntOrNull() ?: minutesValue).coerceIn(30, 720)
                                onConfirm(confirmedMins)
                            }
                            .testTag("btn_confirm_custom_timer"),
                        contentAlignment = Alignment.Center
                    ) {
                        val displayConfirmMins = (textInput.toIntOrNull() ?: minutesValue).coerceIn(30, 720)
                        Text(
                            text = "Set Timer (${displayConfirmMins}m)",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }
                }
            }
        }
    }
}
