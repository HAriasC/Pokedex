package com.bks.pokedex.ui.screens.home

import androidx.paging.PagingData
import com.bks.pokedex.domain.model.Pokemon
import kotlinx.coroutines.flow.Flow

interface HomeContract {
    enum class SortType {
        NUMBER, NAME
    }

    data class State(
        val pokemonPagingData: Flow<PagingData<Pokemon>>? = null,
        val searchQuery: String = "",
        val sortType: SortType = SortType.NUMBER,
        val isSortMenuVisible: Boolean = false,
        val isLoggingOut: Boolean = false
    )

    sealed class Intent {
        data class OnPokemonClick(val name: String, val id: Int) : Intent()
        data class OnSearchQueryChange(val query: String) : Intent()
        data class OnSortTypeChange(val sortType: SortType) : Intent()
        data class OnToggleFavorite(val pokemonId: Int) : Intent()
        object OnSortIconClick : Intent()
        object OnDismissSortMenu : Intent()
        object OnFavoritesClick : Intent()
        object OnLogoutClick : Intent()
    }

    sealed class Effect {
        data class NavigateToDetail(val name: String, val id: Int) : Effect()
        object NavigateToFavorites : Effect()
    }
}
