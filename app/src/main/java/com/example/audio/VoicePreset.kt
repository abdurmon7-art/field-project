package com.example.audio

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assistant
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.ChildCare
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Female
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Male
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Nightlight
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.WavingHand
import androidx.compose.ui.graphics.vector.ImageVector

enum class FilterType {
    NONE, BANDPASS_RADIO, BANDPASS_TELEPHONE, HIGHPASS, LOWPASS, ROBOT_MOD, ALIEN_RING
}

data class VoicePreset(
    val id: String,
    val name: String,
    val description: String,
    val pitchFactor: Float,      // 0.5f to 2.0f
    val formantShift: Float,     // 0.5f to 2.0f
    val intensity: Float = 1.0f,  // 0.0f to 1.0f
    val reverbMix: Float = 0.0f,  // 0.0f to 1.0f
    val echoDelayMs: Int = 0,    // 0 to 500 ms
    val echoFeedback: Float = 0.0f,// 0.0f to 0.9f
    val noiseThreshold: Float = 0.02f,
    val filterType: FilterType = FilterType.NONE,
    val isCustomizable: Boolean = false,
    val icon: ImageVector = Icons.Default.RecordVoiceOver
) {
    companion object {
        val ALL_PRESETS = listOf(
            VoicePreset(
                id = "natural_female",
                name = "Natural Female",
                description = "Smooth feminine pitch and resonant formant shift",
                pitchFactor = 1.35f,
                formantShift = 1.22f,
                reverbMix = 0.08f,
                icon = Icons.Default.Female
            ),
            VoicePreset(
                id = "natural_male",
                name = "Natural Male",
                description = "Deeper masculine tone with resonant chest cavity warmth",
                pitchFactor = 0.82f,
                formantShift = 0.86f,
                reverbMix = 0.05f,
                icon = Icons.Default.Male
            ),
            VoicePreset(
                id = "young_female",
                name = "Young Female",
                description = "Bright, youthful feminine cadence with high formant frequency",
                pitchFactor = 1.45f,
                formantShift = 1.30f,
                reverbMix = 0.10f,
                icon = Icons.Default.Face
            ),
            VoicePreset(
                id = "young_male",
                name = "Young Male",
                description = "Clear, vibrant young adult tone with balanced vocal range",
                pitchFactor = 0.92f,
                formantShift = 0.94f,
                reverbMix = 0.05f,
                icon = Icons.Default.Person
            ),
            VoicePreset(
                id = "baby",
                name = "Baby",
                description = "Ultra high pitch and fast formant modulation",
                pitchFactor = 1.70f,
                formantShift = 1.50f,
                filterType = FilterType.HIGHPASS,
                icon = Icons.Default.ChildCare
            ),
            VoicePreset(
                id = "child",
                name = "Child",
                description = "Playful child voice profile with light high-mid harmonics",
                pitchFactor = 1.48f,
                formantShift = 1.25f,
                icon = Icons.Default.WavingHand
            ),
            VoicePreset(
                id = "deep_male",
                name = "Deep Male",
                description = "Sub-bass reinforced deep baritone voice with heavy rumble",
                pitchFactor = 0.65f,
                formantShift = 0.72f,
                reverbMix = 0.15f,
                icon = Icons.Default.Shield
            ),
            VoicePreset(
                id = "high_female",
                name = "High Female",
                description = "Elevated soprano vocal range with crisp treble emphasis",
                pitchFactor = 1.60f,
                formantShift = 1.38f,
                icon = Icons.Default.Assistant
            ),
            VoicePreset(
                id = "robot",
                name = "Robot",
                description = "Synthesized robotic voice with pitch quantization and metallic ring",
                pitchFactor = 1.0f,
                formantShift = 1.0f,
                filterType = FilterType.ROBOT_MOD,
                icon = Icons.Default.Memory
            ),
            VoicePreset(
                id = "alien",
                name = "Alien",
                description = "Extraterrestrial chorus effect with ring modulation vibrato",
                pitchFactor = 1.25f,
                formantShift = 0.85f,
                reverbMix = 0.35f,
                echoDelayMs = 120,
                echoFeedback = 0.40f,
                filterType = FilterType.ALIEN_RING,
                icon = Icons.Default.Nightlight
            ),
            VoicePreset(
                id = "echo",
                name = "Echo",
                description = "Spatial voice with multi-stage repeating delay reflections",
                pitchFactor = 1.0f,
                formantShift = 1.0f,
                reverbMix = 0.25f,
                echoDelayMs = 220,
                echoFeedback = 0.55f,
                icon = Icons.Default.GraphicEq
            ),
            VoicePreset(
                id = "monster",
                name = "Monster",
                description = "Sub-octave pitched demonic creature effect with distortion",
                pitchFactor = 0.52f,
                formantShift = 0.60f,
                reverbMix = 0.30f,
                echoDelayMs = 80,
                echoFeedback = 0.30f,
                icon = Icons.Default.Navigation
            ),
            VoicePreset(
                id = "radio",
                name = "Radio",
                description = "Tactical walkie-talkie bandpass filter with subtle noise",
                pitchFactor = 1.0f,
                formantShift = 1.0f,
                filterType = FilterType.BANDPASS_RADIO,
                icon = Icons.Default.Radio
            ),
            VoicePreset(
                id = "telephone",
                name = "Telephone",
                description = "Classic landline phone bandpass audio frequency response",
                pitchFactor = 1.0f,
                formantShift = 1.0f,
                filterType = FilterType.BANDPASS_TELEPHONE,
                icon = Icons.Default.Call
            ),
            VoicePreset(
                id = "chipmunk",
                name = "Chipmunk",
                description = "Hyper-speed high pitch squirrel voice",
                pitchFactor = 1.85f,
                formantShift = 1.60f,
                icon = Icons.Default.Mic
            ),
            VoicePreset(
                id = "low_voice",
                name = "Low Voice",
                description = "Shifted down pitch for anonymous low rumble vocal tone",
                pitchFactor = 0.75f,
                formantShift = 0.85f,
                icon = Icons.Default.Tune
            ),
            VoicePreset(
                id = "high_voice",
                name = "High Voice",
                description = "Shifted up pitch for lightweight energetic voice tone",
                pitchFactor = 1.40f,
                formantShift = 1.15f,
                icon = Icons.Default.Build
            ),
            VoicePreset(
                id = "custom_voice",
                name = "Custom Voice",
                description = "Fully user-configurable pitch, formant, reverb, echo and filters",
                pitchFactor = 1.0f,
                formantShift = 1.0f,
                isCustomizable = true,
                icon = Icons.Default.Tune
            )
        )

        fun getById(id: String): VoicePreset {
            return ALL_PRESETS.find { it.id == id } ?: ALL_PRESETS.first()
        }
    }
}
