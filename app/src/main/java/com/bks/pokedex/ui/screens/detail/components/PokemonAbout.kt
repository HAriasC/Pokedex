package com.bks.pokedex.ui.screens.detail.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.bks.pokedex.R

@Composable
fun PokemonAbout(
    weight: Int,
    height: Int,
    abilities: List<String>,
    themeColor: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "About",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleLarge.copy(color = themeColor)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            AboutInfoItem(
                value = "${weight / 10f} kg",
                label = "Weight",
                iconId = R.drawable.weight,
                modifier = Modifier.weight(1f)
            )

            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(64.dp)
                    .background(Color(0xFFE0E0E0))
                    .align(Alignment.CenterVertically)
            )

            AboutInfoItem(
                value = "${height / 10f} m",
                label = "Height",
                iconId = R.drawable.straighten,
                modifier = Modifier.weight(1f)
            )

            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(64.dp)
                    .background(Color(0xFFE0E0E0))
                    .align(Alignment.CenterVertically)
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                Column(
                    modifier = Modifier.height(44.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    abilities.forEach { ability ->
                        Text(
                            text = ability.replaceFirstChar { it.uppercase() },
                            style = MaterialTheme.typography.bodyLarge.copy(color = Color(0xFF1D1D1D)),
                            textAlign = TextAlign.Center
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Moves",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
private fun AboutInfoItem(
    value: String,
    label: String,
    iconId: Int,
    modifier: Modifier = Modifier
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = modifier) {
        Row(modifier = Modifier.height(44.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painterResource(id = iconId),
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = Color(0xFF1D1D1D)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge.copy(color = Color(0xFF1D1D1D))
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text(text = label, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
    }
}
