package com.bks.pokedex.ui.screens.detail.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun StatItem(name: String, value: Int, color: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val statAbbreviation = when (name.lowercase()) {
            "special-attack" -> "SATK"
            "special-defense" -> "SDEF"
            "speed" -> "SPD"
            else -> name.uppercase().take(3)
        }
        Text(
            text = statAbbreviation,
            modifier = Modifier.width(48.dp),
            style = MaterialTheme.typography.labelLarge.copy(color = color)
        )
        Box(
            modifier = Modifier
                .width(1.dp)
                .height(20.dp)
                .background(Color(0xFFE0E0E0))
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = value.toString().padStart(3, '0'),
            modifier = Modifier.width(36.dp),
            style = MaterialTheme.typography.bodyLarge.copy(color = Color(0xFF1D1D1D))
        )
        Spacer(modifier = Modifier.width(12.dp))
        LinearProgressIndicator(
            progress = { value / 255f },
            modifier = Modifier
                .weight(1f)
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp)),
            color = color,
            trackColor = color.copy(alpha = 0.2f)
        )
    }
}
