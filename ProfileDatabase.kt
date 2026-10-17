package com.example.data

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey val id: Int = 1,
    val name: String = "Krish",
    val username: String = "krish",
    val statusTitle: String = "Level 3 • Young Tree",
    val selectedAvatarId: String = "avatar_red_tree_logo",
    val customProfilePictureUri: String? = null
)

@Dao
interface UserProfileDao {
    @Query("SELECT * FROM user_profile WHERE id = 1 LIMIT 1")
    fun observeProfile(): Flow<UserProfileEntity?>

    @Query("SELECT * FROM user_profile WHERE id = 1 LIMIT 1")
    suspend fun getProfileOnce(): UserProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertProfile(profile: UserProfileEntity)
}

@Database(entities = [UserProfileEntity::class, StudySessionEntity::class], version = 3, exportSchema = false)
abstract class RedTreeDatabase : RoomDatabase() {
    abstract fun userProfileDao(): UserProfileDao
    abstract fun studySessionDao(): StudySessionDao

    companion object {
        @Volatile
        internal var INSTANCE: RedTreeDatabase? = null

        fun getInstance(context: Context): RedTreeDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    RedTreeDatabase::class.java,
                    "red_tree_profile.db"
                )
                    .fallbackToDestructiveMigration(dropAllTables = true)
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}

class UserProfileRepository private constructor(context: Context) {
    private val appContext = context.applicationContext
    private val prefs = appContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val dao = RedTreeDatabase.getInstance(appContext).userProfileDao()
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    @Volatile
    private var hasExplicitUpdate = false

    private val initialProfile = loadInitialFromPrefs()
    private val _profileState = MutableStateFlow(initialProfile)
    val profileState: StateFlow<UserProfileEntity> = _profileState.asStateFlow()

    private val _composeProfileState = androidx.compose.runtime.mutableStateOf(initialProfile)
    val composeProfileState: androidx.compose.runtime.State<UserProfileEntity> = _composeProfileState

    init {
        scope.launch {
            runCatching {
                val existing = dao.getProfileOnce()
                if (!hasExplicitUpdate) {
                    if (existing != null) {
                        _profileState.value = existing
                        _composeProfileState.value = existing
                        saveToPrefs(existing)
                    } else {
                        dao.upsertProfile(_profileState.value)
                    }
                }
            }
        }
    }

    fun updateProfile(
        name: String,
        username: String,
        customProfilePictureUri: String? = _profileState.value.customProfilePictureUri,
        statusTitle: String = _profileState.value.statusTitle
    ) {
        hasExplicitUpdate = true
        val current = _profileState.value
        val cleanName = name.trim().ifEmpty { current.name }
        val cleanUsername = username.trim().removePrefix("@").trim().ifEmpty { current.username }
        val cleanStatus = statusTitle.trim().ifEmpty { current.statusTitle }
        val cleanPicUri = customProfilePictureUri?.trim()?.takeIf { it.isNotEmpty() }

        val updated = current.copy(
            id = 1,
            name = cleanName,
            username = cleanUsername,
            statusTitle = cleanStatus,
            customProfilePictureUri = cleanPicUri
        )

        // Update state and preferences immediately so UI and navigation re-opens are instant
        _profileState.value = updated
        _composeProfileState.value = updated
        saveToPrefs(updated)

        // Persist asynchronously in Room database
        scope.launch {
            runCatching {
                dao.upsertProfile(updated)
            }
        }
    }

    fun updateSelectedAvatar(avatarId: String) {
        hasExplicitUpdate = true
        val current = _profileState.value
        val cleanAvatarId = avatarId.trim().ifEmpty { current.selectedAvatarId }
        val updated = current.copy(
            id = 1,
            selectedAvatarId = cleanAvatarId
        )

        _profileState.value = updated
        _composeProfileState.value = updated
        saveToPrefs(updated)

        scope.launch {
            runCatching {
                dao.upsertProfile(updated)
            }
        }
    }

    private fun loadInitialFromPrefs(): UserProfileEntity {
        val name = prefs.getString(KEY_NAME, "Krish")?.takeIf { it.isNotBlank() } ?: "Krish"
        val username = prefs.getString(KEY_USERNAME, "krish")?.takeIf { it.isNotBlank() } ?: "krish"
        val statusTitle = prefs.getString(KEY_STATUS, "Level 3 • Young Tree")?.takeIf { it.isNotBlank() }
            ?: "Level 3 • Young Tree"
        val selectedAvatarId = prefs.getString(KEY_AVATAR_ID, "avatar_red_tree_logo")?.takeIf { it.isNotBlank() }
            ?: "avatar_red_tree_logo"
        val customProfilePictureUri = prefs.getString(KEY_PROFILE_PIC_URI, null)?.takeIf { it.isNotBlank() }
        return UserProfileEntity(
            id = 1,
            name = name,
            username = username,
            statusTitle = statusTitle,
            selectedAvatarId = selectedAvatarId,
            customProfilePictureUri = customProfilePictureUri
        )
    }

    private fun saveToPrefs(profile: UserProfileEntity) {
        prefs.edit()
            .putString(KEY_NAME, profile.name)
            .putString(KEY_USERNAME, profile.username)
            .putString(KEY_STATUS, profile.statusTitle)
            .putString(KEY_AVATAR_ID, profile.selectedAvatarId)
            .putString(KEY_PROFILE_PIC_URI, profile.customProfilePictureUri)
            .commit()
    }

    companion object {
        private const val PREFS_NAME = "red_tree_user_profile_prefs"
        private const val KEY_NAME = "profile_name"
        private const val KEY_USERNAME = "profile_username"
        private const val KEY_STATUS = "profile_status_title"
        private const val KEY_AVATAR_ID = "profile_selected_avatar_id"
        private const val KEY_PROFILE_PIC_URI = "profile_custom_picture_uri"

        @Volatile
        private var INSTANCE: UserProfileRepository? = null

        fun getInstance(context: Context): UserProfileRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: UserProfileRepository(context).also { INSTANCE = it }
            }
        }

        fun resetInMemoryInstance() {
            synchronized(this) {
                INSTANCE = null
            }
        }

        fun resetAllPersistenceForTesting(context: Context) {
            synchronized(this) {
                val appContext = context.applicationContext
                appContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                    .edit()
                    .clear()
                    .commit()
                com.example.ui.screens.AchievementManager.resetInMemoryStateForTesting()
                com.example.ui.screens.AuthSessionManager.clearSession(appContext)
                StudySessionRepository.resetForTesting(appContext)
                RedTreeDatabase.INSTANCE?.close()
                RedTreeDatabase.INSTANCE = null
                appContext.deleteDatabase("red_tree_profile.db")
                INSTANCE = null
            }
        }
    }
}
