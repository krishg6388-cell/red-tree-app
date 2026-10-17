package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.ArrowForwardIos
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.AlternateEmail
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.Face
import androidx.compose.material.icons.outlined.HelpOutline
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.PhotoLibrary
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material.icons.outlined.Spa
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.UserProfileEntity
import com.example.data.UserProfileRepository
import com.example.ui.components.CompanionPlaceholderCard
import com.example.ui.components.FloatingLeavesAtmosphere
import com.example.ui.components.GlassCard
import com.example.ui.navigation.ScreenDestination
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

data class ProfileMenuItem(
    val title: String,
    val icon: ImageVector,
    val iconColor: Color,
    val destination: ScreenDestination? = null,
    val isDestructive: Boolean = false
)

private val PROFILE_MENU_ITEMS = listOf(
    ProfileMenuItem("Edit Profile", Icons.Outlined.AccountCircle, NeonGreen),
    ProfileMenuItem("Change Avatar", Icons.Outlined.Face, NeonYellow),
    ProfileMenuItem("Notification Settings", Icons.Outlined.Notifications, NeonOrange),
    ProfileMenuItem("App Restrictions", Icons.Outlined.Security, NeonRed, destination = ScreenDestination.FOCUS_GUARD),
    ProfileMenuItem("Tree Progression", Icons.Outlined.Spa, NeonGreen, destination = ScreenDestination.TREE_GROWTH),
    ProfileMenuItem("Help & Support", Icons.Outlined.HelpOutline, NeonYellow),
    ProfileMenuItem("Log Out", Icons.Outlined.Logout, NeonRed, destination = ScreenDestination.AUTH, isDestructive = true)
)

@Composable
fun ProfileScreen(
    onNavigateTo: (ScreenDestination) -> Unit = {},
    onBack: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val repository = remember(context) { UserProfileRepository.getInstance(context) }
    val flowProfile by repository.profileState.collectAsStateWithLifecycle()
    val composeProfile by repository.composeProfileState
    val userProfile = if (composeProfile != flowProfile) composeProfile else flowProfile

    var showEditProfileDialog by rememberSaveable { mutableStateOf(false) }
    var showChangeAvatarDialog by rememberSaveable { mutableStateOf(false) }
    var showNotificationSettingsDialog by rememberSaveable { mutableStateOf(false) }
    var showHelpSupportDialog by rememberSaveable { mutableStateOf(false) }
    val menuItems = PROFILE_MENU_ITEMS

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
                .testTag("profile_lazy_list"),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(4.dp))

                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = "Back",
                            tint = NeonYellow,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Text(
                        text = "Profile",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
            }

            // User Identity Card (Screen 8 reference)
            item {
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    borderColor = NeonGreen.copy(alpha = 0.45f)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Displays Custom Profile Picture if set, otherwise displays the selected Avatar
                        ProfileIdentityImage(
                            customProfilePictureUri = userProfile.customProfilePictureUri,
                            selectedAvatarId = userProfile.selectedAvatarId,
                            size = 60.dp,
                            modifier = Modifier.testTag("profile_header_image")
                        )

                        Spacer(modifier = Modifier.width(16.dp))

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = userProfile.name,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary,
                                    modifier = Modifier.testTag("profile_display_name")
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(text = "🍃", fontSize = 16.sp)
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "@${userProfile.username}",
                                fontSize = 12.sp,
                                color = TextSecondary,
                                fontFamily = FontFamily.Monospace,
                                modifier = Modifier.testTag("profile_display_username")
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = userProfile.statusTitle,
                                fontSize = 12.sp,
                                color = NeonYellow,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.testTag("profile_display_status")
                            )
                        }
                    }
                }
            }

            // 3 Stats Cards: Total Focus (12.5h), Current Streak (3d), Total XP (320)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ProfileStatBox(
                        title = "Total Focus",
                        value = "12.5h",
                        accentColor = NeonGreen,
                        modifier = Modifier.weight(1f)
                    )
                    ProfileStatBox(
                        title = "Current Streak",
                        value = "3d",
                        accentColor = NeonOrange,
                        modifier = Modifier.weight(1f)
                    )
                    ProfileStatBox(
                        title = "Total XP",
                        value = "320",
                        accentColor = NeonYellow,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Menu List
            items(menuItems) { menuItem ->
                val itemTag = when (menuItem.title) {
                    "Edit Profile" -> "btn_edit_profile"
                    "Change Avatar" -> "btn_change_avatar"
                    "Notification Settings" -> "menu_notification_settings"
                    else -> "menu_${menuItem.title.lowercase().replace(" ", "_")}"
                }
                GlassCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag(itemTag),
                    borderColor = Color.White.copy(alpha = 0.08f),
                    onClick = {
                        when {
                            menuItem.title == "Edit Profile" -> {
                                showEditProfileDialog = true
                            }
                            menuItem.title == "Change Avatar" -> {
                                showChangeAvatarDialog = true
                            }
                            menuItem.title == "Notification Settings" -> {
                                showNotificationSettingsDialog = true
                            }
                            menuItem.title == "Help & Support" -> {
                                showHelpSupportDialog = true
                            }
                            menuItem.destination != null -> {
                                onNavigateTo(menuItem.destination)
                            }
                        }
                    }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = menuItem.icon,
                                contentDescription = menuItem.title,
                                tint = menuItem.iconColor,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(14.dp))
                            Text(
                                text = menuItem.title,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = if (menuItem.isDestructive) NeonRed else TextPrimary
                            )
                        }

                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ArrowForwardIos,
                            contentDescription = "Navigate",
                            tint = TextMuted,
                            modifier = Modifier.size(13.dp)
                        )
                    }
                }
            }

            // Companion Future Incubator Slot (Per user instructions)
            item {
                CompanionPlaceholderCard()
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))
            }
        }

        if (showEditProfileDialog) {
            EditProfileDialog(
                currentProfile = userProfile,
                onDismiss = { showEditProfileDialog = false },
                onSave = { updatedName, updatedUsername, updatedPictureUri ->
                    repository.updateProfile(
                        name = updatedName,
                        username = updatedUsername,
                        customProfilePictureUri = updatedPictureUri
                    )
                    showEditProfileDialog = false
                }
            )
        }

        if (showChangeAvatarDialog) {
            ChangeAvatarDialog(
                selectedAvatarId = userProfile.selectedAvatarId,
                onSelectAvatar = { newAvatarId ->
                    repository.updateSelectedAvatar(newAvatarId)
                },
                onDismiss = { showChangeAvatarDialog = false }
            )
        }

        if (showNotificationSettingsDialog) {
            NotificationSettingsDialog(
                onDismiss = { showNotificationSettingsDialog = false }
            )
        }

        if (showHelpSupportDialog) {
            HelpSupportDialog(
                onDismiss = { showHelpSupportDialog = false }
            )
        }
    }
}

@Composable
private fun NotificationSettingsDialog(
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val prefs = remember(context) {
        context.applicationContext.getSharedPreferences("red_tree_notification_prefs", android.content.Context.MODE_PRIVATE)
    }

    var studyReminders by rememberSaveable {
        mutableStateOf(prefs.getBoolean("notify_study_reminders", true))
    }
    var timerAlerts by rememberSaveable {
        mutableStateOf(prefs.getBoolean("notify_timer_alerts", true))
    }
    var achievementAlerts by rememberSaveable {
        mutableStateOf(prefs.getBoolean("notify_achievement_alerts", true))
    }
    var streakProtection by rememberSaveable {
        mutableStateOf(prefs.getBoolean("notify_streak_protection", true))
    }
    var quietFocusMode by rememberSaveable {
        mutableStateOf(AchievementManager.isFocusModeEnabled(context))
    }

    BackHandler(onBack = onDismiss)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.76f))
            .padding(horizontal = 20.dp, vertical = 18.dp)
            .testTag("dialog_notification_settings"),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .border(1.5.dp, NeonOrange.copy(alpha = 0.55f), RoundedCornerShape(24.dp))
                .background(BlackSurfaceElevated)
                .padding(18.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Header with Back & Close buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .size(32.dp)
                                .testTag("btn_back_notification_settings")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                                contentDescription = "Back",
                                tint = NeonOrange,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Column {
                            Text(
                                text = "Notification Settings",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary,
                                modifier = Modifier.testTag("notification_settings_title")
                            )
                            Text(
                                text = "Study alerts, streak reminders & Focus Mode",
                                fontSize = 10.5.sp,
                                color = TextSecondary
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(28.dp)
                            .testTag("btn_close_notification_settings")
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Close,
                            contentDescription = "Close",
                            tint = TextMuted,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f, fill = false)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    NotificationSettingRow(
                        title = "Study Session Reminders",
                        subtitle = "Daily schedule & study start notifications",
                        checked = studyReminders,
                        accentColor = NeonGreen,
                        tag = "toggle_notify_study_reminders",
                        onCheckedChange = {
                            studyReminders = it
                            prefs.edit().putBoolean("notify_study_reminders", it).apply()
                        }
                    )

                    NotificationSettingRow(
                        title = "Break & Timer Completion Alerts",
                        subtitle = "Sound and vibration when study session finishes",
                        checked = timerAlerts,
                        accentColor = NeonYellow,
                        tag = "toggle_notify_timer_alerts",
                        onCheckedChange = {
                            timerAlerts = it
                            prefs.edit().putBoolean("notify_timer_alerts", it).apply()
                        }
                    )

                    NotificationSettingRow(
                        title = "Achievement Unlock Notifications",
                        subtitle = "Alerts for Bronze, Silver, Gold & Emerald milestones",
                        checked = achievementAlerts,
                        accentColor = NeonOrange,
                        tag = "toggle_notify_achievements",
                        onCheckedChange = {
                            achievementAlerts = it
                            prefs.edit().putBoolean("notify_achievement_alerts", it).apply()
                        }
                    )

                    NotificationSettingRow(
                        title = "Daily Streak Protection",
                        subtitle = "Evening reminder before your active streak expires",
                        checked = streakProtection,
                        accentColor = NeonRed,
                        tag = "toggle_notify_streak_protection",
                        onCheckedChange = {
                            streakProtection = it
                            prefs.edit().putBoolean("notify_streak_protection", it).apply()
                        }
                    )

                    NotificationSettingRow(
                        title = "Focus Mode Quiet Banners",
                        subtitle = "Hold full celebrations until Focus Mode ends",
                        checked = quietFocusMode,
                        accentColor = NeonGreenBright,
                        tag = "toggle_notify_quiet_focus",
                        onCheckedChange = {
                            quietFocusMode = it
                            AchievementManager.setFocusModeEnabled(context, it)
                        }
                    )
                }

                // Done Button
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .clip(RoundedCornerShape(22.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(NeonOrange, NeonYellow)
                            )
                        )
                        .testTag("btn_done_notification_settings")
                        .clickable(onClick = onDismiss),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Save & Close",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
            }
        }
    }
}

@Composable
private fun NotificationSettingRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    accentColor: Color,
    tag: String,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .border(
                1.dp,
                if (checked) accentColor.copy(alpha = 0.4f) else Color.White.copy(alpha = 0.08f),
                RoundedCornerShape(14.dp)
            )
            .background(BlackSurface.copy(alpha = 0.75f))
            .clickable { onCheckedChange(!checked) }
            .padding(horizontal = 14.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )
            Text(
                text = subtitle,
                fontSize = 10.5.sp,
                color = TextSecondary
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = accentColor,
                uncheckedThumbColor = TextMuted,
                uncheckedTrackColor = BlackSurface
            ),
            modifier = Modifier.testTag(tag)
        )
    }
}

@Composable
private fun HelpSupportDialog(
    onDismiss: () -> Unit
) {
    BackHandler(onBack = onDismiss)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.76f))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onDismiss
            )
            .padding(horizontal = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .border(1.5.dp, NeonYellow.copy(alpha = 0.5f), RoundedCornerShape(24.dp))
                .background(BlackSurfaceElevated)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = {}
                )
                .padding(20.dp)
                .testTag("dialog_help_support")
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "R.E.D TREE Help & Guide",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeonYellow
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(26.dp)) {
                        Icon(
                            imageVector = Icons.Outlined.Close,
                            contentDescription = "Close",
                            tint = TextMuted,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
                Text(
                    text = "• Remove • Education • Distraction\n• Complete focused study sessions to grow your 9-stage Living Tree.\n• Unlock 50 Sovereign Avatars and 4 tiers of R.E.D TREE Achievements (Bronze, Silver, Gold, and Legendary Emerald).",
                    fontSize = 12.5.sp,
                    color = TextSecondary,
                    lineHeight = 19.sp
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(42.dp)
                        .clip(RoundedCornerShape(21.dp))
                        .background(NeonGreen)
                        .clickable(onClick = onDismiss)
                        .testTag("btn_close_help_support"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Got It",
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
            }
        }
    }
}

/**
 * Edit Profile Modal containing ONLY:
 * 1. Profile Picture (Current picture preview + Change Profile Picture from Gallery + Remove/Reset to Avatar)
 * 2. Name
 * 3. Username
 * 4. Save & Cancel
 */
@Composable
private fun EditProfileDialog(
    currentProfile: UserProfileEntity,
    onDismiss: () -> Unit,
    onSave: (name: String, username: String, customProfilePictureUri: String?) -> Unit
) {
    val context = LocalContext.current
    var nameInput by rememberSaveable(currentProfile.name) { mutableStateOf(currentProfile.name) }
    var usernameInput by rememberSaveable(currentProfile.username) { mutableStateOf(currentProfile.username) }
    var pendingPictureUri by rememberSaveable(currentProfile.customProfilePictureUri) {
        mutableStateOf(currentProfile.customProfilePictureUri)
    }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val focusManager = LocalFocusManager.current

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { pickedUri ->
        if (pickedUri != null) {
            pendingPictureUri = persistPickedImageToInternalStorage(context, pickedUri)
        }
    }

    BackHandler(onBack = onDismiss)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.72f))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onDismiss
            )
            .padding(horizontal = 20.dp, vertical = 14.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .border(1.5.dp, NeonGreen.copy(alpha = 0.55f), RoundedCornerShape(24.dp))
                .background(BlackSurfaceElevated)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = {}
                )
                .padding(16.dp)
                .testTag("dialog_edit_profile")
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Dialog Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(30.dp)
                                .clip(CircleShape)
                                .background(NeonGreen.copy(alpha = 0.15f))
                                .border(1.dp, NeonGreen.copy(alpha = 0.5f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.AccountCircle,
                                contentDescription = "Edit Profile",
                                tint = NeonGreen,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Edit Profile",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "Profile Picture, Name & Username",
                                fontSize = 10.sp,
                                color = TextSecondary
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(26.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Close,
                            contentDescription = "Close",
                            tint = TextMuted,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f, fill = false)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // 1. Profile Picture Section
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(14.dp))
                            .background(BlackSurface.copy(alpha = 0.7f))
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ProfileIdentityImage(
                            customProfilePictureUri = pendingPictureUri,
                            selectedAvatarId = currentProfile.selectedAvatarId,
                            size = 48.dp,
                            modifier = Modifier.testTag("edit_profile_picture_preview")
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Profile Picture",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary
                            )
                            Text(
                                text = if (!pendingPictureUri.isNullOrBlank()) {
                                    "Custom picture selected"
                                } else {
                                    "Showing selected Avatar"
                                },
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                color = if (!pendingPictureUri.isNullOrBlank()) NeonGreenBright else TextSecondary,
                                modifier = Modifier.testTag("edit_profile_picture_status")
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Change Profile Picture Button
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(NeonGreen.copy(alpha = 0.18f))
                                        .border(1.dp, NeonGreen.copy(alpha = 0.6f), RoundedCornerShape(10.dp))
                                        .testTag("btn_change_profile_picture")
                                        .clickable {
                                            runCatching {
                                                photoPickerLauncher.launch(
                                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                                )
                                            }
                                        }
                                        .padding(horizontal = 10.dp, vertical = 5.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Outlined.PhotoLibrary,
                                            contentDescription = "Change Profile Picture",
                                            tint = NeonGreenBright,
                                            modifier = Modifier.size(13.dp)
                                        )
                                        Spacer(modifier = Modifier.width(5.dp))
                                        Text(
                                            text = "Change Profile Picture",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = NeonGreenBright
                                        )
                                    }
                                }

                                // Remove / Reset Custom Profile Picture Option
                                if (!pendingPictureUri.isNullOrBlank()) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(NeonRed.copy(alpha = 0.15f))
                                            .border(1.dp, NeonRed.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                                            .testTag("btn_remove_profile_picture")
                                            .clickable {
                                                pendingPictureUri = null
                                            }
                                            .padding(horizontal = 8.dp, vertical = 5.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = Icons.Outlined.DeleteOutline,
                                                contentDescription = "Remove Custom Picture",
                                                tint = NeonRed,
                                                modifier = Modifier.size(13.dp)
                                            )
                                            Spacer(modifier = Modifier.width(3.dp))
                                            Text(
                                                text = "Reset",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Medium,
                                                color = NeonRed
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // 2. Name Input
                    OutlinedTextField(
                        value = nameInput,
                        onValueChange = {
                            nameInput = it
                            if (errorMessage != null) errorMessage = null
                        },
                        label = { Text("Name", fontSize = 11.sp, fontFamily = FontFamily.Monospace) },
                        placeholder = { Text("Enter your name", color = TextMuted, fontSize = 13.sp) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Outlined.Person,
                                contentDescription = "Name",
                                tint = NeonGreen,
                                modifier = Modifier.size(18.dp)
                            )
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        shape = RoundedCornerShape(14.dp),
                        colors = editProfileTextFieldColors(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_profile_name")
                    )

                    // 3. Username Input
                    OutlinedTextField(
                        value = usernameInput,
                        onValueChange = {
                            usernameInput = it
                            if (errorMessage != null) errorMessage = null
                        },
                        label = { Text("Username", fontSize = 11.sp, fontFamily = FontFamily.Monospace) },
                        placeholder = { Text("Enter username", color = TextMuted, fontSize = 13.sp) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Outlined.AlternateEmail,
                                contentDescription = "Username",
                                tint = NeonYellow,
                                modifier = Modifier.size(18.dp)
                            )
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                        shape = RoundedCornerShape(14.dp),
                        colors = editProfileTextFieldColors(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_profile_username")
                    )

                    if (errorMessage != null) {
                        Text(
                            text = errorMessage!!,
                            fontSize = 12.sp,
                            color = NeonRed,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.testTag("edit_profile_error")
                        )
                    }
                }

                // 4. Action Buttons: Cancel & Save (always pinned and visible)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(42.dp)
                            .clip(RoundedCornerShape(21.dp))
                            .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(21.dp))
                            .background(BlackSurface)
                            .testTag("btn_cancel_profile")
                            .clickable(onClick = onDismiss),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Cancel",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextSecondary
                        )
                    }

                    Box(
                        modifier = Modifier
                            .weight(1.3f)
                            .height(42.dp)
                            .clip(RoundedCornerShape(21.dp))
                            .background(
                                Brush.horizontalGradient(
                                    listOf(NeonGreen, NeonGreenBright)
                                )
                            )
                            .testTag("btn_save_profile")
                            .clickable {
                                val cleanName = nameInput.trim()
                                val cleanUsername = usernameInput.trim().removePrefix("@").trim()

                                when {
                                    cleanName.isEmpty() -> {
                                        errorMessage = "Please enter a valid name."
                                    }
                                    cleanUsername.isEmpty() -> {
                                        errorMessage = "Please enter a valid username."
                                    }
                                    else -> {
                                        focusManager.clearFocus()
                                        onSave(cleanName, cleanUsername, pendingPictureUri)
                                    }
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Save",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF01240F)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun editProfileTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedContainerColor = BlackSurface.copy(alpha = 0.9f),
    unfocusedContainerColor = BlackSurface.copy(alpha = 0.7f),
    focusedBorderColor = NeonGreen,
    unfocusedBorderColor = Color.White.copy(alpha = 0.14f),
    focusedTextColor = TextPrimary,
    unfocusedTextColor = TextPrimary
)

@Composable
private fun ProfileStatBox(
    title: String,
    value: String,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    GlassCard(
        modifier = modifier,
        borderColor = accentColor.copy(alpha = 0.35f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                fontSize = 10.sp,
                color = TextSecondary,
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = value,
                fontFamily = FontFamily.Monospace,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        }
    }
}
