package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CyberCardBorder
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberNeonGreen
import com.example.ui.theme.CyberPink
import com.example.ui.theme.CyberPurple
import com.example.ui.theme.CyberRed

@Composable
fun MicPulseButton(
    isEngineRunning: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = if (isEngineRunning) 1.25f else 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = if (isEngineRunning) 0.0f else 0.6f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(170.dp)
        ) {
            // Animated pulsing background aura
            if (isEngineRunning) {
                Box(
                    modifier = Modifier
                        .size(160.dp)
                        .scale(pulseScale)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    CyberNeonGreen.copy(alpha = pulseAlpha),
                                    CyberCyan.copy(alpha = pulseAlpha * 0.5f),
                                    Color.Transparent
                                )
                            )
                        )
                )
            }

            // Outer ring
            Box(
                modifier = Modifier
                    .size(135.dp)
                    .clip(CircleShape)
                    .border(
                        width = 3.dp,
                        brush = if (isEngineRunning) {
                            Brush.sweepGradient(listOf(CyberNeonGreen, CyberCyan, CyberPink, CyberNeonGreen))
                        } else {
                            Brush.linearGradient(listOf(CyberCardBorder, CyberCardBorder))
                        },
                        shape = CircleShape
                    )
                    .padding(6.dp)
            )

            // Inner main 3D button
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(115.dp)
                    .shadow(
                        elevation = if (isEngineRunning) 16.dp else 4.dp,
                        shape = CircleShape,
                        spotColor = if (isEngineRunning) CyberNeonGreen else Color.Black
                    )
                    .clip(CircleShape)
                    .background(
                        brush = if (isEngineRunning) {
                            Brush.verticalGradient(listOf(Color(0xFF003822), Color(0xFF001F12)))
                        } else {
                            Brush.verticalGradient(listOf(Color(0xFF1E2235), Color(0xFF121422)))
                        }
                    )
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onClick
                    )
                    .testTag("main_mic_button")
            ) {
                Icon(
                    imageVector = if (isEngineRunning) Icons.Default.Mic else Icons.Default.MicOff,
                    contentDescription = "Microphone Status",
                    tint = if (isEngineRunning) CyberNeonGreen else CyberRed,
                    modifier = Modifier.size(48.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = if (isEngineRunning) "STOP VOICE CHANGER" else "START VOICE CHANGER",
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = if (isEngineRunning) CyberNeonGreen else CyberCyan
        )
    }
}
