package com.bks.pokedex.ui.screens.detail

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.Spring
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bks.pokedex.R
import com.bks.pokedex.ui.components.ErrorState
import com.bks.pokedex.ui.screens.detail.components.*
import com.bks.pokedex.ui.theme.PokemonColors
import androidx.compose.foundation.pager.rememberPagerState

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun DetailScreen(
    pokemonName: String,
    pokemonId: Int,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onNavigateBack: () -> Unit,
    viewModel: DetailViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val mainType = state.pokemon?.types?.firstOrNull() ?: "normal"
    val themeColor = PokemonColors.getColorForType(mainType)
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                DetailContract.Effect.NavigateBack -> onNavigateBack()
            }
        }
    }

    Surface(modifier = Modifier.fillMaxSize(), color = themeColor) {
        Box(modifier = Modifier.fillMaxSize()) {
            Icon(
                painter = painterResource(id = R.drawable.pokeball),
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.15f),
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 16.dp, end = 16.dp)
                    .size(242.dp)
            )

            Column(modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()) {
                DetailHeader(
                    name = state.pokemon?.name ?: pokemonName,
                    pokemonId = pokemonId,
                    onBackClick = { viewModel.onIntent(DetailContract.Intent.OnBackClick) }
                )

                Spacer(modifier = Modifier.height(180.dp))

                Box(modifier = Modifier.fillMaxSize()) {
                    Card(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(4.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        if (state.isLoading) {
                            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator(color = themeColor)
                            }
                        } else if (state.error != null && state.pokemon == null) {
                            ErrorState(
                                message = state.error!!,
                                onRetry = {
                                    viewModel.onIntent(
                                        DetailContract.Intent.LoadPokemon(
                                            pokemonName
                                        )
                                    )
                                },
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(top = 64.dp)
                            )
                        } else {
                            state.pokemon?.let { pokemon ->
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(top = 64.dp)
                                        .verticalScroll(rememberScrollState()),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    PokemonTypes(types = pokemon.types)
                                    Spacer(modifier = Modifier.height(24.dp))
                                    PokemonAbout(
                                        weight = pokemon.weight,
                                        height = pokemon.height,
                                        abilities = pokemon.abilities,
                                        themeColor = themeColor
                                    )
                                    Text(
                                        text = pokemon.description,
                                        modifier = Modifier.padding(
                                            horizontal = 28.dp,
                                            vertical = 12.dp
                                        ),
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                            color = Color(0xFF1D1D1D)
                                        )
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    PokemonStats(stats = pokemon.stats, themeColor = themeColor)
                                }
                            }
                        }
                    }

                    state.pokemon?.let { pokemon ->
                        val heartScale by animateFloatAsState(
                            targetValue = if (pokemon.isFavorite) 1.1f else 1.0f,
                            animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                            label = "heartScale"
                        )
                        SmallFloatingActionButton(
                            onClick = { viewModel.onIntent(DetailContract.Intent.ToggleFavorite) },
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(end = 20.dp)
                                .offset(y = (-20).dp)
                                .scale(heartScale),
                            shape = CircleShape,
                            containerColor = Color.White,
                            contentColor = if (pokemon.isFavorite) themeColor else Color.LightGray
                        ) {
                            Icon(
                                imageVector = if (pokemon.isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                                contentDescription = "Favorite",
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }

            val imageUrls = if (state.isLoading || state.pokemon == null) {
                listOf("https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/$pokemonId.png")
            } else {
                state.pokemon!!.imageUrls
            }

            val pagerState = rememberPagerState(pageCount = { imageUrls.size })

            PokemonCarousel(
                pokemonId = pokemonId,
                imageUrls = imageUrls,
                pagerState = pagerState,
                scope = scope,
                sharedTransitionScope = sharedTransitionScope,
                animatedVisibilityScope = animatedVisibilityScope
            )
        }
    }
}
