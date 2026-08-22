package com.example.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Gamepad
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Tune
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector? = null) {
    object Splash : Screen("splash", "Splash")
    object Home : Screen("home", "Home", Icons.Default.Home)
    object VoiceEffects : Screen("voice_effects", "Effects", Icons.Default.Tune)
    object GameMode : Screen("game_mode", "Game Mode", Icons.Default.Gamepad)
    object VoiceChat : Screen("voice_chat", "Voice Chat", Icons.Default.Call)
    object VoiceRecorder : Screen("voice_recorder", "Recorder", Icons.Default.Mic)
    object Settings : Screen("settings", "Settings", Icons.Default.Settings)
    object Permissions : Screen("permissions", "Permissions", Icons.Default.Security)
    object Compatibility : Screen("compatibility", "Diagnostic", Icons.Default.Assessment)

    companion object {
        val bottomNavItems = listOf(Home, VoiceEffects, GameMode, VoiceChat, VoiceRecorder)
    }
}
