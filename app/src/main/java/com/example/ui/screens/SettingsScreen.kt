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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.LatencyMode
import com.example.audio.VoicePreset
import com.example.ui.components.GamerHeader
import com.example.ui.components.SliderWithLabel
import com.example.ui.theme.CyberCardBg
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberNeonGreen
import com.example.ui.theme.CyberPink
import com.example.ui.theme.CyberPurple
import com.example.ui.theme.CyberYellow
import com.example.viewmodel.VoiceChangerViewModel

@Composable
fun SettingsScreen(
    viewModel: VoiceChangerViewModel,
    onBack: () -> Unit
) {
    val settings by viewModel.userSettings.collectAsState()
    val processor = viewModel.processor

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text("Settings & Audio Configuration", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Gain & Volume Master Controls
            item {
                Card(colors = CardDefaults.cardColors(containerColor = CyberCardBg)) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("AUDIO ENGINE GAIN & VOLUME", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = CyberCyan)
                        Spacer(modifier = Modifier.height(8.dp))

                        SliderWithLabel(
                            label = "Input Gain",
                            value = processor.inputGain,
                            onValueChange = {
                                processor.inputGain = it
                                viewModel.settingsRepo.updateSettings { copy(inputGain = it) }
                            },
                            valueRange = 0.2f..3.0f,
                            accentColor = CyberCyan
                        )

                        SliderWithLabel(
                            label = "Master Output Volume",
                            value = processor.outputVolume,
                            onValueChange = {
                                processor.outputVolume = it
                                viewModel.settingsRepo.updateSettings { copy(outputVolume = it) }
                            },
                            valueRange = 0.0f..2.0f,
                            accentColor = CyberNeonGreen
                        )
                    }
                }
            }

            // Latency / Performance Mode
            item {
                Card(colors = CardDefaults.cardColors(containerColor = CyberCardBg)) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("DSP LATENCY & BUFFER MODE", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = CyberCyan)
                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            LatencyMode.values().forEach { mode ->
                                val isSelected = viewModel.audioEngine.latencyMode == mode
                                Button(
                                    onClick = {
                                        viewModel.audioEngine.latencyMode = mode
                                        viewModel.settingsRepo.updateSettings { copy(latencyMode = mode) }
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (isSelected) CyberCyan else Color(0xFF23283D)
                                    ),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        text = mode.name.replace("_", " "),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) Color.Black else Color.White
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Privacy & Safety Information Statement
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF131B2A)),
                    modifier = Modifier.border(1.dp, CyberNeonGreen.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Lock, contentDescription = "Privacy", tint = CyberNeonGreen)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("100% LOCAL DEVICE PRIVACY & SAFETY", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = CyberNeonGreen)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "FF Voice Changer processes all microphone audio locally on your Android device in real time. Microphone audio is NEVER recorded secretly or uploaded to remote cloud servers. No permissions beyond Audio Capture and Foreground Service are ever requested.",
                            fontSize = 11.sp,
                            color = Color.LightGray,
                            lineHeight = 15.sp
                        )
                    }
                }
            }
        }
    }
}
