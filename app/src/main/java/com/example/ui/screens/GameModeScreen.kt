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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Gamepad
import androidx.compose.material.icons.filled.Headset
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.GameCompatibilityItem
import com.example.ui.components.CompatibilityBadge
import com.example.ui.components.GamerHeader
import com.example.ui.theme.CyberCardBg
import com.example.ui.theme.CyberCardBorder
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberNeonGreen
import com.example.ui.theme.CyberPink
import com.example.ui.theme.CyberPurple
import com.example.ui.theme.CyberYellow
import com.example.viewmodel.VoiceChangerViewModel

@Composable
fun GameModeScreen(
    viewModel: VoiceChangerViewModel,
    onOpenSettings: () -> Unit,
    onOpenDiagnostic: () -> Unit
) {
    val isEngineRunning by viewModel.isEngineRunning.collectAsState()
    val micLevel by viewModel.micAudioLevel.collectAsState()
    val selectedPreset by viewModel.selectedPreset.collectAsState()
    val selectedGamePkg by viewModel.selectedGamePackage.collectAsState()
    val report by viewModel.compatibilityReport.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        GamerHeader(
            title = "Game Mode Center",
            subtitle = "Low-Latency In-Game Voice Processing",
            isEngineRunning = isEngineRunning,
            onOpenSettings = onOpenSettings,
            onOpenDiagnostic = onOpenDiagnostic
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Android OS Sandbox Notice
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1B2032)),
                    modifier = Modifier.border(1.dp, CyberCyan.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Info",
                            tint = CyberCyan,
                            modifier = Modifier
                                .size(24.dp)
                                .padding(top = 2.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Android Audio Architecture Notice",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = CyberCyan
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Android security sandboxing prevents non-system apps from directly replacing another game's internal mic stream. FF Voice Changer uses supported real-time audio routing, foreground service processing, and headset loopback for maximum compatibility.",
                                fontSize = 11.sp,
                                color = Color.LightGray.copy(alpha = 0.85f),
                                lineHeight = 15.sp
                            )
                        }
                    }
                }
            }

            // Real-Time Control Engine Panel
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = CyberCardBg),
                    modifier = Modifier.border(1.dp, if (isEngineRunning) CyberNeonGreen else CyberCardBorder, RoundedCornerShape(16.dp))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("AUDIO PROCESSING STATUS", fontSize = 10.sp, color = CyberCyan, fontWeight = FontWeight.Bold)
                                Text(
                                    text = if (isEngineRunning) "ACTIVE (Low Latency DSP)" else "STOPPED",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isEngineRunning) CyberNeonGreen else Color.Gray
                                )
                            }

                            Button(
                                onClick = { viewModel.toggleVoiceChangerEngine() },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isEngineRunning) Color(0xFF3B121A) else CyberNeonGreen
                                )
                            ) {
                                Icon(
                                    imageVector = if (isEngineRunning) Icons.Default.Stop else Icons.Default.PlayArrow,
                                    contentDescription = "Toggle",
                                    tint = if (isEngineRunning) Color(0xFFFF6B81) else Color.Black
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isEngineRunning) "Stop Processing" else "Start Processing",
                                    fontWeight = FontWeight.Bold,
                                    color = if (isEngineRunning) Color(0xFFFF6B81) else Color.Black
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Mic status & active effect indicator
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF141726))
                                .padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Mic, contentDescription = "Mic", tint = CyberCyan, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Selected Effect: ", fontSize = 12.sp, color = Color.Gray)
                                Text(selectedPreset.name, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Headset, contentDescription = "Headset", tint = CyberPink, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (report.isHeadsetConnected) "Headset Active" else "No Headset",
                                    fontSize = 11.sp,
                                    color = if (report.isHeadsetConnected) CyberNeonGreen else CyberYellow
                                )
                            }
                        }
                    }
                }
            }

            // Supported Mobile Games Selector
            item {
                Text(
                    text = "SELECT SUPPORTED GAME OR VOICE LOBBY",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.LightGray,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            items(report.gamesList) { game ->
                val isSelected = game.packageName == selectedGamePkg
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .border(
                            width = if (isSelected) 2.dp else 1.dp,
                            color = if (isSelected) CyberPink else CyberCardBorder,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .clickable { viewModel.setSelectedGamePackage(game.packageName) },
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) Color(0xFF261D33) else CyberCardBg
                    )
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Gamepad, contentDescription = "Game", tint = CyberPink, modifier = Modifier.size(24.dp))
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(game.gameName, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color.White)
                            }
                            CompatibilityBadge(status = game.status)
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = game.routingAdvice,
                            fontSize = 11.sp,
                            color = Color.LightGray.copy(alpha = 0.8f)
                        )
                    }
                }
            }
        }
    }
}
