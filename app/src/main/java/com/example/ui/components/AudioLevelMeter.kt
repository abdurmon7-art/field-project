package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberNeonGreen
import com.example.ui.theme.CyberPink
import com.example.ui.theme.CyberYellow
import kotlin.math.sin

@Composable
fun AudioLevelMeter(
    level: Float, // 0.0f to 1.0f
    isEngineRunning: Boolean,
    modifier: Modifier = Modifier
) {
    val animatedLevel by animateFloatAsState(
        targetValue = if (isEngineRunning) level else 0f,
        label = "levelAnim"
    )

    val barCount = 18

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(28.dp)
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 0 until barCount) {
            // Give each bar slight variation to emulate multi-band spectral meter
            val phase = sin(i * 0.7) * 0.2
            val barLevel = (animatedLevel + phase).toFloat().coerceIn(0.08f, 1.0f)

            val color = when {
                i > 14 -> CyberPink
                i > 10 -> CyberYellow
                i > 5 -> CyberCyan
                else -> CyberNeonGreen
            }

            Box(
                modifier = Modifier
                    .width(12.dp)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(3.dp))
                    .background(Color(0xFF1B2032)),
                contentAlignment = Alignment.BottomCenter
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(barLevel)
                        .clip(RoundedCornerShape(3.dp))
                        .background(if (isEngineRunning) color else Color(0xFF2C324B))
                )
            }
        }
    }
}
