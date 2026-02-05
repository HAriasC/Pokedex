package com.bks.pokedex.ui.screens.home

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SortByAlpha
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.bks.pokedex.R
import com.bks.pokedex.ui.components.PokemonCard

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
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

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is HomeContract.Effect.NavigateToDetail -> {}
                HomeContract.Effect.NavigateToFavorites -> onNavigateToFavorites()
            }
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFDC0A2D)
    ) {
        Column(modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
        ) {
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
                    text = "Pokédex",
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
                    textStyle = TextStyle(
                        color = Color.Black,
                        fontSize = 14.sp
                    ),
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
                                        text = "Search",
                                        color = Color(0xFF666666),
                                        fontSize = 14.sp
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
                    if (pagingItems.itemCount > 0 || pagingItems.loadState.refresh is LoadState.NotLoading) {
                        val sortedIndices =
                            (0 until pagingItems.itemCount).toList().sortedWith { a, b ->
                                val pokemonA = pagingItems[a]
                                val pokemonB = pagingItems[b]
                                if (pokemonA != null && pokemonB != null) {
                                    if (state.sortType == HomeContract.SortType.NAME) {
                                        pokemonA.name.compareTo(pokemonB.name)
                                    } else {
                                        pokemonA.id.compareTo(pokemonB.id)
                                    }
                                } else 0
                            }

                        if (sortedIndices.isEmpty() && pagingItems.loadState.refresh is LoadState.NotLoading) {
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
                                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
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

                                if (pagingItems.loadState.append is LoadState.Loading) {
                                    item(contentType = "loading") {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(16.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            CircularProgressIndicator(
                                                modifier = Modifier.size(24.dp),
                                                color = Color(0xFFDC0A2D)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    if (pagingItems.loadState.refresh is LoadState.Loading) {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center),
                            color = Color(0xFFDC0A2D)
                        )
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

@Composable
fun EmptyState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = painterResource(id = R.drawable.pokeball),
            contentDescription = null,
            tint = Color.LightGray,
            modifier = Modifier.size(80.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "No Pokémon found",
            style = MaterialTheme.typography.titleMedium,
            color = Color.Gray
        )
        Text(
            text = "Try a different search term",
            style = MaterialTheme.typography.bodySmall,
            color = Color.LightGray
        )
    }
}

@Composable
fun SortDialog(
    currentSortType: HomeContract.SortType,
    onSortSelected: (HomeContract.SortType) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFDC0A2D)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Sort by:",
                    style = MaterialTheme.typography.titleLarge.copy(color = Color.White),
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        SortOption(
                            text = "Number",
                            selected = currentSortType == HomeContract.SortType.NUMBER,
                            onClick = { onSortSelected(HomeContract.SortType.NUMBER) }
                        )
                        SortOption(
                            text = "Name",
                            selected = currentSortType == HomeContract.SortType.NAME,
                            onClick = { onSortSelected(HomeContract.SortType.NAME) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SortOption(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .selectable(
                selected = selected,
                onClick = onClick
            )
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = null,
            colors = RadioButtonDefaults.colors(
                selectedColor = Color(0xFFDC0A2D),
                unselectedColor = Color.Gray
            )
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = Color.Black
            ),
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}

@Composable
fun ErrorState(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = painterResource(id = R.drawable.pokeball),
            contentDescription = null,
            tint = Color(0xFFDC0A2D),
            modifier = Modifier.size(64.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Ops! Something went wrong",
            style = MaterialTheme.typography.titleLarge.copy(color = Color(0xFFDC0A2D)),
            textAlign = TextAlign.Center
        )
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = onRetry,
            colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                containerColor = Color(
                    0xFFDC0A2D
                )
            )
        ) {
            Text("Retry")
        }
    }
}
