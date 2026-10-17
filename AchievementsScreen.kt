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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.FloatingLeavesAtmosphere
import com.example.ui.components.GlassCard
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

@Composable
fun AchievementsScreen(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val version by AchievementManager.stateVersion
    val subtleQueue by AchievementManager.subtleBannerQueue
    val fullCelebrationQueue by AchievementManager.activeFullCelebrationQueue

    // Selected level tab: 0 = BRONZE, 1 = SILVER, 2 = GOLD, 3 = LEGENDARY R.E.D TREE EMERALD
    var selectedLevelIndex by rememberSaveable { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        AchievementManager.syncAndEvaluateUnlocks(context)
        AchievementManager.promotePendingCelebrationsIfFocusOff(context)
    }

    val totalStudyMinutes = remember(version) {
        AchievementManager.getCompletedStudyMinutes(context)
    }
    val currentLevel = remember(totalStudyMinutes) {
        AchievementManager.getCurrentLevelForMinutes(totalStudyMinutes)
    }
    val allEvaluated = remember(version) {
        AchievementManager.evaluateAllAchievements(context)
    }
    val pendingCelebrations = remember(version) {
        AchievementManager.getPendingCelebrationAchievements(context)
    }
    val nextMilestoneText = remember(totalStudyMinutes, allEvaluated) {
        AchievementManager.getNextMilestoneRemainingText(totalStudyMinutes, allEvaluated)
    }

    val levels = AchievementLevel.entries
    val selectedLevel = levels[selectedLevelIndex.coerceIn(0, levels.lastIndex)]
    val levelItems = remember(allEvaluated, selectedLevel) {
        allEvaluated.filter { it.definition.level == selectedLevel }
    }

    val totalUnlockedCount = remember(allEvaluated) {
        allEvaluated.count { it.isUnlocked }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BlackBase)
            .testTag("achievements_screen_root")
    ) {
        FloatingLeavesAtmosphere()

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .testTag("achievements_lazy_list"),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // 1. Top Header: ACHIEVEMENTS
            item {
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Outlined.EmojiEvents,
                            contentDescription = "Achievements",
                            tint = NeonYellow,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "ACHIEVEMENTS",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = TextPrimary,
                            letterSpacing = 1.sp
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(NeonGreen.copy(alpha = 0.14f))
                            .border(1.dp, NeonGreen.copy(alpha = 0.45f), RoundedCornerShape(12.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "$totalUnlockedCount / ${allEvaluated.size} UNLOCKED",
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = NeonGreenBright,
                            modifier = Modifier.testTag("achievements_unlocked_counter")
                        )
                    }
                }
            }

            // 2. Overall Study Progress Summary Card
            item {
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    borderColor = currentLevel.primaryColor.copy(alpha = 0.55f),
                    backgroundColor = BlackSurfaceElevated
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // TOTAL STUDY TIME
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "TOTAL STUDY TIME",
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    color = TextMuted,
                                    letterSpacing = 0.8.sp
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = AchievementManager.formatStudyDurationLong(totalStudyMinutes),
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = TextPrimary,
                                    modifier = Modifier.testTag("achievements_total_study_time")
                                )
                            }

                            // CURRENT LEVEL Pill
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "CURRENT LEVEL",
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    color = TextMuted,
                                    letterSpacing = 0.8.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(currentLevel.primaryColor.copy(alpha = 0.18f))
                                        .border(1.dp, currentLevel.primaryColor, RoundedCornerShape(14.dp))
                                        .padding(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = "${currentLevel.badgeSymbol} ${currentLevel.displayName}",
                                        fontSize = 11.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = currentLevel.secondaryColor,
                                        modifier = Modifier.testTag("achievements_current_level")
                                    )
                                }
                            }
                        }

                        // Divider
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(1.dp)
                                .background(Color.White.copy(alpha = 0.08f))
                        )

                        // Next Milestone
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Outlined.Timer,
                                    contentDescription = "Next Milestone",
                                    tint = NeonYellow,
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Next milestone:",
                                    fontSize = 11.5.sp,
                                    color = TextSecondary
                                )
                            }
                            Text(
                                text = nextMilestoneText,
                                fontSize = 11.5.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = NeonYellow,
                                modifier = Modifier.testTag("achievements_next_milestone")
                            )
                        }
                    }
                }
            }

            // 3. Pending Celebrations Notice (If achievements unlocked during Focus Mode ON)
            if (pendingCelebrations.isNotEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(NeonGreen.copy(alpha = 0.14f))
                            .border(1.2.dp, NeonGreenBright, RoundedCornerShape(16.dp))
                            .clickable { AchievementManager.launchPendingCelebrationsNow(context) }
                            .padding(horizontal = 14.dp, vertical = 12.dp)
                            .testTag("btn_view_pending_celebrations")
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.AutoAwesome,
                                    contentDescription = "Pending Celebration",
                                    tint = NeonYellow,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "${pendingCelebrations.size} Pending Achievement Celebration(s)",
                                        fontSize = 12.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                    Text(
                                        text = "Saved during Focus Mode • Tap to view full reward celebration",
                                        fontSize = 10.5.sp,
                                        color = NeonGreenBright
                                    )
                                }
                            }
                            Text(
                                text = "Celebrate →",
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = NeonYellow
                            )
                        }
                    }
                }
            }

            // 4. Four Level Category Tabs: [ 🥉 BRONZE | 🥈 SILVER | 🥇 GOLD | 💚 EMERALD ]
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    levels.forEachIndexed { index, level ->
                        val isSelected = selectedLevelIndex == index
                        val tag = when (level) {
                            AchievementLevel.BRONZE -> "tab_level_bronze"
                            AchievementLevel.SILVER -> "tab_level_silver"
                            AchievementLevel.GOLD -> "tab_level_gold"
                            AchievementLevel.LEGENDARY_EMERALD -> "tab_level_emerald"
                        }
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(38.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(
                                    if (isSelected) level.primaryColor.copy(alpha = 0.24f)
                                    else BlackSurface
                                )
                                .border(
                                    width = if (isSelected) 1.5.dp else 1.dp,
                                    color = if (isSelected) level.primaryColor else Color.White.copy(alpha = 0.10f),
                                    shape = RoundedCornerShape(14.dp)
                                )
                                .clickable { selectedLevelIndex = index }
                                .testTag(tag),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = level.shortTabTitle,
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium,
                                color = if (isSelected) level.secondaryColor else TextSecondary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }

            // 5. Active Level Banner Header
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(selectedLevel.deepCoreColor)
                        .border(1.dp, selectedLevel.primaryColor.copy(alpha = 0.45f), RoundedCornerShape(14.dp))
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "${selectedLevel.badgeSymbol} ${selectedLevel.displayName.uppercase()}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = selectedLevel.secondaryColor,
                            modifier = Modifier.testTag("active_level_section_header")
                        )
                        Text(
                            text = "Requirement: ${selectedLevel.hourRangeDescription} of completed study",
                            fontSize = 10.5.sp,
                            color = TextSecondary
                        )
                    }

                    val unlockedInTier = levelItems.count { it.isUnlocked }
                    Text(
                        text = "$unlockedInTier / ${levelItems.size}",
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = selectedLevel.primaryColor
                    )
                }
            }

            // 6. Achievement Milestone Cards for Selected Level
            items(levelItems, key = { it.definition.id }) { item ->
                AchievementMilestoneCard(
                    item = item,
                    onClickUnlocked = {
                        // Allow student to re-view celebration or view pending celebration
                        AchievementManager.activeFullCelebrationQueue.value = listOf(item.definition)
                    }
                )
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))
            }
        }

        // Subtle Banner when Focus Mode is ON
        val currentSubtle = subtleQueue.firstOrNull()
        if (currentSubtle != null) {
            SubtleFocusAchievementBanner(
                achievement = currentSubtle,
                onDismiss = { AchievementManager.dismissSubtleBanner() },
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }

        // Full Celebration Overlay when Focus Mode is OFF
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

@Composable
private fun AchievementMilestoneCard(
    item: EvaluatedAchievementItem,
    onClickUnlocked: () -> Unit
) {
    val def = item.definition
    val level = def.level
    val isUnlocked = item.isUnlocked

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(
                if (isUnlocked) level.deepCoreColor.copy(alpha = 0.92f)
                else BlackSurface.copy(alpha = 0.86f)
            )
            .border(
                width = if (isUnlocked) 1.5.dp else 1.dp,
                color = if (isUnlocked) level.primaryColor else Color.White.copy(alpha = 0.10f),
                shape = RoundedCornerShape(18.dp)
            )
            .clickable(enabled = isUnlocked, onClick = onClickUnlocked)
            .padding(14.dp)
            .testTag("achievement_card_${def.id}")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Level-Specific Metallic / Emerald Badge
            OfficialAchievementBadgeCanvas(
                achievement = def,
                isUnlocked = isUnlocked,
                size = 56.dp,
                modifier = Modifier.testTag("achievement_badge_${def.id}")
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                // Title Row + Status Badge (✓ UNLOCKED or LOCKED)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${level.badgeSymbol} ${def.title}",
                        fontSize = 14.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isUnlocked) TextPrimary else TextPrimary.copy(alpha = 0.88f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("achievement_title_${def.id}")
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                if (isUnlocked) NeonGreen.copy(alpha = 0.18f)
                                else Color.White.copy(alpha = 0.06f)
                            )
                            .border(
                                1.dp,
                                if (isUnlocked) NeonGreenBright else Color.White.copy(alpha = 0.15f),
                                RoundedCornerShape(10.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = if (isUnlocked) "✓ UNLOCKED" else "LOCKED",
                            fontSize = 9.5.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (isUnlocked) NeonGreenBright else TextMuted,
                            modifier = Modifier.testTag("achievement_status_${def.id}")
                        )
                    }
                }

                Spacer(modifier = Modifier.height(3.dp))

                // Short Description
                Text(
                    text = def.description,
                    fontSize = 11.5.sp,
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Progress Bar + Required Study Hours & Current Progress
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Progress: ${item.progressText}",
                        fontSize = 10.5.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isUnlocked) NeonGreenBright else level.secondaryColor,
                        modifier = Modifier.testTag("achievement_progress_${def.id}")
                    )

                    Text(
                        text = item.requirementLabel,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        color = TextMuted
                    )
                }

                Spacer(modifier = Modifier.height(5.dp))

                // Progress Bar Track
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(Color.White.copy(alpha = 0.10f))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(item.progressFraction.coerceIn(if (isUnlocked) 1f else 0.04f, 1f))
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(
                                Brush.horizontalGradient(
                                    if (isUnlocked) listOf(NeonGreen, NeonGreenBright)
                                    else listOf(level.primaryColor, level.secondaryColor)
                                )
                            )
                    )
                }
            }
        }
    }
}
