package com.bks.pokedex.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PokemonDetailDto(
    @field:Json(name = "id") val id: Int,
    @field:Json(name = "name") val name: String,
    @field:Json(name = "height") val height: Int,
    @field:Json(name = "weight") val weight: Int,
    @field:Json(name = "sprites") val sprites: SpritesDto,
    @field:Json(name = "types") val types: List<TypeSlotDto>,
    @field:Json(name = "stats") val stats: List<StatSlotDto>,
    @field:Json(name = "abilities") val abilities: List<AbilitySlotDto>
)

@JsonClass(generateAdapter = true)
data class SpritesDto(
    @field:Json(name = "front_default") val frontDefault: String?,
    @field:Json(name = "back_default") val backDefault: String?,
    @field:Json(name = "front_shiny") val frontShiny: String?,
    @field:Json(name = "back_shiny") val backShiny: String?,
    @field:Json(name = "other") val other: OtherSpritesDto?
)

@JsonClass(generateAdapter = true)
data class OtherSpritesDto(
    @field:Json(name = "official-artwork") val officialArtwork: OfficialArtworkDto?,
    @field:Json(name = "home") val home: OfficialArtworkDto?
)

@JsonClass(generateAdapter = true)
data class OfficialArtworkDto(
    @field:Json(name = "front_default") val frontDefault: String?
)

@JsonClass(generateAdapter = true)
data class TypeSlotDto(
    @field:Json(name = "type") val type: TypeDto
)

@JsonClass(generateAdapter = true)
data class TypeDto(
    @field:Json(name = "name") val name: String
)

@JsonClass(generateAdapter = true)
data class StatSlotDto(
    @field:Json(name = "base_stat") val baseStat: Int,
    @field:Json(name = "stat") val stat: StatDto
)

@JsonClass(generateAdapter = true)
data class StatDto(
    @field:Json(name = "name") val name: String
)

@JsonClass(generateAdapter = true)
data class AbilitySlotDto(
    @field:Json(name = "ability") val ability: AbilityDto
)

@JsonClass(generateAdapter = true)
data class AbilityDto(
    @field:Json(name = "name") val name: String
)
