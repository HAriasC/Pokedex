package com.bks.pokedex.domain.usecase

import com.bks.pokedex.domain.repository.PokemonRepository
import javax.inject.Inject

class ToggleFavoriteUseCase @Inject constructor(
    private val repository: PokemonRepository
) {
    suspend operator fun invoke(pokemonId: Int) {
        repository.toggleFavorite(pokemonId)
    }
}
