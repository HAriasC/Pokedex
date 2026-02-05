package com.bks.pokedex.ui.screens.favorites

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SortByAlpha
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bks.pokedex.R
import com.bks.pokedex.ui.components.PokemonCard
import com.bks.pokedex.ui.screens.home.EmptyState
import com.bks.pokedex.ui.screens.home.HomeContract
import com.bks.pokedex.ui.screens.home.SortDialog

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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.pokeball),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Favorites",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        color = Color.White
                    )
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BasicTextField(
                    value = state.searchQuery,
                    onValueChange = { viewModel.onIntent(HomeContract.Intent.OnSearchQueryChange(it)) },
                    modifier = Modifier
                        .weight(1f)
                        .height(32.dp)
                        .background(Color.White, RoundedCornerShape(16.dp)),
                    textStyle = MaterialTheme.typography.bodyMedium.copy(color = Color.Black),
                    singleLine = true,
                    cursorBrush = SolidColor(Color(0xFFDC0A2D)),
                    decorationBox = { innerTextField ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = null,
                                tint = Color(0xFFDC0A2D),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Box(modifier = Modifier.weight(1f)) {
                                if (state.searchQuery.isEmpty()) {
                                    Text(
                                        text = "Search Favorites",
                                        color = Color(0xFF666666),
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                                Box(modifier = Modifier.padding(start = 2.dp)) {
                                    innerTextField()
                                }
                            }
                        }
                    }
                )

                Spacer(modifier = Modifier.width(8.dp))

                Surface(
                    modifier = Modifier
                        .size(32.dp)
                        .clickable {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            viewModel.onIntent(HomeContract.Intent.OnSortIconClick)
                        },
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = if (state.sortType == HomeContract.SortType.NUMBER) Icons.Default.Tag else Icons.Default.SortByAlpha,
                            contentDescription = "Sort",
                            tint = Color(0xFFDC0A2D),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

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
                        EmptyState(modifier = Modifier.align(Alignment.Center))
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
