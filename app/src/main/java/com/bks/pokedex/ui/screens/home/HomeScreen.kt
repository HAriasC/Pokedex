package com.bks.pokedex.ui.screens.home

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.bks.pokedex.ui.components.*

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun HomeScreen(
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onNavigateToDetail: (String, Int) -> Unit,
    onNavigateToFavorites: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val pagingItems = viewModel.pokemonPagingData.collectAsLazyPagingItems()
    val haptic = LocalHapticFeedback.current

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFDC0A2D)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
            ) {
                PokedexHeader(
                    title = "Pokédex",
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
                    onSortIconClick = { viewModel.onIntent(HomeContract.Intent.OnSortIconClick) }
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
                        val loadState = pagingItems.loadState
                        if (loadState.refresh is LoadState.Error && pagingItems.itemCount == 0) {
                            ErrorState(
                                message = (loadState.refresh as LoadState.Error).error.localizedMessage
                                    ?: "Error",
                                onRetry = { pagingItems.retry() },
                                modifier = Modifier.align(Alignment.Center)
                            )
                        } else {
                            val sortedIndices =
                                (0 until pagingItems.itemCount).toList().sortedWith { a, b ->
                                    val pokemonA = pagingItems[a];
                                    val pokemonB = pagingItems[b]
                                    if (pokemonA != null && pokemonB != null) {
                                        if (state.sortType == HomeContract.SortType.NAME) pokemonA.name.compareTo(
                                            pokemonB.name
                                        )
                                        else pokemonA.id.compareTo(pokemonB.id)
                                    } else 0
                                }

                            if (sortedIndices.isEmpty() && loadState.refresh is LoadState.NotLoading) {
                                EmptyState(modifier = Modifier.align(Alignment.Center))
                            } else {
                                LazyVerticalGrid(
                                    columns = GridCells.Fixed(3),
                                    contentPadding = PaddingValues(8.dp),
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    items(sortedIndices.size) { index ->
                                        val realIndex = sortedIndices[index]
                                        val pokemon = pagingItems[realIndex]
                                        if (pokemon != null) {
                                            PokemonCard(
                                                pokemon = pokemon,
                                                sharedTransitionScope = sharedTransitionScope,
                                                animVisibilityScope = animatedVisibilityScope,
                                                onClick = {
                                                    onNavigateToDetail(
                                                        pokemon.name.lowercase(),
                                                        pokemon.id
                                                    )
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
                        if (loadState.refresh is LoadState.Loading && pagingItems.itemCount == 0) {
                            CircularProgressIndicator(
                                modifier = Modifier.align(Alignment.Center),
                                color = Color(0xFFDC0A2D)
                            )
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
            onSortSelected = { viewModel.onIntent(HomeContract.Intent.OnSortTypeChange(it)) },
            onDismiss = { viewModel.onIntent(HomeContract.Intent.OnDismissSortMenu) }
        )
    }
}
