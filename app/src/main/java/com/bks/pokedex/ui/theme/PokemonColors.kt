package com.bks.pokedex.ui.theme

import androidx.compose.ui.graphics.Color

object PokemonColors {
    fun getColorForType(type: String): Color {
        return when (type.lowercase()) {
            "fire" -> Color(0xFFF08030)
            "water" -> Color(0xFF6890F0)
            "grass" -> Color(0xFF78C850)
            "electric" -> Color(0xFFF8D030)
            "psychic" -> Color(0xFFF85888)
            "ice" -> Color(0xFF98D8D8)
            "dragon" -> Color(0xFF7038F8)
            "dark" -> Color(0xFF705848)
            "fairy" -> Color(0xFFEE99AC)
            "normal" -> Color(0xFFA8A878)
            "fighting" -> Color(0xFFC03028)
            "flying" -> Color(0xFFA890F0)
            "poison" -> Color(0xFFA040A0)
            "ground" -> Color(0xFFE0C068)
            "rock" -> Color(0xFFB8A038)
            "bug" -> Color(0xFFA8B820)
            "ghost" -> Color(0xFF705898)
            "steel" -> Color(0xFFB8B8D0)
            else -> Color.Gray
        }
    }
}
