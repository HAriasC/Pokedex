package com.bks.pokedex.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.bks.pokedex.domain.usecase.GetPokemonPagingUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getPokemonPagingUseCase: GetPokemonPagingUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(HomeContract.State())
    val state = _state.asStateFlow()

    private val _effect = Channel<HomeContract.Effect>()
    val effect = _effect.receiveAsFlow()

    init {
        _state.update {
            it.copy(
                pokemonPagingData = getPokemonPagingUseCase().cachedIn(viewModelScope)
            )
        }
    }

    fun onIntent(intent: HomeContract.Intent) {
        when (intent) {
            is HomeContract.Intent.OnPokemonClick -> {
                viewModelScope.launch {
                    _effect.send(HomeContract.Effect.NavigateToDetail(intent.name))
                }
            }

            is HomeContract.Intent.OnSearchQueryChange -> {
                _state.update { it.copy(searchQuery = intent.query) }
            }

            is HomeContract.Intent.OnSortTypeChange -> {
                _state.update { it.copy(sortType = intent.sortType, isSortMenuVisible = false) }
            }

            HomeContract.Intent.OnSortIconClick -> {
                _state.update { it.copy(isSortMenuVisible = true) }
            }

            HomeContract.Intent.OnDismissSortMenu -> {
                _state.update { it.copy(isSortMenuVisible = false) }
            }

            HomeContract.Intent.OnFavoritesClick -> {
                viewModelScope.launch {
                    _effect.send(HomeContract.Effect.NavigateToFavorites)
                }
            }
        }
    }
}
