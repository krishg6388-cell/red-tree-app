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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Face
import androidx.compose.material.icons.outlined.NotificationsActive
import androidx.compose.material.icons.outlined.Pets
import androidx.compose.material.icons.outlined.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.CompanionCharacter
import com.example.data.CompanionManager
import com.example.ui.components.FloatingLeavesAtmosphere
import com.example.ui.components.ForestCompanionArtwork
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
fun CompanionSettingsScreen(
    onBack: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val companionManager = remember { CompanionManager.getInstance(context) }
    val config by companionManager.configState

    var showNameDialog by rememberSaveable { mutableStateOf(false) }
    var nameInput by rememberSaveable { mutableStateOf(config.customName) }

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
                .padding(horizontal = 20.dp)
                .testTag("companion_settings_list"),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(4.dp))

                // Top Header: Back Arrow + Title
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = onBack,
                            modifier = Modifier.padding(end = 4.dp).testTag("btn_companion_settings_back")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                                contentDescription = "Back",
                                tint = NeonGreen,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Text(
                            text = "Study Companion",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }

                    // Active Status Badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (config.isEnabled) NeonGreen.copy(alpha = 0.15f) else Color.White.copy(alpha = 0.08f))
                            .border(1.dp, if (config.isEnabled) NeonGreen else TextMuted, RoundedCornerShape(12.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = if (config.isEnabled) "ACTIVE" else "DISABLED",
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = if (config.isEnabled) NeonGreen else TextMuted
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "A little friend for your big dreams. Your Companion stays with you during your study journey, encouraging focus and celebrating growth.",
                    fontSize = 12.5.sp,
                    color = TextSecondary,
                    lineHeight = 17.sp
                )
            }

            // Master Toggle Card
            item {
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    borderGlowBrush = Brush.horizontalGradient(
                        listOf(
                            if (config.isEnabled) NeonGreen.copy(alpha = 0.6f) else Color.White.copy(alpha = 0.15f),
                            if (config.isEnabled) NeonYellow.copy(alpha = 0.6f) else Color.White.copy(alpha = 0.15f)
                        )
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (config.isEnabled) NeonGreen.copy(alpha = 0.2f) else Color.White.copy(alpha = 0.08f)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Pets,
                                    contentDescription = "Companion",
                                    tint = if (config.isEnabled) NeonGreen else TextMuted,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Enable Companion",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextPrimary
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = if (config.isEnabled) "Floating companion active on screen" else "Completely inactive, invisible & silent",
                                    fontSize = 11.5.sp,
                                    color = TextSecondary
                                )
                            }
                        }

                        Switch(
                            checked = config.isEnabled,
                            onCheckedChange = { companionManager.setCompanionEnabled(it) },
                            modifier = Modifier.testTag("switch_companion_master"),
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color(0xFF032212),
                                checkedTrackColor = NeonGreen,
                                uncheckedThumbColor = TextMuted,
                                uncheckedTrackColor = BlackSurface
                            )
                        )
                    }
                }
            }

            // Character Choice Section (Matching Reference Screen Layout: Male vs Female)
            item {
                Text(
                    text = "CHOOSE YOUR COMPANION",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = NeonYellow,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    CompanionCharacter.entries.forEach { character ->
                        val isSelected = config.selectedCharacter == character
                        val auraColor = Color(character.auraColorHex)

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(18.dp))
                                .border(
                                    width = if (isSelected) 1.8.dp else 1.dp,
                                    color = if (isSelected) auraColor else Color.White.copy(alpha = 0.12f),
                                    shape = RoundedCornerShape(18.dp)
                                )
                                .background(if (isSelected) Color(0xF20F1B24) else Color(0x990A0E15))
                                .clickable {
                                    companionManager.selectCharacter(character)
                                }
                                .padding(12.dp)
                                .testTag("card_companion_${character.id}"),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                // Selected badge indicator
                                Box(
                                    modifier = Modifier.fillMaxWidth(),
                                    contentAlignment = Alignment.TopEnd
                                ) {
                                    if (isSelected) {
                                        Box(
                                            modifier = Modifier
                                                .size(20.dp)
                                                .clip(CircleShape)
                                                .background(auraColor),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = "Selected",
                                                tint = Color.Black,
                                                modifier = Modifier.size(13.dp)
                                            )
                                        }
                                    }
                                }

                                // Character Artwork Preview
                                ForestCompanionArtwork(
                                    character = character,
                                    size = 105.dp
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = character.displayName,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) auraColor else TextPrimary
                                )

                                Spacer(modifier = Modifier.height(2.dp))

                                Text(
                                    text = character.genderLabel,
                                    fontSize = 10.5.sp,
                                    color = TextSecondary
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color.White.copy(alpha = 0.06f))
                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = character.titleBadge,
                                        fontSize = 9.sp,
                                        color = Color(character.accentColorHex),
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                // Selection Button
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(if (isSelected) auraColor else Color.White.copy(alpha = 0.08f))
                                        .padding(vertical = 7.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = if (isSelected) "Active" else "Choose",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) Color(0xFF04180A) else TextPrimary
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Companion Name Customization Card
            item {
                Text(
                    text = "NAME YOUR COMPANION",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = NeonYellow,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(8.dp))

                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Companion Name",
                                fontSize = 12.sp,
                                color = TextSecondary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = config.customName,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Your companion uses this name while greeting you.",
                                fontSize = 11.sp,
                                color = TextMuted
                            )
                        }

                        IconButton(
                            onClick = {
                                nameInput = config.customName
                                showNameDialog = true
                            },
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(NeonGreen.copy(alpha = 0.15f))
                                .testTag("btn_edit_companion_name")
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Edit,
                                contentDescription = "Edit Name",
                                tint = NeonGreen,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            // Audio & Interaction Preferences Card
            item {
                Text(
                    text = "BEHAVIOUR & FOCUS SETTINGS",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = NeonYellow,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(8.dp))

                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // Sound effects toggle
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.VolumeUp,
                                    contentDescription = "Sound",
                                    tint = NeonYellow,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "Gentle Companion Sounds",
                                        fontSize = 13.5.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = TextPrimary
                                    )
                                    Text(
                                        text = "Subtle chimes on milestones (auto-muted in Focus Mode)",
                                        fontSize = 11.sp,
                                        color = TextSecondary
                                    )
                                }
                            }
                            Switch(
                                checked = config.soundEffectsEnabled,
                                onCheckedChange = { companionManager.setSoundEffectsEnabled(it) },
                                modifier = Modifier.testTag("switch_companion_sound"),
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color(0xFF032212),
                                    checkedTrackColor = NeonGreen
                                )
                            )
                        }

                        // Focus Mode rules explanation
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.Black.copy(alpha = 0.35f))
                                .padding(12.dp)
                        ) {
                            Column {
                                Text(
                                    text = "🛡️ Focus Mode Protection",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = CyberCyan
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "During Focus Mode or active study sessions, your companion stays quiet and non-distracting. No loud sounds or intrusive popups will interrupt your work.",
                                    fontSize = 11.sp,
                                    color = TextSecondary,
                                    lineHeight = 15.sp
                                )
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        // Custom Name Dialog
        if (showNameDialog) {
            Dialog(onDismissRequest = { showNameDialog = false }) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .border(
                            1.5.dp,
                            Brush.horizontalGradient(listOf(NeonGreen, NeonYellow)),
                            RoundedCornerShape(20.dp)
                        )
                        .background(Color(0xFF0C1017))
                        .padding(20.dp)
                        .testTag("dialog_companion_name")
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        Text(
                            text = "Name Your Companion",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Give your companion a custom name. You can change this at any time.",
                            fontSize = 12.sp,
                            color = Color(0xFF94A3B8)
                        )
                        OutlinedTextField(
                            value = nameInput,
                            onValueChange = { nameInput = it.take(20) },
                            singleLine = true,
                            placeholder = { Text("e.g. Cutu, Sparky, Leafy...", color = Color(0xFF64748B)) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_companion_name"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NeonGreen,
                                unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            )
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Box(
                                modifier = Modifier
                                    .clickable { showNameDialog = false }
                                    .padding(horizontal = 14.dp, vertical = 10.dp)
                            ) {
                                Text(text = "Cancel", color = Color(0xFF94A3B8), fontSize = 14.sp)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(NeonGreen)
                                    .clickable {
                                        companionManager.updateCustomName(nameInput)
                                        showNameDialog = false
                                    }
                                    .padding(horizontal = 18.dp, vertical = 10.dp)
                                    .testTag("btn_save_companion_name")
                            ) {
                                Text(
                                    text = "Save Name",
                                    color = Color(0xFF031A0B),
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
