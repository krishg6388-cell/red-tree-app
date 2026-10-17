package com.example.ui.screens

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Face
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BlackSurface
import com.example.ui.theme.BlackSurfaceElevated
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonGreenBright
import com.example.ui.theme.NeonRed
import com.example.ui.theme.NeonYellow
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.io.File

fun persistPickedImageToInternalStorage(context: Context, sourceUri: Uri): String {
    return runCatching {
        if (sourceUri.scheme == "android.resource") {
            return@runCatching sourceUri.toString()
        }
        val inputStream = context.contentResolver.openInputStream(sourceUri)
            ?: return@runCatching sourceUri.toString()
        val targetFile = File(context.filesDir, "custom_profile_picture.jpg")
        inputStream.use { input ->
            targetFile.outputStream().use { output ->
                input.copyTo(output)
            }
        }
        Uri.fromFile(targetFile).toString()
    }.getOrDefault(sourceUri.toString())
}

fun loadProfileBitmap(context: Context, uriString: String?): ImageBitmap? {
    if (uriString.isNullOrBlank()) return null
    return runCatching {
        val uri = Uri.parse(uriString)
        when (uri.scheme) {
            "file" -> {
                val path = uri.path ?: return@runCatching null
                BitmapFactory.decodeFile(path)?.asImageBitmap()
            }
            "android.resource" -> {
                val resId = uri.lastPathSegment?.toIntOrNull()
                if (resId != null) {
                    BitmapFactory.decodeResource(context.resources, resId)?.asImageBitmap()
                } else {
                    context.contentResolver.openInputStream(uri)?.use { stream ->
                        BitmapFactory.decodeStream(stream)?.asImageBitmap()
                    }
                }
            }
            else -> {
                context.contentResolver.openInputStream(uri)?.use { stream ->
                    BitmapFactory.decodeStream(stream)?.asImageBitmap()
                }
            }
        }
    }.getOrNull()
}

/**
 * Displays the user's Custom Profile Picture if set; otherwise displays the user's selected Avatar
 * using the official R.E.D TREE Emblem renderer.
 */
@Composable
fun ProfileIdentityImage(
    customProfilePictureUri: String?,
    selectedAvatarId: String,
    size: Dp,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val hasCustomPicture = !customProfilePictureUri.isNullOrBlank()
    val customBitmap = remember(customProfilePictureUri) {
        loadProfileBitmap(context, customProfilePictureUri)
    }
    val selectedAvatar = remember(selectedAvatarId) {
        findAvatarById(selectedAvatarId)
    }

    val semanticLabel = if (hasCustomPicture) {
        "Custom Profile Picture: $customProfilePictureUri"
    } else {
        "Avatar: ${selectedAvatar.id}"
    }

    Box(
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(14.dp))
            .background(BlackSurface)
            .border(
                width = 1.2.dp,
                color = if (hasCustomPicture) NeonGreen else selectedAvatar.primaryColor.copy(alpha = 0.65f),
                shape = RoundedCornerShape(14.dp)
            )
            .semantics { contentDescription = semanticLabel },
        contentAlignment = Alignment.Center
    ) {
        if (hasCustomPicture) {
            if (customBitmap != null) {
                Image(
                    bitmap = customBitmap,
                    contentDescription = "Custom Profile Picture",
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("profile_custom_picture_image"),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(NeonGreen.copy(alpha = 0.18f))
                        .testTag("profile_custom_picture_image"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.PhotoCamera,
                        contentDescription = "Custom Profile Picture",
                        tint = NeonGreen,
                        modifier = Modifier.size(size * 0.45f)
                    )
                }
            }
        } else {
            RedTreeOfficialEmblem(
                avatar = selectedAvatar,
                isUnlocked = true,
                size = size - 4.dp,
                modifier = Modifier.testTag("profile_avatar_image_${selectedAvatar.id}")
            )
        }
    }
}

/**
 * Change Avatar Modal (Opened when the user taps "Change Avatar" on the Profile page).
 * Features the 50-avatar R.E.D TREE collection (10 Free + 40 Study-Task Locked)
 * with every avatar locked to STANDARD_AVATAR_EMBLEM_SIZE (96.dp).
 */
@Composable
fun ChangeAvatarDialog(
    selectedAvatarId: String,
    onSelectAvatar: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var inspectedLockedAvatar by remember { mutableStateOf<ProfileAvatarItem?>(null) }
    var selectedFilterTab by remember { mutableIntStateOf(0) } // 0 = All (50), 1 = Unlocked, 2 = Locked

    BackHandler(onBack = onDismiss)

    val avatarEntries = remember {
        AVAILABLE_PROFILE_AVATARS.map { item ->
            item to evaluateAvatarUnlockStatus(context, item)
        }
    }

    val unlockedCount = remember(avatarEntries) {
        avatarEntries.count { it.second.isUnlocked }
    }
    val lockedCount = remember(avatarEntries) {
        avatarEntries.size - unlockedCount
    }

    val filteredEntries = remember(avatarEntries, selectedFilterTab) {
        when (selectedFilterTab) {
            1 -> avatarEntries.filter { it.second.isUnlocked }
            2 -> avatarEntries.filter { !it.second.isUnlocked }
            else -> avatarEntries
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.82f))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onDismiss
            )
            .padding(horizontal = 14.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.94f)
                .clip(RoundedCornerShape(24.dp))
                .border(1.5.dp, NeonYellow.copy(alpha = 0.55f), RoundedCornerShape(24.dp))
                .background(BlackSurfaceElevated)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = {}
                )
                .padding(14.dp)
                .testTag("dialog_change_avatar")
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Header
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
                                .background(NeonYellow.copy(alpha = 0.15f))
                                .border(1.dp, NeonYellow.copy(alpha = 0.5f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Face,
                                contentDescription = "Change Avatar",
                                tint = NeonYellow,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Change Avatar",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "50 Official Emblems • $unlockedCount Unlocked • $lockedCount Locked",
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                color = TextSecondary
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(26.dp)
                            .testTag("btn_close_avatar_dialog")
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Close,
                            contentDescription = "Close",
                            tint = TextMuted,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                // Filter Tabs: All (50) | Unlocked | Locked
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AvatarFilterChip(
                        label = "All (${avatarEntries.size})",
                        selected = selectedFilterTab == 0,
                        accentColor = NeonYellow,
                        onClick = { selectedFilterTab = 0 },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("filter_avatars_all")
                    )
                    AvatarFilterChip(
                        label = "Unlocked ($unlockedCount)",
                        selected = selectedFilterTab == 1,
                        accentColor = NeonGreenBright,
                        onClick = { selectedFilterTab = 1 },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("filter_avatars_unlocked")
                    )
                    AvatarFilterChip(
                        label = "Locked ($lockedCount)",
                        selected = selectedFilterTab == 2,
                        accentColor = NeonRed,
                        onClick = { selectedFilterTab = 2 },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("filter_avatars_locked")
                    )
                }

                // Locked Avatar Details Card (Shown when user taps any locked avatar)
                val lockedDetailAvatar = inspectedLockedAvatar
                if (lockedDetailAvatar != null) {
                    val lockedStatus = remember(lockedDetailAvatar.id, avatarEntries) {
                        avatarEntries.firstOrNull { it.first.id == lockedDetailAvatar.id }?.second
                            ?: evaluateAvatarUnlockStatus(context, lockedDetailAvatar)
                    }
                    LockedAvatarDetailsCard(
                        avatar = lockedDetailAvatar,
                        status = lockedStatus,
                        onCloseDetails = { inspectedLockedAvatar = null }
                    )
                }

                // 2-Column Grid of Large Standard-Sized (96.dp) Official R.E.D TREE Emblems
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .testTag("avatar_selection_grid"),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filteredEntries, key = { it.first.id }) { (avatar, unlockStatus) ->
                        AvatarGridCell(
                            avatar = avatar,
                            unlockStatus = unlockStatus,
                            isSelected = avatar.id == selectedAvatarId,
                            isInspectedLocked = inspectedLockedAvatar?.id == avatar.id,
                            onClick = {
                                if (unlockStatus.isUnlocked) {
                                    inspectedLockedAvatar = null
                                    onSelectAvatar(avatar.id)
                                } else {
                                    inspectedLockedAvatar = avatar
                                }
                            }
                        )
                    }
                }

                // Done Button (Always pinned and accessible at bottom)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(NeonGreen, NeonGreenBright)
                            )
                        )
                        .testTag("btn_done_change_avatar")
                        .clickable(onClick = onDismiss),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Done",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF01240F)
                    )
                }
            }
        }
    }
}

@Composable
private fun AvatarFilterChip(
    label: String,
    selected: Boolean,
    accentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(26.dp)
            .clip(RoundedCornerShape(13.dp))
            .background(if (selected) accentColor.copy(alpha = 0.20f) else BlackSurface)
            .border(
                width = 1.dp,
                color = if (selected) accentColor else Color.White.copy(alpha = 0.12f),
                shape = RoundedCornerShape(13.dp)
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontSize = 10.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            color = if (selected) accentColor else TextSecondary
        )
    }
}

/**
 * Displays the full details of a tapped locked avatar:
 * - Avatar Name
 * - Unlock Task
 * - Current Study Progress (with progress bar & numbers)
 * - Short motivational message
 */
@Composable
private fun LockedAvatarDetailsCard(
    avatar: ProfileAvatarItem,
    status: AvatarUnlockStatus,
    onCloseDetails: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(BlackSurface)
            .border(1.2.dp, NeonRed.copy(alpha = 0.65f), RoundedCornerShape(16.dp))
            .padding(10.dp)
            .testTag("avatar_locked_notice")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("locked_avatar_details_card"),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RedTreeOfficialEmblem(
                avatar = avatar,
                isUnlocked = false,
                size = 56.dp
            )

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Outlined.Lock,
                            contentDescription = "Locked",
                            tint = NeonYellow,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = avatar.title,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            modifier = Modifier.testTag("locked_avatar_detail_title")
                        )
                    }

                    IconButton(
                        onClick = onCloseDetails,
                        modifier = Modifier.size(20.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Close,
                            contentDescription = "Dismiss Details",
                            tint = TextMuted,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }

                Text(
                    text = "Unlock Task: ${avatar.unlockTask}",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = NeonYellow,
                    modifier = Modifier.testTag("locked_avatar_detail_task")
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Progress bar + numeric progress
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(Color.White.copy(alpha = 0.10f))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(status.progressFraction.coerceIn(0.06f, 1f))
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(NeonRed, NeonYellow)
                                    )
                                )
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = status.formattedProgress,
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace,
                        color = TextPrimary,
                        modifier = Modifier.testTag("locked_avatar_detail_progress")
                    )
                }

                Spacer(modifier = Modifier.height(3.dp))

                Text(
                    text = avatar.motivationalText,
                    fontSize = 9.sp,
                    color = TextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.testTag("locked_avatar_detail_motivation")
                )
            }
        }
    }
}

@Composable
private fun AvatarGridCell(
    avatar: ProfileAvatarItem,
    unlockStatus: AvatarUnlockStatus,
    isSelected: Boolean,
    isInspectedLocked: Boolean,
    onClick: () -> Unit
) {
    val isUnlocked = unlockStatus.isUnlocked
    val borderColor = when {
        isSelected -> NeonGreenBright
        isInspectedLocked -> NeonYellow
        isUnlocked -> avatar.primaryColor.copy(alpha = 0.48f)
        else -> avatar.primaryColor.copy(alpha = 0.24f)
    }
    val bgColor = when {
        isSelected -> NeonGreen.copy(alpha = 0.15f)
        isInspectedLocked -> NeonRed.copy(alpha = 0.14f)
        isUnlocked -> BlackSurface.copy(alpha = 0.90f)
        else -> BlackSurface.copy(alpha = 0.68f)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .border(
                width = if (isSelected || isInspectedLocked) 1.8.dp else 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(18.dp)
            )
            .background(bgColor)
            .testTag("avatar_item_${avatar.id}")
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp, horizontal = 8.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Standard Locked Size (96.dp) identical across all 50 avatars
            Box(
                modifier = Modifier.size(STANDARD_AVATAR_EMBLEM_SIZE),
                contentAlignment = Alignment.Center
            ) {
                RedTreeOfficialEmblem(
                    avatar = avatar,
                    isUnlocked = isUnlocked,
                    size = STANDARD_AVATAR_EMBLEM_SIZE
                )

                if (!isUnlocked) {
                    // Elegant corner lock indicator that preserves full emblem visibility
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .size(26.dp)
                            .clip(CircleShape)
                            .background(Color(0xEB080B10))
                            .border(1.2.dp, NeonYellow.copy(alpha = 0.85f), CircleShape)
                            .testTag("avatar_lock_icon_${avatar.id}"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Lock,
                            contentDescription = "Locked",
                            tint = NeonYellow,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                } else if (isSelected) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .size(22.dp)
                            .clip(CircleShape)
                            .background(NeonGreenBright)
                            .border(1.dp, Color.Black, CircleShape)
                            .testTag("avatar_selected_badge_${avatar.id}"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Selected",
                            tint = Color.Black,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = avatar.title,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = if (isUnlocked) TextPrimary else TextPrimary.copy(alpha = 0.82f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = when {
                    isSelected -> "Selected"
                    isUnlocked -> "Unlocked • #${avatar.number}"
                    else -> avatar.unlockTask
                },
                fontSize = 9.sp,
                fontFamily = FontFamily.Monospace,
                color = when {
                    isSelected -> NeonGreenBright
                    isUnlocked -> TextSecondary
                    else -> NeonYellow.copy(alpha = 0.88f)
                },
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )
        }
    }
}
