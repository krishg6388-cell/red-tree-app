package com.example.data

import android.content.Context
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.example.R

enum class CompanionCharacter(
    val id: String,
    val displayName: String,
    val genderLabel: String,
    val titleBadge: String,
    val personality: String,
    val description: String,
    val auraColorHex: Long,
    val accentColorHex: Long,
    val drawableRes: Int
) {
    FOREST_CUTU(
        id = "forest_cutu",
        displayName = "Forest Cutu",
        genderLabel = "Male Companion",
        titleBadge = "Calm • Focused • Loyal",
        personality = "Calm, focused and loyal little forest buddy with a big heart. Stays by your side to help you resist distractions.",
        description = "A gentle forest spirit who loves silence, deep work, and watching the R.E.D Tree grow leaf by leaf.",
        auraColorHex = 0xFF00E676,
        accentColorHex = 0xFFFFD600,
        drawableRes = R.drawable.forest_cutu
    ),
    FOREST_CUTIE(
        id = "forest_cutie",
        displayName = "Forest Cutie",
        genderLabel = "Female Companion",
        titleBadge = "Cheerful • Caring • Inspiring",
        personality = "A bright little companion with a warm smile. Brings positivity, joy, and encouragement to your study journey.",
        description = "Adorned with delicate forest blossoms, bringing cheerful inspiration and celebrating every completed goal.",
        auraColorHex = 0xFFFF6090,
        accentColorHex = 0xFFFFAB00,
        drawableRes = R.drawable.forest_cutie
    );

    companion object {
        fun fromId(id: String?): CompanionCharacter {
            return entries.firstOrNull { it.id.equals(id, ignoreCase = true) } ?: FOREST_CUTU
        }
    }
}

data class CompanionConfig(
    val isEnabled: Boolean = true,
    val selectedCharacter: CompanionCharacter = CompanionCharacter.FOREST_CUTU,
    val customName: String = "Cutu",
    val soundEffectsEnabled: Boolean = true
)

class CompanionManager private constructor(context: Context) {
    private val appContext = context.applicationContext
    private val prefs = appContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val _configState = mutableStateOf(loadConfig())
    val configState: State<CompanionConfig> = _configState

    private fun loadConfig(): CompanionConfig {
        val enabled = prefs.getBoolean(KEY_COMPANION_ENABLED, true)
        val charId = prefs.getString(KEY_COMPANION_CHARACTER, CompanionCharacter.FOREST_CUTU.id)
        val char = CompanionCharacter.fromId(charId)
        val defaultName = if (char == CompanionCharacter.FOREST_CUTU) "Cutu" else "Cutie"
        val customName = prefs.getString(KEY_COMPANION_CUSTOM_NAME, defaultName) ?: defaultName
        val sound = prefs.getBoolean(KEY_COMPANION_SOUND, true)
        return CompanionConfig(
            isEnabled = enabled,
            selectedCharacter = char,
            customName = customName.ifBlank { defaultName },
            soundEffectsEnabled = sound
        )
    }

    fun setCompanionEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_COMPANION_ENABLED, enabled).apply()
        _configState.value = _configState.value.copy(isEnabled = enabled)
    }

    fun selectCharacter(character: CompanionCharacter) {
        val current = _configState.value
        val defaultName = if (character == CompanionCharacter.FOREST_CUTU) "Cutu" else "Cutie"
        val newName = if (current.customName == "Cutu" || current.customName == "Cutie") {
            defaultName
        } else {
            current.customName
        }
        prefs.edit()
            .putString(KEY_COMPANION_CHARACTER, character.id)
            .putString(KEY_COMPANION_CUSTOM_NAME, newName)
            .apply()
        _configState.value = current.copy(
            selectedCharacter = character,
            customName = newName
        )
    }

    fun updateCustomName(name: String) {
        val trimmed = name.trim()
        val defaultName = if (_configState.value.selectedCharacter == CompanionCharacter.FOREST_CUTU) "Cutu" else "Cutie"
        val safeName = if (trimmed.isBlank()) defaultName else trimmed.take(20)
        prefs.edit().putString(KEY_COMPANION_CUSTOM_NAME, safeName).apply()
        _configState.value = _configState.value.copy(customName = safeName)
    }

    fun setSoundEffectsEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_COMPANION_SOUND, enabled).apply()
        _configState.value = _configState.value.copy(soundEffectsEnabled = enabled)
    }

    companion object {
        private const val PREFS_NAME = "red_tree_companion_prefs"
        private const val KEY_COMPANION_ENABLED = "companion_is_enabled"
        private const val KEY_COMPANION_CHARACTER = "companion_character_id"
        private const val KEY_COMPANION_CUSTOM_NAME = "companion_custom_name"
        private const val KEY_COMPANION_SOUND = "companion_sound_enabled"

        @Volatile
        private var INSTANCE: CompanionManager? = null

        fun getInstance(context: Context): CompanionManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: CompanionManager(context).also { INSTANCE = it }
            }
        }
    }
}
