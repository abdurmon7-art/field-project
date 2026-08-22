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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Headset
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Speed
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CyberCardBg
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberNeonGreen
import com.example.ui.theme.CyberPink
import com.example.ui.theme.CyberYellow
import com.example.viewmodel.VoiceChangerViewModel

@Composable
fun CompatibilityScreen(
    viewModel: VoiceChangerViewModel,
    onBack: () -> Unit
) {
    val report by viewModel.compatibilityReport.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text("System Audio Diagnostic", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }

            IconButton(onClick = { viewModel.refreshSystemCompatibility() }) {
                Icon(Icons.Default.Refresh, contentDescription = "Refresh", tint = CyberCyan)
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Diagnostic Summary Card
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF131D2E)),
                    modifier = Modifier.border(1.dp, CyberCyan, RoundedCornerShape(14.dp))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Assessment, contentDescription = "Diag", tint = CyberCyan)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("AUDIO SYSTEM CAPABILITY REPORT", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = CyberCyan)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(report.compatibilitySummary, fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.SemiBold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(report.androidVersion, fontSize = 11.sp, color = Color.LightGray)
                    }
                }
            }

            // Specs Grid
            item {
                Text("HARDWARE AUDIO PARAMETERS", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = CyberCyan)
            }

            item {
                Card(colors = CardDefaults.cardColors(containerColor = CyberCardBg)) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Native Hardware Sample Rate", fontSize = 12.sp, color = Color.Gray)
                            Text("${report.nativeSampleRate} Hz", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = CyberCyan)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Hardware Buffer Size", fontSize = 12.sp, color = Color.Gray)
                            Text("${report.nativeBufferSize} frames", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = CyberCyan)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Low Latency Audio Feature", fontSize = 12.sp, color = Color.Gray)
                            Text(
                                text = if (report.supportsLowLatencyAudio) "SUPPORTED" else "STANDARD",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (report.supportsLowLatencyAudio) CyberNeonGreen else CyberYellow
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Headset / Bluetooth Audio Device", fontSize = 12.sp, color = Color.Gray)
                            Text(
                                text = if (report.isHeadsetConnected) "CONNECTED" else "NOT CONNECTED",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (report.isHeadsetConnected) CyberNeonGreen else CyberYellow
                            )
                        }
                    }
                }
            }

            // Advice on Android Audio Sandbox
            item {
                Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF1B2030))) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Info, contentDescription = "Info", tint = CyberPink)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Optimal Audio Quality Tips", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = CyberPink)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "1. Plug in headphones or connect a Bluetooth headset to prevent microphone echo feedback loops.\n2. Set Latency Mode in Settings to 'Ultra Low' for gaming.\n3. Keep Noise Reduction enabled for clear voice speech.",
                            fontSize = 11.sp,
                            color = Color.LightGray,
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }
    }
}
