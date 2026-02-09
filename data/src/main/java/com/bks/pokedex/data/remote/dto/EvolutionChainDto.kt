package com.bks.pokedex.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class EvolutionChainDto(
    @field:Json(name = "chain") val chain: ChainLinkDto
)

@JsonClass(generateAdapter = true)
data class ChainLinkDto(
    @field:Json(name = "species") val species: SpeciesShortDto,
    @field:Json(name = "evolves_to") val evolvesTo: List<ChainLinkDto>
)

@JsonClass(generateAdapter = true)
data class SpeciesShortDto(
    @field:Json(name = "name") val name: String,
    @field:Json(name = "url") val url: String
)
