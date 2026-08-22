package com.example.data

import android.content.Context
import com.example.audio.LatencyMode
import com.example.audio.VoicePreset
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class UserSettings(
    val defaultEffectId: String = "natural_female",
    val inputGain: Float = 1.0f,
    val outputVolume: Float = 1.0f,
    val pitchFactor: Float = 1.0f,
    val formantShift: Float = 1.0f,
    val isNoiseReductionEnabled: Boolean = true,
    val echoDelayMs: Int = 0,
    val echoFeedback: Float = 0.0f,
    val reverbMix: Float = 0.0f,
    val latencyMode: LatencyMode = LatencyMode.BALANCED,
    val isNotificationsEnabled: Boolean = true,
    val themeAccent: String = "Cyber Cyan"
)

class UserSettingsRepository(context: Context) {
    private val prefs = context.getSharedPreferences("ff_voice_changer_prefs", Context.MODE_PRIVATE)

    private val _settings = MutableStateFlow(loadSettings())
    val settings: StateFlow<UserSettings> = _settings.asStateFlow()

    private fun loadSettings(): UserSettings {
        return UserSettings(
            defaultEffectId = prefs.getString("default_effect_id", "natural_female") ?: "natural_female",
            inputGain = prefs.getFloat("input_gain", 1.0f),
            outputVolume = prefs.getFloat("output_volume", 1.0f),
            pitchFactor = prefs.getFloat("pitch_factor", 1.0f),
            formantShift = prefs.getFloat("formant_shift", 1.0f),
            isNoiseReductionEnabled = prefs.getBoolean("noise_reduction", true),
            echoDelayMs = prefs.getInt("echo_delay_ms", 0),
            echoFeedback = prefs.getFloat("echo_feedback", 0.0f),
            reverbMix = prefs.getFloat("reverb_mix", 0.0f),
            latencyMode = try {
                LatencyMode.valueOf(prefs.getString("latency_mode", "BALANCED") ?: "BALANCED")
            } catch (e: Exception) {
                LatencyMode.BALANCED
            },
            isNotificationsEnabled = prefs.getBoolean("notifications_enabled", true),
            themeAccent = prefs.getString("theme_accent", "Cyber Cyan") ?: "Cyber Cyan"
        )
    }

    fun updateSettings(update: UserSettings.() -> UserSettings) {
        val current = _settings.value
        val updated = current.update()
        _settings.value = updated

        prefs.edit().apply {
            putString("default_effect_id", updated.defaultEffectId)
            putFloat("input_gain", updated.inputGain)
            putFloat("output_volume", updated.outputVolume)
            putFloat("pitch_factor", updated.pitchFactor)
            putFloat("formant_shift", updated.formantShift)
            putBoolean("noise_reduction", updated.isNoiseReductionEnabled)
            putInt("echo_delay_ms", updated.echoDelayMs)
            putFloat("echo_feedback", updated.echoFeedback)
            putFloat("reverb_mix", updated.reverbMix)
            putString("latency_mode", updated.latencyMode.name)
            putBoolean("notifications_enabled", updated.isNotificationsEnabled)
            putString("theme_accent", updated.themeAccent)
            apply()
        }
    }
}
