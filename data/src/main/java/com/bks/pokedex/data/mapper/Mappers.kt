package com.bks.pokedex.data.mapper

import com.bks.pokedex.data.local.entity.FavoritePokemonEntity
import com.bks.pokedex.data.remote.dto.PokemonDetailDto
import com.bks.pokedex.data.remote.dto.PokemonResultDto
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

fun PokemonDetailDto.toDomain(isFavorite: Boolean): PokemonDetail {
    return PokemonDetail(
        id = id,
        name = name.replaceFirstChar { it.uppercase() },
        imageUrl = sprites.other?.officialArtwork?.frontDefault ?: "",
        types = types.map { it.type.name },
        stats = stats.map { Stat(it.stat.name, it.baseStat) },
        abilities = abilities.map { it.ability.name },
        weight = weight,
        height = height,
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
        imageUrl = imageUrl
    )
}

fun Pokemon.toFavoriteEntity(): FavoritePokemonEntity {
    return FavoritePokemonEntity(
        id = id,
        name = name,
        imageUrl = imageUrl
    )
}
