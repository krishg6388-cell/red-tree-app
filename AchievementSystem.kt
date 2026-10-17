package com.example.ui.screens

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Bolt
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Diamond
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.MilitaryTech
import androidx.compose.material.icons.outlined.Psychology
import androidx.compose.material.icons.outlined.School
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material.icons.outlined.Spa
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material.icons.outlined.Verified
import androidx.compose.material.icons.outlined.WorkspacePremium
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.BlackBase
import com.example.ui.theme.BlackSurface
import com.example.ui.theme.BlackSurfaceElevated
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonGreenBright
import com.example.ui.theme.NeonOrange
import com.example.ui.theme.NeonRed
import com.example.ui.theme.NeonYellow
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * The four official R.E.D TREE Achievement Levels based on TOTAL COMPLETED STUDY HOURS.
 */
enum class AchievementLevel(
    val id: String,
    val displayName: String,
    val shortTabTitle: String,
    val badgeSymbol: String,
    val hourRangeDescription: String,
    val primaryColor: Color,
    val secondaryColor: Color,
    val deepCoreColor: Color,
    val accentGlowColor: Color
) {
    BRONZE(
        id = "BRONZE",
        displayName = "Bronze",
        shortTabTitle = "🥉 BRONZE",
        badgeSymbol = "🥉",
        hourRangeDescription = "1 – 20 Hours",
        primaryColor = Color(0xFFD98A46),
        secondaryColor = Color(0xFFF3B87D),
        deepCoreColor = Color(0xFF281508),
        accentGlowColor = Color(0xFFFF334B)
    ),
    SILVER(
        id = "SILVER",
        displayName = "Silver",
        shortTabTitle = "🥈 SILVER",
        badgeSymbol = "🥈",
        hourRangeDescription = "21 – 80 Hours",
        primaryColor = Color(0xFFCBD5E1),
        secondaryColor = Color(0xFFF8FAFC),
        deepCoreColor = Color(0xFF131B26),
        accentGlowColor = Color(0xFF38BDF8)
    ),
    GOLD(
        id = "GOLD",
        displayName = "Gold",
        shortTabTitle = "🥇 GOLD",
        badgeSymbol = "🥇",
        hourRangeDescription = "81 – 200 Hours",
        primaryColor = Color(0xFFFFD700),
        secondaryColor = Color(0xFFFFF59D),
        deepCoreColor = Color(0xFF261B04),
        accentGlowColor = Color(0xFFFF9100)
    ),
    LEGENDARY_EMERALD(
        id = "LEGENDARY_EMERALD",
        displayName = "Legendary R.E.D Tree Emerald",
        shortTabTitle = "💚 EMERALD",
        badgeSymbol = "💚",
        hourRangeDescription = "200+ Hours",
        primaryColor = Color(0xFF00E676),
        secondaryColor = Color(0xFF69F0AE),
        deepCoreColor = Color(0xFF032212),
        accentGlowColor = Color(0xFFFF1744)
    )
}

/**
 * Definition of a single official R.E.D TREE Achievement milestone.
 */
data class RedTreeAchievementDefinition(
    val id: String,
    val title: String,
    val description: String,
    val level: AchievementLevel,
    val requiredHours: Int, // 0 if based on first completed session
    val requiredMinutes: Int, // 0 if based on first completed session
    val requiresFirstSessionOnly: Boolean = false,
    val motivationalMessage: String,
    val icon: ImageVector,
    val usesOfficialLogoCrest: Boolean = false
)

/**
 * Permanent record stored whenever an achievement is unlocked.
 */
data class AchievementRewardRecord(
    val achievementId: String,
    val achievementName: String,
    val level: AchievementLevel,
    val unlockedAtTimestamp: Long,
    val requiredStudyMinutes: Int,
    val actualCompletedStudyMinutes: Int,
    val celebrationViewed: Boolean
)

/**
 * Evaluated status of an achievement for UI display.
 */
data class EvaluatedAchievementItem(
    val definition: RedTreeAchievementDefinition,
    val isUnlocked: Boolean,
    val progressFraction: Float,
    val progressText: String,
    val requirementLabel: String,
    val rewardRecord: AchievementRewardRecord?
)

/**
 * Complete catalog of R.E.D TREE Achievements across the 4 official levels:
 * 1. BRONZE (1 to 20 Hours)
 * 2. SILVER (21 to 80 Hours)
 * 3. GOLD (81 to 200 Hours)
 * 4. LEGENDARY R.E.D TREE EMERALD (Above 200 Hours)
 */
val OFFICIAL_RED_TREE_ACHIEVEMENTS: List<RedTreeAchievementDefinition> = listOf(
    // ==================== 1. BRONZE LEVEL (1 TO 20 HOURS) ====================
    RedTreeAchievementDefinition(
        id = "ach_bronze_first_session",
        title = "First Study Session",
        description = "Complete your first focused study session.",
        level = AchievementLevel.BRONZE,
        requiredHours = 0,
        requiredMinutes = 1,
        requiresFirstSessionOnly = true,
        motivationalMessage = "Your journey with R.E.D TREE has officially begun.",
        icon = Icons.Outlined.Spa
    ),
    RedTreeAchievementDefinition(
        id = "ach_bronze_1_hour",
        title = "First Hour",
        description = "Complete 1 total hour of focused study.",
        level = AchievementLevel.BRONZE,
        requiredHours = 1,
        requiredMinutes = 60,
        motivationalMessage = "Your first hour of focused study is complete.",
        icon = Icons.Outlined.Timer
    ),
    RedTreeAchievementDefinition(
        id = "ach_bronze_5_hours",
        title = "5 Hour Focus",
        description = "Complete 5 total hours of focused study.",
        level = AchievementLevel.BRONZE,
        requiredHours = 5,
        requiredMinutes = 5 * 60,
        motivationalMessage = "Five hours of deep focus—your roots are growing strong.",
        icon = Icons.Outlined.Bolt
    ),
    RedTreeAchievementDefinition(
        id = "ach_bronze_10_hours",
        title = "10 Hour Learner",
        description = "Complete 10 total hours of focused study.",
        level = AchievementLevel.BRONZE,
        requiredHours = 10,
        requiredMinutes = 10 * 60,
        motivationalMessage = "Ten hours of real study discipline completed.",
        icon = Icons.Outlined.School
    ),
    RedTreeAchievementDefinition(
        id = "ach_bronze_15_hours",
        title = "15 Hour Dedication",
        description = "Complete 15 total hours of focused study.",
        level = AchievementLevel.BRONZE,
        requiredHours = 15,
        requiredMinutes = 15 * 60,
        motivationalMessage = "Fifteen hours of unwavering dedication to your goals.",
        icon = Icons.Outlined.LocalFireDepartment
    ),
    RedTreeAchievementDefinition(
        id = "ach_bronze_20_hours",
        title = "20 Hour Foundation",
        description = "Complete 20 total hours of focused study.",
        level = AchievementLevel.BRONZE,
        requiredHours = 20,
        requiredMinutes = 20 * 60,
        motivationalMessage = "Twenty hours complete! You have built an unshakeable Bronze foundation.",
        icon = Icons.Outlined.Shield
    ),

    // ==================== 2. SILVER LEVEL (21 TO 80 HOURS) ====================
    RedTreeAchievementDefinition(
        id = "ach_silver_25_hours",
        title = "25 Hour Commitment",
        description = "Complete 25 total hours of study.",
        level = AchievementLevel.SILVER,
        requiredHours = 25,
        requiredMinutes = 25 * 60,
        motivationalMessage = "Welcome to Silver tier—25 hours of serious study commitment.",
        icon = Icons.Outlined.Verified
    ),
    RedTreeAchievementDefinition(
        id = "ach_silver_30_hours",
        title = "30 Hour Focus",
        description = "Complete 30 total hours of study.",
        level = AchievementLevel.SILVER,
        requiredHours = 30,
        requiredMinutes = 30 * 60,
        motivationalMessage = "Thirty hours of pure focus. Distractions no longer control you.",
        icon = Icons.Outlined.Psychology
    ),
    RedTreeAchievementDefinition(
        id = "ach_silver_40_hours",
        title = "40 Hour Discipline",
        description = "Complete 40 total hours of study.",
        level = AchievementLevel.SILVER,
        requiredHours = 40,
        requiredMinutes = 40 * 60,
        motivationalMessage = "Forty hours of disciplined learning—your consistency is elite.",
        icon = Icons.Outlined.Shield
    ),
    RedTreeAchievementDefinition(
        id = "ach_silver_50_hours",
        title = "50 Hour Scholar",
        description = "Complete 50 total hours of study.",
        level = AchievementLevel.SILVER,
        requiredHours = 50,
        requiredMinutes = 50 * 60,
        motivationalMessage = "Half a century of focused study hours achieved.",
        icon = Icons.Outlined.School
    ),
    RedTreeAchievementDefinition(
        id = "ach_silver_60_hours",
        title = "60 Hour Dedication",
        description = "Complete 60 total hours of study.",
        level = AchievementLevel.SILVER,
        requiredHours = 60,
        requiredMinutes = 60 * 60,
        motivationalMessage = "Sixty hours of sustained intellectual growth.",
        icon = Icons.Outlined.MilitaryTech
    ),
    RedTreeAchievementDefinition(
        id = "ach_silver_70_hours",
        title = "70 Hour Mastery",
        description = "Complete 70 total hours of study.",
        level = AchievementLevel.SILVER,
        requiredHours = 70,
        requiredMinutes = 70 * 60,
        motivationalMessage = "Seventy hours of deep mastery—Gold tier is within reach.",
        icon = Icons.Outlined.Star
    ),
    RedTreeAchievementDefinition(
        id = "ach_silver_80_hours",
        title = "80 Hour Milestone",
        description = "Complete 80 total hours of study.",
        level = AchievementLevel.SILVER,
        requiredHours = 80,
        requiredMinutes = 80 * 60,
        motivationalMessage = "Eighty hours completed! You have conquered the Silver tier.",
        icon = Icons.Outlined.WorkspacePremium
    ),

    // ==================== 3. GOLD LEVEL (81 TO 200 HOURS) ====================
    RedTreeAchievementDefinition(
        id = "ach_gold_100_hours",
        title = "100 Hour Scholar",
        description = "Complete 100 total hours of study.",
        level = AchievementLevel.GOLD,
        requiredHours = 100,
        requiredMinutes = 100 * 60,
        motivationalMessage = "100 Hours of Focused Study! You have entered the prestigious Gold Scholar ranks.",
        icon = Icons.Outlined.EmojiEvents,
        usesOfficialLogoCrest = true
    ),
    RedTreeAchievementDefinition(
        id = "ach_gold_120_hours",
        title = "120 Hour Focus",
        description = "Complete 120 total hours of study.",
        level = AchievementLevel.GOLD,
        requiredHours = 120,
        requiredMinutes = 120 * 60,
        motivationalMessage = "One hundred and twenty hours of relentless academic focus.",
        icon = Icons.Outlined.Bolt
    ),
    RedTreeAchievementDefinition(
        id = "ach_gold_150_hours",
        title = "150 Hour Discipline",
        description = "Complete 150 total hours of study.",
        level = AchievementLevel.GOLD,
        requiredHours = 150,
        requiredMinutes = 150 * 60,
        motivationalMessage = "150 hours of Golden discipline—few students ever reach this height.",
        icon = Icons.Outlined.MilitaryTech
    ),
    RedTreeAchievementDefinition(
        id = "ach_gold_175_hours",
        title = "175 Hour Mastery",
        description = "Complete 175 total hours of study.",
        level = AchievementLevel.GOLD,
        requiredHours = 175,
        requiredMinutes = 175 * 60,
        motivationalMessage = "175 hours of mastery. You stand on the threshold of R.E.D TREE Legend.",
        icon = Icons.Outlined.WorkspacePremium
    ),
    RedTreeAchievementDefinition(
        id = "ach_gold_200_hours",
        title = "200 Hour Champion",
        description = "Complete 200 total hours of study.",
        level = AchievementLevel.GOLD,
        requiredHours = 200,
        requiredMinutes = 200 * 60,
        motivationalMessage = "200 Hours Completed! You are a Gold Champion of R.E.D TREE.",
        icon = Icons.Outlined.EmojiEvents,
        usesOfficialLogoCrest = true
    ),

    // ==================== 4. LEGENDARY R.E.D TREE EMERALD (> 200 HOURS) ====================
    RedTreeAchievementDefinition(
        id = "ach_emerald_250_hours",
        title = "Emerald Seeker",
        description = "Complete 250 total hours of study.",
        level = AchievementLevel.LEGENDARY_EMERALD,
        requiredHours = 250,
        requiredMinutes = 250 * 60,
        motivationalMessage = "250 Hours! You have ascended to Legendary R.E.D Tree Emerald.",
        icon = Icons.Outlined.Diamond,
        usesOfficialLogoCrest = true
    ),
    RedTreeAchievementDefinition(
        id = "ach_emerald_300_hours",
        title = "Emerald Guardian",
        description = "Complete 300 total hours of study.",
        level = AchievementLevel.LEGENDARY_EMERALD,
        requiredHours = 300,
        requiredMinutes = 300 * 60,
        motivationalMessage = "300 Hours of focused mastery—an Emerald Guardian of knowledge.",
        icon = Icons.Outlined.Shield,
        usesOfficialLogoCrest = true
    ),
    RedTreeAchievementDefinition(
        id = "ach_emerald_400_hours",
        title = "R.E.D Tree Legend",
        description = "Complete 400 total hours of study.",
        level = AchievementLevel.LEGENDARY_EMERALD,
        requiredHours = 400,
        requiredMinutes = 400 * 60,
        motivationalMessage = "400 Hours! Your name is etched into the living R.E.D Tree.",
        icon = Icons.Outlined.AutoAwesome,
        usesOfficialLogoCrest = true
    ),
    RedTreeAchievementDefinition(
        id = "ach_emerald_500_hours",
        title = "Legendary Scholar",
        description = "Complete 500 total hours of study.",
        level = AchievementLevel.LEGENDARY_EMERALD,
        requiredHours = 500,
        requiredMinutes = 500 * 60,
        motivationalMessage = "500 Hours of pure education and focus—a true Legendary Scholar.",
        icon = Icons.Outlined.School,
        usesOfficialLogoCrest = true
    ),
    RedTreeAchievementDefinition(
        id = "ach_emerald_750_hours",
        title = "Emerald Master",
        description = "Complete 750 total hours of study.",
        level = AchievementLevel.LEGENDARY_EMERALD,
        requiredHours = 750,
        requiredMinutes = 750 * 60,
        motivationalMessage = "750 Hours! Extraordinary lifelong dedication to knowledge.",
        icon = Icons.Outlined.Diamond,
        usesOfficialLogoCrest = true
    ),
    RedTreeAchievementDefinition(
        id = "ach_emerald_1000_hours",
        title = "Legendary R.E.D Tree",
        description = "Complete 1000 total hours of study.",
        level = AchievementLevel.LEGENDARY_EMERALD,
        requiredHours = 1000,
        requiredMinutes = 1000 * 60,
        motivationalMessage = "1000 Hours Completed! You have reached the pinnacle: Legendary R.E.D Tree.",
        icon = Icons.Outlined.Spa,
        usesOfficialLogoCrest = true
    )
)

/**
 * Centralized Achievement & Study Session Engine.
 * Reads from the exact same SharedPreferences keys as the Avatar & Study Session system
 * ("red_tree_user_profile_prefs") so there are zero duplicate counters or fake study hours.
 */
object AchievementManager {
    private const val PREFS_NAME = "red_tree_user_profile_prefs"
    private const val KEY_COMPLETED_SESSIONS = "avatar_completed_study_sessions"
    private const val KEY_COMPLETED_MINUTES = "avatar_completed_study_minutes"
    private const val KEY_LONGEST_SESSION_MINUTES = "avatar_longest_session_minutes"
    private const val KEY_STREAK_DAYS = "avatar_study_streak_days"

    private const val KEY_FOCUS_MODE_ACTIVE = "focus_mode_active"
    private const val KEY_UNLOCKED_ACHIEVEMENT_IDS = "achievement_unlocked_ids_v2"
    private const val KEY_PENDING_CELEBRATION_IDS = "achievement_pending_celebration_ids_v2"
    private const val KEY_RECORD_PREFIX = "achievement_record_v2_"

    // Reactive state so Compose screens update immediately when sessions complete or celebrations trigger
    val stateVersion = mutableStateOf(0)
    val focusModeActiveState = mutableStateOf(false)
    val subtleBannerQueue = mutableStateOf<List<RedTreeAchievementDefinition>>(emptyList())
    val activeFullCelebrationQueue = mutableStateOf<List<RedTreeAchievementDefinition>>(emptyList())

    fun isFocusModeEnabled(context: Context): Boolean {
        val prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val enabled = prefs.getBoolean(KEY_FOCUS_MODE_ACTIVE, false)
        focusModeActiveState.value = enabled
        return enabled
    }

    fun setFocusModeEnabled(context: Context, enabled: Boolean) {
        val appContext = context.applicationContext
        val prefs = appContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(KEY_FOCUS_MODE_ACTIVE, enabled).commit()
        focusModeActiveState.value = enabled
        if (!enabled) {
            // Leaving Focus Mode: promote any pending celebrations so they can be viewed!
            promotePendingCelebrationsIfFocusOff(appContext)
        }
        stateVersion.value++
    }

    fun getCompletedStudyMinutes(context: Context): Int {
        val prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getInt(KEY_COMPLETED_MINUTES, 0).coerceAtLeast(0)
    }

    fun getCompletedStudySessions(context: Context): Int {
        val prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getInt(KEY_COMPLETED_SESSIONS, 0).coerceAtLeast(0)
    }

    fun getStreakDays(context: Context): Int {
        val prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getInt(KEY_STREAK_DAYS, 1).coerceAtLeast(1)
    }

    fun getLongestStreakDays(context: Context): Int {
        val prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val cur = getStreakDays(context)
        val longest = prefs.getInt("avatar_longest_study_streak_days", cur)
        return maxOf(cur, longest)
    }

    /**
     * Determines the user's current achievement level based on TOTAL COMPLETED STUDY HOURS:
     * - Bronze: 0–20 hours (1 to 20 hours tier)
     * - Silver: 21–80 hours
     * - Gold: 81–200 hours
     * - Legendary R.E.D Tree Emerald: > 200 hours
     */
    fun getCurrentLevelForMinutes(totalMinutes: Int): AchievementLevel {
        val completedHours = totalMinutes / 60
        return when {
            completedHours > 200 -> AchievementLevel.LEGENDARY_EMERALD
            completedHours >= 81 -> AchievementLevel.GOLD
            completedHours >= 21 -> AchievementLevel.SILVER
            else -> AchievementLevel.BRONZE
        }
    }

    /**
     * Records a genuinely completed study session of [sessionMinutes] duration,
     * updates the shared completed study session counters, and evaluates achievement unlocks.
     */
    fun recordCompletedStudySession(
        context: Context,
        sessionMinutes: Int,
        focusModeOverride: Boolean? = null
    ): List<RedTreeAchievementDefinition> {
        if (sessionMinutes <= 0) return emptyList()
        val appContext = context.applicationContext
        val prefs = appContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        if (focusModeOverride != null) {
            prefs.edit().putBoolean(KEY_FOCUS_MODE_ACTIVE, focusModeOverride).commit()
            focusModeActiveState.value = focusModeOverride
        }

        val newSessions = prefs.getInt(KEY_COMPLETED_SESSIONS, 0) + 1
        val newTotalMinutes = prefs.getInt(KEY_COMPLETED_MINUTES, 0) + sessionMinutes
        val prevLongest = prefs.getInt(KEY_LONGEST_SESSION_MINUTES, 0)
        val newLongest = maxOf(prevLongest, sessionMinutes)
        val newStreak = maxOf(1, prefs.getInt(KEY_STREAK_DAYS, 0))

        prefs.edit()
            .putInt(KEY_COMPLETED_SESSIONS, newSessions)
            .putInt(KEY_COMPLETED_MINUTES, newTotalMinutes)
            .putInt(KEY_LONGEST_SESSION_MINUTES, newLongest)
            .putInt(KEY_STREAK_DAYS, newStreak)
            .commit()

        return syncAndEvaluateUnlocks(appContext)
    }

    /**
     * Synchronizes with the current persisted study session metrics, unlocks any newly earned
     * achievements ONCE permanently, and routes celebrations based on whether Focus Mode is ON or OFF.
     */
    fun syncAndEvaluateUnlocks(context: Context): List<RedTreeAchievementDefinition> {
        val appContext = context.applicationContext
        val prefs = appContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        val sessions = prefs.getInt(KEY_COMPLETED_SESSIONS, 0)
        val totalMinutes = prefs.getInt(KEY_COMPLETED_MINUTES, 0)
        val focusModeOn = prefs.getBoolean(KEY_FOCUS_MODE_ACTIVE, false)
        focusModeActiveState.value = focusModeOn

        val unlockedIds = (prefs.getStringSet(KEY_UNLOCKED_ACHIEVEMENT_IDS, emptySet()) ?: emptySet()).toMutableSet()
        val pendingIds = (prefs.getStringSet(KEY_PENDING_CELEBRATION_IDS, emptySet()) ?: emptySet()).toMutableSet()

        val newlyUnlocked = mutableListOf<RedTreeAchievementDefinition>()
        val now = System.currentTimeMillis()

        for (definition in OFFICIAL_RED_TREE_ACHIEVEMENTS) {
            val qualifies = if (definition.requiresFirstSessionOnly) {
                sessions >= 1 && totalMinutes >= 1
            } else {
                totalMinutes >= definition.requiredMinutes
            }

            if (qualifies && definition.id !in unlockedIds) {
                unlockedIds.add(definition.id)
                newlyUnlocked.add(definition)

                val celebrationViewedImmediately = false
                pendingIds.add(definition.id)

                val recordString = serializeRecord(
                    AchievementRewardRecord(
                        achievementId = definition.id,
                        achievementName = definition.title,
                        level = definition.level,
                        unlockedAtTimestamp = now,
                        requiredStudyMinutes = definition.requiredMinutes,
                        actualCompletedStudyMinutes = totalMinutes,
                        celebrationViewed = celebrationViewedImmediately
                    )
                )
                prefs.edit()
                    .putString(KEY_RECORD_PREFIX + definition.id, recordString)
                    .commit()
            }
        }

        if (newlyUnlocked.isNotEmpty()) {
            prefs.edit()
                .putStringSet(KEY_UNLOCKED_ACHIEVEMENT_IDS, unlockedIds)
                .putStringSet(KEY_PENDING_CELEBRATION_IDS, pendingIds)
                .commit()

            if (focusModeOn) {
                // FOCUS MODE IS ON: Do NOT interrupt with full celebration!
                // Queue only subtle non-intrusive banner and keep full celebration in pendingIds.
                subtleBannerQueue.value = subtleBannerQueue.value + newlyUnlocked
            } else {
                // FOCUS MODE IS OFF: Show full celebration sequence immediately!
                val currentActiveIds = activeFullCelebrationQueue.value.map { it.id }.toSet()
                val toAdd = newlyUnlocked.filter { it.id !in currentActiveIds }
                activeFullCelebrationQueue.value = activeFullCelebrationQueue.value + toAdd
                playCelebrationSoundSafe()
            }
        }

        stateVersion.value++
        return newlyUnlocked
    }

    /**
     * When Focus Mode ends or the user opens the Achievements screen with Focus Mode OFF,
     * any pending unviewed celebrations can be promoted to the full celebration sequence.
     */
    fun promotePendingCelebrationsIfFocusOff(context: Context) {
        val appContext = context.applicationContext
        if (isFocusModeEnabled(appContext)) return

        val pending = getPendingCelebrationAchievements(appContext)
        if (pending.isNotEmpty()) {
            val currentIds = activeFullCelebrationQueue.value.map { it.id }.toSet()
            val newItems = pending.filter { it.id !in currentIds }
            if (newItems.isNotEmpty()) {
                activeFullCelebrationQueue.value = activeFullCelebrationQueue.value + newItems
                playCelebrationSoundSafe()
                stateVersion.value++
            }
        }
    }

    /**
     * Explicitly opens the full celebration sequence for all pending unviewed achievements
     * (e.g., when the student taps "View Pending Celebrations" after finishing a Focus Mode session).
     */
    fun launchPendingCelebrationsNow(context: Context) {
        val appContext = context.applicationContext
        val pending = getPendingCelebrationAchievements(appContext)
        if (pending.isNotEmpty()) {
            activeFullCelebrationQueue.value = pending
            subtleBannerQueue.value = emptyList()
            playCelebrationSoundSafe()
            stateVersion.value++
        }
    }

    fun dismissSubtleBanner() {
        val current = subtleBannerQueue.value
        if (current.isNotEmpty()) {
            subtleBannerQueue.value = current.drop(1)
            stateVersion.value++
        }
    }

    /**
     * Marks the currently displayed full celebration achievement as viewed in persistent storage
     * and advances to the next queued celebration (if any).
     */
    fun markCelebrationViewed(context: Context, achievementId: String) {
        val appContext = context.applicationContext
        val prefs = appContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val pendingIds = (prefs.getStringSet(KEY_PENDING_CELEBRATION_IDS, emptySet()) ?: emptySet()).toMutableSet()
        pendingIds.remove(achievementId)

        val existingRecord = getRewardRecord(appContext, achievementId)
        val editor = prefs.edit().putStringSet(KEY_PENDING_CELEBRATION_IDS, pendingIds)
        if (existingRecord != null) {
            editor.putString(
                KEY_RECORD_PREFIX + achievementId,
                serializeRecord(existingRecord.copy(celebrationViewed = true))
            )
        }
        editor.commit()

        activeFullCelebrationQueue.value = activeFullCelebrationQueue.value.filterNot { it.id == achievementId }
        subtleBannerQueue.value = subtleBannerQueue.value.filterNot { it.id == achievementId }
        stateVersion.value++
    }

    fun getPendingCelebrationAchievements(context: Context): List<RedTreeAchievementDefinition> {
        val prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val pendingIds = prefs.getStringSet(KEY_PENDING_CELEBRATION_IDS, emptySet()) ?: emptySet()
        return OFFICIAL_RED_TREE_ACHIEVEMENTS.filter { it.id in pendingIds }
    }

    fun getRewardRecord(context: Context, achievementId: String): AchievementRewardRecord? {
        val prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val raw = prefs.getString(KEY_RECORD_PREFIX + achievementId, null) ?: return null
        return deserializeRecord(raw)
    }

    fun evaluateAllAchievements(context: Context): List<EvaluatedAchievementItem> {
        val appContext = context.applicationContext
        val prefs = appContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val sessions = prefs.getInt(KEY_COMPLETED_SESSIONS, 0)
        val totalMinutes = prefs.getInt(KEY_COMPLETED_MINUTES, 0)
        val unlockedIds = prefs.getStringSet(KEY_UNLOCKED_ACHIEVEMENT_IDS, emptySet()) ?: emptySet()

        return OFFICIAL_RED_TREE_ACHIEVEMENTS.map { def ->
            val isUnlocked = def.id in unlockedIds
            val record = getRewardRecord(appContext, def.id)

            val fraction: Float
            val progressText: String
            val requirementLabel: String

            if (def.requiresFirstSessionOnly) {
                val cur = if (isUnlocked || sessions >= 1) 1 else 0
                fraction = if (cur >= 1) 1f else 0f
                progressText = "$cur / 1 session"
                requirementLabel = "1 Study Session"
            } else if (def.requiredHours == 1) {
                val curMins = if (isUnlocked) 60 else totalMinutes.coerceAtMost(60)
                fraction = (curMins.toFloat() / 60f).coerceIn(0f, 1f)
                progressText = "$curMins / 60 minutes"
                requirementLabel = "1 Hour"
            } else {
                val targetMins = def.requiredMinutes
                val curMins = if (isUnlocked) targetMins else totalMinutes.coerceAtMost(targetMins)
                fraction = (curMins.toFloat() / targetMins.toFloat()).coerceIn(0f, 1f)
                progressText = "${formatStudyDurationShort(curMins)} / ${def.requiredHours}h"
                requirementLabel = "${def.requiredHours} Hours"
            }

            EvaluatedAchievementItem(
                definition = def,
                isUnlocked = isUnlocked,
                progressFraction = fraction,
                progressText = progressText,
                requirementLabel = requirementLabel,
                rewardRecord = record
            )
        }
    }

    fun formatStudyDurationLong(totalMinutes: Int): String {
        val hours = totalMinutes / 60
        val minutes = totalMinutes % 60
        return "$hours hours $minutes minutes"
    }

    fun formatStudyDurationShort(totalMinutes: Int): String {
        val hours = totalMinutes / 60
        val minutes = totalMinutes % 60
        return when {
            hours == 0 -> "${minutes}m"
            minutes == 0 -> "${hours}h"
            else -> "${hours}h ${minutes}m"
        }
    }

    fun getNextMilestoneRemainingText(totalMinutes: Int, items: List<EvaluatedAchievementItem>): String {
        val nextLocked = items.firstOrNull { !it.isUnlocked } ?: return "All milestones unlocked!"
        if (nextLocked.definition.requiresFirstSessionOnly) {
            return "Complete 1 session to unlock ${nextLocked.definition.title}"
        }
        val remainingMinutes = (nextLocked.definition.requiredMinutes - totalMinutes).coerceAtLeast(0)
        val remHours = remainingMinutes / 60
        val remMins = remainingMinutes % 60
        return when {
            remHours > 0 && remMins > 0 -> "${remHours}h ${remMins}m remaining (${nextLocked.definition.title})"
            remHours > 0 -> "$remHours hours remaining (${nextLocked.definition.title})"
            else -> "$remMins minutes remaining (${nextLocked.definition.title})"
        }
    }

    fun resetInMemoryStateForTesting() {
        subtleBannerQueue.value = emptyList()
        activeFullCelebrationQueue.value = emptyList()
        focusModeActiveState.value = false
        stateVersion.value = 0
    }

    private fun playCelebrationSoundSafe() {
        runCatching {
            val toneGen = ToneGenerator(AudioManager.STREAM_NOTIFICATION, 75)
            toneGen.startTone(ToneGenerator.TONE_PROP_ACK, 180)
        }
    }

    private fun serializeRecord(record: AchievementRewardRecord): String {
        return listOf(
            record.achievementId,
            record.achievementName.replace("|", " "),
            record.level.name,
            record.unlockedAtTimestamp.toString(),
            record.requiredStudyMinutes.toString(),
            record.actualCompletedStudyMinutes.toString(),
            record.celebrationViewed.toString()
        ).joinToString("|")
    }

    private fun deserializeRecord(serialized: String): AchievementRewardRecord? {
        return runCatching {
            val parts = serialized.split("|")
            if (parts.size < 7) return null
            AchievementRewardRecord(
                achievementId = parts[0],
                achievementName = parts[1],
                level = AchievementLevel.valueOf(parts[2]),
                unlockedAtTimestamp = parts[3].toLong(),
                requiredStudyMinutes = parts[4].toInt(),
                actualCompletedStudyMinutes = parts[5].toInt(),
                celebrationViewed = parts[6].toBoolean()
            )
        }.getOrNull()
    }
}

/**
 * Renders the official R.E.D TREE Level-Specific Metallic & Emerald Achievement Badge:
 * - BRONZE: Burnished bronze octagonal-shield medallion with R.E.D TREE crest
 * - SILVER: Brushed platinum-silver star-shield medallion with R.E.D TREE crest
 * - GOLD: Imperial radiant gold crown-laurel medallion with R.E.D TREE crest
 * - LEGENDARY R.E.D TREE EMERALD: Deep emerald crystal & crimson R.E.D TREE logo crest
 */
@Composable
fun OfficialAchievementBadgeCanvas(
    achievement: RedTreeAchievementDefinition,
    isUnlocked: Boolean,
    size: Dp,
    modifier: Modifier = Modifier
) {
    val level = achievement.level
    val alphaFactor = if (isUnlocked) 1f else 0.55f
    val primary = level.primaryColor.copy(alpha = alphaFactor)
    val secondary = level.secondaryColor.copy(alpha = alphaFactor)
    val accent = level.accentGlowColor.copy(alpha = alphaFactor)

    val ringBrush = remember(achievement.id, isUnlocked) {
        Brush.sweepGradient(
            colors = listOf(
                primary,
                secondary,
                primary,
                accent,
                primary
            )
        )
    }

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(this.size.width / 2f, this.size.height / 2f)
            val minDim = this.size.minDimension
            val outerR = minDim * 0.46f
            val innerR = minDim * 0.35f

            // 1. Level-specific ambient prestige aura
            drawCircle(
                color = primary.copy(alpha = if (isUnlocked) 0.22f else 0.08f),
                radius = minDim * 0.49f,
                center = center
            )

            // 2. Level-specific metallic / emerald outer crest geometry
            when (level) {
                AchievementLevel.BRONZE -> {
                    // Hexagonal Burnished Bronze Shield
                    drawRegularPolygon(
                        center = center,
                        radius = outerR,
                        sides = 6,
                        rotationDeg = -90f,
                        fillColor = level.deepCoreColor,
                        strokeBrush = ringBrush,
                        strokeWidth = minDim * 0.055f
                    )
                }
                AchievementLevel.SILVER -> {
                    // 8-Pointed Platinum Silver Bastion
                    drawRegularPolygon(
                        center = center,
                        radius = outerR,
                        sides = 8,
                        rotationDeg = 22.5f,
                        fillColor = level.deepCoreColor,
                        strokeBrush = ringBrush,
                        strokeWidth = minDim * 0.058f
                    )
                    for (i in 0 until 8) {
                        val rad = (i * 45.0) * PI / 180.0
                        val pt = Offset(
                            center.x + (outerR * 0.92f * cos(rad)).toFloat(),
                            center.y + (outerR * 0.92f * sin(rad)).toFloat()
                        )
                        drawCircle(color = secondary, radius = minDim * 0.024f, center = pt)
                    }
                }
                AchievementLevel.GOLD -> {
                    // 12-Ray Imperial Gold Medallion
                    for (i in 0 until 12) {
                        val rad = (i * 30.0) * PI / 180.0
                        val p1 = Offset(
                            center.x + (innerR * 1.02f * cos(rad)).toFloat(),
                            center.y + (innerR * 1.02f * sin(rad)).toFloat()
                        )
                        val p2 = Offset(
                            center.x + (outerR * 1.05f * cos(rad)).toFloat(),
                            center.y + (outerR * 1.05f * sin(rad)).toFloat()
                        )
                        drawLine(
                            color = if (i % 2 == 0) primary else secondary,
                            start = p1,
                            end = p2,
                            strokeWidth = minDim * 0.036f,
                            cap = StrokeCap.Round
                        )
                    }
                    drawCircle(
                        color = level.deepCoreColor,
                        radius = outerR * 0.92f,
                        center = center
                    )
                    drawCircle(
                        brush = ringBrush,
                        radius = outerR * 0.92f,
                        center = center,
                        style = Stroke(width = minDim * 0.055f)
                    )
                }
                AchievementLevel.LEGENDARY_EMERALD -> {
                    // Legendary R.E.D Tree Emerald Faceted Crystal + Crimson/Gold Crown
                    for (i in 0 until 16) {
                        val rad = (i * 22.5) * PI / 180.0
                        val rOut = if (i % 2 == 0) outerR * 1.06f else outerR * 0.96f
                        val p1 = Offset(
                            center.x + (innerR * cos(rad)).toFloat(),
                            center.y + (innerR * sin(rad)).toFloat()
                        )
                        val p2 = Offset(
                            center.x + (rOut * cos(rad)).toFloat(),
                            center.y + (rOut * sin(rad)).toFloat()
                        )
                        drawLine(
                            color = when (i % 4) {
                                0 -> primary
                                1 -> NeonYellow.copy(alpha = alphaFactor)
                                2 -> secondary
                                else -> NeonRed.copy(alpha = alphaFactor)
                            },
                            start = p1,
                            end = p2,
                            strokeWidth = minDim * 0.034f,
                            cap = StrokeCap.Round
                        )
                    }
                    drawRegularPolygon(
                        center = center,
                        radius = outerR * 0.92f,
                        sides = 8,
                        rotationDeg = 22.5f,
                        fillColor = level.deepCoreColor,
                        strokeBrush = ringBrush,
                        strokeWidth = minDim * 0.06f
                    )
                }
            }

            // 3. Inner R.E.D TREE Obsidian Core Ring
            drawCircle(
                color = Color(0xFF06090E),
                radius = innerR,
                center = center
            )
            drawCircle(
                color = primary.copy(alpha = 0.85f),
                radius = innerR,
                center = center,
                style = Stroke(width = minDim * 0.025f)
            )
        }

        if (achievement.usesOfficialLogoCrest) {
            Image(
                painter = painterResource(id = R.drawable.img_logo_red_tree),
                contentDescription = achievement.title,
                modifier = Modifier
                    .size(size * 0.62f)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        } else {
            Icon(
                imageVector = achievement.icon,
                contentDescription = achievement.title,
                tint = if (isUnlocked) secondary else primary,
                modifier = Modifier.size(size * 0.42f)
            )
        }

        // Corner status badge (Checkmark if unlocked, Lock if locked)
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .size(size * 0.28f)
                .clip(CircleShape)
                .background(if (isUnlocked) level.primaryColor else Color(0xE60B0F17))
                .border(
                    1.dp,
                    if (isUnlocked) Color.Black else level.primaryColor.copy(alpha = 0.7f),
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isUnlocked) Icons.Default.Check else Icons.Outlined.Lock,
                contentDescription = if (isUnlocked) "Unlocked" else "Locked",
                tint = if (isUnlocked) Color.Black else level.primaryColor,
                modifier = Modifier.size(size * 0.16f)
            )
        }
    }
}

private fun DrawScope.drawRegularPolygon(
    center: Offset,
    radius: Float,
    sides: Int,
    rotationDeg: Float,
    fillColor: Color,
    strokeBrush: Brush,
    strokeWidth: Float
) {
    val path = Path()
    for (i in 0 until sides) {
        val angleRad = ((rotationDeg + i * (360f / sides)) * PI / 180.0)
        val x = center.x + (radius * cos(angleRad)).toFloat()
        val y = center.y + (radius * sin(angleRad)).toFloat()
        if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
    }
    path.close()
    drawPath(path = path, color = fillColor)
    drawPath(path = path, brush = strokeBrush, style = Stroke(width = strokeWidth))
}

/**
 * SUBTLE NON-INTRUSIVE NOTIFICATION BANNER (Shown ONLY when Focus Mode is ON).
 * Never covers the study timer, never plays loud sounds, and never shows balloons/ribbons
 * while the student is concentrating in Focus Mode.
 */
@Composable
fun SubtleFocusAchievementBanner(
    achievement: RedTreeAchievementDefinition,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xF20B1018))
            .border(1.dp, achievement.level.primaryColor.copy(alpha = 0.65f), RoundedCornerShape(14.dp))
            .padding(horizontal = 14.dp, vertical = 10.dp)
            .testTag("subtle_achievement_banner")
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
                OfficialAchievementBadgeCanvas(
                    achievement = achievement,
                    isUnlocked = true,
                    size = 36.dp
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "🏆 Achievement Unlocked",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeonYellow,
                        modifier = Modifier.testTag("subtle_banner_header")
                    )
                    Text(
                        text = "“${achievement.title}” • ${achievement.level.badgeSymbol} ${achievement.level.displayName}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary,
                        modifier = Modifier.testTag("subtle_banner_title")
                    )
                    Text(
                        text = "Focus Mode active • Full celebration saved for after session",
                        fontSize = 9.5.sp,
                        color = TextSecondary
                    )
                }
            }

            IconButton(
                onClick = onDismiss,
                modifier = Modifier
                    .size(26.dp)
                    .testTag("btn_dismiss_subtle_banner")
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

/**
 * FULL PREMIUM ACHIEVEMENT CELEBRATION OVERLAY (Shown when Focus Mode is OFF,
 * or when viewing queued celebrations after Focus Mode ends).
 * Includes:
 * - Festive Ribbons/Streamers, Balloons & Prestige Particles
 * - Large Official Level Badge Reveal
 * - Achievement Title & Level Pill (Bronze / Silver / Gold / Legendary R.E.D Tree Emerald)
 * - Motivational Message
 * - Sequential playback when multiple achievements are queued
 */
@Composable
fun FullAchievementCelebrationOverlay(
    achievement: RedTreeAchievementDefinition,
    queuePosition: Int = 1,
    queueTotal: Int = 1,
    onContinue: () -> Unit
) {
    val level = achievement.level

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.88f))
            .clickable(enabled = true, onClick = {})
            .padding(20.dp)
            .testTag("full_achievement_celebration_overlay"),
        contentAlignment = Alignment.Center
    ) {
        // Layer 1: Celebratory Ribbons, Streamers, Balloons & Particles Canvas
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .testTag("celebration_ribbons_balloons_canvas")
        ) {
            val w = size.width
            val h = size.height

            val colors = listOf(
                NeonRed,
                NeonGreen,
                NeonYellow,
                level.primaryColor,
                Color(0xFF00E5FF)
            )

            // 1. Celebratory Streamers / Ribbons radiating from top corners & upper arch
            for (i in 0 until 14) {
                val x = w * (0.06f + (i / 13f) * 0.88f)
                val topY = h * (0.04f + (i % 3) * 0.03f)
                val ribbonH = 44.dp.toPx() + (i % 4) * 12.dp.toPx()
                val c = colors[i % colors.size]
                drawRoundRect(
                    color = c.copy(alpha = 0.78f),
                    topLeft = Offset(x, topY),
                    size = Size(5.dp.toPx(), ribbonH),
                    cornerRadius = CornerRadius(3.dp.toPx(), 3.dp.toPx())
                )
            }

            // 2. Celebratory Balloons floating along the sides
            val balloonPositions = listOf(
                Triple(0.12f, 0.22f, NeonRed),
                Triple(0.88f, 0.20f, NeonGreen),
                Triple(0.10f, 0.46f, NeonYellow),
                Triple(0.90f, 0.44f, level.primaryColor),
                Triple(0.16f, 0.72f, NeonGreenBright),
                Triple(0.84f, 0.74f, NeonRed)
            )
            balloonPositions.forEach { (fx, fy, bColor) ->
                val bx = w * fx
                val by = h * fy
                val br = 15.dp.toPx()
                // Balloon string
                drawLine(
                    color = Color.White.copy(alpha = 0.35f),
                    start = Offset(bx, by + br),
                    end = Offset(bx, by + br + 24.dp.toPx()),
                    strokeWidth = 1.2.dp.toPx()
                )
                // Balloon body
                drawOval(
                    color = bColor.copy(alpha = 0.85f),
                    topLeft = Offset(bx - br * 0.85f, by - br),
                    size = Size(br * 1.7f, br * 2.0f)
                )
                // Balloon highlight
                drawCircle(
                    color = Color.White.copy(alpha = 0.45f),
                    radius = br * 0.22f,
                    center = Offset(bx - br * 0.3f, by - br * 0.35f)
                )
            }

            // 3. Golden & Emerald Sparkle Particles around center card
            for (i in 0 until 20) {
                val angle = (i * 18.0) * PI / 180.0
                val dist = minOf(w, h) * (0.36f + (i % 3) * 0.05f)
                val px = (w / 2f) + (dist * cos(angle)).toFloat()
                val py = (h / 2f) + (dist * sin(angle)).toFloat()
                drawCircle(
                    color = colors[i % colors.size].copy(alpha = 0.8f),
                    radius = (3 + (i % 3) * 2).dp.toPx(),
                    center = Offset(px, py)
                )
            }
        }

        // Layer 2: Prestigious R.E.D TREE Celebration Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(26.dp))
                .background(BlackSurfaceElevated)
                .border(2.dp, level.primaryColor, RoundedCornerShape(26.dp))
                .padding(horizontal = 22.dp, vertical = 24.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (queueTotal > 1) {
                    Text(
                        text = "REWARD $queuePosition OF $queueTotal",
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = NeonGreenBright,
                        letterSpacing = 1.2.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }

                Text(
                    text = "🏆 ACHIEVEMENT UNLOCKED",
                    fontSize = 14.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.ExtraBold,
                    color = NeonYellow,
                    letterSpacing = 1.4.sp,
                    modifier = Modifier.testTag("celebration_header_text")
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Large Official Level Badge Reveal
                OfficialAchievementBadgeCanvas(
                    achievement = achievement,
                    isUnlocked = true,
                    size = 112.dp,
                    modifier = Modifier.testTag("celebration_badge_${achievement.id}")
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Achievement Name
                Text(
                    text = achievement.title.uppercase(),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = TextPrimary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.testTag("celebration_achievement_title")
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Level Pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(level.primaryColor.copy(alpha = 0.18f))
                        .border(1.dp, level.primaryColor, RoundedCornerShape(16.dp))
                        .padding(horizontal = 14.dp, vertical = 5.dp)
                        .testTag("celebration_achievement_level")
                ) {
                    Text(
                        text = "${level.badgeSymbol} ${level.displayName.uppercase()}",
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = level.secondaryColor
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Motivational Message
                Text(
                    text = "“${achievement.motivationalMessage}”",
                    fontSize = 13.5.sp,
                    color = TextSecondary,
                    textAlign = TextAlign.Center,
                    lineHeight = 19.sp,
                    modifier = Modifier.testTag("celebration_motivational_message")
                )

                Spacer(modifier = Modifier.height(22.dp))

                // Continue / Next Celebration CTA
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                        .clip(RoundedCornerShape(23.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(level.primaryColor, level.secondaryColor)
                            )
                        )
                        .clickable(onClick = onContinue)
                        .testTag("btn_celebration_continue"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (queuePosition < queueTotal) "Next Celebration →" else "Continue Studying",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF070B10)
                    )
                }
            }
        }
    }
}
