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
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.Face
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.WorkspacePremium
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.FloatingLeavesAtmosphere
import com.example.ui.components.GlassCard
import com.example.ui.components.NeonGlowCard
import com.example.ui.components.SegmentedNeonTabs
import com.example.ui.theme.BlackBase
import com.example.ui.theme.BlackSurface
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonGreenBright
import com.example.ui.theme.NeonOrange
import com.example.ui.theme.NeonYellow
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

import androidx.compose.ui.platform.LocalContext
import com.example.ui.components.TreeGrowthStage

data class LeaderboardStudent(
    val rank: Int,
    val name: String,
    val xp: Int,
    val studyTimeMinutes: Int,
    val treeStageName: String,
    val achievementLevel: String,
    val isHighlighted: Boolean = false
)

private val LEADERBOARD_FILTERS = listOf("Top 5", "Top 10", "Top 100", "My Rank")

// Benchmark student seeds with consistent strictly descending XP
private val BENCHMARK_PLAYERS = listOf(
    Pair("ShadowStud..", 1250),
    Pair("FocusFreak", 1020),
    Pair("NovaMaster", 890),
    Pair("Dreamer", 780),
    Pair("StudyBae", 690),
    Pair("ZenFocus", 610),
    Pair("PixelSage", 570),
    Pair("QuantumMind", 530),
    Pair("AstralScholar", 490),
    Pair("EchoLearner", 460),
    Pair("CyberMonk", 430),
    Pair("LogicPulse", 410),
    Pair("AlphaFocus", 390),
    Pair("ChronoSage", 370),
    Pair("VividMind", 350)
)

@Composable
fun LeaderboardScreen(
    onBack: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val studyRepo = remember { com.example.data.StudySessionRepository.getInstance(context) }
    val completedMinutes = com.example.ui.screens.AchievementManager.getCompletedStudyMinutes(context)
    val userXp = 320 + (completedMinutes * 10)
    val userRank = studyRepo.calculateRankFromMinutes(completedMinutes)

    var selectedFilterIndex by remember { mutableStateOf(0) } // 0=Top 5, 1=Top 10, 2=Top 100, 3=My Rank

    fun formatStudyMins(mins: Int): String {
        val h = mins / 60
        val m = mins % 60
        return when {
            h > 0 && m > 0 -> "${h}h ${m}m"
            h > 0 -> "${h}h"
            else -> "${m}m"
        }
    }

    val userLevel = com.example.ui.screens.AchievementManager.getCurrentLevelForMinutes(completedMinutes).displayName
    val userStage = TreeGrowthStage.fromMinutes(completedMinutes).stageName

    // Generate strict descending list of leaderboard students
    val allLeaderboardStudents = remember(userRank, userXp, completedMinutes) {
        val list = mutableListOf<LeaderboardStudent>()
        var currentXpTracker = 1300
        for (r in 1..100) {
            if (r == userRank) {
                list.add(
                    LeaderboardStudent(
                        rank = r,
                        name = "Krish (You)",
                        xp = userXp,
                        studyTimeMinutes = completedMinutes,
                        treeStageName = userStage,
                        achievementLevel = userLevel,
                        isHighlighted = true
                    )
                )
            } else {
                val seedName = if (r - 1 < BENCHMARK_PLAYERS.size) {
                    BENCHMARK_PLAYERS[r - 1].first
                } else {
                    "Scholar#$r"
                }
                currentXpTracker = (currentXpTracker - (8 + (r % 7))).coerceAtLeast(40)
                val peerMins = ((currentXpTracker - 320) / 10).coerceAtLeast(15)
                val peerStage = TreeGrowthStage.fromMinutes(peerMins).stageName
                val peerLevel = com.example.ui.screens.AchievementManager.getCurrentLevelForMinutes(peerMins).displayName
                list.add(
                    LeaderboardStudent(
                        rank = r,
                        name = seedName,
                        xp = currentXpTracker,
                        studyTimeMinutes = peerMins,
                        treeStageName = peerStage,
                        achievementLevel = peerLevel,
                        isHighlighted = false
                    )
                )
            }
        }
        list
    }

    // Filter displayed entries based on selected tab
    val displayedStudents = when (selectedFilterIndex) {
        0 -> allLeaderboardStudents.take(5)
        1 -> allLeaderboardStudents.take(10)
        2 -> allLeaderboardStudents.take(100)
        else -> {
            // My Rank: Show user and adjacent peers
            val start = (userRank - 3).coerceAtLeast(0)
            val end = (userRank + 2).coerceAtMost(allLeaderboardStudents.size)
            allLeaderboardStudents.subList(start, end)
        }
    }

    val userIsInDisplayedList = displayedStudents.any { it.isHighlighted }

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
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(4.dp))

                // Top Header: Back Arrow + Leaderboard
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.padding(end = 2.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = "Back",
                            tint = NeonYellow,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Text(
                        text = "Global Leaderboard",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
            }

            // Segmented Switch: [ Top 5 | Top 10 | Top 100 | My Rank ]
            item {
                SegmentedNeonTabs(
                    items = LEADERBOARD_FILTERS,
                    selectedIndex = selectedFilterIndex,
                    onSelect = { selectedFilterIndex = it },
                    activeColor = NeonYellow,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Table Header Row: # | Player | XP
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "#",
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        color = TextMuted,
                        modifier = Modifier.width(32.dp)
                    )
                    Text(
                        text = "Player",
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        color = TextMuted,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = "XP",
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        color = TextMuted
                    )
                }
            }

            // Student Roster
            items(displayedStudents) { student ->
                val isSelf = student.isHighlighted
                val avatarColor = when (student.rank) {
                    1 -> NeonYellow
                    2 -> NeonOrange
                    3 -> NeonGreen
                    else -> TextMuted
                }

                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    borderColor = if (isSelf) NeonGreen.copy(alpha = 0.8f) else Color.White.copy(alpha = 0.08f),
                    backgroundColor = if (isSelf) BlackSurface.copy(alpha = 0.9f) else BlackSurface.copy(alpha = 0.65f)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "#${student.rank}",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelf) NeonGreen else TextSecondary,
                            modifier = Modifier.width(36.dp)
                        )

                        // Avatar icon
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(avatarColor.copy(alpha = 0.15f))
                                .border(1.dp, avatarColor.copy(alpha = 0.6f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Face,
                                contentDescription = student.name,
                                tint = avatarColor,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = student.name,
                                fontSize = 14.sp,
                                fontWeight = if (isSelf) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelf) NeonGreenBright else TextPrimary
                            )
                            Text(
                                text = "${formatStudyMins(student.studyTimeMinutes)} • ${student.treeStageName} • ${student.achievementLevel}",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }

                        Text(
                            text = "${student.xp} XP",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelf) NeonGreenBright else TextPrimary
                        )
                    }
                }
            }

            // Separated "YOUR CURRENT RANK" section when user is outside displayed range (Item 5)
            if (!userIsInDisplayedList) {
                item {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "YOUR RANK",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeonGreen,
                        letterSpacing = 1.2.sp,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    GlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        borderColor = NeonGreen.copy(alpha = 0.85f),
                        backgroundColor = BlackSurface.copy(alpha = 0.95f)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "#$userRank",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = NeonGreen,
                                modifier = Modifier.width(36.dp)
                            )

                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(NeonGreen.copy(alpha = 0.2f))
                                    .border(1.dp, NeonGreen, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Person,
                                    contentDescription = "You",
                                    tint = NeonGreen,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Krish (You)",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NeonGreenBright
                                )
                                Text(
                                    text = "${formatStudyMins(completedMinutes)} completed • $userStage • $userLevel",
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )
                            }

                            Text(
                                text = "$userXp XP",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = NeonGreenBright
                            )
                        }
                    }
                }
            }

            // Bottom Pinned Standing Card (Screen 7 reference)
            item {
                Spacer(modifier = Modifier.height(4.dp))
                NeonGlowCard(
                    accentColor = NeonYellow,
                    modifier = Modifier.fillMaxWidth(),
                    glowIntensity = 0.35f
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(NeonYellow.copy(alpha = 0.2f))
                                    .border(1.dp, NeonYellow, RoundedCornerShape(12.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.EmojiEvents,
                                    contentDescription = "Trophy",
                                    tint = NeonYellow,
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column {
                                Text(
                                    text = "Your Global Rank",
                                    color = TextSecondary,
                                    fontSize = 11.sp
                                )
                                Text(
                                    text = "#$userRank",
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NeonYellow
                                )
                            }
                        }

                        Text(
                            text = "Keep focusing\nto rise!",
                            fontSize = 12.sp,
                            color = NeonGreen,
                            fontWeight = FontWeight.SemiBold,
                            lineHeight = 16.sp
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}
