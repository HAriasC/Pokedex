package com.bks.pokedex.ui.screens.favorites

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bks.pokedex.ui.components.*
import com.bks.pokedex.ui.screens.home.HomeContract

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun FavoritesScreen(
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onNavigateToDetail: (String, Int) -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: FavoritesViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val favorites by viewModel.filteredFavorites.collectAsState()
    val haptic = LocalHapticFeedback.current

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFDC0A2D)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()) {
                PokedexHeader(
                    title = "Favorites",
                    onLogoutClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        viewModel.onIntent(HomeContract.Intent.OnLogoutClick)
                    },
                    sharedTransitionScope = sharedTransitionScope,
                    animatedVisibilityScope = animatedVisibilityScope
                )

                PokedexSearchBar(
                    query = state.searchQuery,
                    onQueryChange = { viewModel.onIntent(HomeContract.Intent.OnSearchQueryChange(it)) },
                    sortType = state.sortType,
                    onSortIconClick = { viewModel.onIntent(HomeContract.Intent.OnSortIconClick) },
                    placeholder = "Search Favorites"
                )

                Card(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(4.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        if (favorites.isEmpty()) {
                            if (state.searchQuery.isEmpty()) {
                                EmptyState(
                                    title = "No favorites yet",
                                    subtitle = "Go catch 'em all by adding some Pokémon!",
                                    modifier = Modifier.align(Alignment.Center)
                                )
                            } else {
                                EmptyState(
                                    title = "No matches found",
                                    subtitle = "Try searching for a different name",
                                    modifier = Modifier.align(Alignment.Center)
                                )
                            }
                        } else {
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(3),
                                contentPadding = PaddingValues(8.dp),
                                modifier = Modifier.fillMaxSize()
                            ) {
                                items(
                                    items = favorites,
                                    key = { it.id },
                                    contentType = { "pokemon_card" }
                                ) { pokemon ->
                                    PokemonCard(
                                        pokemon = pokemon,
                                        sharedTransitionScope = sharedTransitionScope,
                                        animVisibilityScope = animatedVisibilityScope,
                                        onClick = {
                                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                            onNavigateToDetail(pokemon.name.lowercase(), pokemon.id)
                                        },
                                        onToggleFavorite = {
                                            viewModel.onIntent(
                                                HomeContract.Intent.OnToggleFavorite(
                                                    pokemon.id
                                                )
                                            )
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            if (state.isLoggingOut) {
                LoadingOverlay()
            }
        }
    }

    if (state.isSortMenuVisible) {
        SortDialog(
            currentSortType = state.sortType,
            onSortSelected = {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                viewModel.onIntent(HomeContract.Intent.OnSortTypeChange(it))
            },
            onDismiss = { viewModel.onIntent(HomeContract.Intent.OnDismissSortMenu) }
        )
    }
}
