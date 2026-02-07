package com.bks.pokedex.ui.screens.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bks.pokedex.domain.model.Pokemon
import com.bks.pokedex.domain.usecase.GetFavoritePokemonUseCase
import com.bks.pokedex.domain.usecase.LogoutUseCase
import com.bks.pokedex.domain.usecase.ToggleFavoriteUseCase
import com.bks.pokedex.ui.screens.home.HomeContract
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val getFavoritePokemonUseCase: GetFavoritePokemonUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val logoutUseCase: LogoutUseCase
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    private val _sortType = MutableStateFlow(HomeContract.SortType.NUMBER)
    private val _isSortMenuVisible = MutableStateFlow(false)
    private val _isLoggingOut = MutableStateFlow(false)

    val state: StateFlow<HomeContract.State> = combine(
        _searchQuery,
        _sortType,
        _isSortMenuVisible,
        _isLoggingOut
    ) { query, sort, isMenuVisible, isLoggingOut ->
        HomeContract.State(
            searchQuery = query,
            sortType = sort,
            isSortMenuVisible = isMenuVisible,
            isLoggingOut = isLoggingOut
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), HomeContract.State())

    val filteredFavorites: StateFlow<List<Pokemon>> = combine(
        getFavoritePokemonUseCase(),
        _searchQuery,
        _sortType
    ) { favorites, query, sort ->
        favorites.filter {
            it.name.contains(query, ignoreCase = true)
        }.sortedWith { a, b ->
            if (sort == HomeContract.SortType.NAME) a.name.compareTo(b.name)
            else a.id.compareTo(b.id)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun onIntent(intent: HomeContract.Intent) {
        when (intent) {
            is HomeContract.Intent.OnSearchQueryChange -> _searchQuery.value = intent.query
            is HomeContract.Intent.OnSortTypeChange -> {
                _sortType.value = intent.sortType
                _isSortMenuVisible.value = false
            }

            is HomeContract.Intent.OnToggleFavorite -> {
                viewModelScope.launch {
                    toggleFavoriteUseCase(intent.pokemonId)
                }
            }

            is HomeContract.Intent.OnLogoutClick -> {
                viewModelScope.launch {
                    _isLoggingOut.value = true
                    delay(2000)
                    logoutUseCase()
                }
            }

            HomeContract.Intent.OnSortIconClick -> _isSortMenuVisible.value = true
            HomeContract.Intent.OnDismissSortMenu -> _isSortMenuVisible.value = false
            else -> {}
        }
    }
}
