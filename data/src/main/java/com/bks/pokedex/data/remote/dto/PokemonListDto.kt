package com.bks.pokedex.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PokemonListDto(
    @field:Json(name = "results") val results: List<PokemonResultDto>
)

@JsonClass(generateAdapter = true)
data class PokemonResultDto(
    @field:Json(name = "name") val name: String,
    @field:Json(name = "url") val url: String
)
