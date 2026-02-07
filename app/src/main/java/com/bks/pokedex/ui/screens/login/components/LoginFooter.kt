package com.bks.pokedex.ui.screens.login.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LoginFooter(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .navigationBarsPadding()
            .padding(bottom = 32.dp, start = 24.dp, end = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "©2026 Pokémon. ©1995–2026 Nintendo/Creatures Inc./GAME FREAK inc.",
            style = MaterialTheme.typography.labelSmall.copy(
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 8.sp,
                textAlign = TextAlign.Center
            )
        )
        Text(
            text = "TM, ® and Pokémon character names are trademarks of Nintendo.",
            style = MaterialTheme.typography.labelSmall.copy(
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 8.sp,
                textAlign = TextAlign.Center
            )
        )
    }
}
