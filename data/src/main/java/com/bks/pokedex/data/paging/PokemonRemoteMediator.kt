package com.bks.pokedex.data.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.bks.pokedex.data.local.db.PokemonDatabase
import com.bks.pokedex.data.local.entity.PokemonEntity
import com.bks.pokedex.data.local.entity.RemoteKeys
import com.bks.pokedex.data.mapper.toEntity
import com.bks.pokedex.data.remote.PokeApi
import java.io.IOException
import retrofit2.HttpException

@OptIn(ExperimentalPagingApi::class)
class PokemonRemoteMediator(
    private val db: PokemonDatabase,
    private val api: PokeApi
) : RemoteMediator<Int, PokemonEntity>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, PokemonEntity>
    ): MediatorResult {
        val offset = when (loadType) {
            LoadType.REFRESH -> 0
            LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
            LoadType.APPEND -> {
                val remoteKeys = getRemoteKeyForLastItem(state)
                val nextKey = remoteKeys?.nextKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                nextKey
            }
        }

        try {
            val loadSize = if (offset == 0) 150 else 200

            val response = api.getPokemonList(
                limit = loadSize,
                offset = offset
            )

            val results = response.results
            val endOfPaginationReached = results.isEmpty() || offset >= 1020

            db.withTransaction {
                val nextKey = if (endOfPaginationReached) null else offset + results.size
                val prevKey = if (offset == 0) null else offset - results.size

                val keys = results.map {
                    val id = it.url.split("/").filter { s -> s.isNotEmpty() }.last().toInt()
                    RemoteKeys(pokemonId = id, prevKey = prevKey, nextKey = nextKey)
                }
                db.remoteKeysDao.insertAll(keys)

                val pokemonEntities =
                    results.map { it.toEntity(page = offset / state.config.pageSize) }
                db.pokemonDao.insertAllPokemon(pokemonEntities)
            }

            return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (e: IOException) {
            return MediatorResult.Error(e)
        } catch (e: HttpException) {
            return MediatorResult.Error(e)
        }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, PokemonEntity>): RemoteKeys? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()
            ?.let { pokemon ->
                db.remoteKeysDao.getRemoteKeysForPokemonId(pokemon.id)
            }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(state: PagingState<Int, PokemonEntity>): RemoteKeys? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { id ->
                db.remoteKeysDao.getRemoteKeysForPokemonId(id)
            }
        }
    }
}
