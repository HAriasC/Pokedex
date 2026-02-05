package com.bks.pokedex.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.bks.pokedex.domain.model.Pokemon
import com.bks.pokedex.domain.usecase.GetFavoritePokemonUseCase
import com.bks.pokedex.domain.usecase.GetPokemonPagingUseCase
import com.bks.pokedex.domain.usecase.ToggleFavoriteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getPokemonPagingUseCase: GetPokemonPagingUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val getFavoritePokemonUseCase: GetFavoritePokemonUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(HomeContract.State())
    val state: StateFlow<HomeContract.State> = _state.asStateFlow()

    private val _effect = Channel<HomeContract.Effect>()
    val effect = _effect.receiveAsFlow()

    private val basePagingData = combine(
        _state.map { it.searchQuery }.distinctUntilChanged(),
        _state.map { it.sortType }.distinctUntilChanged()
    ) { query, _ -> query }
        .flatMapLatest { query ->
            getPokemonPagingUseCase()
        }
        .cachedIn(viewModelScope)

    val pokemonPagingData: Flow<PagingData<Pokemon>> = combine(
        basePagingData,
        getFavoritePokemonUseCase()
    ) { pagingData, favorites ->
        val favoriteIds = favorites.map { it.id }.toSet()
        pagingData.map { pokemon ->
            pokemon.copy(isFavorite = favoriteIds.contains(pokemon.id))
        }
    }

    fun onIntent(intent: HomeContract.Intent) {
        when (intent) {
            is HomeContract.Intent.OnPokemonClick -> {
                viewModelScope.launch {
                    _effect.send(HomeContract.Effect.NavigateToDetail(intent.name, intent.id))
                }
            }

            is HomeContract.Intent.OnSearchQueryChange -> {
                _state.update { it.copy(searchQuery = intent.query) }
            }

            is HomeContract.Intent.OnSortTypeChange -> {
                _state.update { it.copy(sortType = intent.sortType, isSortMenuVisible = false) }
            }

            is HomeContract.Intent.OnToggleFavorite -> {
                viewModelScope.launch {
                    toggleFavoriteUseCase(intent.pokemonId)
                }
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
