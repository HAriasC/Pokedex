package com.bks.pokedex.ui.screens.detail

import com.bks.pokedex.domain.model.PokemonDetail

interface DetailContract {
    data class State(
        val isLoading: Boolean = false,
        val pokemon: PokemonDetail? = null,
        val error: String? = null
    )

    sealed class Intent {
        data class LoadPokemon(val name: String) : Intent()
        object Retry : Intent()
        object ToggleFavorite : Intent()
        object OnBackClick : Intent()
    }

    sealed class Effect {
        object NavigateBack : Effect()
    }
}
