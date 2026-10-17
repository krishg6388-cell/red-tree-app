package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CompanionManager
import com.example.ui.components.FloatingCompanionWidget
import com.example.ui.components.GlassCard
import com.example.ui.navigation.ScreenDestination
import com.example.ui.screens.AchievementsScreen
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.AuthSessionManager
import com.example.ui.screens.CompanionSettingsScreen
import com.example.ui.screens.CompletionScreen
import com.example.ui.screens.DailyTasksScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.FocusGuardScreen
import com.example.ui.screens.LeaderboardScreen
import com.example.ui.screens.PlannerScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.screens.StatisticsScreen
import com.example.ui.screens.StudyScreen
import com.example.ui.screens.TreeGrowthScreen
import com.example.ui.theme.BlackBase
import com.example.ui.theme.BlackSurface
import com.example.ui.theme.BlackSurfaceElevated
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.NeonOrange
import com.example.ui.theme.NeonYellow
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

private val EXTRA_SHEET_SCREENS = listOf(
    ScreenDestination.LEADERBOARD,
    ScreenDestination.COMPANION,
    ScreenDestination.PROFILE,
    ScreenDestination.DAILY_TASKS,
    ScreenDestination.PLANNER,
    ScreenDestination.FOCUS_GUARD,
    ScreenDestination.SPLASH,
    ScreenDestination.TREE_GROWTH,
    ScreenDestination.AUTH
)

private val MAIN_DOCK_SCREENS = setOf(
    ScreenDestination.DASHBOARD,
    ScreenDestination.STUDY,
    ScreenDestination.STATISTICS,
    ScreenDestination.ACHIEVEMENTS
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                RedTreeAppShell()
            }
        }
    }
}

@Composable
fun RedTreeAppShell() {
    val context = LocalContext.current
    var currentScreen by rememberSaveable { mutableStateOf(ScreenDestination.SPLASH) }
    var showMoreSheet by rememberSaveable { mutableStateOf(false) }
    var isAuthenticated by rememberSaveable {
        mutableStateOf(AuthSessionManager.isAuthenticated(context))
    }
    var splashReplayKey by rememberSaveable { mutableIntStateOf(0) }

    fun navigateWithAuthGuard(destination: ScreenDestination) {
        showMoreSheet = false
        val hasValidSession = isAuthenticated || AuthSessionManager.isAuthenticated(context)
        when (destination) {
            ScreenDestination.SPLASH -> {
                // "Restart App" -> Replay complete Beginning Animation -> Hold for manual "Enter R.E.D Tree" -> Sign In
                AuthSessionManager.clearSession(context)
                isAuthenticated = false
                splashReplayKey++
                currentScreen = ScreenDestination.SPLASH
            }
            ScreenDestination.AUTH -> {
                AuthSessionManager.clearSession(context)
                isAuthenticated = false
                currentScreen = ScreenDestination.AUTH
            }
            else -> {
                if (hasValidSession) {
                    isAuthenticated = true
                    currentScreen = destination
                } else {
                    currentScreen = ScreenDestination.AUTH
                }
            }
        }
    }

    // Handle back button smoothly while strictly enforcing authentication
    BackHandler(enabled = showMoreSheet || currentScreen != ScreenDestination.SPLASH) {
        when {
            showMoreSheet -> {
                showMoreSheet = false
            }
            currentScreen == ScreenDestination.AUTH -> {
                currentScreen = ScreenDestination.SPLASH
            }
            !isAuthenticated && !AuthSessionManager.isAuthenticated(context) -> {
                currentScreen = ScreenDestination.AUTH
            }
            currentScreen != ScreenDestination.DASHBOARD -> {
                currentScreen = ScreenDestination.DASHBOARD
            }
        }
    }

    val activeScreen = if (!isAuthenticated && !AuthSessionManager.isAuthenticated(context) && currentScreen != ScreenDestination.SPLASH && currentScreen != ScreenDestination.AUTH) {
        ScreenDestination.AUTH
    } else {
        currentScreen
    }

    val isFullScreenFlow = activeScreen == ScreenDestination.SPLASH || activeScreen == ScreenDestination.AUTH

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = BlackBase,
        contentWindowInsets = WindowInsets.systemBars,
        bottomBar = {
            if (!isFullScreenFlow) {
                RedTreeBottomDock(
                    currentScreen = activeScreen,
                    isMoreSheetOpen = showMoreSheet,
                    onSelectScreen = { destination ->
                        navigateWithAuthGuard(destination)
                    },
                    onOpenMore = {
                        showMoreSheet = !showMoreSheet
                    }
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = innerPadding.calculateTopPadding(),
                    bottom = innerPadding.calculateBottomPadding()
                )
                .background(BlackBase)
        ) {
            // Stable Top Quick Bar: Rendered in layout flow so Scaffold insets never shift or remeasure
            if (!isFullScreenFlow && activeScreen != ScreenDestination.DASHBOARD && activeScreen != ScreenDestination.STUDY) {
                RedTreeTopQuickBar(
                    currentScreen = activeScreen,
                    onSelectScreen = { destination ->
                        navigateWithAuthGuard(destination)
                    }
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                when (activeScreen) {
                    ScreenDestination.SPLASH -> {
                        key(splashReplayKey) {
                            SplashScreen(
                                onEnterApp = {
                                    showMoreSheet = false
                                    currentScreen = ScreenDestination.AUTH
                                }
                            )
                        }
                    }
                    ScreenDestination.AUTH -> AuthScreen(
                        onContinueToDashboard = {
                            showMoreSheet = false
                            isAuthenticated = true
                            currentScreen = ScreenDestination.DASHBOARD
                        }
                    )
                    ScreenDestination.DASHBOARD -> DashboardScreen(
                        onNavigateTo = { destination ->
                            navigateWithAuthGuard(destination)
                        },
                        isPaused = showMoreSheet
                    )
                    ScreenDestination.STUDY -> StudyScreen(
                        onNavigateTo = { destination ->
                            navigateWithAuthGuard(destination)
                        }
                    )
                    ScreenDestination.STATISTICS -> StatisticsScreen()
                    ScreenDestination.ACHIEVEMENTS -> AchievementsScreen()
                    ScreenDestination.DAILY_TASKS -> DailyTasksScreen(
                        onBack = {
                            navigateWithAuthGuard(ScreenDestination.DASHBOARD)
                        },
                        onNavigateToStudy = {
                            navigateWithAuthGuard(ScreenDestination.STUDY)
                        }
                    )
                    ScreenDestination.PLANNER -> PlannerScreen(
                        onBack = {
                            navigateWithAuthGuard(ScreenDestination.DASHBOARD)
                        },
                        onStartPlannedSession = { subject, targetMinutes ->
                            com.example.data.StudySessionRepository.getInstance(context)
                                .startPlannedSession(subject, targetMinutes)
                            navigateWithAuthGuard(ScreenDestination.STUDY)
                        }
                    )
                    ScreenDestination.LEADERBOARD -> LeaderboardScreen(
                        onBack = {
                            navigateWithAuthGuard(ScreenDestination.DASHBOARD)
                        }
                    )
                    ScreenDestination.PROFILE -> ProfileScreen(
                        onNavigateTo = { destination ->
                            navigateWithAuthGuard(destination)
                        },
                        onBack = {
                            navigateWithAuthGuard(ScreenDestination.DASHBOARD)
                        }
                    )
                    ScreenDestination.FOCUS_GUARD -> FocusGuardScreen(
                        onBack = {
                            navigateWithAuthGuard(ScreenDestination.DASHBOARD)
                        }
                    )
                    ScreenDestination.TREE_GROWTH -> TreeGrowthScreen(
                        onBack = {
                            navigateWithAuthGuard(ScreenDestination.DASHBOARD)
                        },
                        onNavigateToStudy = {
                            navigateWithAuthGuard(ScreenDestination.STUDY)
                        }
                    )
                    ScreenDestination.COMPLETION -> CompletionScreen(
                        onContinue = {
                            navigateWithAuthGuard(ScreenDestination.DASHBOARD)
                        }
                    )
                    ScreenDestination.COMPANION -> CompanionSettingsScreen(
                        onBack = {
                            navigateWithAuthGuard(ScreenDestination.DASHBOARD)
                        }
                    )
                }

                // Non-intrusive Leaderboard Rank Up Notification (Item 7 requirement)
                val studyRepo = remember { com.example.data.StudySessionRepository.getInstance(context) }
                val rankUpEvent = studyRepo.rankUpEvent.value
                val isFocusModeOn = com.example.ui.screens.AchievementManager.isFocusModeEnabled(context)
                if (rankUpEvent != null) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(top = 10.dp, start = 16.dp, end = 16.dp)
                            .testTag("notification_rank_up")
                    ) {
                        if (isFocusModeOn) {
                            // Focus Mode ON: Subtle quiet pill
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(Color(0xFF0F172A).copy(alpha = 0.92f))
                                    .border(1.dp, Color(0xFFFFD600).copy(alpha = 0.45f), RoundedCornerShape(20.dp))
                                    .clickable {
                                        studyRepo.dismissRankUpNotification()
                                        navigateWithAuthGuard(ScreenDestination.LEADERBOARD)
                                    }
                                    .padding(horizontal = 14.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = "🏆 Rank Up: #${rankUpEvent.newRank}",
                                    fontSize = 11.5.sp,
                                    color = Color(0xFFFFD600),
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        } else {
                            // Focus Mode OFF: Small standard banner
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(16.dp))
                                    .border(
                                        1.2.dp,
                                        Brush.horizontalGradient(
                                            listOf(Color(0xFFFFD600), Color(0xFF00E676))
                                        ),
                                        RoundedCornerShape(16.dp)
                                    )
                                    .background(Color(0xF20B1320))
                                    .clickable {
                                        studyRepo.dismissRankUpNotification()
                                        navigateWithAuthGuard(ScreenDestination.LEADERBOARD)
                                    }
                                    .padding(horizontal = 14.dp, vertical = 10.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text(text = "🏆", fontSize = 20.sp)
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text(
                                                text = "Leaderboard Rank Up!",
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFFFFD600)
                                            )
                                            Text(
                                                text = "You moved from #${rankUpEvent.previousRank} to #${rankUpEvent.newRank}. Tap to view.",
                                                fontSize = 11.5.sp,
                                                color = Color(0xFFCBD5E1)
                                            )
                                        }
                                    }
                                    IconButton(
                                        onClick = { studyRepo.dismissRankUpNotification() },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Outlined.Close,
                                            contentDescription = "Dismiss",
                                            tint = TextMuted,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Floating Living Companion (Forest Cutu / Forest Cutie)
                val companionManager = remember { com.example.data.CompanionManager.getInstance(context) }
                val companionConfig by companionManager.configState
                val isCompanionVisible = !isFullScreenFlow && !showMoreSheet && activeScreen != ScreenDestination.COMPLETION && activeScreen != ScreenDestination.COMPANION && companionConfig.isEnabled

                if (isCompanionVisible) {
                    FloatingCompanionWidget(
                        config = companionConfig,
                        isFocusModeActive = isFocusModeOn,
                        isStudyTimerRunning = activeScreen == ScreenDestination.STUDY,
                        onOpenSettings = {
                            navigateWithAuthGuard(ScreenDestination.COMPANION)
                        },
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(end = 12.dp, bottom = 8.dp)
                    )
                }

                // Full-Screen More Destinations Page (Item 6 requirement)
                if (showMoreSheet && !isFullScreenFlow) {
                    androidx.activity.compose.BackHandler {
                        showMoreSheet = false
                    }
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(BlackBase)
                            .padding(horizontal = 18.dp, vertical = 6.dp)
                            .testTag("more_sheet_overlay")
                    ) {
                        // Header
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 2.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "R.E.D TREE DESTINATIONS",
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NeonYellow,
                                    letterSpacing = 1.2.sp
                                )
                                Text(
                                    text = "Full-screen access to all study modules & tools",
                                    fontSize = 10.sp,
                                    color = TextSecondary
                                )
                            }

                            IconButton(
                                onClick = { showMoreSheet = false },
                                modifier = Modifier
                                    .size(30.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.08f))
                                    .testTag("btn_close_more_sheet")
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Close,
                                    contentDescription = "Close",
                                    tint = TextPrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        // Full-screen Options Column with professional thin RGB borders (RED + GREEN + GOLD)
                        val rgbBrush = remember {
                            Brush.linearGradient(
                                listOf(
                                    Color(0xFFFF2A4B).copy(alpha = 0.55f),
                                    Color(0xFF00E676).copy(alpha = 0.55f),
                                    Color(0xFFFFD600).copy(alpha = 0.55f)
                                )
                            )
                        }

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .verticalScroll(rememberScrollState())
                                .testTag("more_sheet_scroll"),
                            verticalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            EXTRA_SHEET_SCREENS.forEach { destination ->
                                val isCurrent = activeScreen == destination
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .border(
                                            1.dp,
                                            if (isCurrent) SolidColor(destination.accentColor) else rgbBrush,
                                            RoundedCornerShape(12.dp)
                                        )
                                        .background(BlackSurface.copy(alpha = 0.88f))
                                        .clickable {
                                            navigateWithAuthGuard(destination)
                                        }
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                        .testTag("more_item_${destination.name.lowercase()}")
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(28.dp)
                                                    .clip(RoundedCornerShape(8.dp))
                                                    .background(destination.accentColor.copy(alpha = 0.16f)),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(
                                                    imageVector = destination.selectedIcon,
                                                    contentDescription = destination.title,
                                                    tint = destination.accentColor,
                                                    modifier = Modifier.size(15.dp)
                                                )
                                            }
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Column {
                                                Text(
                                                    text = if (destination == ScreenDestination.LEADERBOARD) "🏆 Global Leaderboard" else destination.title,
                                                    fontSize = 13.sp,
                                                    fontWeight = FontWeight.SemiBold,
                                                    color = TextPrimary
                                                )
                                                Text(
                                                    text = destination.subtitle,
                                                    fontSize = 10.sp,
                                                    color = TextSecondary
                                                )
                                            }
                                        }

                                        Text(
                                            text = "→",
                                            fontSize = 14.sp,
                                            color = destination.accentColor.copy(alpha = 0.8f),
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                        }
                    }
                }
            }
        }
    }
}

/**
 * Top Quick-Jump Horizontal Pills Bar: Direct 1-tap jump to any screen or Restart App!
 */
@Composable
fun RedTreeTopQuickBar(
    currentScreen: ScreenDestination,
    onSelectScreen: (ScreenDestination) -> Unit
) {
    val allDestinations = remember { ScreenDestination.values() }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(BlackBase)
            .padding(top = 8.dp, bottom = 6.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            allDestinations.forEach { destination ->
                val isSelected = currentScreen == destination
                val accentColor = destination.accentColor

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(18.dp))
                        .border(
                            width = if (isSelected) 1.2.dp else 1.dp,
                            color = if (isSelected) accentColor else Color.White.copy(alpha = 0.08f),
                            shape = RoundedCornerShape(18.dp)
                        )
                        .background(
                            if (isSelected) accentColor.copy(alpha = 0.18f) else BlackSurface.copy(alpha = 0.5f)
                        )
                        .clickable { onSelectScreen(destination) }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                        .testTag("pill_${destination.name.lowercase()}"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (isSelected) destination.selectedIcon else destination.unselectedIcon,
                            contentDescription = destination.title,
                            tint = if (isSelected) accentColor else TextSecondary,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = destination.title,
                            fontFamily = FontFamily.SansSerif,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) TextPrimary else TextSecondary
                        )
                    }
                }
            }
        }
    }
}

/**
 * Bottom Dock Navigation Bar: Full-cell 1-tap touch targets for Home, Study, Stats, Leaderboard, and More.
 */
@Composable
fun RedTreeBottomDock(
    currentScreen: ScreenDestination,
    isMoreSheetOpen: Boolean = false,
    onSelectScreen: (ScreenDestination) -> Unit,
    onOpenMore: () -> Unit
) {
    val isMoreActive = isMoreSheetOpen || currentScreen !in MAIN_DOCK_SCREENS

    NavigationBar(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                brush = Brush.verticalGradient(
                    listOf(Color.White.copy(alpha = 0.08f), Color.Transparent)
                ),
                shape = RectangleShape
            ),
        containerColor = BlackSurfaceElevated.copy(alpha = 0.96f),
        windowInsets = NavigationBarDefaults.windowInsets
    ) {
        // Tab 1: Home (Dashboard)
        val isHome = !isMoreSheetOpen && currentScreen == ScreenDestination.DASHBOARD
        DockTabCell(
            label = "Home",
            isSelected = isHome,
            selectedColor = Color(0xFFFFD54F),
            useGoldBoxHighlight = true,
            icon = {
                Icon(
                    imageVector = if (isHome) ScreenDestination.DASHBOARD.selectedIcon else ScreenDestination.DASHBOARD.unselectedIcon,
                    contentDescription = "Home",
                    tint = if (isHome) Color(0xFFFFD54F) else TextMuted,
                    modifier = Modifier.size(20.dp)
                )
            },
            onClick = { onSelectScreen(ScreenDestination.DASHBOARD) },
            modifier = Modifier
                .weight(1f)
                .testTag("nav_home")
        )

        // Tab 2: Study
        val isStudy = !isMoreSheetOpen && currentScreen == ScreenDestination.STUDY
        DockTabCell(
            label = "Study",
            isSelected = isStudy,
            selectedColor = Color(0xFFFFD54F),
            useGoldBoxHighlight = true,
            icon = {
                Icon(
                    imageVector = if (isStudy) ScreenDestination.STUDY.selectedIcon else ScreenDestination.STUDY.unselectedIcon,
                    contentDescription = "Study",
                    tint = if (isStudy) Color(0xFFFFD54F) else TextMuted,
                    modifier = Modifier.size(20.dp)
                )
            },
            onClick = { onSelectScreen(ScreenDestination.STUDY) },
            modifier = Modifier
                .weight(1f)
                .testTag("nav_study")
        )

        // Tab 3: Stats
        val isStats = !isMoreSheetOpen && currentScreen == ScreenDestination.STATISTICS
        DockTabCell(
            label = "Stats",
            isSelected = isStats,
            selectedColor = NeonYellow,
            useGoldBoxHighlight = false,
            icon = {
                Icon(
                    imageVector = if (isStats) ScreenDestination.STATISTICS.selectedIcon else ScreenDestination.STATISTICS.unselectedIcon,
                    contentDescription = "Stats",
                    tint = if (isStats) NeonYellow else TextMuted,
                    modifier = Modifier.size(20.dp)
                )
            },
            onClick = { onSelectScreen(ScreenDestination.STATISTICS) },
            modifier = Modifier
                .weight(1f)
                .testTag("nav_stats")
        )

        // Tab 4: Achievements (Item 7 requirement)
        val isAchievements = !isMoreSheetOpen && currentScreen == ScreenDestination.ACHIEVEMENTS
        DockTabCell(
            label = "Achievements",
            isSelected = isAchievements,
            selectedColor = NeonYellow,
            useGoldBoxHighlight = false,
            icon = {
                Icon(
                    imageVector = if (isAchievements) ScreenDestination.ACHIEVEMENTS.selectedIcon else ScreenDestination.ACHIEVEMENTS.unselectedIcon,
                    contentDescription = "Achievements",
                    tint = if (isAchievements) NeonYellow else TextMuted,
                    modifier = Modifier.size(20.dp)
                )
            },
            onClick = { onSelectScreen(ScreenDestination.ACHIEVEMENTS) },
            modifier = Modifier
                .weight(1f)
                .testTag("nav_achievements")
        )

        // Tab 5: More (Direct full-slot 1-tap hit target)
        DockTabCell(
            label = "More",
            isSelected = isMoreActive,
            selectedColor = NeonOrange,
            useGoldBoxHighlight = false,
            icon = {
                Icon(
                    imageVector = Icons.Default.MoreHoriz,
                    contentDescription = "More",
                    tint = if (isMoreActive) NeonOrange else TextMuted,
                    modifier = Modifier.size(20.dp)
                )
            },
            onClick = onOpenMore,
            modifier = Modifier
                .weight(1f)
                .testTag("nav_more")
        )
    }
}

@Composable
private fun DockTabCell(
    label: String,
    isSelected: Boolean,
    selectedColor: Color,
    useGoldBoxHighlight: Boolean,
    icon: @Composable () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(68.dp)
            .clickable(onClick = onClick)
            .padding(vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (isSelected && useGoldBoxHighlight) {
                Box(
                    modifier = Modifier
                        .width(46.dp)
                        .height(32.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF2E2208))
                        .border(1.2.dp, Color(0xFFFFB300), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    icon()
                }
            } else if (isSelected) {
                Box(
                    modifier = Modifier
                        .width(46.dp)
                        .height(32.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(selectedColor.copy(alpha = 0.18f)),
                    contentAlignment = Alignment.Center
                ) {
                    icon()
                }
            } else {
                Box(
                    modifier = Modifier
                        .width(46.dp)
                        .height(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    icon()
                }
            }

            Spacer(modifier = Modifier.height(3.dp))

            Text(
                text = label,
                fontSize = 10.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) selectedColor else TextMuted,
                maxLines = 1
            )
        }
    }
}
