package com.bks.pokedex.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.bks.pokedex.data.local.db.PokemonDao
import com.bks.pokedex.data.local.db.PokemonDatabase
import com.bks.pokedex.data.mapper.mapEvolutionChain
import com.bks.pokedex.data.mapper.toDomain
import com.bks.pokedex.data.mapper.toEntity
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
        val normalizedName = name.lowercase().trim()

        // 1. Intentar obtener desde caché local primero
        try {
            val localDetail = dao.getPokemonDetailByName(normalizedName)
            if (localDetail != null) {
                val favorite = dao.isFavorite(localDetail.id)
                return Result.success(localDetail.toDomain(favorite))
            }
        } catch (e: Exception) {
            // Error silencioso en caché, procedemos a red
        }

        // 2. Si no hay caché, ir a red
        return try {
            val response = api.getPokemonDetail(normalizedName)

            val species = try {
                api.getPokemonSpecies(response.id)
            } catch (e: Exception) {
                null
            }

            val evolutionChain = try {
                species?.evolutionChain?.url?.let { url ->
                    val chainDto = api.getEvolutionChain(url)
                    mapEvolutionChain(chainDto.chain)
                }
            } catch (e: Exception) {
                null
            }

            val favorite = dao.isFavorite(response.id)
            val domainDetail = response.toDomain(favorite, species, evolutionChain)

            // 3. Guardar en caché para futuras consultas offline (usando nombre normalizado)
            dao.insertPokemonDetail(domainDetail.toEntity().copy(name = normalizedName))

            Result.success(domainDetail)
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
                val localDetail = dao.getPokemonDetailById(pokemonId)
                if (localDetail != null) {
                    dao.insertFavorite(localDetail.toDomain(true).toFavoriteEntity())
                } else {
                    val detail = api.getPokemonDetail(pokemonId.toString())
                    dao.insertFavorite(detail.toDomain(true).toFavoriteEntity())
                }
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
