package com.bks.pokedex.ui.screens.detail

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.Spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.bks.pokedex.R
import com.bks.pokedex.ui.theme.PokemonColors
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun DetailScreen(
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

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = themeColor
    ) {
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

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .navigationBarsPadding()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { viewModel.onIntent(DetailContract.Intent.OnBackClick) }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                    Text(
                        text = state.pokemon?.name?.replaceFirstChar { it.uppercase() }
                            ?: "Loading...",
                        style = MaterialTheme.typography.headlineMedium.copy(color = Color.White),
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = "#${pokemonId.toString().padStart(3, '0')}",
                        style = MaterialTheme.typography.labelLarge.copy(color = Color.White),
                        modifier = Modifier.padding(end = 16.dp)
                    )
                }

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
                        } else {
                            state.pokemon?.let { pokemon ->
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(top = 64.dp)
                                        .verticalScroll(rememberScrollState()),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                        pokemon.types.forEach { type ->
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(20.dp))
                                                    .background(PokemonColors.getColorForType(type))
                                                    .padding(horizontal = 16.dp, vertical = 6.dp)
                                            ) {
                                                Text(
                                                    text = type.replaceFirstChar { it.uppercase() },
                                                    color = Color.White,
                                                    style = MaterialTheme.typography.labelLarge
                                                )
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(24.dp))

                                    Text(
                                        text = "About",
                                        textAlign = TextAlign.Center,
                                        style = MaterialTheme.typography.titleLarge.copy(color = themeColor)
                                    )

                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 24.dp, vertical = 16.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.Top
                                    ) {
                                        AboutInfoItem(
                                            value = "${pokemon.weight / 10f} kg",
                                            label = "Weight",
                                            iconId = R.drawable.weight,
                                            modifier = Modifier.weight(1f)
                                        )
                                        Box(
                                            modifier = Modifier
                                                .width(1.dp)
                                                .height(64.dp)
                                                .background(Color(0xFFE0E0E0))
                                                .align(Alignment.CenterVertically)
                                        )
                                        AboutInfoItem(
                                            value = "${pokemon.height / 10f} m",
                                            label = "Height",
                                            iconId = R.drawable.straighten,
                                            modifier = Modifier.weight(1f)
                                        )
                                        Box(
                                            modifier = Modifier
                                                .width(1.dp)
                                                .height(64.dp)
                                                .background(Color(0xFFE0E0E0))
                                                .align(Alignment.CenterVertically)
                                        )

                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Column(
                                                modifier = Modifier.height(44.dp),
                                                verticalArrangement = Arrangement.Center
                                            ) {
                                                pokemon.abilities.forEach { ability ->
                                                    Text(
                                                        text = ability.replaceFirstChar { it.uppercase() },
                                                        style = MaterialTheme.typography.bodyLarge.copy(
                                                            color = Color(0xFF1D1D1D)
                                                        ),
                                                        textAlign = TextAlign.Center
                                                    )
                                                }
                                            }
                                            Spacer(modifier = Modifier.height(12.dp))
                                            Text(
                                                text = "Moves",
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = Color.Gray
                                            )
                                        }
                                    }

                                    Text(
                                        text = pokemon.description,
                                        modifier = Modifier.padding(
                                            horizontal = 28.dp,
                                            vertical = 12.dp
                                        ),
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            textAlign = TextAlign.Center,
                                            color = Color(0xFF1D1D1D)
                                        )
                                    )

                                    Spacer(modifier = Modifier.height(16.dp))

                                    Text(
                                        text = "Base Stats",
                                        textAlign = TextAlign.Center,
                                        style = MaterialTheme.typography.titleLarge.copy(color = themeColor)
                                    )

                                    Column(
                                        modifier = Modifier.padding(
                                            horizontal = 24.dp,
                                            vertical = 20.dp
                                        )
                                    ) {
                                        pokemon.stats.forEach { stat ->
                                            StatRow(stat.name, stat.value, themeColor)
                                        }
                                    }
                                }
                            }
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

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .navigationBarsPadding()
                    .align(Alignment.TopCenter)
                    .padding(top = 76.dp)
            ) {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp),
                    beyondViewportPageCount = 1
                ) { page ->
                    val pageOffset =
                        ((pagerState.currentPage - page) + pagerState.currentPageOffsetFraction).absoluteValue

                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        with(sharedTransitionScope) {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(imageUrls.getOrNull(page))
                                    .crossfade(true)
                                    .build(),
                                contentDescription = null,
                                modifier = Modifier
                                    .run {
                                        if (page == 0) {
                                            this.sharedElement(
                                                rememberSharedContentState(key = "image-$pokemonId"),
                                                animatedVisibilityScope = animatedVisibilityScope,
                                                boundsTransform = { _, _ ->
                                                    spring(
                                                        stiffness = Spring.StiffnessLow,
                                                        dampingRatio = Spring.DampingRatioLowBouncy
                                                    )
                                                }
                                            )
                                        } else this
                                    }
                                    .size(240.dp)
                                    .graphicsLayer {
                                        alpha = lerp(1f, 0f, pageOffset.coerceIn(0f, 1f))
                                        val scale = lerp(1f, 0.9f, pageOffset.coerceIn(0f, 1f))
                                        scaleX = scale
                                        scaleY = scale
                                    }
                            )
                        }
                    }
                }

                if (imageUrls.size > 1) {
                    IconButton(
                        onClick = { scope.launch { pagerState.animateScrollToPage(if (pagerState.currentPage > 0) pagerState.currentPage - 1 else imageUrls.size - 1) } },
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .padding(start = 12.dp)
                    ) {
                        Icon(
                            Icons.Default.ChevronLeft,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    IconButton(
                        onClick = { scope.launch { pagerState.animateScrollToPage(if (pagerState.currentPage < imageUrls.size - 1) pagerState.currentPage + 1 else 0) } },
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .padding(end = 12.dp)
                    ) {
                        Icon(
                            Icons.Default.ChevronRight,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(36.dp)
                        )
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
                            .offset(y = (146).dp)
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
    }
}

@Composable
fun AboutInfoItem(value: String, label: String, iconId: Int, modifier: Modifier = Modifier) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = modifier) {
        Row(modifier = Modifier.height(44.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painterResource(id = iconId),
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = Color(0xFF1D1D1D)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge.copy(color = Color(0xFF1D1D1D))
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text(text = label, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
    }
}

@Composable
fun StatRow(name: String, value: Int, color: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val statAbbreviation = when (name.lowercase()) {
            "special-attack" -> "SATK"
            "special-defense" -> "SDEF"
            "speed" -> "SPD"
            else -> name.uppercase().take(3)
        }
        Text(
            text = statAbbreviation,
            modifier = Modifier.width(48.dp),
            style = MaterialTheme.typography.labelLarge.copy(color = color)
        )
        Box(
            modifier = Modifier
                .width(1.dp)
                .height(20.dp)
                .background(Color(0xFFE0E0E0))
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = value.toString().padStart(3, '0'),
            modifier = Modifier.width(36.dp),
            style = MaterialTheme.typography.bodyLarge.copy(color = Color(0xFF1D1D1D))
        )
        Spacer(modifier = Modifier.width(12.dp))
        LinearProgressIndicator(
            progress = { value / 255f },
            modifier = Modifier
                .weight(1f)
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp)),
            color = color,
            trackColor = color.copy(alpha = 0.2f)
        )
    }
}
