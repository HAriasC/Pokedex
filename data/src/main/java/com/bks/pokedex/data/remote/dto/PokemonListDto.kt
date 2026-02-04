package com.bks.pokedex.data.remote.dto

import com.squareup.moshi.Json

data class PokemonListDto(
    @field:Json(name = "results") val results: List<PokemonResultDto>
)

data class PokemonResultDto(
    @field:Json(name = "name") val name: String,
    @field:Json(name = "url") val url: String
)
