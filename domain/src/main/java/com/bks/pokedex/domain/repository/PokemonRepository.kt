package com.bks.pokedex.domain.repository

import androidx.paging.PagingData
import com.bks.pokedex.domain.model.Pokemon
import com.bks.pokedex.domain.model.PokemonDetail
import com.bks.pokedex.domain.model.SortType
import kotlinx.coroutines.flow.Flow

interface PokemonRepository {
    fun getPokemonPagingData(sortType: SortType): Flow<PagingData<Pokemon>>
    suspend fun getPokemonDetail(name: String): Result<PokemonDetail>
    suspend fun toggleFavorite(pokemonId: Int)
    fun getFavoritePokemon(): Flow<List<Pokemon>>
}
