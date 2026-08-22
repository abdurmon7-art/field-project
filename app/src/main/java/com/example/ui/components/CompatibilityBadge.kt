package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CyberNeonGreen
import com.example.ui.theme.CyberRed
import com.example.ui.theme.CyberYellow

@Composable
fun CompatibilityBadge(status: String) {
    val (bgColor, borderColor, textColor) = when {
        status.contains("Supported", ignoreCase = true) && !status.contains("Partially", ignoreCase = true) -> 
            Triple(CyberNeonGreen.copy(alpha = 0.15f), CyberNeonGreen, CyberNeonGreen)
        status.contains("Partially", ignoreCase = true) || status.contains("Routing", ignoreCase = true) ->
            Triple(CyberYellow.copy(alpha = 0.15f), CyberYellow, CyberYellow)
        else ->
            Triple(CyberRed.copy(alpha = 0.15f), CyberRed, CyberRed)
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(bgColor)
            .border(1.dp, borderColor, RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            text = status,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
    }
}
