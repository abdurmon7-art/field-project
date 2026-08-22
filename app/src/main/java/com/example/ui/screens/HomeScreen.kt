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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Gamepad
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.VoicePreset
import com.example.ui.components.AudioLevelMeter
import com.example.ui.components.GamerHeader
import com.example.ui.components.MicPulseButton
import com.example.ui.theme.CyberCardBg
import com.example.ui.theme.CyberCardBorder
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberNeonGreen
import com.example.ui.theme.CyberPink
import com.example.ui.theme.CyberPurple
import com.example.ui.theme.CyberYellow
import com.example.viewmodel.VoiceChangerViewModel

@Composable
fun HomeScreen(
    viewModel: VoiceChangerViewModel,
    onNavigateToEffects: () -> Unit,
    onNavigateToGameMode: () -> Unit,
    onNavigateToVoiceChat: () -> Unit,
    onNavigateToRecorder: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenPermissions: () -> Unit,
    onOpenDiagnostic: () -> Unit
) {
    val isEngineRunning by viewModel.isEngineRunning.collectAsState()
    val micLevel by viewModel.micAudioLevel.collectAsState()
    val selectedPreset by viewModel.selectedPreset.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    val popularPresets = listOf(
        VoicePreset.getById("natural_female"),
        VoicePreset.getById("natural_male"),
        VoicePreset.getById("baby"),
        VoicePreset.getById("robot"),
        VoicePreset.getById("deep_male"),
        VoicePreset.getById("child")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        GamerHeader(
            title = "FF Voice Changer",
            subtitle = "Change your voice in real time",
            isEngineRunning = isEngineRunning,
            onOpenSettings = onOpenSettings,
            onOpenPermissions = onOpenPermissions,
            onOpenDiagnostic = onOpenDiagnostic
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 24.dp)
        ) {
            // Error banner if mic error occurred
            if (errorMessage != null) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF3B121A)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = errorMessage ?: "",
                            color = Color(0xFFFF6B81),
                            fontSize = 12.sp,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = "DISMISS",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = CyberCyan,
                            modifier = Modifier.clickable { viewModel.clearError() }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Main glowing microphone trigger button
            MicPulseButton(
                isEngineRunning = isEngineRunning,
                onClick = { viewModel.toggleVoiceChangerEngine() },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Audio level meter bar
            AudioLevelMeter(
                level = micLevel,
                isEngineRunning = isEngineRunning
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Current Active Preset Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, CyberCyan.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                    .clickable { onNavigateToEffects() }
                    .testTag("active_preset_card"),
                colors = CardDefaults.cardColors(containerColor = CyberCardBg)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(CyberCyan.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = selectedPreset.icon,
                                contentDescription = selectedPreset.name,
                                tint = CyberCyan,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("ACTIVE VOICE EFFECT", fontSize = 10.sp, color = CyberCyan, fontWeight = FontWeight.Bold)
                            Text(selectedPreset.name, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            Text(selectedPreset.description, fontSize = 11.sp, color = Color.LightGray.copy(alpha = 0.8f))
                        }
                    }
                    Icon(
                        imageVector = Icons.Default.Tune,
                        contentDescription = "Tune",
                        tint = CyberCyan,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Popular Voice Effects Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Popular Effects", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Text(
                    text = "See All 18 >",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = CyberCyan,
                    modifier = Modifier.clickable { onNavigateToEffects() }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Popular Effects Horizontal Carousel
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item { Spacer(modifier = Modifier.width(16.dp)) }
                items(popularPresets) { preset ->
                    val isSelected = selectedPreset.id == preset.id
                    Card(
                        modifier = Modifier
                            .width(110.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .border(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) CyberCyan else CyberCardBorder,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable { viewModel.selectPreset(preset) },
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) Color(0xFF1F283F) else CyberCardBg
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = preset.icon,
                                contentDescription = preset.name,
                                tint = if (isSelected) CyberCyan else Color.LightGray,
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = preset.name,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) CyberCyan else Color.White
                            )
                        }
                    }
                }
                item { Spacer(modifier = Modifier.width(16.dp)) }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Action Quick Shortcut Cards (Game Mode, Voice Chat, Voice Recorder)
            Text(
                text = "Voice Modes",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Game Mode Card
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .clickable { onNavigateToGameMode() },
                    colors = CardDefaults.cardColors(containerColor = CyberCardBg)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Icon(Icons.Default.Gamepad, contentDescription = "Game Mode", tint = CyberPink, modifier = Modifier.size(28.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Game Mode", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.White)
                        Text("Free Fire & Games", fontSize = 10.sp, color = Color.Gray)
                    }
                }

                // Voice Chat Card
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .clickable { onNavigateToVoiceChat() },
                    colors = CardDefaults.cardColors(containerColor = CyberCardBg)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Icon(Icons.Default.Call, contentDescription = "Voice Chat", tint = CyberPurple, modifier = Modifier.size(28.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Voice Chat", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.White)
                        Text("Calls & Lobbies", fontSize = 10.sp, color = Color.Gray)
                    }
                }

                // Voice Recorder Card
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .clickable { onNavigateToRecorder() },
                    colors = CardDefaults.cardColors(containerColor = CyberCardBg)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Icon(Icons.Default.Mic, contentDescription = "Voice Recorder", tint = CyberNeonGreen, modifier = Modifier.size(28.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Recorder", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.White)
                        Text("Save & Share", fontSize = 10.sp, color = Color.Gray)
                    }
                }
            }
        }
    }
}
