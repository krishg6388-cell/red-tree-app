package com.example.data

import android.content.Context
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

data class StudyPlanItem(
    val id: String = UUID.randomUUID().toString(),
    val subject: String,
    val startTime: String,
    val endTime: String,
    val targetMinutes: Int,
    val dayLabel: String = "Today",
    val notes: String = "",
    val isCompleted: Boolean = false
)

class StudyPlannerManager private constructor(context: Context) {
    private val appContext = context.applicationContext
    private val prefs = appContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val _plansState = mutableStateOf<List<StudyPlanItem>>(loadPlansFromPrefs())
    val plansState: State<List<StudyPlanItem>> = _plansState

    private fun loadPlansFromPrefs(): List<StudyPlanItem> {
        val raw = prefs.getString(KEY_PLANS_JSON, null)
        if (raw.isNullOrBlank()) {
            return listOf(
                StudyPlanItem(
                    id = "plan_math_sample",
                    subject = "Mathematics",
                    startTime = "7:00 PM",
                    endTime = "7:40 PM",
                    targetMinutes = 40,
                    dayLabel = "Today",
                    notes = "Calculus problem set & review",
                    isCompleted = false
                ),
                StudyPlanItem(
                    id = "plan_science_sample",
                    subject = "Science",
                    startTime = "8:00 PM",
                    endTime = "8:45 PM",
                    targetMinutes = 45,
                    dayLabel = "Today",
                    notes = "Thermodynamics concepts & formulas",
                    isCompleted = false
                )
            )
        }

        return runCatching {
            val jsonArray = JSONArray(raw)
            val list = mutableListOf<StudyPlanItem>()
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                list.add(
                    StudyPlanItem(
                        id = obj.getString("id"),
                        subject = obj.getString("subject"),
                        startTime = obj.getString("startTime"),
                        endTime = obj.getString("endTime"),
                        targetMinutes = obj.getInt("targetMinutes"),
                        dayLabel = obj.optString("dayLabel", "Today"),
                        notes = obj.optString("notes", ""),
                        isCompleted = obj.optBoolean("isCompleted", false)
                    )
                )
            }
            list
        }.getOrElse { emptyList() }
    }

    private fun savePlansToPrefs(plans: List<StudyPlanItem>) {
        val jsonArray = JSONArray()
        plans.forEach { plan ->
            val obj = JSONObject().apply {
                put("id", plan.id)
                put("subject", plan.subject)
                put("startTime", plan.startTime)
                put("endTime", plan.endTime)
                put("targetMinutes", plan.targetMinutes)
                put("dayLabel", plan.dayLabel)
                put("notes", plan.notes)
                put("isCompleted", plan.isCompleted)
            }
            jsonArray.put(obj)
        }
        prefs.edit().putString(KEY_PLANS_JSON, jsonArray.toString()).apply()
    }

    fun addPlan(
        subject: String,
        startTime: String,
        endTime: String,
        targetMinutes: Int,
        dayLabel: String = "Today",
        notes: String = ""
    ): StudyPlanItem {
        val newPlan = StudyPlanItem(
            id = UUID.randomUUID().toString(),
            subject = subject.trim().ifBlank { "General Study" },
            startTime = startTime.trim().ifBlank { "6:00 PM" },
            endTime = endTime.trim().ifBlank { "6:45 PM" },
            targetMinutes = targetMinutes.coerceIn(5, 180),
            dayLabel = dayLabel.trim().ifBlank { "Today" },
            notes = notes.trim(),
            isCompleted = false
        )
        val current = _plansState.value.toMutableList()
        current.add(newPlan)
        _plansState.value = current
        savePlansToPrefs(current)

        // Ensure subject is in global list
        StudySessionRepository.getInstance(appContext).addSubject(newPlan.subject)
        return newPlan
    }

    fun updatePlan(
        id: String,
        subject: String,
        startTime: String,
        endTime: String,
        targetMinutes: Int,
        dayLabel: String,
        notes: String
    ) {
        val current = _plansState.value.map { plan ->
            if (plan.id == id) {
                plan.copy(
                    subject = subject.trim().ifBlank { plan.subject },
                    startTime = startTime.trim().ifBlank { plan.startTime },
                    endTime = endTime.trim().ifBlank { plan.endTime },
                    targetMinutes = targetMinutes.coerceIn(5, 180),
                    dayLabel = dayLabel.trim().ifBlank { plan.dayLabel },
                    notes = notes.trim()
                )
            } else plan
        }
        _plansState.value = current
        savePlansToPrefs(current)
    }

    fun deletePlan(id: String) {
        val current = _plansState.value.filterNot { it.id == id }
        _plansState.value = current
        savePlansToPrefs(current)
    }

    fun togglePlanCompleted(id: String) {
        val current = _plansState.value.map { plan ->
            if (plan.id == id) {
                plan.copy(isCompleted = !plan.isCompleted)
            } else plan
        }
        _plansState.value = current
        savePlansToPrefs(current)
        runCatching {
            DailyTasksManager.getInstance(appContext).refreshTasks()
        }
    }

    fun markPlanCompletedForSubject(subject: String) {
        val current = _plansState.value.map { plan ->
            if (plan.subject.equals(subject, ignoreCase = true) && !plan.isCompleted) {
                plan.copy(isCompleted = true)
            } else plan
        }
        _plansState.value = current
        savePlansToPrefs(current)
        runCatching {
            DailyTasksManager.getInstance(appContext).refreshTasks()
        }
    }

    companion object {
        private const val PREFS_NAME = "red_tree_study_planner_prefs"
        private const val KEY_PLANS_JSON = "study_plans_json"

        @Volatile
        private var INSTANCE: StudyPlannerManager? = null

        fun getInstance(context: Context): StudyPlannerManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: StudyPlannerManager(context).also { INSTANCE = it }
            }
        }
    }
}
