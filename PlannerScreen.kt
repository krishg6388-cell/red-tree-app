package com.example.ui.screens

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.RadioButtonUnchecked
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.StudyPlanItem
import com.example.data.StudyPlannerManager
import com.example.data.StudySessionRepository
import com.example.ui.components.FloatingLeavesAtmosphere
import com.example.ui.components.GlassCard
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

@Composable
fun PlannerScreen(
    onBack: () -> Unit = {},
    onStartPlannedSession: (subject: String, targetMinutes: Int) -> Unit = { _, _ -> },
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val plannerManager = remember { StudyPlannerManager.getInstance(context) }
    val studyRepo = remember { StudySessionRepository.getInstance(context) }

    val plans = plannerManager.plansState.value
    val allSubjects = studyRepo.subjectsState.value

    var showAddDialog by rememberSaveable { mutableStateOf(false) }
    var editingPlan by remember { mutableStateOf<StudyPlanItem?>(null) }

    // Dialog state
    var inputSubject by rememberSaveable { mutableStateOf("Mathematics") }
    var inputStartTime by rememberSaveable { mutableStateOf("7:00 PM") }
    var inputEndTime by rememberSaveable { mutableStateOf("7:40 PM") }
    var inputTargetMinutes by rememberSaveable { mutableIntStateOf(40) }
    var inputNotes by rememberSaveable { mutableStateOf("") }

    fun openNewPlanDialog() {
        inputSubject = allSubjects.firstOrNull() ?: "Mathematics"
        inputStartTime = "7:00 PM"
        inputEndTime = "7:40 PM"
        inputTargetMinutes = 40
        inputNotes = ""
        editingPlan = null
        showAddDialog = true
    }

    fun openEditPlanDialog(plan: StudyPlanItem) {
        editingPlan = plan
        inputSubject = plan.subject
        inputStartTime = plan.startTime
        inputEndTime = plan.endTime
        inputTargetMinutes = plan.targetMinutes
        inputNotes = plan.notes
        showAddDialog = true
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

                // Top Header: Back Arrow + Title + Add Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = onBack,
                            modifier = Modifier.padding(end = 2.dp).testTag("btn_planner_back")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                                contentDescription = "Back",
                                tint = NeonYellow,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "Study Planner",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "Schedule focus targets & sessions",
                                fontSize = 11.5.sp,
                                color = TextSecondary
                            )
                        }
                    }

                    // + New Plan Button
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(18.dp))
                            .background(
                                Brush.horizontalGradient(
                                    listOf(NeonYellow, NeonOrange)
                                )
                            )
                            .clickable { openNewPlanDialog() }
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                            .testTag("btn_add_study_plan"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add Plan",
                                tint = Color(0xFF201300),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Add Plan",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF201300)
                            )
                        }
                    }
                }
            }

            // Overview Summary Card
            item {
                val completedCount = plans.count { it.isCompleted }
                val totalMinutesPlanned = plans.sumOf { it.targetMinutes }

                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    borderColor = NeonYellow.copy(alpha = 0.35f)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "TODAY'S SCHEDULE",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = NeonYellow,
                                letterSpacing = 1.1.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "$completedCount of ${plans.size} Plans Completed",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "${totalMinutesPlanned}m",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = NeonGreen
                            )
                            Text(
                                text = "Total Target",
                                fontSize = 10.5.sp,
                                color = TextSecondary
                            )
                        }
                    }
                }
            }

            // Section Header
            item {
                Text(
                    text = "Planned Sessions",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }

            if (plans.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.White.copy(alpha = 0.02f))
                            .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(16.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "📅", fontSize = 26.sp)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "No planned sessions yet",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary
                            )
                            Text(
                                text = "Tap '+ Add Plan' to schedule your first focus block",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }
                    }
                }
            } else {
                items(plans) { plan ->
                    val borderColor = when {
                        plan.isCompleted -> NeonGreen.copy(alpha = 0.65f)
                        else -> NeonYellow.copy(alpha = 0.35f)
                    }

                    GlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        borderColor = borderColor,
                        backgroundColor = if (plan.isCompleted) BlackSurface.copy(alpha = 0.9f) else BlackSurface.copy(alpha = 0.7f)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    // Complete toggle checkbox
                                    IconButton(
                                        onClick = { plannerManager.togglePlanCompleted(plan.id) },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(
                                            imageVector = if (plan.isCompleted) Icons.Outlined.CheckCircle else Icons.Outlined.RadioButtonUnchecked,
                                            contentDescription = "Toggle Complete",
                                            tint = if (plan.isCompleted) NeonGreen else TextSecondary,
                                            modifier = Modifier.size(22.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(8.dp))

                                    Column {
                                        Text(
                                            text = plan.subject.uppercase(),
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (plan.isCompleted) NeonGreen else TextPrimary,
                                            letterSpacing = 0.8.sp
                                        )
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = Icons.Outlined.AccessTime,
                                                contentDescription = "Time",
                                                tint = NeonYellow,
                                                modifier = Modifier.size(12.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = "${plan.startTime} – ${plan.endTime}",
                                                fontSize = 12.sp,
                                                color = Color(0xFFCBD5E1),
                                                fontWeight = FontWeight.Medium
                                            )
                                        }
                                    }
                                }

                                // Target pill
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color(0xFF261D0A))
                                        .border(1.dp, NeonYellow.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = "Target: ${plan.targetMinutes}m",
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = NeonYellow
                                    )
                                }
                            }

                            if (plan.notes.isNotBlank()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = plan.notes,
                                    fontSize = 11.5.sp,
                                    color = TextSecondary,
                                    modifier = Modifier.padding(start = 36.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Action buttons: [ Start Focus Session ] + [ Edit ] + [ Delete ]
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 36.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Primary Start Button: launches study timer pre-populated with subject and minutes!
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(
                                            Brush.horizontalGradient(
                                                listOf(Color(0xFF00E676), Color(0xFF00C853))
                                            )
                                        )
                                        .clickable {
                                            onStartPlannedSession(plan.subject, plan.targetMinutes)
                                        }
                                        .padding(horizontal = 14.dp, vertical = 7.dp)
                                        .testTag("btn_start_plan_${plan.id}"),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Outlined.PlayArrow,
                                            contentDescription = "Start",
                                            tint = Color(0xFF032212),
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = if (plan.isCompleted) "Study Again" else "Start Session",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF032212)
                                        )
                                    }
                                }

                                Row {
                                    IconButton(
                                        onClick = { openEditPlanDialog(plan) },
                                        modifier = Modifier.size(32.dp).testTag("btn_edit_plan_${plan.id}")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Outlined.Edit,
                                            contentDescription = "Edit",
                                            tint = TextSecondary,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                    IconButton(
                                        onClick = { plannerManager.deletePlan(plan.id) },
                                        modifier = Modifier.size(32.dp).testTag("btn_delete_plan_${plan.id}")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Outlined.Delete,
                                            contentDescription = "Delete",
                                            tint = NeonRed.copy(alpha = 0.8f),
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }

        // Add / Edit Plan Dialog
        if (showAddDialog) {
            Dialog(onDismissRequest = { showAddDialog = false }) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .border(
                            1.2.dp,
                            Brush.horizontalGradient(listOf(NeonYellow, NeonOrange)),
                            RoundedCornerShape(20.dp)
                        )
                        .background(Color(0xFF0C1017))
                        .padding(20.dp)
                        .testTag("dialog_study_plan")
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(
                            text = if (editingPlan != null) "Edit Study Plan" else "Create Study Plan",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )

                        Text(
                            text = "Subject",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = NeonYellow
                        )

                        // Subject Quick Picker or custom
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf("Mathematics", "Science", "Physics", "Computer").forEach { sub ->
                                val isSel = inputSubject.equals(sub, ignoreCase = true)
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(if (isSel) NeonYellow.copy(alpha = 0.2f) else BlackSurface)
                                        .border(
                                            1.dp,
                                            if (isSel) NeonYellow else Color.White.copy(alpha = 0.1f),
                                            RoundedCornerShape(12.dp)
                                        )
                                        .clickable { inputSubject = sub }
                                        .padding(horizontal = 8.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = sub,
                                        fontSize = 11.sp,
                                        color = if (isSel) NeonYellow else TextSecondary
                                    )
                                }
                            }
                        }

                        OutlinedTextField(
                            value = inputSubject,
                            onValueChange = { inputSubject = it },
                            label = { Text("Subject Name", color = TextSecondary) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("input_plan_subject"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NeonYellow,
                                unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            )
                        )

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            OutlinedTextField(
                                value = inputStartTime,
                                onValueChange = { inputStartTime = it },
                                label = { Text("Start Time", color = TextSecondary) },
                                singleLine = true,
                                modifier = Modifier.weight(1f).testTag("input_plan_start_time"),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = NeonYellow,
                                    unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                                    focusedTextColor = TextPrimary,
                                    unfocusedTextColor = TextPrimary
                                )
                            )
                            OutlinedTextField(
                                value = inputEndTime,
                                onValueChange = { inputEndTime = it },
                                label = { Text("End Time", color = TextSecondary) },
                                singleLine = true,
                                modifier = Modifier.weight(1f).testTag("input_plan_end_time"),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = NeonYellow,
                                    unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                                    focusedTextColor = TextPrimary,
                                    unfocusedTextColor = TextPrimary
                                )
                            )
                        }

                        // Target Minutes Selector
                        Text(
                            text = "Target Duration: $inputTargetMinutes minutes",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = NeonGreen
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf(25, 30, 40, 45, 60).forEach { mins ->
                                val isSel = inputTargetMinutes == mins
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(34.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(if (isSel) NeonGreen.copy(alpha = 0.22f) else BlackSurface)
                                        .border(
                                            1.dp,
                                            if (isSel) NeonGreen else Color.White.copy(alpha = 0.1f),
                                            RoundedCornerShape(10.dp)
                                        )
                                        .clickable { inputTargetMinutes = mins },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "${mins}m",
                                        fontSize = 12.sp,
                                        fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSel) NeonGreen else TextSecondary
                                    )
                                }
                            }
                        }

                        OutlinedTextField(
                            value = inputNotes,
                            onValueChange = { inputNotes = it },
                            label = { Text("Notes (Optional)", color = TextSecondary) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("input_plan_notes"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NeonYellow,
                                unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            )
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Box(
                                modifier = Modifier
                                    .clickable { showAddDialog = false }
                                    .padding(horizontal = 14.dp, vertical = 10.dp)
                            ) {
                                Text(text = "Cancel", color = TextSecondary, fontSize = 14.sp)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(NeonYellow)
                                    .clickable {
                                        val cur = editingPlan
                                        if (cur != null) {
                                            plannerManager.updatePlan(
                                                id = cur.id,
                                                subject = inputSubject,
                                                startTime = inputStartTime,
                                                endTime = inputEndTime,
                                                targetMinutes = inputTargetMinutes,
                                                dayLabel = "Today",
                                                notes = inputNotes
                                            )
                                        } else {
                                            plannerManager.addPlan(
                                                subject = inputSubject,
                                                startTime = inputStartTime,
                                                endTime = inputEndTime,
                                                targetMinutes = inputTargetMinutes,
                                                dayLabel = "Today",
                                                notes = inputNotes
                                            )
                                        }
                                        showAddDialog = false
                                    }
                                    .padding(horizontal = 18.dp, vertical = 10.dp)
                                    .testTag("btn_save_plan"),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (editingPlan != null) "Update Plan" else "Save Plan",
                                    color = Color(0xFF1E1000),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
