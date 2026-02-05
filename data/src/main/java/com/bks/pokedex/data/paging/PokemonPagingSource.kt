package com.bks.pokedex.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.bks.pokedex.data.mapper.toDomain
import com.bks.pokedex.data.remote.PokeApi
import com.bks.pokedex.domain.model.Pokemon

class PokemonPagingSource(
    private val api: PokeApi
) : PagingSource<Int, Pokemon>() {

    override fun getRefreshKey(state: PagingState<Int, Pokemon>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(state.config.pageSize)
                ?: anchorPage?.nextKey?.minus(state.config.pageSize)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Pokemon> {
        val offset = params.key ?: 0
        val limit = params.loadSize

        return try {
            val response = api.getPokemonList(limit = limit, offset = offset)
            val pokemonList = response.results.map { it.toDomain() }

            LoadResult.Page(
                data = pokemonList,
                prevKey = if (offset == 0) null else maxOf(0, offset - limit),
                nextKey = if (pokemonList.isEmpty() || pokemonList.size < limit) null else offset + limit
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}
