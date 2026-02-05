package com.bks.pokedex.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.bks.pokedex.data.local.PokemonDao
import com.bks.pokedex.data.mapper.toDomain
import com.bks.pokedex.data.mapper.toFavoriteEntity
import com.bks.pokedex.data.paging.PokemonPagingSource
import com.bks.pokedex.data.remote.PokeApi
import com.bks.pokedex.domain.model.Pokemon
import com.bks.pokedex.domain.model.PokemonDetail
import com.bks.pokedex.domain.repository.PokemonRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PokemonRepositoryImpl @Inject constructor(
    private val api: PokeApi,
    private val dao: PokemonDao
) : PokemonRepository {

    override fun getPokemonPagingData(): Flow<PagingData<Pokemon>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { PokemonPagingSource(api) }
        ).flow
    }

    override suspend fun getPokemonDetail(name: String): Result<PokemonDetail> {
        return try {
            val response = api.getPokemonDetail(name)
            val species = try {
                api.getPokemonSpecies(response.id)
            } catch (e: Exception) {
                null
            }
            val isFavorite = dao.isFavorite(response.id)
            Result.success(response.toDomain(isFavorite, species))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun toggleFavorite(pokemonId: Int) {
        val isFavorite = dao.isFavorite(pokemonId)
        if (isFavorite) {
            val favorite = dao.getFavoriteById(pokemonId)
            favorite?.let { dao.deleteFavorite(it) }
        } else {
            try {
                val detail = api.getPokemonDetail(pokemonId.toString())
                dao.insertFavorite(detail.toDomain(true).toFavoriteEntity())
            } catch (e: Exception) {
                // Log error
            }
        }
    }

    override fun getFavoritePokemon(): Flow<List<Pokemon>> {
        return dao.getFavoritePokemon().map { entities ->
            entities.map { it.toDomain() }
        }
    }
}
