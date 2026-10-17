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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.DoNotDisturbOn
import androidx.compose.material.icons.outlined.NotificationsOff
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.FloatingLeavesAtmosphere
import com.example.ui.components.GlassCard
import com.example.ui.components.NeonBadge
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

import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.compose.runtime.mutableIntStateOf

@Composable
fun FocusGuardScreen(
    onBack: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val prefs = remember {
        context.applicationContext.getSharedPreferences("red_tree_focus_guard_prefs", Context.MODE_PRIVATE)
    }

    var blockNotifications by rememberSaveable {
        mutableStateOf(prefs.getBoolean("block_notifications", true))
    }
    var focusModeActive by rememberSaveable {
        mutableStateOf(AchievementManager.isFocusModeEnabled(context))
    }
    var autoProtectDuringSession by rememberSaveable {
        mutableStateOf(prefs.getBoolean("auto_protect_session", true))
    }
    var protectionDurationMinutes by rememberSaveable {
        mutableIntStateOf(prefs.getInt("protection_duration_mins", 45))
    }

    var instagramBlocked by rememberSaveable { mutableStateOf(prefs.getBoolean("app_instagram", true)) }
    var facebookBlocked by rememberSaveable { mutableStateOf(prefs.getBoolean("app_facebook", true)) }
    var tiktokBlocked by rememberSaveable { mutableStateOf(prefs.getBoolean("app_tiktok", true)) }
    var whatsappBlocked by rememberSaveable { mutableStateOf(prefs.getBoolean("app_whatsapp", true)) }
    var gamesBlocked by rememberSaveable { mutableStateOf(prefs.getBoolean("app_games", true)) }
    var videoBlocked by rememberSaveable { mutableStateOf(prefs.getBoolean("app_video", false)) }

    val activeBlockedCount = listOf(
        instagramBlocked,
        facebookBlocked,
        tiktokBlocked,
        whatsappBlocked,
        gamesBlocked,
        videoBlocked
    ).count { it }

    fun saveGuardPrefs() {
        prefs.edit()
            .putBoolean("block_notifications", blockNotifications)
            .putBoolean("auto_protect_session", autoProtectDuringSession)
            .putInt("protection_duration_mins", protectionDurationMinutes)
            .putBoolean("app_instagram", instagramBlocked)
            .putBoolean("app_facebook", facebookBlocked)
            .putBoolean("app_tiktok", tiktokBlocked)
            .putBoolean("app_whatsapp", whatsappBlocked)
            .putBoolean("app_games", gamesBlocked)
            .putBoolean("app_video", videoBlocked)
            .apply()
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(4.dp))

                // Header with Back Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = onBack,
                            modifier = Modifier.testTag("btn_focus_guard_back")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                                contentDescription = "Back",
                                tint = NeonGreen,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Outlined.Shield,
                            contentDescription = "Focus Guard",
                            tint = NeonGreen,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Focus Guard",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }

                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Outlined.Settings,
                            contentDescription = "Settings",
                            tint = TextSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "REMOVE DISTRACTIONS • EDUCATION • FOCUS",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    color = NeonGreen,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.8.sp
                )
            }

            // Blocked Apps & Distraction Categories Card
            item {
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    borderColor = Color.White.copy(alpha = 0.12f)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Distracting Apps & Categories",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary
                            )
                            NeonBadge(text = "$activeBlockedCount ACTIVE", accentColor = NeonRed)
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Row 1: Instagram, Facebook, TikTok, WhatsApp
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            BlockedAppIcon(
                                name = "Instagram",
                                letter = "📷",
                                tint = if (instagramBlocked) Color(0xFFE1306C) else TextMuted,
                                onClick = {
                                    instagramBlocked = !instagramBlocked
                                    saveGuardPrefs()
                                }
                            )
                            BlockedAppIcon(
                                name = "Facebook",
                                letter = "f",
                                tint = if (facebookBlocked) Color(0xFF1877F2) else TextMuted,
                                onClick = {
                                    facebookBlocked = !facebookBlocked
                                    saveGuardPrefs()
                                }
                            )
                            BlockedAppIcon(
                                name = "TikTok",
                                letter = "♪",
                                tint = if (tiktokBlocked) Color(0xFF00F2FE) else TextMuted,
                                onClick = {
                                    tiktokBlocked = !tiktokBlocked
                                    saveGuardPrefs()
                                }
                            )
                            BlockedAppIcon(
                                name = "WhatsApp",
                                letter = "💬",
                                tint = if (whatsappBlocked) Color(0xFF25D366) else TextMuted,
                                onClick = {
                                    whatsappBlocked = !whatsappBlocked
                                    saveGuardPrefs()
                                }
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Row 2: Games & Video Streaming
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            BlockedAppIcon(
                                name = "Games",
                                letter = "🎮",
                                tint = if (gamesBlocked) NeonOrange else TextMuted,
                                onClick = {
                                    gamesBlocked = !gamesBlocked
                                    saveGuardPrefs()
                                }
                            )
                            BlockedAppIcon(
                                name = "Short Video",
                                letter = "▶",
                                tint = if (videoBlocked) NeonRed else TextMuted,
                                onClick = {
                                    videoBlocked = !videoBlocked
                                    saveGuardPrefs()
                                }
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Manage Apps Pill Button
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(20.dp))
                                .background(BlackSurface.copy(alpha = 0.8f))
                                .clickable {
                                    val enableAll = activeBlockedCount < 6
                                    instagramBlocked = enableAll
                                    facebookBlocked = enableAll
                                    tiktokBlocked = enableAll
                                    whatsappBlocked = enableAll
                                    gamesBlocked = enableAll
                                    videoBlocked = enableAll
                                    saveGuardPrefs()
                                }
                                .padding(horizontal = 24.dp, vertical = 8.dp)
                                .testTag("btn_manage_apps")
                        ) {
                            Text(
                                text = if (activeBlockedCount < 6) "Select All Categories" else "Clear Selection",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary
                            )
                        }
                    }
                }
            }

            // Study Duration Protection Selector
            item {
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    borderColor = NeonYellow.copy(alpha = 0.3f)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Focus Protection Duration",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary
                            )
                            Text(
                                text = "${protectionDurationMinutes} mins",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = NeonYellow
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf(25, 45, 60, 90).forEach { mins ->
                                val isSel = protectionDurationMinutes == mins
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(34.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(if (isSel) NeonYellow.copy(alpha = 0.2f) else BlackSurface)
                                        .border(
                                            1.dp,
                                            if (isSel) NeonYellow else Color.White.copy(alpha = 0.1f),
                                            RoundedCornerShape(10.dp)
                                        )
                                        .clickable {
                                            protectionDurationMinutes = mins
                                            saveGuardPrefs()
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "${mins}m",
                                        fontSize = 12.sp,
                                        fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSel) NeonYellow else TextSecondary
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Toggle 1: Block Notifications
            item {
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    borderColor = if (blockNotifications) NeonGreen.copy(alpha = 0.4f) else Color.White.copy(alpha = 0.08f)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            modifier = Modifier.weight(1f),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(NeonGreen.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.NotificationsOff,
                                    contentDescription = "Notifications",
                                    tint = NeonGreen,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Suppress App Notifications",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "Use subtle banners only while studying",
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )
                            }
                        }

                        Switch(
                            checked = blockNotifications,
                            onCheckedChange = {
                                blockNotifications = it
                                saveGuardPrefs()
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = NeonGreen,
                                uncheckedThumbColor = TextMuted,
                                uncheckedTrackColor = BlackSurface
                            ),
                            modifier = Modifier.testTag("toggle_block_notifications")
                        )
                    }
                }
            }

            // Toggle 2: Focus Mode
            item {
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    borderColor = if (focusModeActive) NeonGreen.copy(alpha = 0.4f) else Color.White.copy(alpha = 0.08f)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            modifier = Modifier.weight(1f),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(NeonGreen.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Security,
                                    contentDescription = "Focus Mode",
                                    tint = NeonGreen,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Focus Mode",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "Queues full celebrations until session ends",
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )
                            }
                        }

                        Switch(
                            checked = focusModeActive,
                            onCheckedChange = {
                                focusModeActive = it
                                AchievementManager.setFocusModeEnabled(context, it)
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = NeonGreen,
                                uncheckedThumbColor = TextMuted,
                                uncheckedTrackColor = BlackSurface
                            ),
                            modifier = Modifier.testTag("toggle_focus_mode")
                        )
                    }
                }
            }

            // Toggle 3: Auto-Protection During Active Study Session
            item {
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    borderColor = if (autoProtectDuringSession) CyberCyan.copy(alpha = 0.4f) else Color.White.copy(alpha = 0.08f)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            modifier = Modifier.weight(1f),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(CyberCyan.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.DoNotDisturbOn,
                                    contentDescription = "Auto Protect",
                                    tint = CyberCyan,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Active Session Protection",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "Automatically enable Focus Mode when timer runs",
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )
                            }
                        }

                        Switch(
                            checked = autoProtectDuringSession,
                            onCheckedChange = {
                                autoProtectDuringSession = it
                                saveGuardPrefs()
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = CyberCyan,
                                uncheckedThumbColor = TextMuted,
                                uncheckedTrackColor = BlackSurface
                            ),
                            modifier = Modifier.testTag("toggle_auto_protect_session")
                        )
                    }
                }
            }

            // Supported Android System DND / Notification Settings Flow
            item {
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    borderColor = NeonOrange.copy(alpha = 0.35f)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "ANDROID SYSTEM FOCUS & DND",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = NeonOrange,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "For OS-level blocking of third-party app notifications and interruptions, configure Android Do Not Disturb or Digital Wellbeing settings.",
                            fontSize = 11.5.sp,
                            color = TextSecondary
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF241608))
                                .border(1.dp, NeonOrange.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                                .clickable {
                                    runCatching {
                                        val intent = Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS).apply {
                                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                        }
                                        context.startActivity(intent)
                                    }.onFailure {
                                        runCatching {
                                            val fallback = Intent(Settings.ACTION_SETTINGS).apply {
                                                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                            }
                                            context.startActivity(fallback)
                                        }
                                    }
                                }
                                .padding(vertical = 10.dp)
                                .testTag("btn_system_dnd_settings"),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Open System Do Not Disturb Settings ↗",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = NeonOrange
                            )
                        }
                    }
                }
            }

            // Motivational Tagline at bottom
            item {
                Spacer(modifier = Modifier.height(10.dp))
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "No distractions.",
                        fontSize = 13.sp,
                        color = TextSecondary
                    )
                    Text(
                        text = "Only progress. 🍃",
                        fontSize = 13.sp,
                        color = NeonGreen,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
private fun BlockedAppIcon(
    name: String,
    letter: String,
    tint: Color,
    onClick: () -> Unit = {}
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(tint.copy(alpha = 0.2f))
                .border(1.dp, tint.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = letter,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = tint
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = name,
            fontSize = 10.sp,
            color = TextMuted
        )
    }
}
