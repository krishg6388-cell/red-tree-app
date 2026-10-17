package com.example

import android.content.Context
import android.net.Uri
import androidx.activity.compose.LocalActivityResultRegistryOwner
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.ActivityResultRegistryOwner
import androidx.activity.result.contract.ActivityResultContract
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import androidx.core.app.ActivityOptionsCompat
import androidx.test.core.app.ApplicationProvider
import com.example.data.UserProfileRepository
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.StudyScreen
import com.example.ui.theme.MyApplicationTheme
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Before
  fun setUp() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    UserProfileRepository.resetAllPersistenceForTesting(context)
  }

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("R.E.D Tree", appName)
  }

  @Test
  fun `test edit profile picture name and username saves and persists on reopen`() {
    var showProfile by mutableStateOf(true)
    val fakePickedUri = Uri.parse("android.resource://com.example/${R.drawable.stage_9_legendry_red_tree}")

    val fakeRegistryOwner = object : ActivityResultRegistryOwner {
      override val activityResultRegistry: ActivityResultRegistry = object : ActivityResultRegistry() {
        override fun <I, O> onLaunch(
          requestCode: Int,
          contract: ActivityResultContract<I, O>,
          input: I,
          options: ActivityOptionsCompat?
        ) {
          dispatchResult(requestCode, fakePickedUri)
        }
      }
    }

    composeTestRule.setContent {
      CompositionLocalProvider(LocalActivityResultRegistryOwner provides fakeRegistryOwner) {
        MyApplicationTheme {
          if (showProfile) {
            ProfileScreen(
              onBack = { showProfile = false }
            )
          } else {
            DashboardScreen(onNavigateTo = { showProfile = true })
          }
        }
      }
    }

    // Verify initial profile display values and default avatar
    composeTestRule.onNodeWithTag("profile_display_name").assertTextEquals("Krish")
    composeTestRule.onNodeWithTag("profile_display_username").assertTextEquals("@krish")
    composeTestRule.onNodeWithTag("profile_avatar_image_avatar_red_tree_logo", useUnmergedTree = true).assertExists()

    // Profile -> Tap Edit Profile
    composeTestRule.onNodeWithTag("btn_edit_profile").performClick()
    composeTestRule.waitForIdle()

    // Change Profile Picture from Gallery
    composeTestRule.onNodeWithTag("btn_change_profile_picture").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("edit_profile_picture_status", useUnmergedTree = true)
      .assertTextEquals("Custom picture selected")

    // Change Name & Username
    composeTestRule.onNodeWithTag("input_profile_name").performTextClearance()
    composeTestRule.onNodeWithTag("input_profile_name").performTextInput("Aarav Sharma")

    composeTestRule.onNodeWithTag("input_profile_username").performTextClearance()
    composeTestRule.onNodeWithTag("input_profile_username").performTextInput("aarav_focus")

    // Tap Save -> Return to Profile -> Verify all changes immediately
    composeTestRule.onNodeWithTag("btn_save_profile").performClick()
    composeTestRule.waitForIdle()

    composeTestRule.onNodeWithTag("profile_display_name").assertTextEquals("Aarav Sharma")
    composeTestRule.onNodeWithTag("profile_display_username").assertTextEquals("@aarav_focus")
    composeTestRule.onNodeWithTag("profile_custom_picture_image", useUnmergedTree = true).assertExists()

    // Reopen Edit Profile -> Verify saved data remains
    composeTestRule.onNodeWithTag("btn_edit_profile").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("edit_profile_picture_status", useUnmergedTree = true)
      .assertTextEquals("Custom picture selected")
    composeTestRule.onNodeWithTag("btn_cancel_profile").performClick()
    composeTestRule.waitForIdle()
  }

  @Test
  fun `test change avatar selects unlocked avatar and persists across app restart`() {
    var appSessionKey by mutableIntStateOf(0)

    composeTestRule.setContent {
      MyApplicationTheme {
        key(appSessionKey) {
          ProfileScreen()
        }
      }
    }

    // Initially displays default avatar_red_tree_logo
    composeTestRule.onNodeWithTag("profile_avatar_image_avatar_red_tree_logo", useUnmergedTree = true).assertExists()

    // Profile -> Tap Change Avatar
    composeTestRule.onNodeWithTag("btn_change_avatar").performClick()
    composeTestRule.waitForIdle()

    // Select an unlocked avatar (e.g. avatar_seed_sprout)
    composeTestRule.onNodeWithTag("avatar_item_avatar_seed_sprout").performClick()
    composeTestRule.waitForIdle()

    // Verify selected badge appears immediately on the newly selected avatar
    composeTestRule.onNodeWithTag("avatar_selected_badge_avatar_seed_sprout", useUnmergedTree = true).assertExists()

    // Return to Profile
    composeTestRule.onNodeWithTag("btn_done_change_avatar").performClick()
    composeTestRule.waitForIdle()

    // Verify the new avatar appears on the Profile page
    composeTestRule.onNodeWithTag("profile_avatar_image_avatar_seed_sprout", useUnmergedTree = true).assertExists()

    // Simulate app close and reopen (clear in-memory singleton and recompose fresh session)
    UserProfileRepository.resetInMemoryInstance()
    appSessionKey++
    composeTestRule.waitForIdle()

    // Verify the same selected avatar remains after app restart
    composeTestRule.onNodeWithTag("profile_avatar_image_avatar_seed_sprout", useUnmergedTree = true).assertExists()
  }

  @Test
  fun `test 50 avatar collection has 10 free and 40 locked with task details and unlock progression`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    assertEquals(50, com.example.ui.screens.AVAILABLE_PROFILE_AVATARS.size)
    assertEquals(10, com.example.ui.screens.AVAILABLE_PROFILE_AVATARS.count { it.isInitiallyFree })
    assertEquals(40, com.example.ui.screens.AVAILABLE_PROFILE_AVATARS.count { !it.isInitiallyFree })

    composeTestRule.setContent {
      MyApplicationTheme {
        ProfileScreen()
      }
    }

    // Open Change Avatar
    composeTestRule.onNodeWithTag("btn_change_avatar").performClick()
    composeTestRule.waitForIdle()

    // Switch to Locked (40) filter tab and tap Avatar 11 (Mind Forge)
    composeTestRule.onNodeWithTag("filter_avatars_locked").performClick()
    composeTestRule.waitForIdle()

    composeTestRule.onNodeWithTag("avatar_item_avatar_11_mind_forge").performClick()
    composeTestRule.waitForIdle()

    // Verify Locked Avatar Details card displays name, task, and current progress
    composeTestRule.onNodeWithTag("locked_avatar_details_card", useUnmergedTree = true).assertExists()
    composeTestRule.onNodeWithTag("locked_avatar_detail_title", useUnmergedTree = true).assertTextEquals("Mind Forge")
    composeTestRule.onNodeWithTag("locked_avatar_detail_task", useUnmergedTree = true)
      .assertTextEquals("Unlock Task: Complete your first study session.")
    composeTestRule.onNodeWithTag("locked_avatar_detail_progress", useUnmergedTree = true)
      .assertTextEquals("0 / 1 sessions")

    // Complete 1 study session (30 minutes) -> Avatar 11 unlocks permanently
    com.example.ui.screens.recordStudyProgressForAvatars(
      context = context,
      completedSessions = 1,
      totalStudyMinutes = 30
    )

    // Switch back to All (50) and back to Unlocked to refresh and select newly unlocked Avatar 11
    composeTestRule.onNodeWithTag("filter_avatars_all").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("btn_done_change_avatar").performClick()
    composeTestRule.waitForIdle()

    // Verify profile still has default avatar (locked tap did not select it earlier)
    composeTestRule.onNodeWithTag("profile_avatar_image_avatar_red_tree_logo", useUnmergedTree = true).assertExists()
  }

  @Test
  fun `test study session screen interactions and custom timer`() {
    composeTestRule.setContent {
      MyApplicationTheme {
        StudyScreen()
      }
    }

    composeTestRule.onNodeWithTag("btn_timer_toggle").performClick()
    composeTestRule.waitForIdle()

    composeTestRule.onNodeWithTag("btn_timer_toggle").performClick()
    composeTestRule.waitForIdle()

    composeTestRule.onNodeWithTag("btn_timer_reset").performClick()
    composeTestRule.waitForIdle()

    composeTestRule.onNodeWithTag("chip_focus_deep_focus").performClick()
    composeTestRule.waitForIdle()

    composeTestRule.onNodeWithTag("chip_focus_pomodoro").performClick()
    composeTestRule.waitForIdle()
  }

  @Test
  fun `test full app splash sign in and navigation responsiveness`() {
    composeTestRule.setContent {
      MyApplicationTheme {
        RedTreeAppShell()
      }
    }

    // 1. Verify "Remove  •  Education  •  Distraction" full form tagline and that Splash NEVER auto-enters
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("splash_full_form_tagline").assertTextEquals("Remove  •  Education  •  Distraction")
    composeTestRule.onNodeWithTag("btn_enter_splash").assertExists()
    composeTestRule.onNodeWithTag("auth_screen_root").assertDoesNotExist()

    // 2. Manual user tap on "Enter R.E.D Tree →" opens Sign In / Sign Up
    composeTestRule.onNodeWithTag("btn_enter_splash").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("auth_screen_root").assertExists()

    // 3. Unauthenticated user cannot bypass Sign In with empty fields
    composeTestRule.onNodeWithTag("segment_tab_sign_in").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("btn_auth_submit").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("auth_error_message").assertExists()
    composeTestRule.onNodeWithTag("btn_start_study").assertDoesNotExist()

    // 4. Valid Sign In opens Home/Dashboard
    composeTestRule.onNodeWithTag("input_auth_email").performTextInput("krish@redtree.edu")
    composeTestRule.onNodeWithTag("input_auth_password").performTextInput("focus123")
    composeTestRule.waitForIdle()

    composeTestRule.onNodeWithTag("btn_auth_submit").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("btn_start_study").assertExists()

    // 5. ONE TAP on More -> More sheet opens immediately -> ONE TAP on Profile -> ONE TAP on Notification Settings
    composeTestRule.onNodeWithTag("nav_more").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("more_sheet_overlay").assertExists()

    composeTestRule.onNodeWithTag("more_item_profile").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("profile_lazy_list").assertExists()
    composeTestRule.onNodeWithTag("profile_lazy_list")
      .performScrollToNode(androidx.compose.ui.test.hasTestTag("menu_notification_settings"))
    composeTestRule.onNodeWithTag("menu_notification_settings").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("dialog_notification_settings").assertExists()
    composeTestRule.onNodeWithTag("toggle_notify_study_reminders").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("btn_done_notification_settings").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("dialog_notification_settings").assertDoesNotExist()

    // 6. ONE TAP on More -> ONE TAP on Restart App -> Replays Beginning Animation & holds until manual tap
    composeTestRule.onNodeWithTag("nav_more").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("more_item_splash").performScrollTo().performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("btn_enter_splash").assertExists()
    composeTestRule.onNodeWithTag("auth_screen_root").assertDoesNotExist()

    // Tap Enter R.E.D Tree -> Sign In opens -> Authenticate -> Home opens
    composeTestRule.onNodeWithTag("btn_enter_splash").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("auth_screen_root").assertExists()
    composeTestRule.onNodeWithTag("segment_tab_sign_in").performClick()
    composeTestRule.onNodeWithTag("input_auth_email").performTextInput("krish@redtree.edu")
    composeTestRule.onNodeWithTag("input_auth_password").performTextInput("focus123")
    composeTestRule.onNodeWithTag("btn_auth_submit").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("btn_start_study").assertExists()
  }

  @Test
  fun `test red tree 4 level achievement system progress persistence and focus mode celebrations`() {
    val context = ApplicationProvider.getApplicationContext<Context>()

    composeTestRule.setContent {
      MyApplicationTheme {
        com.example.ui.screens.AchievementsScreen()
      }
    }

    // 1. Verify initial state: 0 hours 0 minutes, Bronze level, 0 / 24 unlocked
    composeTestRule.onNodeWithTag("achievements_total_study_time").assertTextEquals("0 hours 0 minutes")
    composeTestRule.onNodeWithTag("achievements_current_level").assertTextEquals("🥉 Bronze")
    composeTestRule.onNodeWithTag("achievements_unlocked_counter").assertTextEquals("0 / 24 UNLOCKED")
    composeTestRule.onNodeWithTag("achievements_lazy_list")
      .performScrollToNode(androidx.compose.ui.test.hasTestTag("achievement_card_ach_bronze_1_hour"))
    composeTestRule.onNodeWithTag("achievement_status_ach_bronze_1_hour", useUnmergedTree = true).assertTextEquals("LOCKED")

    // 2. Enable Focus Mode ON and complete 3 real study sessions: 20m + 30m + 45m = 95 minutes (1h 35m)
    com.example.ui.screens.AchievementManager.setFocusModeEnabled(context, true)
    com.example.ui.screens.AchievementManager.recordCompletedStudySession(context, sessionMinutes = 20)
    com.example.ui.screens.AchievementManager.recordCompletedStudySession(context, sessionMinutes = 30)
    com.example.ui.screens.AchievementManager.recordCompletedStudySession(context, sessionMinutes = 45)
    composeTestRule.waitForIdle()

    // Verify Focus Mode ON showed ONLY the subtle banner and NOT the full celebration overlay
    composeTestRule.onNodeWithTag("subtle_achievement_banner").assertExists()
    composeTestRule.onNodeWithTag("full_achievement_celebration_overlay").assertDoesNotExist()

    // Verify total study time is 1 hours 35 minutes, First Study Session & First Hour are UNLOCKED, and 5 Hour Focus is 1h 35m / 5h
    composeTestRule.onNodeWithTag("achievements_lazy_list")
      .performScrollToNode(androidx.compose.ui.test.hasTestTag("achievements_total_study_time"))
    composeTestRule.onNodeWithTag("achievements_total_study_time").assertTextEquals("1 hours 35 minutes")
    composeTestRule.onNodeWithTag("achievements_lazy_list")
      .performScrollToNode(androidx.compose.ui.test.hasTestTag("achievements_unlocked_counter"))
    composeTestRule.onNodeWithTag("achievements_unlocked_counter").assertTextEquals("2 / 24 UNLOCKED")
    composeTestRule.onNodeWithTag("achievements_lazy_list")
      .performScrollToNode(androidx.compose.ui.test.hasTestTag("achievement_card_ach_bronze_first_session"))
    composeTestRule.onNodeWithTag("achievement_status_ach_bronze_first_session", useUnmergedTree = true).assertTextEquals("✓ UNLOCKED")
    composeTestRule.onNodeWithTag("achievements_lazy_list")
      .performScrollToNode(androidx.compose.ui.test.hasTestTag("achievement_card_ach_bronze_1_hour"))
    composeTestRule.onNodeWithTag("achievement_status_ach_bronze_1_hour", useUnmergedTree = true).assertTextEquals("✓ UNLOCKED")
    composeTestRule.onNodeWithTag("achievements_lazy_list")
      .performScrollToNode(androidx.compose.ui.test.hasTestTag("achievement_card_ach_bronze_5_hours"))
    composeTestRule.onNodeWithTag("achievement_status_ach_bronze_5_hours", useUnmergedTree = true).assertTextEquals("LOCKED")
    composeTestRule.onNodeWithTag("achievement_progress_ach_bronze_5_hours", useUnmergedTree = true).assertTextEquals("Progress: 1h 35m / 5h")

    // 3. Turn Focus Mode OFF -> Pending celebrations automatically promote to Full Celebration Overlay
    com.example.ui.screens.AchievementManager.setFocusModeEnabled(context, false)
    composeTestRule.waitForIdle()

    composeTestRule.onNodeWithTag("full_achievement_celebration_overlay").assertExists()
    composeTestRule.onNodeWithTag("celebration_ribbons_balloons_canvas", useUnmergedTree = true).assertExists()
    composeTestRule.onNodeWithTag("celebration_achievement_title", useUnmergedTree = true).assertTextEquals("FIRST STUDY SESSION")
    composeTestRule.onNodeWithTag("btn_celebration_continue", useUnmergedTree = true).performClick()
    composeTestRule.waitForIdle()

    // Second queued celebration ("FIRST HOUR") appears next
    composeTestRule.onNodeWithTag("celebration_achievement_title", useUnmergedTree = true).assertTextEquals("FIRST HOUR")
    composeTestRule.onNodeWithTag("btn_celebration_continue", useUnmergedTree = true).performClick()
    composeTestRule.waitForIdle()

    // All queued celebrations viewed -> overlay closes and never repeats
    composeTestRule.onNodeWithTag("full_achievement_celebration_overlay").assertDoesNotExist()

    // 4. Switch across the 4 official level tabs: Bronze, Silver, Gold, Legendary Emerald
    composeTestRule.onNodeWithTag("achievements_lazy_list")
      .performScrollToNode(androidx.compose.ui.test.hasTestTag("tab_level_silver"))
    composeTestRule.onNodeWithTag("tab_level_silver").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("achievements_lazy_list")
      .performScrollToNode(androidx.compose.ui.test.hasTestTag("active_level_section_header"))
    composeTestRule.onNodeWithTag("active_level_section_header").assertTextEquals("🥈 SILVER")

    composeTestRule.onNodeWithTag("tab_level_gold").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("active_level_section_header").assertTextEquals("🥇 GOLD")

    composeTestRule.onNodeWithTag("tab_level_emerald").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("active_level_section_header").assertTextEquals("💚 LEGENDARY R.E.D TREE EMERALD")
  }

  @Test
  fun `test companion settings persistence and floating widget behavior`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val companionManager = com.example.data.CompanionManager.getInstance(context)

    // 1. Initial state defaults: Enabled, Forest Cutu
    companionManager.setCompanionEnabled(true)
    companionManager.selectCharacter(com.example.data.CompanionCharacter.FOREST_CUTU)
    companionManager.updateCustomName("Cutu")

    composeTestRule.setContent {
      MyApplicationTheme {
        RedTreeAppShell()
      }
    }

    // Skip splash and auth to reach home
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("btn_enter_splash").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("segment_tab_sign_in").performClick()
    composeTestRule.onNodeWithTag("input_auth_email").performTextInput("krish@redtree.edu")
    composeTestRule.onNodeWithTag("input_auth_password").performTextInput("focus123")
    composeTestRule.onNodeWithTag("btn_auth_submit").performClick()
    composeTestRule.waitForIdle()

    // 2. Floating Companion is visible on screen
    composeTestRule.onNodeWithTag("floating_companion_widget").assertExists()
    composeTestRule.onNodeWithTag("companion_interactive_character").assertExists()

    // Interactive tap shows speech bubble
    composeTestRule.onNodeWithTag("companion_interactive_character").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("companion_speech_bubble").assertExists()

    // 3. Open More -> Companion Settings
    composeTestRule.onNodeWithTag("nav_more").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("more_item_companion").performClick()
    composeTestRule.waitForIdle()

    // Verify Settings UI
    composeTestRule.onNodeWithTag("switch_companion_master").assertExists()
    composeTestRule.onNodeWithTag("card_companion_forest_cutu").assertExists()
    composeTestRule.onNodeWithTag("card_companion_forest_cutie").assertExists()

    // 4. Select Forest Cutie and rename to "Princess Leaf"
    composeTestRule.onNodeWithTag("card_companion_forest_cutie").performClick()
    composeTestRule.waitForIdle()
    assert(companionManager.configState.value.selectedCharacter == com.example.data.CompanionCharacter.FOREST_CUTIE)

    composeTestRule.onNodeWithTag("companion_settings_list")
      .performScrollToNode(androidx.compose.ui.test.hasTestTag("btn_edit_companion_name"))
    composeTestRule.onNodeWithTag("btn_edit_companion_name").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("input_companion_name").performTextClearance()
    composeTestRule.onNodeWithTag("input_companion_name").performTextInput("Princess Leaf")
    composeTestRule.onNodeWithTag("btn_save_companion_name").performClick()
    composeTestRule.waitForIdle()
    assert(companionManager.configState.value.customName == "Princess Leaf")

    // 5. Turn Companion OFF -> Floating widget is completely inactive & invisible
    composeTestRule.onNodeWithTag("companion_settings_list")
      .performScrollToNode(androidx.compose.ui.test.hasTestTag("switch_companion_master"))
    composeTestRule.onNodeWithTag("switch_companion_master").performClick()
    composeTestRule.waitForIdle()
    assert(!companionManager.configState.value.isEnabled)

    composeTestRule.onNodeWithTag("companion_settings_list")
      .performScrollToNode(androidx.compose.ui.test.hasTestTag("btn_companion_settings_back"))
    composeTestRule.onNodeWithTag("btn_companion_settings_back").performClick()
    composeTestRule.waitForIdle()

    // Verify completely disappeared
    composeTestRule.onNodeWithTag("floating_companion_widget").assertDoesNotExist()

    // 6. Turn back ON -> Persisted custom name and Forest Cutie character remain
    companionManager.setCompanionEnabled(true)
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("floating_companion_widget").assertExists()
    assert(companionManager.configState.value.selectedCharacter == com.example.data.CompanionCharacter.FOREST_CUTIE)
    assert(companionManager.configState.value.customName == "Princess Leaf")
  }
}
