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
    var isFavorite: Boolean = false,
    val evolutionChain: Evolution? = null
)

data class Stat(
    val name: String,
    val value: Int
)

data class Evolution(
    val id: Int,
    val name: String,
    val imageUrl: String,
    val evolvesTo: List<Evolution> = emptyList()
)
