package com.bks.pokedex.domain.usecase

import androidx.paging.PagingData
import com.bks.pokedex.domain.model.Pokemon
import com.bks.pokedex.domain.model.SortType
import com.bks.pokedex.domain.repository.PokemonRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetPokemonPagingUseCase @Inject constructor(
    private val repository: PokemonRepository
) {
    operator fun invoke(sortType: SortType): Flow<PagingData<Pokemon>> {
        return repository.getPokemonPagingData(sortType)
    }
}
