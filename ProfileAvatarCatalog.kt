package com.example.ui.screens

import android.content.Context
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AcUnit
import androidx.compose.material.icons.outlined.AllInclusive
import androidx.compose.material.icons.outlined.Anchor
import androidx.compose.material.icons.outlined.Architecture
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.AutoStories
import androidx.compose.material.icons.outlined.Balance
import androidx.compose.material.icons.outlined.Bolt
import androidx.compose.material.icons.outlined.Castle
import androidx.compose.material.icons.outlined.CenterFocusStrong
import androidx.compose.material.icons.outlined.Diamond
import androidx.compose.material.icons.outlined.Eco
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Extension
import androidx.compose.material.icons.outlined.FilterCenterFocus
import androidx.compose.material.icons.outlined.Flare
import androidx.compose.material.icons.outlined.FlashOn
import androidx.compose.material.icons.outlined.Gavel
import androidx.compose.material.icons.outlined.HourglassTop
import androidx.compose.material.icons.outlined.Hub
import androidx.compose.material.icons.outlined.Key
import androidx.compose.material.icons.outlined.Layers
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material.icons.outlined.LockClock
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.MilitaryTech
import androidx.compose.material.icons.outlined.Nightlight
import androidx.compose.material.icons.outlined.Psychology
import androidx.compose.material.icons.outlined.Public
import androidx.compose.material.icons.outlined.Radar
import androidx.compose.material.icons.outlined.RocketLaunch
import androidx.compose.material.icons.outlined.School
import androidx.compose.material.icons.outlined.Science
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material.icons.outlined.SelfImprovement
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material.icons.outlined.Spa
import androidx.compose.material.icons.outlined.Stars
import androidx.compose.material.icons.outlined.Terrain
import androidx.compose.material.icons.outlined.Timeline
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material.icons.outlined.Token
import androidx.compose.material.icons.outlined.TrackChanges
import androidx.compose.material.icons.outlined.Verified
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.WbSunny
import androidx.compose.material.icons.outlined.Whatshot
import androidx.compose.material.icons.outlined.WorkspacePremium
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonGreenBright
import com.example.ui.theme.NeonOrange
import com.example.ui.theme.NeonRed
import com.example.ui.theme.NeonYellow
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Fixed standard visual size for ALL 50 avatars in the Change Avatar grid,
 * matching the large first R.E.D emblem badge reference size.
 */
val STANDARD_AVATAR_EMBLEM_SIZE: Dp = 96.dp

enum class EmblemFrameStyle {
    ROYAL_RING,
    OCTAGON_BASTION,
    HEXAGON_CORE,
    STARBURST_HALO,
    LAUREL_SOVEREIGN,
    DIAMOND_AEGIS,
    RUNIC_CHRONO,
    CITADEL_CREST
}

enum class AvatarTaskMetric(val unitLabel: String) {
    FREE("free"),
    COMPLETED_SESSIONS("sessions"),
    TOTAL_STUDY_MINUTES("minutes"),
    TOTAL_STUDY_HOURS("hours"),
    SINGLE_SESSION_MINUTES("min session"),
    STREAK_DAYS("days")
}

data class ProfileAvatarItem(
    val number: Int,
    val id: String,
    val title: String,
    val isInitiallyFree: Boolean,
    val primaryColor: Color,
    val secondaryColor: Color,
    val frameStyle: EmblemFrameStyle,
    val symbolIcon: ImageVector,
    val unlockTask: String,
    val taskMetric: AvatarTaskMetric,
    val targetValue: Int,
    val motivationalText: String,
    val usesOfficialLogoCrest: Boolean = false
)

data class AvatarUnlockStatus(
    val isUnlocked: Boolean,
    val currentValue: Int,
    val targetValue: Int,
    val progressFraction: Float,
    val formattedProgress: String
)

private const val AVATAR_PREFS_NAME = "red_tree_user_profile_prefs"
private const val KEY_UNLOCKED_AVATAR_IDS = "permanently_unlocked_avatar_ids"
private const val KEY_COMPLETED_SESSIONS = "avatar_completed_study_sessions"
private const val KEY_COMPLETED_MINUTES = "avatar_completed_study_minutes"
private const val KEY_LONGEST_SESSION_MINUTES = "avatar_longest_session_minutes"
private const val KEY_STREAK_DAYS = "avatar_study_streak_days"

/**
 * Evaluates real study progress for an avatar and permanently persists unlocked status.
 */
fun evaluateAvatarUnlockStatus(context: Context, avatar: ProfileAvatarItem): AvatarUnlockStatus {
    if (avatar.isInitiallyFree) {
        return AvatarUnlockStatus(
            isUnlocked = true,
            currentValue = 1,
            targetValue = 1,
            progressFraction = 1f,
            formattedProgress = "Unlocked • Free Collection"
        )
    }

    val prefs = context.applicationContext.getSharedPreferences(AVATAR_PREFS_NAME, Context.MODE_PRIVATE)
    val permanentlyUnlocked = prefs.getStringSet(KEY_UNLOCKED_AVATAR_IDS, emptySet()) ?: emptySet()

    val sessions = prefs.getInt(KEY_COMPLETED_SESSIONS, 0)
    val totalMinutes = prefs.getInt(KEY_COMPLETED_MINUTES, 0)
    val totalHours = totalMinutes / 60
    val longestSession = prefs.getInt(KEY_LONGEST_SESSION_MINUTES, 0)
    val streakDays = prefs.getInt(KEY_STREAK_DAYS, 0)

    val rawCurrent = when (avatar.taskMetric) {
        AvatarTaskMetric.FREE -> avatar.targetValue
        AvatarTaskMetric.COMPLETED_SESSIONS -> sessions
        AvatarTaskMetric.TOTAL_STUDY_MINUTES -> totalMinutes
        AvatarTaskMetric.TOTAL_STUDY_HOURS -> totalHours
        AvatarTaskMetric.SINGLE_SESSION_MINUTES -> longestSession
        AvatarTaskMetric.STREAK_DAYS -> streakDays
    }

    val meetsRequirement = rawCurrent >= avatar.targetValue
    val isUnlocked = avatar.id in permanentlyUnlocked || meetsRequirement

    if (meetsRequirement && avatar.id !in permanentlyUnlocked) {
        val updatedSet = permanentlyUnlocked.toMutableSet().apply { add(avatar.id) }
        prefs.edit().putStringSet(KEY_UNLOCKED_AVATAR_IDS, updatedSet).commit()
    }

    val displayCurrent = if (isUnlocked) avatar.targetValue else rawCurrent.coerceAtMost(avatar.targetValue)
    val fraction = if (avatar.targetValue > 0) {
        (displayCurrent.toFloat() / avatar.targetValue.toFloat()).coerceIn(0f, 1f)
    } else 0f

    return AvatarUnlockStatus(
        isUnlocked = isUnlocked,
        currentValue = displayCurrent,
        targetValue = avatar.targetValue,
        progressFraction = fraction,
        formattedProgress = "$displayCurrent / ${avatar.targetValue} ${avatar.taskMetric.unitLabel}"
    )
}

/**
 * Updates persisted study session metrics used to evaluate and permanently unlock avatars.
 */
fun recordStudyProgressForAvatars(
    context: Context,
    completedSessions: Int,
    totalStudyMinutes: Int,
    longestSessionMinutes: Int = totalStudyMinutes,
    streakDays: Int = 1
) {
    val prefs = context.applicationContext.getSharedPreferences(AVATAR_PREFS_NAME, Context.MODE_PRIVATE)
    prefs.edit()
        .putInt(KEY_COMPLETED_SESSIONS, completedSessions)
        .putInt(KEY_COMPLETED_MINUTES, totalStudyMinutes)
        .putInt(KEY_LONGEST_SESSION_MINUTES, longestSessionMinutes)
        .putInt(KEY_STREAK_DAYS, streakDays)
        .commit()
    AchievementManager.syncAndEvaluateUnlocks(context)
}

/**
 * Official 50-Avatar R.E.D TREE Collection:
 * - Avatars 1–10: FREE from the beginning.
 * - Avatars 11–50: LOCKED initially, unlocked through progressive study tasks.
 */
val AVAILABLE_PROFILE_AVATARS: List<ProfileAvatarItem> = listOf(
    // ==================== 1–10: FREE STARTER COLLECTION (10 AVATARS) ====================
    ProfileAvatarItem(
        number = 1,
        id = "avatar_red_tree_logo",
        title = "R.E.D Guardian",
        isInitiallyFree = true,
        primaryColor = NeonRed,
        secondaryColor = NeonYellow,
        frameStyle = EmblemFrameStyle.ROYAL_RING,
        symbolIcon = Icons.Outlined.Spa,
        unlockTask = "Free Starter Emblem",
        taskMetric = AvatarTaskMetric.FREE,
        targetValue = 1,
        motivationalText = "Official R.E.D TREE Sovereign Emblem.",
        usesOfficialLogoCrest = true
    ),
    ProfileAvatarItem(
        number = 2,
        id = "avatar_seed_sprout",
        title = "First Focus",
        isInitiallyFree = true,
        primaryColor = NeonGreenBright,
        secondaryColor = NeonYellow,
        frameStyle = EmblemFrameStyle.OCTAGON_BASTION,
        symbolIcon = Icons.Outlined.CenterFocusStrong,
        unlockTask = "Free Starter Emblem",
        taskMetric = AvatarTaskMetric.FREE,
        targetValue = 1,
        motivationalText = "Clarity of mind begins with a single point of focus."
    ),
    ProfileAvatarItem(
        number = 3,
        id = "avatar_study_initiate",
        title = "Study Initiate",
        isInitiallyFree = true,
        primaryColor = NeonYellow,
        secondaryColor = NeonGreen,
        frameStyle = EmblemFrameStyle.HEXAGON_CORE,
        symbolIcon = Icons.Outlined.AutoStories,
        unlockTask = "Free Starter Emblem",
        taskMetric = AvatarTaskMetric.FREE,
        targetValue = 1,
        motivationalText = "Dedicated to the pursuit of daily knowledge."
    ),
    ProfileAvatarItem(
        number = 4,
        id = "avatar_focus_spark",
        title = "Focus Spark",
        isInitiallyFree = true,
        primaryColor = NeonRed,
        secondaryColor = NeonOrange,
        frameStyle = EmblemFrameStyle.STARBURST_HALO,
        symbolIcon = Icons.Outlined.Bolt,
        unlockTask = "Free Starter Emblem",
        taskMetric = AvatarTaskMetric.FREE,
        targetValue = 1,
        motivationalText = "Ignite the spark of disciplined momentum."
    ),
    ProfileAvatarItem(
        number = 5,
        id = "avatar_deep_focus",
        title = "Deep Focus",
        isInitiallyFree = true,
        primaryColor = NeonGreen,
        secondaryColor = NeonGreenBright,
        frameStyle = EmblemFrameStyle.LAUREL_SOVEREIGN,
        symbolIcon = Icons.Outlined.FilterCenterFocus,
        unlockTask = "Free Starter Emblem",
        taskMetric = AvatarTaskMetric.FREE,
        targetValue = 1,
        motivationalText = "Zero distractions. Pure intellectual immersion."
    ),
    ProfileAvatarItem(
        number = 6,
        id = "avatar_knowledge_seeker",
        title = "Knowledge Seeker",
        isInitiallyFree = true,
        primaryColor = NeonYellow,
        secondaryColor = NeonRed,
        frameStyle = EmblemFrameStyle.DIAMOND_AEGIS,
        symbolIcon = Icons.Outlined.Explore,
        unlockTask = "Free Starter Emblem",
        taskMetric = AvatarTaskMetric.FREE,
        targetValue = 1,
        motivationalText = "Charting new territories of understanding."
    ),
    ProfileAvatarItem(
        number = 7,
        id = "avatar_silent_warrior",
        title = "Silent Warrior",
        isInitiallyFree = true,
        primaryColor = NeonRed,
        secondaryColor = NeonGreenBright,
        frameStyle = EmblemFrameStyle.RUNIC_CHRONO,
        symbolIcon = Icons.Outlined.Shield,
        unlockTask = "Free Starter Emblem",
        taskMetric = AvatarTaskMetric.FREE,
        targetValue = 1,
        motivationalText = "Strength forged in quiet hours of preparation."
    ),
    ProfileAvatarItem(
        number = 8,
        id = "avatar_focus_guardian",
        title = "Focus Guardian",
        isInitiallyFree = true,
        primaryColor = NeonGreenBright,
        secondaryColor = NeonRed,
        frameStyle = EmblemFrameStyle.CITADEL_CREST,
        symbolIcon = Icons.Outlined.Security,
        unlockTask = "Free Starter Emblem",
        taskMetric = AvatarTaskMetric.FREE,
        targetValue = 1,
        motivationalText = "Standing guard against every digital distraction."
    ),
    ProfileAvatarItem(
        number = 9,
        id = "avatar_discipline_core",
        title = "Discipline Core",
        isInitiallyFree = true,
        primaryColor = NeonOrange,
        secondaryColor = NeonYellow,
        frameStyle = EmblemFrameStyle.HEXAGON_CORE,
        symbolIcon = Icons.Outlined.Hub,
        unlockTask = "Free Starter Emblem",
        taskMetric = AvatarTaskMetric.FREE,
        targetValue = 1,
        motivationalText = "Built on unshakable daily habits."
    ),
    ProfileAvatarItem(
        number = 10,
        id = "avatar_study_vanguard",
        title = "Study Vanguard",
        isInitiallyFree = true,
        primaryColor = NeonRed,
        secondaryColor = NeonYellow,
        frameStyle = EmblemFrameStyle.LAUREL_SOVEREIGN,
        symbolIcon = Icons.Outlined.MilitaryTech,
        unlockTask = "Free Starter Emblem",
        taskMetric = AvatarTaskMetric.FREE,
        targetValue = 1,
        motivationalText = "Leading the front line of academic excellence."
    ),

    // ==================== 11–50: LOCKED PROGRESSION AVATARS (40 AVATARS) ====================
    ProfileAvatarItem(
        number = 11,
        id = "avatar_11_mind_forge",
        title = "Mind Forge",
        isInitiallyFree = false,
        primaryColor = NeonRed,
        secondaryColor = NeonYellow,
        frameStyle = EmblemFrameStyle.OCTAGON_BASTION,
        symbolIcon = Icons.Outlined.Psychology,
        unlockTask = "Complete your first study session.",
        taskMetric = AvatarTaskMetric.COMPLETED_SESSIONS,
        targetValue = 1,
        motivationalText = "Complete a full study session to temper your mind."
    ),
    ProfileAvatarItem(
        number = 12,
        id = "avatar_12_ember_watcher",
        title = "Ember Watcher",
        isInitiallyFree = false,
        primaryColor = NeonOrange,
        secondaryColor = NeonRed,
        frameStyle = EmblemFrameStyle.ROYAL_RING,
        symbolIcon = Icons.Outlined.LocalFireDepartment,
        unlockTask = "Complete 2 study sessions.",
        taskMetric = AvatarTaskMetric.COMPLETED_SESSIONS,
        targetValue = 2,
        motivationalText = "Keep the study flame alive across two sessions."
    ),
    ProfileAvatarItem(
        number = 13,
        id = "avatar_13_grove_pathfinder",
        title = "Grove Pathfinder",
        isInitiallyFree = false,
        primaryColor = NeonGreenBright,
        secondaryColor = NeonYellow,
        frameStyle = EmblemFrameStyle.STARBURST_HALO,
        symbolIcon = Icons.Outlined.Eco,
        unlockTask = "Complete 3 study sessions.",
        taskMetric = AvatarTaskMetric.COMPLETED_SESSIONS,
        targetValue = 3,
        motivationalText = "Every completed session deepens your roots."
    ),
    ProfileAvatarItem(
        number = 14,
        id = "avatar_14_pulse_keeper",
        title = "Pulse Keeper",
        isInitiallyFree = false,
        primaryColor = NeonGreen,
        secondaryColor = NeonRed,
        frameStyle = EmblemFrameStyle.RUNIC_CHRONO,
        symbolIcon = Icons.Outlined.Timer,
        unlockTask = "Complete a 30-minute study session.",
        taskMetric = AvatarTaskMetric.SINGLE_SESSION_MINUTES,
        targetValue = 30,
        motivationalText = "Hold unbroken focus for a full 30-minute session."
    ),
    ProfileAvatarItem(
        number = 15,
        id = "avatar_15_time_master",
        title = "Time Master",
        isInitiallyFree = false,
        primaryColor = NeonYellow,
        secondaryColor = NeonOrange,
        frameStyle = EmblemFrameStyle.DIAMOND_AEGIS,
        symbolIcon = Icons.Outlined.HourglassTop,
        unlockTask = "Complete 60 minutes of focused study.",
        taskMetric = AvatarTaskMetric.TOTAL_STUDY_MINUTES,
        targetValue = 60,
        motivationalText = "Master your first full hour of focused study."
    ),
    ProfileAvatarItem(
        number = 16,
        id = "avatar_16_twin_flame",
        title = "Twin Flame",
        isInitiallyFree = false,
        primaryColor = NeonRed,
        secondaryColor = NeonOrange,
        frameStyle = EmblemFrameStyle.CITADEL_CREST,
        symbolIcon = Icons.Outlined.Whatshot,
        unlockTask = "Complete 2 sessions with a total of 90 minutes.",
        taskMetric = AvatarTaskMetric.TOTAL_STUDY_MINUTES,
        targetValue = 90,
        motivationalText = "Accumulate 90 minutes of deep focus across your sessions."
    ),
    ProfileAvatarItem(
        number = 17,
        id = "avatar_17_iron_scholar",
        title = "Iron Scholar",
        isInitiallyFree = false,
        primaryColor = NeonGreenBright,
        secondaryColor = NeonGreen,
        frameStyle = EmblemFrameStyle.HEXAGON_CORE,
        symbolIcon = Icons.Outlined.MenuBook,
        unlockTask = "Complete 4 focused study sessions.",
        taskMetric = AvatarTaskMetric.COMPLETED_SESSIONS,
        targetValue = 4,
        motivationalText = "Four sessions of disciplined effort build lasting focus."
    ),
    ProfileAvatarItem(
        number = 18,
        id = "avatar_18_dawn_sentinel",
        title = "Dawn Sentinel",
        isInitiallyFree = false,
        primaryColor = NeonYellow,
        secondaryColor = NeonGreenBright,
        frameStyle = EmblemFrameStyle.STARBURST_HALO,
        symbolIcon = Icons.Outlined.WbSunny,
        unlockTask = "Complete 2 consecutive study days.",
        taskMetric = AvatarTaskMetric.STREAK_DAYS,
        targetValue = 2,
        motivationalText = "Return two days in a row to prove your consistency."
    ),
    ProfileAvatarItem(
        number = 19,
        id = "avatar_19_verdant_core",
        title = "Verdant Core",
        isInitiallyFree = false,
        primaryColor = NeonGreen,
        secondaryColor = NeonYellow,
        frameStyle = EmblemFrameStyle.LAUREL_SOVEREIGN,
        symbolIcon = Icons.Outlined.Layers,
        unlockTask = "Complete a 45-minute focused study session.",
        taskMetric = AvatarTaskMetric.SINGLE_SESSION_MINUTES,
        targetValue = 45,
        motivationalText = "Push your endurance with a 45-minute uninterrupted session."
    ),
    ProfileAvatarItem(
        number = 20,
        id = "avatar_20_focus_sentinel",
        title = "Focus Sentinel",
        isInitiallyFree = false,
        primaryColor = NeonRed,
        secondaryColor = NeonYellow,
        frameStyle = EmblemFrameStyle.OCTAGON_BASTION,
        symbolIcon = Icons.Outlined.Visibility,
        unlockTask = "Complete 5 focused study sessions.",
        taskMetric = AvatarTaskMetric.COMPLETED_SESSIONS,
        targetValue = 5,
        motivationalText = "Keep studying to unlock this avatar."
    ),
    ProfileAvatarItem(
        number = 21,
        id = "avatar_21_rhythm_striker",
        title = "Rhythm Striker",
        isInitiallyFree = false,
        primaryColor = NeonOrange,
        secondaryColor = NeonGreenBright,
        frameStyle = EmblemFrameStyle.RUNIC_CHRONO,
        symbolIcon = Icons.Outlined.Timeline,
        unlockTask = "Complete 3 sessions in 2 days.",
        taskMetric = AvatarTaskMetric.COMPLETED_SESSIONS,
        targetValue = 3,
        motivationalText = "Build steady momentum across consecutive study sessions."
    ),
    ProfileAvatarItem(
        number = 22,
        id = "avatar_22_knowledge_keeper",
        title = "Knowledge Keeper",
        isInitiallyFree = false,
        primaryColor = NeonYellow,
        secondaryColor = NeonRed,
        frameStyle = EmblemFrameStyle.ROYAL_RING,
        symbolIcon = Icons.Outlined.Key,
        unlockTask = "Complete 120 minutes of focused study.",
        taskMetric = AvatarTaskMetric.TOTAL_STUDY_MINUTES,
        targetValue = 120,
        motivationalText = "Two full hours of focused study unlocks deeper mastery."
    ),
    ProfileAvatarItem(
        number = 23,
        id = "avatar_23_crimson_monolith",
        title = "Crimson Monolith",
        isInitiallyFree = false,
        primaryColor = NeonRed,
        secondaryColor = NeonGreen,
        frameStyle = EmblemFrameStyle.DIAMOND_AEGIS,
        symbolIcon = Icons.Outlined.Terrain,
        unlockTask = "Complete a 60-minute focused session.",
        taskMetric = AvatarTaskMetric.SINGLE_SESSION_MINUTES,
        targetValue = 60,
        motivationalText = "Stand unmoved for a full 60-minute deep focus session."
    ),
    ProfileAvatarItem(
        number = 24,
        id = "avatar_24_shadow_tactician",
        title = "Shadow Tactician",
        isInitiallyFree = false,
        primaryColor = NeonGreenBright,
        secondaryColor = NeonYellow,
        frameStyle = EmblemFrameStyle.HEXAGON_CORE,
        symbolIcon = Icons.Outlined.Radar,
        unlockTask = "Complete 6 focused study sessions.",
        taskMetric = AvatarTaskMetric.COMPLETED_SESSIONS,
        targetValue = 6,
        motivationalText = "Precision planning across six completed sessions."
    ),
    ProfileAvatarItem(
        number = 25,
        id = "avatar_25_flame_warden",
        title = "Flame Warden",
        isInitiallyFree = false,
        primaryColor = NeonOrange,
        secondaryColor = NeonYellow,
        frameStyle = EmblemFrameStyle.CITADEL_CREST,
        symbolIcon = Icons.Outlined.Flare,
        unlockTask = "Complete 3 study days in a row.",
        taskMetric = AvatarTaskMetric.STREAK_DAYS,
        targetValue = 3,
        motivationalText = "Maintain a 3-day study streak to claim the Warden crest."
    ),
    ProfileAvatarItem(
        number = 26,
        id = "avatar_26_arcane_reader",
        title = "Arcane Reader",
        isInitiallyFree = false,
        primaryColor = NeonGreen,
        secondaryColor = NeonGreenBright,
        frameStyle = EmblemFrameStyle.LAUREL_SOVEREIGN,
        symbolIcon = Icons.Outlined.School,
        unlockTask = "Complete 3 total hours of study.",
        taskMetric = AvatarTaskMetric.TOTAL_STUDY_HOURS,
        targetValue = 3,
        motivationalText = "Three cumulative hours of distraction-free learning."
    ),
    ProfileAvatarItem(
        number = 27,
        id = "avatar_27_storm_aegis",
        title = "Storm Aegis",
        isInitiallyFree = false,
        primaryColor = NeonRed,
        secondaryColor = NeonOrange,
        frameStyle = EmblemFrameStyle.OCTAGON_BASTION,
        symbolIcon = Icons.Outlined.FlashOn,
        unlockTask = "Complete 8 focused study sessions.",
        taskMetric = AvatarTaskMetric.COMPLETED_SESSIONS,
        targetValue = 8,
        motivationalText = "Harness relentless energy through 8 completed sessions."
    ),
    ProfileAvatarItem(
        number = 28,
        id = "avatar_28_chrono_architect",
        title = "Chrono Architect",
        isInitiallyFree = false,
        primaryColor = NeonYellow,
        secondaryColor = NeonGreen,
        frameStyle = EmblemFrameStyle.RUNIC_CHRONO,
        symbolIcon = Icons.Outlined.Architecture,
        unlockTask = "Complete 4 sessions with a total of 180 minutes.",
        taskMetric = AvatarTaskMetric.TOTAL_STUDY_MINUTES,
        targetValue = 180,
        motivationalText = "Design your schedule to reach 180 minutes of focused work."
    ),
    ProfileAvatarItem(
        number = 29,
        id = "avatar_29_solar_vigil",
        title = "Solar Vigil",
        isInitiallyFree = false,
        primaryColor = NeonYellow,
        secondaryColor = NeonRed,
        frameStyle = EmblemFrameStyle.STARBURST_HALO,
        symbolIcon = Icons.Outlined.Lightbulb,
        unlockTask = "Maintain a 4-day study streak.",
        taskMetric = AvatarTaskMetric.STREAK_DAYS,
        targetValue = 4,
        motivationalText = "Four straight days of illumination and discipline."
    ),
    ProfileAvatarItem(
        number = 30,
        id = "avatar_30_scholar_prime",
        title = "Scholar Prime",
        isInitiallyFree = false,
        primaryColor = NeonGreenBright,
        secondaryColor = NeonYellow,
        frameStyle = EmblemFrameStyle.ROYAL_RING,
        symbolIcon = Icons.Outlined.WorkspacePremium,
        unlockTask = "Complete 5 total hours of study.",
        taskMetric = AvatarTaskMetric.TOTAL_STUDY_HOURS,
        targetValue = 5,
        motivationalText = "Five hours of deep focus marks a true Scholar Prime."
    ),
    ProfileAvatarItem(
        number = 31,
        id = "avatar_31_apex_ranger",
        title = "Apex Ranger",
        isInitiallyFree = false,
        primaryColor = NeonGreen,
        secondaryColor = NeonOrange,
        frameStyle = EmblemFrameStyle.DIAMOND_AEGIS,
        symbolIcon = Icons.Outlined.TrackChanges,
        unlockTask = "Complete 10 study sessions.",
        taskMetric = AvatarTaskMetric.COMPLETED_SESSIONS,
        targetValue = 10,
        motivationalText = "Ten completed study sessions with unwavering aim."
    ),
    ProfileAvatarItem(
        number = 32,
        id = "avatar_32_unbroken_will",
        title = "Unbroken Will",
        isInitiallyFree = false,
        primaryColor = NeonRed,
        secondaryColor = NeonYellow,
        frameStyle = EmblemFrameStyle.CITADEL_CREST,
        symbolIcon = Icons.Outlined.Anchor,
        unlockTask = "Maintain a 5-day study streak.",
        taskMetric = AvatarTaskMetric.STREAK_DAYS,
        targetValue = 5,
        motivationalText = "Five consecutive days of study cements an unbroken habit."
    ),
    ProfileAvatarItem(
        number = 33,
        id = "avatar_33_deep_immersion",
        title = "Deep Immersion",
        isInitiallyFree = false,
        primaryColor = NeonGreenBright,
        secondaryColor = NeonRed,
        frameStyle = EmblemFrameStyle.HEXAGON_CORE,
        symbolIcon = Icons.Outlined.SelfImprovement,
        unlockTask = "Complete a 75-minute deep focus session.",
        taskMetric = AvatarTaskMetric.SINGLE_SESSION_MINUTES,
        targetValue = 75,
        motivationalText = "Enter total flow state with a 75-minute single session."
    ),
    ProfileAvatarItem(
        number = 34,
        id = "avatar_34_emerald_strategist",
        title = "Emerald Strategist",
        isInitiallyFree = false,
        primaryColor = NeonGreen,
        secondaryColor = NeonYellow,
        frameStyle = EmblemFrameStyle.LAUREL_SOVEREIGN,
        symbolIcon = Icons.Outlined.Extension,
        unlockTask = "Complete 7 total hours of study.",
        taskMetric = AvatarTaskMetric.TOTAL_STUDY_HOURS,
        targetValue = 7,
        motivationalText = "Seven hours of strategic study sharpens every skill."
    ),
    ProfileAvatarItem(
        number = 35,
        id = "avatar_35_obsidian_knight",
        title = "Obsidian Knight",
        isInitiallyFree = false,
        primaryColor = NeonRed,
        secondaryColor = NeonGreenBright,
        frameStyle = EmblemFrameStyle.OCTAGON_BASTION,
        symbolIcon = Icons.Outlined.Gavel,
        unlockTask = "Complete 12 focused study sessions.",
        taskMetric = AvatarTaskMetric.COMPLETED_SESSIONS,
        targetValue = 12,
        motivationalText = "Twelve sessions completed with knightly resolve."
    ),
    ProfileAvatarItem(
        number = 36,
        id = "avatar_36_phoenix_ascendant",
        title = "Phoenix Ascendant",
        isInitiallyFree = false,
        primaryColor = NeonOrange,
        secondaryColor = NeonRed,
        frameStyle = EmblemFrameStyle.STARBURST_HALO,
        symbolIcon = Icons.Outlined.RocketLaunch,
        unlockTask = "Complete 6 study days in a row.",
        taskMetric = AvatarTaskMetric.STREAK_DAYS,
        targetValue = 6,
        motivationalText = "Rise higher every day across a 6-day study streak."
    ),
    ProfileAvatarItem(
        number = 37,
        id = "avatar_37_golden_arbiter",
        title = "Golden Arbiter",
        isInitiallyFree = false,
        primaryColor = NeonYellow,
        secondaryColor = NeonOrange,
        frameStyle = EmblemFrameStyle.ROYAL_RING,
        symbolIcon = Icons.Outlined.Balance,
        unlockTask = "Complete 9 total hours of study.",
        taskMetric = AvatarTaskMetric.TOTAL_STUDY_HOURS,
        targetValue = 9,
        motivationalText = "Balance discipline and endurance over 9 hours of study."
    ),
    ProfileAvatarItem(
        number = 38,
        id = "avatar_38_mind_sovereign",
        title = "Mind Sovereign",
        isInitiallyFree = false,
        primaryColor = NeonRed,
        secondaryColor = NeonYellow,
        frameStyle = EmblemFrameStyle.DIAMOND_AEGIS,
        symbolIcon = Icons.Outlined.Token,
        unlockTask = "Complete 15 focused study sessions.",
        taskMetric = AvatarTaskMetric.COMPLETED_SESSIONS,
        targetValue = 15,
        motivationalText = "Command your attention across 15 completed study sessions."
    ),
    ProfileAvatarItem(
        number = 39,
        id = "avatar_39_titan_focus",
        title = "Titan Focus",
        isInitiallyFree = false,
        primaryColor = NeonGreenBright,
        secondaryColor = NeonYellow,
        frameStyle = EmblemFrameStyle.RUNIC_CHRONO,
        symbolIcon = Icons.Outlined.LockClock,
        unlockTask = "Complete a 90-minute focused session.",
        taskMetric = AvatarTaskMetric.SINGLE_SESSION_MINUTES,
        targetValue = 90,
        motivationalText = "Conquer a 90-minute marathon session without distraction."
    ),
    ProfileAvatarItem(
        number = 40,
        id = "avatar_40_radiant_crown",
        title = "Radiant Crown",
        isInitiallyFree = false,
        primaryColor = NeonYellow,
        secondaryColor = NeonGreenBright,
        frameStyle = EmblemFrameStyle.CITADEL_CREST,
        symbolIcon = Icons.Outlined.EmojiEvents,
        unlockTask = "Maintain a 7-day study streak.",
        taskMetric = AvatarTaskMetric.STREAK_DAYS,
        targetValue = 7,
        motivationalText = "A full week of unbroken daily study earns the Radiant Crown."
    ),
    ProfileAvatarItem(
        number = 41,
        id = "avatar_41_celestial_sage",
        title = "Celestial Sage",
        isInitiallyFree = false,
        primaryColor = NeonGreen,
        secondaryColor = NeonYellow,
        frameStyle = EmblemFrameStyle.STARBURST_HALO,
        symbolIcon = Icons.Outlined.Nightlight,
        unlockTask = "Complete 12 total hours of study.",
        taskMetric = AvatarTaskMetric.TOTAL_STUDY_HOURS,
        targetValue = 12,
        motivationalText = "Twelve cumulative hours of wisdom and quiet contemplation."
    ),
    ProfileAvatarItem(
        number = 42,
        id = "avatar_42_vortex_commander",
        title = "Vortex Commander",
        isInitiallyFree = false,
        primaryColor = NeonRed,
        secondaryColor = NeonOrange,
        frameStyle = EmblemFrameStyle.HEXAGON_CORE,
        symbolIcon = Icons.Outlined.Science,
        unlockTask = "Complete 18 focused study sessions.",
        taskMetric = AvatarTaskMetric.COMPLETED_SESSIONS,
        targetValue = 18,
        motivationalText = "Eighteen focused sessions completed with scientific precision."
    ),
    ProfileAvatarItem(
        number = 43,
        id = "avatar_43_prism_oracle",
        title = "Prism Oracle",
        isInitiallyFree = false,
        primaryColor = NeonYellow,
        secondaryColor = NeonRed,
        frameStyle = EmblemFrameStyle.DIAMOND_AEGIS,
        symbolIcon = Icons.Outlined.Diamond,
        unlockTask = "Complete 600 minutes of focused study.",
        taskMetric = AvatarTaskMetric.TOTAL_STUDY_MINUTES,
        targetValue = 600,
        motivationalText = "Six hundred minutes of pure focus refracts into brilliance."
    ),
    ProfileAvatarItem(
        number = 44,
        id = "avatar_44_immortal_streak",
        title = "Immortal Streak",
        isInitiallyFree = false,
        primaryColor = NeonOrange,
        secondaryColor = NeonYellow,
        frameStyle = EmblemFrameStyle.LAUREL_SOVEREIGN,
        symbolIcon = Icons.Outlined.AllInclusive,
        unlockTask = "Maintain a 10-day study streak.",
        taskMetric = AvatarTaskMetric.STREAK_DAYS,
        targetValue = 10,
        motivationalText = "Ten consecutive study days of unstoppable momentum."
    ),
    ProfileAvatarItem(
        number = 45,
        id = "avatar_45_grand_archivist",
        title = "Grand Archivist",
        isInitiallyFree = false,
        primaryColor = NeonGreenBright,
        secondaryColor = NeonGreen,
        frameStyle = EmblemFrameStyle.ROYAL_RING,
        symbolIcon = Icons.Outlined.Public,
        unlockTask = "Complete 15 total hours of study.",
        taskMetric = AvatarTaskMetric.TOTAL_STUDY_HOURS,
        targetValue = 15,
        motivationalText = "Fifteen hours of dedicated study expands your world."
    ),
    ProfileAvatarItem(
        number = 46,
        id = "avatar_46_citadel_master",
        title = "Citadel Master",
        isInitiallyFree = false,
        primaryColor = NeonRed,
        secondaryColor = NeonYellow,
        frameStyle = EmblemFrameStyle.CITADEL_CREST,
        symbolIcon = Icons.Outlined.Castle,
        unlockTask = "Complete 22 focused study sessions.",
        taskMetric = AvatarTaskMetric.COMPLETED_SESSIONS,
        targetValue = 22,
        motivationalText = "Twenty-two completed sessions fortify your mental citadel."
    ),
    ProfileAvatarItem(
        number = 47,
        id = "avatar_47_zenith_conqueror",
        title = "Zenith Conqueror",
        isInitiallyFree = false,
        primaryColor = NeonYellow,
        secondaryColor = NeonGreenBright,
        frameStyle = EmblemFrameStyle.OCTAGON_BASTION,
        symbolIcon = Icons.Outlined.AcUnit,
        unlockTask = "Complete a 120-minute study session.",
        taskMetric = AvatarTaskMetric.SINGLE_SESSION_MINUTES,
        targetValue = 120,
        motivationalText = "Reach the zenith with a single 2-hour deep study session."
    ),
    ProfileAvatarItem(
        number = 48,
        id = "avatar_48_eternal_flame",
        title = "Eternal Flame",
        isInitiallyFree = false,
        primaryColor = NeonRed,
        secondaryColor = NeonOrange,
        frameStyle = EmblemFrameStyle.STARBURST_HALO,
        symbolIcon = Icons.Outlined.AutoAwesome,
        unlockTask = "Maintain a 14-day study streak.",
        taskMetric = AvatarTaskMetric.STREAK_DAYS,
        targetValue = 14,
        motivationalText = "Two full weeks of daily study keeps the Eternal Flame burning."
    ),
    ProfileAvatarItem(
        number = 49,
        id = "avatar_49_omega_vanguard",
        title = "Omega Vanguard",
        isInitiallyFree = false,
        primaryColor = NeonGreenBright,
        secondaryColor = NeonRed,
        frameStyle = EmblemFrameStyle.RUNIC_CHRONO,
        symbolIcon = Icons.Outlined.Verified,
        unlockTask = "Complete 25 focused study sessions.",
        taskMetric = AvatarTaskMetric.COMPLETED_SESSIONS,
        targetValue = 25,
        motivationalText = "Twenty-five completed study sessions of elite discipline."
    ),
    ProfileAvatarItem(
        number = 50,
        id = "avatar_50_red_legend",
        title = "R.E.D Legend",
        isInitiallyFree = false,
        primaryColor = NeonRed,
        secondaryColor = NeonYellow,
        frameStyle = EmblemFrameStyle.LAUREL_SOVEREIGN,
        symbolIcon = Icons.Outlined.Stars,
        unlockTask = "Complete 20 total hours of study.",
        taskMetric = AvatarTaskMetric.TOTAL_STUDY_HOURS,
        targetValue = 20,
        motivationalText = "Twenty hours of completed study crowns you a R.E.D Legend."
    )
)

fun findAvatarById(avatarId: String): ProfileAvatarItem {
    return AVAILABLE_PROFILE_AVATARS.firstOrNull { it.id == avatarId }
        ?: AVAILABLE_PROFILE_AVATARS.first()
}

/**
 * Renders an official R.E.D TREE Emblem Badge at a locked, identical visual scale
 * matching the first R.E.D emblem badge size across all 50 avatars.
 */
@Composable
fun RedTreeOfficialEmblem(
    avatar: ProfileAvatarItem,
    isUnlocked: Boolean,
    size: Dp = STANDARD_AVATAR_EMBLEM_SIZE,
    modifier: Modifier = Modifier
) {
    val visualAlpha = if (isUnlocked) 1f else 0.72f
    val primaryAdjusted = avatar.primaryColor.copy(alpha = visualAlpha)
    val secondaryAdjusted = avatar.secondaryColor.copy(alpha = visualAlpha)

    val coreRingBrush = androidx.compose.runtime.remember(avatar.id, isUnlocked) {
        Brush.sweepGradient(
            colors = listOf(
                primaryAdjusted,
                secondaryAdjusted,
                NeonGreen.copy(alpha = visualAlpha),
                primaryAdjusted
            )
        )
    }

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            val center = Offset(this.size.width / 2f, this.size.height / 2f)
            val minDim = this.size.minDimension
            // Locked standard emblem outer & inner radii (matches the first R.E.D emblem badge size for all 50 avatars)
            val outerRadius = minDim * 0.46f
            val innerMedallionRadius = minDim * 0.38f

            // 1. Subtle outer aura glow
            drawCircle(
                color = avatar.primaryColor.copy(alpha = if (isUnlocked) 0.16f else 0.09f),
                radius = minDim * 0.49f,
                center = center
            )

            // 2. Dark obsidian emblem base plate
            drawCircle(
                color = Color(0xFF070B11),
                radius = outerRadius,
                center = center
            )
            drawCircle(
                color = Color(0xFF111823),
                radius = innerMedallionRadius,
                center = center
            )

            // 3. Unique Frame Geometry locked to outerRadius
            drawEmblemFrame(
                style = avatar.frameStyle,
                center = center,
                outerRadius = outerRadius,
                innerRadius = innerMedallionRadius,
                primary = primaryAdjusted,
                secondary = secondaryAdjusted
            )

            // 4. Inner Medallion Core Ring & Sacred Sigil Geometry
            drawCircle(
                brush = coreRingBrush,
                radius = innerMedallionRadius,
                center = center,
                style = Stroke(width = minDim * 0.026f)
            )

            drawInnerSigilPattern(
                number = avatar.number,
                center = center,
                radius = innerMedallionRadius * 0.90f,
                primary = primaryAdjusted,
                secondary = secondaryAdjusted
            )
        }

        if (avatar.usesOfficialLogoCrest) {
            Image(
                painter = painterResource(id = R.drawable.img_logo_red_tree),
                contentDescription = avatar.title,
                modifier = Modifier
                    .size(size * 0.76f)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        } else {
            Icon(
                imageVector = avatar.symbolIcon,
                contentDescription = avatar.title,
                tint = if (isUnlocked) avatar.secondaryColor else avatar.secondaryColor.copy(alpha = 0.82f),
                modifier = Modifier.size(size * 0.46f)
            )
        }
    }
}

private fun DrawScope.drawEmblemFrame(
    style: EmblemFrameStyle,
    center: Offset,
    outerRadius: Float,
    innerRadius: Float,
    primary: Color,
    secondary: Color
) {
    val strokeBold = size.minDimension * 0.036f
    val strokeFine = size.minDimension * 0.016f

    when (style) {
        EmblemFrameStyle.ROYAL_RING -> {
            drawCircle(
                color = primary,
                radius = outerRadius,
                center = center,
                style = Stroke(width = strokeBold)
            )
            for (i in 0 until 8) {
                val angle = (i * 45.0) * PI / 180.0
                val pt = Offset(
                    center.x + (outerRadius * cos(angle)).toFloat(),
                    center.y + (outerRadius * sin(angle)).toFloat()
                )
                drawCircle(color = secondary, radius = strokeBold * 0.9f, center = pt)
            }
        }
        EmblemFrameStyle.OCTAGON_BASTION -> {
            drawPolygonRing(center, outerRadius, sides = 8, rotationDeg = 22.5f, color = primary, strokeWidth = strokeBold)
            drawPolygonRing(center, (outerRadius + innerRadius) * 0.5f, sides = 8, rotationDeg = 22.5f, color = secondary.copy(alpha = 0.65f), strokeWidth = strokeFine)
        }
        EmblemFrameStyle.HEXAGON_CORE -> {
            drawPolygonRing(center, outerRadius, sides = 6, rotationDeg = 30f, color = primary, strokeWidth = strokeBold)
            drawPolygonRing(center, outerRadius * 0.92f, sides = 6, rotationDeg = 0f, color = secondary.copy(alpha = 0.7f), strokeWidth = strokeFine)
        }
        EmblemFrameStyle.STARBURST_HALO -> {
            drawCircle(color = primary.copy(alpha = 0.85f), radius = outerRadius, center = center, style = Stroke(width = strokeFine * 1.3f))
            for (i in 0 until 12) {
                val angle = (i * 30.0) * PI / 180.0
                val p1 = Offset(center.x + (innerRadius * 1.04f * cos(angle)).toFloat(), center.y + (innerRadius * 1.04f * sin(angle)).toFloat())
                val p2 = Offset(center.x + (outerRadius * cos(angle)).toFloat(), center.y + (outerRadius * sin(angle)).toFloat())
                drawLine(color = if (i % 2 == 0) secondary else primary, start = p1, end = p2, strokeWidth = strokeBold * 0.65f, cap = StrokeCap.Round)
            }
        }
        EmblemFrameStyle.LAUREL_SOVEREIGN -> {
            drawCircle(color = secondary, radius = outerRadius, center = center, style = Stroke(width = strokeBold * 0.85f))
            for (i in 0 until 16) {
                val angle = (i * 22.5) * PI / 180.0
                val rMid = (outerRadius + innerRadius) * 0.5f
                val pt = Offset(center.x + (rMid * cos(angle)).toFloat(), center.y + (rMid * sin(angle)).toFloat())
                drawCircle(color = primary, radius = strokeFine * 1.1f, center = pt)
            }
        }
        EmblemFrameStyle.DIAMOND_AEGIS -> {
            drawCircle(color = primary.copy(alpha = 0.7f), radius = outerRadius, center = center, style = Stroke(width = strokeFine * 1.2f))
            drawPolygonRing(center, outerRadius, sides = 4, rotationDeg = 45f, color = secondary, strokeWidth = strokeBold * 0.85f)
            drawPolygonRing(center, outerRadius, sides = 4, rotationDeg = 0f, color = primary, strokeWidth = strokeFine * 1.2f)
        }
        EmblemFrameStyle.RUNIC_CHRONO -> {
            drawCircle(color = primary, radius = outerRadius, center = center, style = Stroke(width = strokeBold * 0.8f))
            for (i in 0 until 24) {
                val angle = (i * 15.0) * PI / 180.0
                val innerTick = if (i % 6 == 0) innerRadius * 1.02f else outerRadius * 0.88f
                val p1 = Offset(center.x + (innerTick * cos(angle)).toFloat(), center.y + (innerTick * sin(angle)).toFloat())
                val p2 = Offset(center.x + (outerRadius * 0.98f * cos(angle)).toFloat(), center.y + (outerRadius * 0.98f * sin(angle)).toFloat())
                drawLine(
                    color = if (i % 6 == 0) secondary else primary.copy(alpha = 0.75f),
                    start = p1,
                    end = p2,
                    strokeWidth = if (i % 6 == 0) strokeBold * 0.7f else strokeFine
                )
            }
        }
        EmblemFrameStyle.CITADEL_CREST -> {
            drawPolygonRing(center, outerRadius, sides = 10, rotationDeg = 18f, color = primary, strokeWidth = strokeBold * 0.9f)
            for (i in 0 until 4) {
                val angle = (i * 90.0) * PI / 180.0
                val pt = Offset(center.x + (outerRadius * cos(angle)).toFloat(), center.y + (outerRadius * sin(angle)).toFloat())
                drawCircle(color = secondary, radius = strokeBold, center = pt)
            }
        }
    }
}

private fun DrawScope.drawPolygonRing(
    center: Offset,
    radius: Float,
    sides: Int,
    rotationDeg: Float,
    color: Color,
    strokeWidth: Float
) {
    val path = Path()
    for (i in 0 until sides) {
        val angle = ((i * 360.0 / sides) + rotationDeg) * PI / 180.0
        val x = center.x + (radius * cos(angle)).toFloat()
        val y = center.y + (radius * sin(angle)).toFloat()
        if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
    }
    path.close()
    drawPath(path = path, color = color, style = Stroke(width = strokeWidth))
}

private fun DrawScope.drawInnerSigilPattern(
    number: Int,
    center: Offset,
    radius: Float,
    primary: Color,
    secondary: Color
) {
    val fineStroke = size.minDimension * 0.012f
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                primary.copy(alpha = 0.28f),
                secondary.copy(alpha = 0.08f),
                Color.Transparent
            ),
            center = center,
            radius = radius
        ),
        radius = radius,
        center = center
    )

    val sides = 3 + (number % 5) // 3, 4, 5, 6, or 7-sided inner sigil geometry
    drawPolygonRing(
        center = center,
        radius = radius * 0.84f,
        sides = sides,
        rotationDeg = (number * 15f) % 360f,
        color = primary.copy(alpha = 0.32f),
        strokeWidth = fineStroke
    )
}
