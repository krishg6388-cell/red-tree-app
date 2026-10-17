package com.example.data

import android.content.Context
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import com.example.ui.screens.AchievementManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar

@Entity(tableName = "study_sessions")
data class StudySessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val subject: String,
    val durationMinutes: Int,
    val timestamp: Long,
    val completed: Boolean = true
)

@Dao
interface StudySessionDao {
    @Query("SELECT * FROM study_sessions ORDER BY timestamp DESC")
    fun observeAllSessions(): Flow<List<StudySessionEntity>>

    @Query("SELECT * FROM study_sessions ORDER BY timestamp DESC")
    suspend fun getAllSessionsOnce(): List<StudySessionEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: StudySessionEntity): Long

    @Query("SELECT SUM(durationMinutes) FROM study_sessions WHERE timestamp >= :startOfDayTimestamp")
    suspend fun getTodayMinutesOnce(startOfDayTimestamp: Long): Int?

    @Query("SELECT SUM(durationMinutes) FROM study_sessions")
    suspend fun getTotalMinutesOnce(): Int?

    @Query("DELETE FROM study_sessions")
    suspend fun clearSessions()
}

data class LeaderboardRankUpEvent(
    val previousRank: Int,
    val newRank: Int,
    val timestamp: Long = System.currentTimeMillis()
)

class StudySessionRepository private constructor(context: Context) {
    private val appContext = context.applicationContext
    private val prefs = appContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val _subjectsState = mutableStateOf(loadSubjectsFromPrefs())
    val subjectsState: State<List<String>> = _subjectsState

    private val _todayMinutesState = mutableIntStateOf(prefs.getInt(KEY_TODAY_MINUTES_CACHE, 0))
    val todayMinutesState: State<Int> = _todayMinutesState

    private val _subjectBreakdownState = mutableStateOf<Map<String, Int>>(loadBreakdownFromPrefs())
    val subjectBreakdownState: State<Map<String, Int>> = _subjectBreakdownState

    private val _rankUpEvent = mutableStateOf<LeaderboardRankUpEvent?>(null)
    val rankUpEvent: State<LeaderboardRankUpEvent?> = _rankUpEvent

    private val _pendingPlannedSubject = mutableStateOf<String?>(null)
    val pendingPlannedSubject: State<String?> = _pendingPlannedSubject

    private val _pendingPlannedMinutes = mutableIntStateOf(0)
    val pendingPlannedMinutes: State<Int> = _pendingPlannedMinutes

    private val _sessionsCache = mutableStateOf<List<StudySessionEntity>>(emptyList())
    val sessionsCache: State<List<StudySessionEntity>> = _sessionsCache

    private var previousLegitimateRank = prefs.getInt(KEY_LAST_KNOWN_RANK, 32)

    init {
        scope.launch {
            runCatching {
                val dao = RedTreeDatabase.getInstance(appContext).studySessionDao()
                val allSessions = dao.getAllSessionsOnce()
                _sessionsCache.value = allSessions
                val cal = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }
                val todayDb = dao.getTodayMinutesOnce(cal.timeInMillis) ?: 0
                if (todayDb > _todayMinutesState.intValue) {
                    _todayMinutesState.intValue = todayDb
                    prefs.edit().putInt(KEY_TODAY_MINUTES_CACHE, todayDb).apply()
                }
            }
        }
    }

    fun startPlannedSession(subject: String, targetMinutes: Int) {
        val cleanSubject = subject.trim().ifBlank { "Mathematics" }
        addSubject(cleanSubject)
        _pendingPlannedSubject.value = cleanSubject
        _pendingPlannedMinutes.intValue = targetMinutes.coerceIn(1, 180)
    }

    fun consumePendingPlannedSession(): Pair<String, Int>? {
        val sub = _pendingPlannedSubject.value ?: return null
        val mins = _pendingPlannedMinutes.intValue
        _pendingPlannedSubject.value = null
        _pendingPlannedMinutes.intValue = 0
        return sub to mins
    }

    fun dismissRankUpNotification() {
        _rankUpEvent.value = null
    }

    fun addSubject(newSubject: String): Boolean {
        val clean = newSubject.trim()
        if (clean.isBlank()) return false
        val current = _subjectsState.value.toMutableList()
        if (current.any { it.equals(clean, ignoreCase = true) }) {
            return false
        }
        current.add(clean)
        _subjectsState.value = current
        saveSubjectsToPrefs(current)
        return true
    }

    fun recordCompletedSession(subject: String, durationMinutes: Int) {
        val safeMinutes = durationMinutes.coerceAtLeast(1)
        val now = System.currentTimeMillis()
        val cleanSubject = subject.trim().ifBlank { "General Study" }

        // 1. Update in-memory & SharedPreferences caches immediately
        val updatedToday = _todayMinutesState.intValue + safeMinutes
        _todayMinutesState.intValue = updatedToday
        prefs.edit().putInt(KEY_TODAY_MINUTES_CACHE, updatedToday).apply()

        val breakdown = _subjectBreakdownState.value.toMutableMap()
        breakdown[cleanSubject] = (breakdown[cleanSubject] ?: 0) + safeMinutes
        _subjectBreakdownState.value = breakdown
        saveBreakdownToPrefs(breakdown)

        val newEntity = StudySessionEntity(
            subject = cleanSubject,
            durationMinutes = safeMinutes,
            timestamp = now,
            completed = true
        )
        _sessionsCache.value = listOf(newEntity) + _sessionsCache.value

        // Ensure subject is in list
        val currentSubjects = _subjectsState.value.toMutableList()
        if (!currentSubjects.any { it.equals(cleanSubject, ignoreCase = true) }) {
            currentSubjects.add(cleanSubject)
            _subjectsState.value = currentSubjects
            saveSubjectsToPrefs(currentSubjects)
        }

        // 2. Synchronize with AchievementManager
        val prevTotalMinutes = AchievementManager.getCompletedStudyMinutes(appContext)
        AchievementManager.recordCompletedStudySession(appContext, safeMinutes)

        // 3. Synchronize with StudyPlannerManager & DailyTasksManager
        runCatching {
            StudyPlannerManager.getInstance(appContext).markPlanCompletedForSubject(cleanSubject)
            DailyTasksManager.getInstance(appContext).refreshTasks()
        }

        // 4. Save to Room database asynchronously
        scope.launch {
            runCatching {
                val dao = RedTreeDatabase.getInstance(appContext).studySessionDao()
                dao.insertSession(newEntity)
            }
        }

        // 5. Calculate Leaderboard rank change based on total completed study minutes
        val totalMinutes = prevTotalMinutes + safeMinutes
        val newRank = calculateRankFromMinutes(totalMinutes)
        val oldRank = previousLegitimateRank
        if (newRank < oldRank) {
            _rankUpEvent.value = LeaderboardRankUpEvent(previousRank = oldRank, newRank = newRank)
            previousLegitimateRank = newRank
            prefs.edit().putInt(KEY_LAST_KNOWN_RANK, newRank).apply()
        }
    }

    fun calculateRankFromMinutes(totalMinutes: Int): Int {
        // Community benchmark thresholds
        return when {
            totalMinutes >= 1200 -> 1
            totalMinutes >= 1000 -> 2
            totalMinutes >= 850 -> 3
            totalMinutes >= 750 -> 4
            totalMinutes >= 650 -> 5
            totalMinutes >= 550 -> 8
            totalMinutes >= 450 -> 12
            totalMinutes >= 380 -> 18
            totalMinutes >= 320 -> 24
            totalMinutes >= 200 -> 32
            totalMinutes >= 100 -> 45
            totalMinutes >= 40 -> 60
            else -> 75
        }
    }

    fun getTodayMinutes(): Int = _todayMinutesState.intValue

    fun getThisWeekMinutes(): Int {
        val weekly = getWeeklyData()
        return if (weekly.isEmpty()) _todayMinutesState.intValue else weekly.sumOf { it.second }.coerceAtLeast(_todayMinutesState.intValue)
    }

    fun getThisMonthMinutes(): Int {
        val monthly = getMonthlyData()
        return if (monthly.isEmpty()) _todayMinutesState.intValue else monthly.sumOf { it.second }.coerceAtLeast(_todayMinutesState.intValue)
    }

    fun getTotalStudyMinutes(): Int {
        val achTotal = AchievementManager.getCompletedStudyMinutes(appContext)
        return maxOf(achTotal, _todayMinutesState.intValue)
    }

    fun getDailyData(): List<Pair<String, Int>> {
        val total = _todayMinutesState.intValue
        if (total <= 0) return emptyList()

        val startOfDay = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        val todaySessions = _sessionsCache.value.filter { it.timestamp >= startOfDay }
        if (todaySessions.isNotEmpty()) {
            val slots = IntArray(6)
            for (session in todaySessions) {
                val cal = Calendar.getInstance().apply { timeInMillis = session.timestamp }
                val hour = cal.get(Calendar.HOUR_OF_DAY)
                val slotIndex = when {
                    hour < 8 -> 0
                    hour < 11 -> 1
                    hour < 14 -> 2
                    hour < 17 -> 3
                    hour < 20 -> 4
                    else -> 5
                }
                slots[slotIndex] += session.durationMinutes
            }
            val currentSlot = when (Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) {
                in 0..7 -> 0
                in 8..10 -> 1
                in 11..13 -> 2
                in 14..16 -> 3
                in 17..19 -> 4
                else -> 5
            }
            val sumSlots = slots.sum()
            if (sumSlots < total) {
                slots[currentSlot] += (total - sumSlots)
            }
            val labels = listOf("6am", "9am", "12pm", "3pm", "6pm", "9pm")
            return labels.mapIndexed { idx, label -> label to slots[idx] }
        }

        val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        val activeSlot = when {
            currentHour < 8 -> 0
            currentHour < 11 -> 1
            currentHour < 14 -> 2
            currentHour < 17 -> 3
            currentHour < 20 -> 4
            else -> 5
        }
        val labels = listOf("6am", "9am", "12pm", "3pm", "6pm", "9pm")
        return labels.mapIndexed { idx, label ->
            label to if (idx == activeSlot) total else 0
        }
    }

    fun getWeeklyData(): List<Pair<String, Int>> {
        val total = getTotalStudyMinutes()
        val todayMins = _todayMinutesState.intValue
        if (total <= 0 && todayMins <= 0) return emptyList()

        val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
        val calNow = Calendar.getInstance()
        val dow = calNow.get(Calendar.DAY_OF_WEEK)
        val todayIdx = when (dow) {
            Calendar.MONDAY -> 0
            Calendar.TUESDAY -> 1
            Calendar.WEDNESDAY -> 2
            Calendar.THURSDAY -> 3
            Calendar.FRIDAY -> 4
            Calendar.SATURDAY -> 5
            else -> 6
        }

        val dayBuckets = IntArray(7)
        val weekStartCal = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, -todayIdx)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val weekStartMillis = weekStartCal.timeInMillis

        val weekSessions = _sessionsCache.value.filter { it.timestamp >= weekStartMillis }
        for (session in weekSessions) {
            val c = Calendar.getInstance().apply { timeInMillis = session.timestamp }
            val idx = when (c.get(Calendar.DAY_OF_WEEK)) {
                Calendar.MONDAY -> 0
                Calendar.TUESDAY -> 1
                Calendar.WEDNESDAY -> 2
                Calendar.THURSDAY -> 3
                Calendar.FRIDAY -> 4
                Calendar.SATURDAY -> 5
                else -> 6
            }
            dayBuckets[idx] += session.durationMinutes
        }

        if (dayBuckets[todayIdx] < todayMins) {
            dayBuckets[todayIdx] = todayMins
        }

        val pastMins = (total - dayBuckets.sum()).coerceAtLeast(0)
        if (pastMins > 0 && todayIdx > 0) {
            val perPastDay = pastMins / todayIdx
            val rem = pastMins % todayIdx
            for (i in 0 until todayIdx) {
                dayBuckets[i] += perPastDay + if (i == todayIdx - 1) rem else 0
            }
        } else if (pastMins > 0 && todayIdx == 0) {
            dayBuckets[0] += pastMins
        }

        return days.mapIndexed { idx, day -> day to dayBuckets[idx] }
    }

    fun getMonthlyData(): List<Pair<String, Int>> {
        val total = getTotalStudyMinutes()
        val todayMins = _todayMinutesState.intValue
        if (total <= 0 && todayMins <= 0) return emptyList()

        val weeks = listOf("Week 1", "Week 2", "Week 3", "Week 4")
        val calNow = Calendar.getInstance()
        val dayOfMonth = calNow.get(Calendar.DAY_OF_MONTH)
        val currentWeekIdx = ((dayOfMonth - 1) / 7).coerceIn(0, 3)

        val monthStartCal = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val monthBuckets = IntArray(4)
        val monthSessions = _sessionsCache.value.filter { it.timestamp >= monthStartCal.timeInMillis }
        for (session in monthSessions) {
            val c = Calendar.getInstance().apply { timeInMillis = session.timestamp }
            val wIdx = ((c.get(Calendar.DAY_OF_MONTH) - 1) / 7).coerceIn(0, 3)
            monthBuckets[wIdx] += session.durationMinutes
        }

        if (monthBuckets[currentWeekIdx] < todayMins) {
            monthBuckets[currentWeekIdx] = todayMins
        }

        val unallocated = (total - monthBuckets.sum()).coerceAtLeast(0)
        if (unallocated > 0) {
            if (currentWeekIdx > 0) {
                val perPrevWeek = unallocated / currentWeekIdx
                val rem = unallocated % currentWeekIdx
                for (i in 0 until currentWeekIdx) {
                    monthBuckets[i] += perPrevWeek + if (i == currentWeekIdx - 1) rem else 0
                }
            } else {
                monthBuckets[0] += unallocated
            }
        }

        return weeks.mapIndexed { idx, label -> label to monthBuckets[idx] }
    }

    private fun loadSubjectsFromPrefs(): List<String> {
        val saved = prefs.getStringSet(KEY_SUBJECTS_LIST, null)
        if (saved.isNullOrEmpty()) {
            return DEFAULT_SUBJECTS
        }
        return saved.toList().sorted()
    }

    private fun saveSubjectsToPrefs(subjects: List<String>) {
        prefs.edit().putStringSet(KEY_SUBJECTS_LIST, subjects.toSet()).apply()
    }

    private fun loadBreakdownFromPrefs(): Map<String, Int> {
        val keys = prefs.getStringSet(KEY_BREAKDOWN_SUBJECTS, emptySet()) ?: emptySet()
        val result = mutableMapOf<String, Int>()
        for (sub in keys) {
            val mins = prefs.getInt("sub_mins_$sub", 0)
            if (mins > 0) result[sub] = mins
        }
        return result
    }

    private fun saveBreakdownToPrefs(breakdown: Map<String, Int>) {
        val editor = prefs.edit()
        editor.putStringSet(KEY_BREAKDOWN_SUBJECTS, breakdown.keys)
        for ((sub, mins) in breakdown) {
            editor.putInt("sub_mins_$sub", mins)
        }
        editor.apply()
    }

    companion object {
        private const val PREFS_NAME = "red_tree_study_session_prefs"
        private const val KEY_SUBJECTS_LIST = "study_subjects_list"
        private const val KEY_TODAY_MINUTES_CACHE = "today_minutes_cache"
        private const val KEY_BREAKDOWN_SUBJECTS = "breakdown_subjects"
        private const val KEY_LAST_KNOWN_RANK = "last_known_leaderboard_rank"

        val DEFAULT_SUBJECTS = listOf(
            "Mathematics",
            "Physics",
            "Computer Science",
            "Biology",
            "Literature",
            "Chemistry"
        )

        @Volatile
        private var INSTANCE: StudySessionRepository? = null

        fun getInstance(context: Context): StudySessionRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: StudySessionRepository(context).also { INSTANCE = it }
            }
        }

        fun resetForTesting(context: Context) {
            synchronized(this) {
                context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                    .edit()
                    .clear()
                    .commit()
                INSTANCE = null
            }
        }
    }
}
