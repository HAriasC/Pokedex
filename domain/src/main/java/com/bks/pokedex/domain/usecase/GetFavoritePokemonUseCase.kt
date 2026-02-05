package com.bks.pokedex.domain.usecase

import com.bks.pokedex.domain.model.Pokemon
import com.bks.pokedex.domain.repository.PokemonRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetFavoritePokemonUseCase @Inject constructor(
    private val repository: PokemonRepository
) {
    operator fun invoke(): Flow<List<Pokemon>> {
        return repository.getFavoritePokemon()
    }
}
