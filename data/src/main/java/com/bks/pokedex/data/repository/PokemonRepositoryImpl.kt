package com.bks.pokedex.data.repository

import com.bks.pokedex.data.local.PokemonDao
import com.bks.pokedex.data.mapper.toDomain
import com.bks.pokedex.data.mapper.toFavoriteEntity
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

    override suspend fun getPokemonList(limit: Int, offset: Int): Result<List<Pokemon>> {
        return try {
            val response = api.getPokemonList(limit, offset)
            val pokemonList = response.results.map { it.toDomain() }
            Result.success(pokemonList)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getPokemonDetail(name: String): Result<PokemonDetail> {
        return try {
            val response = api.getPokemonDetail(name)
            val isFavorite = dao.isFavorite(response.id)
            Result.success(response.toDomain(isFavorite))
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
            // We need the basic info to save it. For simplicity, we can fetch it if not available
            // but in a real app we might have it from the list or detail.
            // Here we'll fetch the detail to get the full info for the favorite entity
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
