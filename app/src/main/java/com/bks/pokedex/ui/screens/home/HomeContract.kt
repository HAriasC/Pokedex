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
        val isSortMenuVisible: Boolean = false
    )

    sealed class Intent {
        data class OnPokemonClick(val name: String) : Intent()
        data class OnSearchQueryChange(val query: String) : Intent()
        data class OnSortTypeChange(val sortType: SortType) : Intent()
        object OnSortIconClick : Intent()
        object OnDismissSortMenu : Intent()
        object OnFavoritesClick : Intent()
    }

    sealed class Effect {
        data class NavigateToDetail(val name: String) : Effect()
        object NavigateToFavorites : Effect()
    }
}
