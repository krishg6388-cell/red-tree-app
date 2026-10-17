package com.example.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Park
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Leaderboard
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Park
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material.icons.outlined.Spa
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonOrange
import com.example.ui.theme.NeonRed
import com.example.ui.theme.NeonYellow

enum class ScreenDestination(
    val title: String,
    val subtitle: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val accentColor: Color
) {
    SPLASH(
        title = "Restart App",
        subtitle = "Replay Beginning Animation",
        selectedIcon = Icons.Filled.Spa,
        unselectedIcon = Icons.Outlined.Spa,
        accentColor = NeonGreen
    ),
    AUTH(
        title = "Sign In",
        subtitle = "Sign Up / Sign In",
        selectedIcon = Icons.Filled.Lock,
        unselectedIcon = Icons.Outlined.Lock,
        accentColor = NeonOrange
    ),
    DASHBOARD(
        title = "Home",
        subtitle = "Overview & R.E.D Tree",
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home,
        accentColor = NeonGreen
    ),
    STUDY(
        title = "Study",
        subtitle = "Study Session Timer",
        selectedIcon = Icons.Filled.Timer,
        unselectedIcon = Icons.Outlined.Timer,
        accentColor = NeonYellow
    ),
    STATISTICS(
        title = "Stats",
        subtitle = "Metrics & Telemetry",
        selectedIcon = Icons.Filled.BarChart,
        unselectedIcon = Icons.Outlined.BarChart,
        accentColor = NeonYellow
    ),
    ACHIEVEMENTS(
        title = "Achievements",
        subtitle = "Milestones & 4 Tiers",
        selectedIcon = Icons.Filled.EmojiEvents,
        unselectedIcon = Icons.Outlined.EmojiEvents,
        accentColor = NeonYellow
    ),
    DAILY_TASKS(
        title = "Daily Tasks",
        subtitle = "Daily Focus Goals & XP",
        selectedIcon = Icons.Filled.CheckCircle,
        unselectedIcon = Icons.Outlined.CheckCircle,
        accentColor = NeonGreen
    ),
    PLANNER(
        title = "Study Planner",
        subtitle = "Schedule & Focus Targets",
        selectedIcon = Icons.Filled.CalendarMonth,
        unselectedIcon = Icons.Outlined.CalendarMonth,
        accentColor = NeonYellow
    ),
    LEADERBOARD(
        title = "Global Leaderboard",
        subtitle = "Community focus rankings",
        selectedIcon = Icons.Filled.EmojiEvents,
        unselectedIcon = Icons.Outlined.EmojiEvents,
        accentColor = NeonYellow
    ),
    PROFILE(
        title = "Profile",
        subtitle = "Student Identity & Settings",
        selectedIcon = Icons.Filled.Person,
        unselectedIcon = Icons.Outlined.Person,
        accentColor = NeonGreen
    ),
    FOCUS_GUARD(
        title = "Focus Guard",
        subtitle = "Distraction Shield Protocol",
        selectedIcon = Icons.Filled.Shield,
        unselectedIcon = Icons.Outlined.Shield,
        accentColor = NeonRed
    ),
    TREE_GROWTH(
        title = "Tree Growth",
        subtitle = "Progression & XP",
        selectedIcon = Icons.Filled.Park,
        unselectedIcon = Icons.Outlined.Park,
        accentColor = NeonGreen
    ),
    COMPLETION(
        title = "Check In",
        subtitle = "Session Complete Flow",
        selectedIcon = Icons.Filled.CheckCircle,
        unselectedIcon = Icons.Outlined.CheckCircle,
        accentColor = NeonGreen
    ),
    COMPANION(
        title = "Companion",
        subtitle = "Forest Cutu & Cutie",
        selectedIcon = Icons.Filled.Spa,
        unselectedIcon = Icons.Outlined.Spa,
        accentColor = NeonGreen
    )
}
