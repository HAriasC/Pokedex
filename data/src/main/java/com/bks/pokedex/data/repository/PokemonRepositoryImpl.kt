package com.bks.pokedex.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.bks.pokedex.data.local.PokemonDao
import com.bks.pokedex.data.local.PokemonDatabase
import com.bks.pokedex.data.mapper.toDomain
import com.bks.pokedex.data.mapper.toFavoriteEntity
import com.bks.pokedex.data.paging.PokemonRemoteMediator
import com.bks.pokedex.data.remote.PokeApi
import com.bks.pokedex.domain.model.Pokemon
import com.bks.pokedex.domain.model.PokemonDetail
import com.bks.pokedex.domain.model.SortType
import com.bks.pokedex.domain.repository.PokemonRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PokemonRepositoryImpl @Inject constructor(
    private val api: PokeApi,
    private val dao: PokemonDao,
    private val db: PokemonDatabase
) : PokemonRepository {

    @OptIn(ExperimentalPagingApi::class)
    override fun getPokemonPagingData(sortType: SortType): Flow<PagingData<Pokemon>> {
        val config = when (sortType) {
            SortType.NUMBER -> PagingConfig(
                pageSize = 20,
                prefetchDistance = 10,
                enablePlaceholders = false,
                initialLoadSize = 40,
                maxSize = PagingConfig.MAX_SIZE_UNBOUNDED
            )

            SortType.NAME -> PagingConfig(
                pageSize = 50,
                prefetchDistance = 20,
                enablePlaceholders = false,
                initialLoadSize = 150,
                maxSize = PagingConfig.MAX_SIZE_UNBOUNDED
            )
        }

        return Pager(
            config = config,
            remoteMediator = PokemonRemoteMediator(db, api),
            pagingSourceFactory = {
                when (sortType) {
                    SortType.NUMBER -> dao.getPokemonListPagingById()
                    SortType.NAME -> dao.getPokemonListPagingByName()
                }
            }
        ).flow.map { pagingData ->
            pagingData.map { entity -> entity.toDomain() }
        }
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
            }
        }
    }

    override fun getFavoritePokemon(): Flow<List<Pokemon>> {
        return dao.getFavoritePokemon().map { entities ->
            entities.map { it.toDomain() }
        }
    }
}
