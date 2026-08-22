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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FiberManualRecord
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.example.data.RecordingItem
import com.example.ui.components.GamerHeader
import com.example.ui.theme.CyberCardBg
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberNeonGreen
import com.example.ui.theme.CyberPink
import com.example.ui.theme.CyberPurple
import com.example.ui.theme.CyberRed
import com.example.viewmodel.VoiceChangerViewModel

@Composable
fun VoiceRecorderScreen(
    viewModel: VoiceChangerViewModel,
    onOpenSettings: () -> Unit
) {
    val isRecording by viewModel.isRecording.collectAsState()
    val recordingDuration by viewModel.recordingDuration.collectAsState()
    val recordingsList by viewModel.recordingsList.collectAsState()
    val currentlyPlaying by viewModel.currentlyPlayingFilePath.collectAsState()
    val selectedPreset by viewModel.selectedPreset.collectAsState()
    val isEngineRunning by viewModel.isEngineRunning.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        GamerHeader(
            title = "Voice Recorder & Export",
            subtitle = "Record & Share Changed Audio Clips",
            isEngineRunning = isEngineRunning,
            onOpenSettings = onOpenSettings
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Recorder Console
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = CyberCardBg),
                    modifier = Modifier.border(
                        1.dp,
                        if (isRecording) CyberRed else CyberCyan.copy(alpha = 0.5f),
                        RoundedCornerShape(16.dp)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (isRecording) "RECORDING IN PROGRESS..." else "READY TO RECORD",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isRecording) CyberRed else CyberCyan
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        val mins = recordingDuration / 60
                        val secs = recordingDuration % 60
                        val timerText = String.format("%02d:%02d", mins, secs)

                        Text(
                            text = timerText,
                            fontSize = 36.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )

                        Text(
                            text = "Active Preset: ${selectedPreset.name}",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            if (!isRecording) {
                                // Record Changed Voice
                                Button(
                                    onClick = { viewModel.startRecording(recordOriginal = false) },
                                    colors = ButtonDefaults.buttonColors(containerColor = CyberCyan)
                                ) {
                                    Icon(Icons.Default.FiberManualRecord, contentDescription = "Rec Changed", tint = Color.Black)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Changed Voice", color = Color.Black, fontWeight = FontWeight.Bold)
                                }

                                // Record Original Voice
                                Button(
                                    onClick = { viewModel.startRecording(recordOriginal = true) },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF262E48))
                                ) {
                                    Icon(Icons.Default.Mic, contentDescription = "Rec Original", tint = Color.White)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Original Voice", color = Color.White, fontWeight = FontWeight.Bold)
                                }
                            } else {
                                // Stop Recording Button
                                Button(
                                    onClick = { viewModel.stopRecording() },
                                    colors = ButtonDefaults.buttonColors(containerColor = CyberRed)
                                ) {
                                    Icon(Icons.Default.Stop, contentDescription = "Stop Rec", tint = Color.White)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Stop & Save Recording", color = Color.White, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }

            item {
                Text(
                    text = "SAVED AUDIO RECORDINGS (${recordingsList.size})",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.LightGray,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            if (recordingsList.isEmpty()) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF141726)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("No recordings saved yet. Tap 'Changed Voice' above to record!", fontSize = 12.sp, color = Color.Gray)
                        }
                    }
                }
            }

            items(recordingsList) { item ->
                val isPlaying = currentlyPlaying == item.filePath

                Card(
                    colors = CardDefaults.cardColors(containerColor = if (isPlaying) Color(0xFF1E2842) else CyberCardBg),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, if (isPlaying) CyberNeonGreen else CyberCardBg, RoundedCornerShape(12.dp))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = {
                                if (isPlaying) viewModel.stopPlayback() else viewModel.playRecording(item)
                            }
                        ) {
                            Icon(
                                imageVector = if (isPlaying) Icons.Default.Stop else Icons.Default.PlayArrow,
                                contentDescription = "Play/Stop",
                                tint = if (isPlaying) CyberNeonGreen else CyberCyan,
                                modifier = Modifier.size(28.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = item.title,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = Color.White,
                                maxLines = 1
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "${item.effectName} • ${item.formattedDuration} • ${item.formattedSize}",
                                fontSize = 11.sp,
                                color = Color.Gray
                            )
                        }

                        IconButton(onClick = { viewModel.shareRecording(item) }) {
                            Icon(Icons.Default.Share, contentDescription = "Share", tint = CyberPurple)
                        }

                        IconButton(onClick = { viewModel.deleteRecording(item) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = CyberRed)
                        }
                    }
                }
            }
        }
    }
}
