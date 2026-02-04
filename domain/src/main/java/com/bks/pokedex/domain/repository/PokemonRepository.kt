package com.bks.pokedex.domain.repository

import com.bks.pokedex.domain.model.Pokemon
import com.bks.pokedex.domain.model.PokemonDetail
import kotlinx.coroutines.flow.Flow

interface PokemonRepository {
    suspend fun getPokemonList(limit: Int, offset: Int): Result<List<Pokemon>>
    suspend fun getPokemonDetail(name: String): Result<PokemonDetail>
    suspend fun toggleFavorite(pokemonId: Int)
    fun getFavoritePokemon(): Flow<List<Pokemon>>
}
