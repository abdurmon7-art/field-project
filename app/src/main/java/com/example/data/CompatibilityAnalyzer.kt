package com.example.data

import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioManager
import android.os.Build

data class GameCompatibilityItem(
    val gameName: String,
    val packageName: String,
    val status: String, // "Supported", "Partially Supported", "Requires Audio Routing"
    val routingAdvice: String,
    val iconName: String
)

data class CompatibilityReport(
    val hasMicrophoneHardware: Boolean,
    val supportsLowLatencyAudio: Boolean,
    val supportsProAudio: Boolean,
    val nativeSampleRate: Int,
    val nativeBufferSize: Int,
    val isHeadsetConnected: Boolean,
    val androidVersion: String,
    val compatibilitySummary: String,
    val gamesList: List<GameCompatibilityItem>
)

class CompatibilityAnalyzer(
    private val context: Context
) {
    fun analyzeSystem(): CompatibilityReport {
        val pm = context.packageManager
        val am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

        val hasMic = pm.hasSystemFeature(PackageManager.FEATURE_MICROPHONE)
        val lowLatency = pm.hasSystemFeature(PackageManager.FEATURE_AUDIO_LOW_LATENCY)
        val proAudio = pm.hasSystemFeature(PackageManager.FEATURE_AUDIO_PRO)

        val sampleRateStr = am.getProperty(AudioManager.PROPERTY_OUTPUT_SAMPLE_RATE)
        val sampleRate = sampleRateStr?.toIntOrNull() ?: 44100

        val bufferSizeStr = am.getProperty(AudioManager.PROPERTY_OUTPUT_FRAMES_PER_BUFFER)
        val bufferSize = bufferSizeStr?.toIntOrNull() ?: 256

        val isHeadset = am.isWiredHeadsetOn || am.isBluetoothScoOn || am.isBluetoothA2dpOn

        val games = listOf(
            GameCompatibilityItem(
                gameName = "Free Fire / Free Fire MAX",
                packageName = "com.dts.freefireth",
                status = "Supported via Headset Loopback",
                routingAdvice = "Use Headset / Bluetooth routing mode with FF Voice Changer active in background.",
                iconName = "free_fire"
            ),
            GameCompatibilityItem(
                gameName = "PUBG Mobile",
                packageName = "com.tencent.ig",
                status = "Partially Supported",
                routingAdvice = "Android sandboxing isolates in-game voice chat. Use overlay recorder or headset mic split.",
                iconName = "pubg"
            ),
            GameCompatibilityItem(
                gameName = "Call of Duty: Mobile",
                packageName = "com.activision.callofduty.shooter",
                status = "Requires Audio Routing",
                routingAdvice = "Connect wired/BT headset before launching COD Mobile with Voice Changer active.",
                iconName = "codm"
            ),
            GameCompatibilityItem(
                gameName = "Discord Voice Chat",
                packageName = "com.discord",
                status = "Supported via Audio Stream",
                routingAdvice = "Enable Foreground Microphone Service and select Headset Mic source in Discord.",
                iconName = "discord"
            ),
            GameCompatibilityItem(
                gameName = "Roblox",
                packageName = "com.roblox.client",
                status = "Supported via Background Service",
                routingAdvice = "Run FF Voice Changer in Foreground Service mode.",
                iconName = "roblox"
            ),
            GameCompatibilityItem(
                gameName = "Brawl Stars",
                packageName = "com.supercell.brawlstars",
                status = "Partially Supported",
                routingAdvice = "Use Quick Voice Preview & Share clip features for quick team banter.",
                iconName = "brawlstars"
            )
        )

        val summary = when {
            lowLatency && isHeadset -> "Excellent! Ultra Low Latency audio processing supported with connected headset."
            lowLatency -> "Optimal Hardware. Low Latency DSP active. Plug in headphones for zero feedback loop."
            else -> "Standard Latency. Supported with Balanced Buffer mode."
        }

        return CompatibilityReport(
            hasMicrophoneHardware = hasMic,
            supportsLowLatencyAudio = lowLatency,
            supportsProAudio = proAudio,
            nativeSampleRate = sampleRate,
            nativeBufferSize = bufferSize,
            isHeadsetConnected = isHeadset,
            androidVersion = "Android ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})",
            compatibilitySummary = summary,
            gamesList = games
        )
    }
}
