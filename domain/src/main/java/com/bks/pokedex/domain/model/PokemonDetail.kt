package com.bks.pokedex.domain.model

data class PokemonDetail(
    val id: Int,
    val name: String,
    val imageUrls: List<String>,
    val types: List<String>,
    val stats: List<Stat>,
    val abilities: List<String>,
    val weight: Int,
    val height: Int,
    val description: String = "",
    var isFavorite: Boolean = false
)

data class Stat(
    val name: String,
    val value: Int
)
