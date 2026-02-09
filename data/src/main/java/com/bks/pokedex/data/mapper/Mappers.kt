package com.bks.pokedex.data.mapper

import com.bks.pokedex.data.local.entity.EvolutionEntity
import com.bks.pokedex.data.local.entity.FavoritePokemonEntity
import com.bks.pokedex.data.local.entity.PokemonDetailEntity
import com.bks.pokedex.data.local.entity.PokemonEntity
import com.bks.pokedex.data.local.entity.StatEntity
import com.bks.pokedex.data.remote.dto.ChainLinkDto
import com.bks.pokedex.data.remote.dto.PokemonDetailDto
import com.bks.pokedex.data.remote.dto.PokemonResultDto
import com.bks.pokedex.data.remote.dto.PokemonSpeciesDto
import com.bks.pokedex.domain.model.Evolution
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

fun PokemonResultDto.toEntity(page: Int): PokemonEntity {
    val id = url.split("/").filter { it.isNotEmpty() }.last().toInt()
    return PokemonEntity(
        id = id,
        name = name.replaceFirstChar { it.uppercase() },
        imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/$id.png",
        page = page
    )
}

fun PokemonEntity.toDomain(): Pokemon {
    return Pokemon(
        id = id,
        name = name,
        imageUrl = imageUrl,
        isFavorite = false
    )
}

fun PokemonDetailDto.toDomain(
    isFavorite: Boolean,
    species: PokemonSpeciesDto? = null,
    evolutionChain: Evolution? = null
): PokemonDetail {
    val description = species?.flavorTextEntries
        ?.firstOrNull { it.language.name == "en" }
        ?.flavorText
        ?.replace("\n", " ")
        ?.replace("\u000c", " ") ?: ""

    val images = mutableListOf<String>()
    sprites.other?.officialArtwork?.frontDefault?.let { images.add(it) }
    sprites.other?.home?.frontDefault?.let { images.add(it) }
    sprites.frontDefault?.let { images.add(it) }
    sprites.backDefault?.let { images.add(it) }
    sprites.frontShiny?.let { images.add(it) }
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
        isFavorite = isFavorite,
        evolutionChain = evolutionChain
    )
}

fun PokemonDetail.toEntity(): PokemonDetailEntity {
    return PokemonDetailEntity(
        id = id,
        name = name,
        height = height,
        weight = weight,
        types = types,
        stats = stats.map { StatEntity(it.name, it.value) },
        abilities = abilities,
        description = description,
        imageUrls = imageUrls,
        evolutionChain = evolutionChain?.toEntity()
    )
}

fun PokemonDetailEntity.toDomain(isFavorite: Boolean): PokemonDetail {
    return PokemonDetail(
        id = id,
        name = name,
        imageUrls = imageUrls,
        types = types,
        stats = stats.map { Stat(it.name, it.baseStat) },
        abilities = abilities,
        weight = weight,
        height = height,
        description = description,
        isFavorite = isFavorite,
        evolutionChain = evolutionChain?.toDomain()
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

fun Evolution.toEntity(): EvolutionEntity {
    return EvolutionEntity(
        id = id,
        name = name,
        imageUrl = imageUrl,
        evolvesTo = evolvesTo.map { it.toEntity() }
    )
}

fun EvolutionEntity.toDomain(): Evolution {
    return Evolution(
        id = id,
        name = name,
        imageUrl = imageUrl,
        evolvesTo = evolvesTo.map { it.toDomain() }
    )
}

fun mapEvolutionChain(chain: ChainLinkDto): Evolution {
    val species = chain.species
    val id = species.url.split("/").filter { it.isNotEmpty() }.last().toInt()

    return Evolution(
        id = id,
        name = species.name.replaceFirstChar { it.uppercase() },
        imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/$id.png",
        evolvesTo = chain.evolvesTo.map { mapEvolutionChain(it) }
    )
}
