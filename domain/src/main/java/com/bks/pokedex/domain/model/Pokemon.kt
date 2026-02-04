package com.bks.pokedex.domain.model

data class Pokemon(
    val id: Int,
    val name: String,
    val imageUrl: String,
    val isFavorite: Boolean = false
)
