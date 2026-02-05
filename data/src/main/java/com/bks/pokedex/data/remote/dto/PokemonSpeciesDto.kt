package com.bks.pokedex.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PokemonSpeciesDto(
    @field:Json(name = "flavor_text_entries") val flavorTextEntries: List<FlavorTextEntryDto>
)

@JsonClass(generateAdapter = true)
data class FlavorTextEntryDto(
    @field:Json(name = "flavor_text") val flavorText: String,
    @field:Json(name = "language") val language: LanguageDto
)

@JsonClass(generateAdapter = true)
data class LanguageDto(
    @field:Json(name = "name") val name: String
)
