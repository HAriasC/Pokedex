package com.bks.pokedex.data.remote.dto

import com.squareup.moshi.Json

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

data class SpritesDto(
    @field:Json(name = "other") val other: OtherSpritesDto?
)

data class OtherSpritesDto(
    @field:Json(name = "official-artwork") val officialArtwork: OfficialArtworkDto?
)

data class OfficialArtworkDto(
    @field:Json(name = "front_default") val frontDefault: String?
)

data class TypeSlotDto(
    @field:Json(name = "type") val type: TypeDto
)

data class TypeDto(
    @field:Json(name = "name") val name: String
)

data class StatSlotDto(
    @field:Json(name = "base_stat") val baseStat: Int,
    @field:Json(name = "stat") val stat: StatDto
)

data class StatDto(
    @field:Json(name = "name") val name: String
)

data class AbilitySlotDto(
    @field:Json(name = "ability") val ability: AbilityDto
)

data class AbilityDto(
    @field:Json(name = "name") val name: String
)
