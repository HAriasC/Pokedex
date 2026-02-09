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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bks.pokedex.R
import com.bks.pokedex.ui.components.ErrorState
import com.bks.pokedex.ui.screens.detail.components.*
import com.bks.pokedex.ui.theme.PokemonColors
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.ui.zIndex

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun DetailScreen(
    pokemonName: String,
    pokemonId: Int,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onNavigateBack: () -> Unit,
    onNavigateToPokemon: (String) -> Unit,
    viewModel: DetailViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val mainType = state.pokemon?.types?.firstOrNull() ?: "normal"
    val themeColor = PokemonColors.getColorForType(mainType)
    val scope = rememberCoroutineScope()

    val displayedPokemonId = if (pokemonId != 0) pokemonId else (state.pokemon?.id ?: 0)

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                DetailContract.Effect.NavigateBack -> onNavigateBack()
                is DetailContract.Effect.NavigateToPokemon -> onNavigateToPokemon(effect.name)
            }
        }
    }

    Scaffold(
        containerColor = themeColor,
        floatingActionButton = {
            state.pokemon?.let { pokemon ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(
                        12.dp,
                        Alignment.CenterHorizontally
                    ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (pokemon.evolutionChain != null) {
                        Button(
                            onClick = { viewModel.onIntent(DetailContract.Intent.ToggleEvolutionDialog) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = themeColor,
                                contentColor = Color.White
                            ),
                            shape = CircleShape,
                            modifier = Modifier.height(40.dp),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.pokeball),
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Evoluciones",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }

                    val heartScale by animateFloatAsState(
                        targetValue = if (pokemon.isFavorite) 1.2f else 1.0f,
                        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                        label = "heartScale"
                    )

                    Button(
                        onClick = { viewModel.onIntent(DetailContract.Intent.ToggleFavorite) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = themeColor,
                            contentColor = Color.White
                        ),
                        shape = CircleShape,
                        modifier = Modifier
                            .size(40.dp)
                            .scale(heartScale),
                        contentPadding = PaddingValues(0.dp),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
                    ) {
                        Icon(
                            imageVector = if (pokemon.isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                            contentDescription = "Favorite",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        },
        floatingActionButtonPosition = FabPosition.Center
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = innerPadding.calculateBottomPadding())
        ) {
            // 1. Capa de fondo: Pokébola decorativa
            Icon(
                painter = painterResource(id = R.drawable.pokeball),
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.15f),
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 16.dp, end = 16.dp)
                    .size(242.dp)
            )

            // 2. Capa de contenido: Detalles
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .navigationBarsPadding()
            ) {
                DetailHeader(
                    name = state.pokemon?.name ?: pokemonName,
                    pokemonId = displayedPokemonId,
                    onBackClick = { viewModel.onIntent(DetailContract.Intent.OnBackClick) }
                )

                Spacer(modifier = Modifier.height(180.dp))

                Card(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 4.dp),
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
                                Spacer(modifier = Modifier.height(4.dp))
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
                                Spacer(modifier = Modifier.height(80.dp))
                            }
                        }
                    }
                }
            }

            // 3. Capa del Carrusel
            val imageUrls = if (state.isLoading || state.pokemon == null) {
                if (displayedPokemonId != 0) {
                    listOf("https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/$displayedPokemonId.png")
                } else emptyList()
            } else {
                state.pokemon!!.imageUrls
            }

            val pagerState = rememberPagerState(pageCount = { imageUrls.size })

            Box(modifier = Modifier.zIndex(1f)) {
                PokemonCarousel(
                    pokemonId = displayedPokemonId,
                    imageUrls = imageUrls,
                    pagerState = pagerState,
                    scope = scope,
                    sharedTransitionScope = sharedTransitionScope,
                    animatedVisibilityScope = animatedVisibilityScope
                )
            }

            // 5. Diálogo de Evoluciones (Se pasa el themeColor corregido)
            if (state.isEvolutionDialogOpen) {
                state.pokemon?.let { pokemon ->
                    EvolutionDialog(
                        currentPokemonName = pokemon.name,
                        currentPokemonImageUrl = pokemon.imageUrls.firstOrNull() ?: "",
                        evolutionChain = pokemon.evolutionChain,
                        themeColor = themeColor,
                        onEvolutionClick = {
                            viewModel.onIntent(
                                DetailContract.Intent.OnEvolutionClick(
                                    it
                                )
                            )
                        },
                        onDismiss = { viewModel.onIntent(DetailContract.Intent.ToggleEvolutionDialog) }
                    )
                }
            }
        }
    }
}
