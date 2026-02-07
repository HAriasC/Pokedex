package com.bks.pokedex.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.filter
import androidx.paging.map
import com.bks.pokedex.domain.model.Pokemon
import com.bks.pokedex.domain.model.SortType
import com.bks.pokedex.domain.usecase.GetFavoritePokemonUseCase
import com.bks.pokedex.domain.usecase.GetPokemonPagingUseCase
import com.bks.pokedex.domain.usecase.LogoutUseCase
import com.bks.pokedex.domain.usecase.ToggleFavoriteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlinx.coroutines.delay

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getPokemonPagingUseCase: GetPokemonPagingUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val getFavoritePokemonUseCase: GetFavoritePokemonUseCase,
    private val logoutUseCase: LogoutUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(HomeContract.State())
    val state: StateFlow<HomeContract.State> = _state.asStateFlow()

    private val _effect = Channel<HomeContract.Effect>()
    val effect = _effect.receiveAsFlow()

    // Flujo de datos optimizado
    private val pagingDataFlow: Flow<PagingData<Pokemon>> = _state
        .map { it.searchQuery }
        .distinctUntilChanged()
        .debounce { query -> if (query.isEmpty()) 0L else 500L }
        .flatMapLatest { query ->
            getPokemonPagingUseCase(SortType.NUMBER).map { pagingData ->
                pagingData.filter { pokemon ->
                    query.isEmpty() || pokemon.name.contains(query, ignoreCase = true)
                }
            }
        }
        .flowOn(Dispatchers.IO)
        .cachedIn(viewModelScope)

    private val favoriteIds: StateFlow<Set<Int>> = getFavoritePokemonUseCase()
        .map { list -> list.map { it.id }.toSet() }
        .flowOn(Dispatchers.IO)
        .stateIn(
            scope = viewModelScope,
            started = kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5000),
            initialValue = emptySet()
        )

    val pokemonPagingData: Flow<PagingData<Pokemon>> = combine(
        pagingDataFlow,
        favoriteIds
    ) { pagingData, favorites ->
        pagingData.map { pokemon ->
            pokemon.copy(isFavorite = favorites.contains(pokemon.id))
        }
    }.cachedIn(viewModelScope)

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
            is HomeContract.Intent.OnLogoutClick -> {
                viewModelScope.launch {
                    // IMPLEMENTACIÓN: Salida con estilo
                    _state.update { it.copy(isLoggingOut = true) }
                    delay(2000) // Mismo delay que el login para consistencia
                    logoutUseCase()
                    // El cambio de sesión en DataStore provocará la redirección automática
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
