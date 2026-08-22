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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.VoicePreset
import com.example.ui.components.GamerHeader
import com.example.ui.components.SliderWithLabel
import com.example.ui.components.VoicePresetCard
import com.example.ui.theme.CyberCardBg
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberNeonGreen
import com.example.ui.theme.CyberPink
import com.example.ui.theme.CyberPurple
import com.example.ui.theme.CyberRed
import com.example.ui.theme.CyberYellow
import com.example.viewmodel.PreviewVoiceType
import com.example.viewmodel.VoiceChangerViewModel

@Composable
fun VoiceEffectsScreen(
    viewModel: VoiceChangerViewModel,
    onOpenSettings: () -> Unit
) {
    val selectedPreset by viewModel.selectedPreset.collectAsState()
    val isEngineRunning by viewModel.isEngineRunning.collectAsState()
    val pitch by viewModel.customPitchFactor.collectAsState()
    val formant by viewModel.customFormantShift.collectAsState()
    val intensity by viewModel.customEffectIntensity.collectAsState()
    val reverb by viewModel.customReverbMix.collectAsState()
    val echoDelay by viewModel.customEchoDelayMs.collectAsState()
    val echoFeedback by viewModel.customEchoFeedback.collectAsState()
    val noiseReduction by viewModel.isNoiseReductionOn.collectAsState()
    val isMuted by viewModel.isMuted.collectAsState()
    val previewType by viewModel.previewVoiceType.collectAsState()

    val presets = VoicePreset.ALL_PRESETS

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        GamerHeader(
            title = "Voice Effects Studio",
            subtitle = "18 Presets + Real-Time DSP Controls",
            isEngineRunning = isEngineRunning,
            onOpenSettings = onOpenSettings
        )

        // Live Voice Preview Bar
        Card(
            colors = CardDefaults.cardColors(containerColor = CyberCardBg),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp)
                .border(1.dp, CyberCyan.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { viewModel.toggleMute() }) {
                        Icon(
                            imageVector = if (isMuted) Icons.Default.VolumeMute else Icons.Default.VolumeUp,
                            contentDescription = "Mute",
                            tint = if (isMuted) CyberRed else CyberNeonGreen
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Column {
                        Text("LIVE VOICE PREVIEW", fontSize = 10.sp, color = CyberCyan, fontWeight = FontWeight.Bold)
                        Text(
                            text = if (previewType == PreviewVoiceType.ORIGINAL) "Original Voice" else "Changed Voice (${selectedPreset.name})",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                Row {
                    Button(
                        onClick = { viewModel.setPreviewVoiceType(PreviewVoiceType.ORIGINAL) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (previewType == PreviewVoiceType.ORIGINAL) CyberPink else Color(0xFF23283D)
                        ),
                        modifier = Modifier.height(36.dp)
                    ) {
                        Text("Original", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Button(
                        onClick = { viewModel.setPreviewVoiceType(PreviewVoiceType.CHANGED) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (previewType == PreviewVoiceType.CHANGED) CyberCyan else Color(0xFF23283D)
                        ),
                        modifier = Modifier.height(36.dp)
                    ) {
                        Text("Changed", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (previewType == PreviewVoiceType.CHANGED) Color.Black else Color.White)
                    }
                }
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Parameter Tuning Panel
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF161A28))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("REAL-TIME EFFECT FINE-TUNING", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = CyberCyan)
                        Spacer(modifier = Modifier.height(8.dp))

                        SliderWithLabel(
                            label = "Pitch Factor",
                            value = pitch,
                            onValueChange = { viewModel.setPitchFactor(it) },
                            valueRange = 0.5f..2.0f,
                            accentColor = CyberCyan
                        )

                        SliderWithLabel(
                            label = "Formant Shift",
                            value = formant,
                            onValueChange = { viewModel.setFormantShift(it) },
                            valueRange = 0.5f..2.0f,
                            accentColor = CyberPink
                        )

                        SliderWithLabel(
                            label = "Effect Intensity",
                            value = intensity,
                            onValueChange = { viewModel.setEffectIntensity(it) },
                            valueRange = 0.0f..1.0f,
                            accentColor = CyberPurple
                        )

                        SliderWithLabel(
                            label = "Reverb Mix",
                            value = reverb,
                            onValueChange = { viewModel.setReverbMix(it) },
                            valueRange = 0.0f..0.8f,
                            accentColor = CyberYellow
                        )

                        SliderWithLabel(
                            label = "Echo Delay",
                            value = echoDelay.toFloat(),
                            onValueChange = { viewModel.setEchoDelayMs(it.toInt()) },
                            valueRange = 0f..500f,
                            valueFormatter = { "${it.toInt()} ms" },
                            accentColor = CyberNeonGreen
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Noise Reduction", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                            Switch(
                                checked = noiseReduction,
                                onCheckedChange = { viewModel.toggleNoiseReduction() },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = CyberNeonGreen,
                                    checkedTrackColor = CyberNeonGreen.copy(alpha = 0.3f)
                                )
                            )
                        }
                    }
                }
            }

            item {
                Text(
                    text = "ALL 18 VOICE PRESETS",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.LightGray,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            items(presets) { preset ->
                VoicePresetCard(
                    preset = preset,
                    isSelected = selectedPreset.id == preset.id,
                    onSelect = { viewModel.selectPreset(preset) }
                )
            }
        }
    }
}
