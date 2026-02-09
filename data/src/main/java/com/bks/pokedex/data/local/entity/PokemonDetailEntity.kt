package com.bks.pokedex.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.JsonClass

@Entity(tableName = "pokemon_details")
data class PokemonDetailEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val height: Int,
    val weight: Int,
    val types: List<String>,
    val stats: List<StatEntity>,
    val abilities: List<String>,
    val description: String,
    val imageUrls: List<String>,
    val evolutionChain: EvolutionEntity? = null
)

@JsonClass(generateAdapter = true)
data class StatEntity(
    val name: String,
    val baseStat: Int
)

@JsonClass(generateAdapter = true)
data class EvolutionEntity(
    val id: Int,
    val name: String,
    val imageUrl: String,
    val evolvesTo: List<EvolutionEntity> = emptyList()
)
