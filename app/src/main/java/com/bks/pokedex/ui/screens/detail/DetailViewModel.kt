package com.bks.pokedex.ui.screens.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bks.pokedex.domain.usecase.GetPokemonDetailUseCase
import com.bks.pokedex.domain.usecase.ToggleFavoriteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val getPokemonDetailUseCase: GetPokemonDetailUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow(DetailContract.State())
    val state = _state.asStateFlow()

    private val _effect = Channel<DetailContract.Effect>()
    val effect = _effect.receiveAsFlow()

    private val pokemonName: String? = savedStateHandle["pokemonName"]

    init {
        pokemonName?.let { loadPokemon(it) }
    }

    private fun loadPokemon(name: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            getPokemonDetailUseCase(name)
                .onSuccess { pokemon ->
                    _state.update { it.copy(isLoading = false, pokemon = pokemon, error = null) }
                }
                .onFailure { error ->
                    _state.update { it.copy(isLoading = false, error = error.message) }
                }
        }
    }

    fun onIntent(intent: DetailContract.Intent) {
        when (intent) {
            is DetailContract.Intent.LoadPokemon -> loadPokemon(intent.name)
            DetailContract.Intent.Retry -> {
                pokemonName?.let { loadPokemon(it) }
            }

            DetailContract.Intent.ToggleFavorite -> {
                viewModelScope.launch {
                    val currentPokemon = _state.value.pokemon ?: return@launch
                    toggleFavoriteUseCase(currentPokemon.id)
                    // Update local state for immediate feedback
                    _state.update { it.copy(pokemon = currentPokemon.copy(isFavorite = !currentPokemon.isFavorite)) }
                }
            }

            DetailContract.Intent.OnBackClick -> {
                viewModelScope.launch {
                    _effect.send(DetailContract.Effect.NavigateBack)
                }
            }
        }
    }
}
