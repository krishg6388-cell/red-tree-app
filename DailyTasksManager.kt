package com.example.data

import android.content.Context
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class DailyTaskItem(
    val id: String,
    val title: String,
    val description: String,
    val currentProgress: Int,
    val targetProgress: Int,
    val unit: String,
    val isCompleted: Boolean,
    val rewardXp: Int,
    val iconType: String
)

class DailyTasksManager private constructor(context: Context) {
    private val appContext = context.applicationContext
    private val prefs = appContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val _tasksState = mutableStateOf<List<DailyTaskItem>>(emptyList())
    val tasksState: State<List<DailyTaskItem>> = _tasksState

    init {
        refreshTasks()
    }

    fun getTodayDateKey(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }

    fun refreshTasks() {
        val todayKey = getTodayDateKey()
        val lastDate = prefs.getString(KEY_LAST_REFRESH_DATE, "")

        // If new day, clear completed tasks cache for new day
        if (lastDate != todayKey) {
            prefs.edit()
                .putString(KEY_LAST_REFRESH_DATE, todayKey)
                .remove(KEY_CLAIMED_TASK_IDS)
                .apply()
        }

        val studyRepo = StudySessionRepository.getInstance(appContext)
        val plannerManager = StudyPlannerManager.getInstance(appContext)
        val todayMinutes = studyRepo.todayMinutesState.value
        val completedSessions = com.example.ui.screens.AchievementManager.getCompletedStudySessions(appContext)
        val subjectsCount = studyRepo.subjectBreakdownState.value.filter { it.value > 0 }.size
        val hasCompletedPlan = plannerManager.plansState.value.any { it.isCompleted }

        val claimedSet = prefs.getStringSet(KEY_CLAIMED_TASK_IDS, emptySet()) ?: emptySet()
        val newClaimed = claimedSet.toMutableSet()

        val task1Completed = completedSessions >= 1 || todayMinutes >= 1
        val task2Completed = todayMinutes >= 20
        val task3Completed = todayMinutes >= 40
        val task4Completed = subjectsCount >= 1 || todayMinutes >= 15
        val taskPlanCompleted = hasCompletedPlan || todayMinutes >= 40
        val task5Completed = todayMinutes >= 60

        val list = listOf(
            DailyTaskItem(
                id = "task_first_session",
                title = "Complete 1 Study Session",
                description = "Start your day by finishing at least 1 study session",
                currentProgress = (if (task1Completed) 1 else 0).coerceAtMost(1),
                targetProgress = 1,
                unit = "session",
                isCompleted = task1Completed,
                rewardXp = 25,
                iconType = "session"
            ),
            DailyTaskItem(
                id = "task_focus_20",
                title = "Complete 20 minutes of study",
                description = "Dedicate 20 minutes to unbroken, distraction-free study",
                currentProgress = todayMinutes.coerceAtMost(20),
                targetProgress = 20,
                unit = "mins",
                isCompleted = task2Completed,
                rewardXp = 30,
                iconType = "timer"
            ),
            DailyTaskItem(
                id = "task_focus_40",
                title = "Complete 40 minutes of study",
                description = "Push into deep learning with 40 total minutes of focus",
                currentProgress = todayMinutes.coerceAtMost(40),
                targetProgress = 40,
                unit = "mins",
                isCompleted = task3Completed,
                rewardXp = 50,
                iconType = "deep"
            ),
            DailyTaskItem(
                id = "task_subject_scholar",
                title = "Study a selected subject",
                description = "Focus on a specific academic subject in your schedule",
                currentProgress = (if (task4Completed) 1 else 0).coerceAtMost(1),
                targetProgress = 1,
                unit = "subject",
                isCompleted = task4Completed,
                rewardXp = 35,
                iconType = "subject"
            ),
            DailyTaskItem(
                id = "task_planned_target",
                title = "Complete today's planned study target",
                description = "Finish a scheduled study block from your Study Planner",
                currentProgress = (if (taskPlanCompleted) 1 else 0).coerceAtMost(1),
                targetProgress = 1,
                unit = "plan",
                isCompleted = taskPlanCompleted,
                rewardXp = 45,
                iconType = "subject"
            ),
            DailyTaskItem(
                id = "task_focus_goal",
                title = "Maintain today's focus goal",
                description = "Reach 60 minutes to maintain your daily study target",
                currentProgress = todayMinutes.coerceAtMost(60),
                targetProgress = 60,
                unit = "mins",
                isCompleted = task5Completed,
                rewardXp = 60,
                iconType = "goal"
            )
        )

        // Automatically record completion state
        list.forEach { task ->
            if (task.isCompleted && task.id !in newClaimed) {
                newClaimed.add(task.id)
            }
        }
        if (newClaimed != claimedSet) {
            prefs.edit().putStringSet(KEY_CLAIMED_TASK_IDS, newClaimed).apply()
        }

        _tasksState.value = list
    }

    fun getCompletedCount(): Int = _tasksState.value.count { it.isCompleted }

    fun getTotalCount(): Int = _tasksState.value.size

    fun getTotalXpEarnedToday(): Int = _tasksState.value.filter { it.isCompleted }.sumOf { it.rewardXp }

    companion object {
        private const val PREFS_NAME = "red_tree_daily_tasks_prefs"
        private const val KEY_LAST_REFRESH_DATE = "last_tasks_date"
        private const val KEY_CLAIMED_TASK_IDS = "claimed_task_ids"

        @Volatile
        private var INSTANCE: DailyTasksManager? = null

        fun getInstance(context: Context): DailyTasksManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: DailyTasksManager(context).also { INSTANCE = it }
            }
        }
    }
}
