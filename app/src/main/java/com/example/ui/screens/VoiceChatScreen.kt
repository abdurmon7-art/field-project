package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Headset
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Warning
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
import com.example.ui.components.CompatibilityBadge
import com.example.ui.components.GamerHeader
import com.example.ui.theme.CyberCardBg
import com.example.ui.theme.CyberCardBorder
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberNeonGreen
import com.example.ui.theme.CyberPink
import com.example.ui.theme.CyberPurple
import com.example.ui.theme.CyberRed
import com.example.ui.theme.CyberYellow
import com.example.viewmodel.VoiceChangerViewModel

@Composable
fun VoiceChatScreen(
    viewModel: VoiceChangerViewModel,
    onOpenSettings: () -> Unit,
    onNavigateToRecorder: () -> Unit
) {
    val isEngineRunning by viewModel.isEngineRunning.collectAsState()
    val selectedPreset by viewModel.selectedPreset.collectAsState()
    val report by viewModel.compatibilityReport.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        GamerHeader(
            title = "Calling & Voice Chat",
            subtitle = "Call Audio Routing & Voice Compatibility",
            isEngineRunning = isEngineRunning,
            onOpenSettings = onOpenSettings
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Android Calling Limitation Explanation
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF22172C)),
                    modifier = Modifier.border(1.dp, CyberPurple.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Info",
                            tint = CyberPurple,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Standard Phone Call Stream Isolation",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = CyberPurple
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Android OS isolates cellular phone calls (In-Call audio stream) for privacy. Third-party apps cannot inject modified microphone audio directly into standard cellular telephony calls without carrier/hardware level access. For VOIP calling apps (Discord, Telegram, WhatsApp Web, Games), audio routing via Foreground Service or Headset loopback is supported.",
                                fontSize = 11.sp,
                                color = Color.LightGray.copy(alpha = 0.85f),
                                lineHeight = 15.sp
                            )
                        }
                    }
                }
            }

            // Compatibility Matrix per Calling Environment
            item {
                Text("CALLING ENVIRONMENTS STATUS", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = CyberCyan)
            }

            item {
                Card(colors = CardDefaults.cardColors(containerColor = CyberCardBg)) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Discord / Gaming VOIP", fontWeight = FontWeight.Bold, color = Color.White)
                            CompatibilityBadge("Supported")
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "Runs via background microphone foreground service. Active voice effect: ${selectedPreset.name}",
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                    }
                }
            }

            item {
                Card(colors = CardDefaults.cardColors(containerColor = CyberCardBg)) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("WhatsApp / Telegram Voice Notes", fontWeight = FontWeight.Bold, color = Color.White)
                            CompatibilityBadge("Supported via Recorder")
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "Record changed voice in FF Voice Changer and tap Share directly to WhatsApp/Telegram.",
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                    }
                }
            }

            item {
                Card(colors = CardDefaults.cardColors(containerColor = CyberCardBg)) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Cellular Phone Calls (GSM/LTE)", fontWeight = FontWeight.Bold, color = Color.White)
                            CompatibilityBadge("Not Supported by OS")
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "Android OS restricts third-party audio injection into active GSM phone calls. Alternative: Use Voice Recorder share or external headset routing.",
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                    }
                }
            }

            // Quick Voice Recording & Share Alternative
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF131A2D)),
                    modifier = Modifier.border(1.dp, CyberCyan, RoundedCornerShape(14.dp))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("RECORD & SHARE CHANGED VOICE CLIP", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = CyberCyan)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            "The guaranteed alternative for any messenger or call application is recording a voice note with your selected effect (${selectedPreset.name}) and sharing it directly.",
                            fontSize = 11.sp,
                            color = Color.LightGray
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = onNavigateToRecorder,
                            colors = ButtonDefaults.buttonColors(containerColor = CyberCyan),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Share, contentDescription = "Share", tint = Color.Black)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Open Voice Clip Recorder", color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
