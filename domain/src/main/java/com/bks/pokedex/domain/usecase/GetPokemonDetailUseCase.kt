package com.bks.pokedex.domain.usecase

import com.bks.pokedex.domain.model.PokemonDetail
import com.bks.pokedex.domain.repository.PokemonRepository
import javax.inject.Inject

class GetPokemonDetailUseCase @Inject constructor(
    private val repository: PokemonRepository
) {
    suspend operator fun invoke(name: String): Result<PokemonDetail> {
        return repository.getPokemonDetail(name)
    }
}
