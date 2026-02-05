package com.bks.pokedex.data.mapper

import com.bks.pokedex.data.local.entity.FavoritePokemonEntity
import com.bks.pokedex.data.remote.dto.PokemonDetailDto
import com.bks.pokedex.data.remote.dto.PokemonResultDto
import com.bks.pokedex.data.remote.dto.PokemonSpeciesDto
import com.bks.pokedex.domain.model.Pokemon
import com.bks.pokedex.domain.model.PokemonDetail
import com.bks.pokedex.domain.model.Stat

fun PokemonResultDto.toDomain(): Pokemon {
    val id = url.split("/").filter { it.isNotEmpty() }.last().toInt()
    return Pokemon(
        id = id,
        name = name.replaceFirstChar { it.uppercase() },
        imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/$id.png"
    )
}

fun PokemonDetailDto.toDomain(
    isFavorite: Boolean,
    species: PokemonSpeciesDto? = null
): PokemonDetail {
    val description = species?.flavorTextEntries
        ?.firstOrNull { it.language.name == "en" }
        ?.flavorText
        ?.replace("\n", " ")
        ?.replace("\u000c", " ") ?: ""

    val images = mutableListOf<String>()

    // 1. Official Artwork (Primary)
    sprites.other?.officialArtwork?.frontDefault?.let { images.add(it) }
    // 2. Home Artwork
    sprites.other?.home?.frontDefault?.let { images.add(it) }
    // 3. Front Default Sprite
    sprites.frontDefault?.let { images.add(it) }
    // 4. Back Default Sprite
    sprites.backDefault?.let { images.add(it) }
    // 5. Front Shiny
    sprites.frontShiny?.let { images.add(it) }
    // 6. Back Shiny
    sprites.backShiny?.let { images.add(it) }

    return PokemonDetail(
        id = id,
        name = name.replaceFirstChar { it.uppercase() },
        imageUrls = images.filter { it.isNotEmpty() },
        types = types.map { it.type.name },
        stats = stats.map { Stat(it.stat.name, it.baseStat) },
        abilities = abilities.map { it.ability.name },
        weight = weight,
        height = height,
        description = description,
        isFavorite = isFavorite
    )
}

fun FavoritePokemonEntity.toDomain(): Pokemon {
    return Pokemon(
        id = id,
        name = name,
        imageUrl = imageUrl,
        isFavorite = true
    )
}

fun PokemonDetail.toFavoriteEntity(): FavoritePokemonEntity {
    return FavoritePokemonEntity(
        id = id,
        name = name,
        imageUrl = imageUrls.firstOrNull() ?: ""
    )
}

fun Pokemon.toFavoriteEntity(): FavoritePokemonEntity {
    return FavoritePokemonEntity(
        id = id,
        name = name,
        imageUrl = imageUrl
    )
}
