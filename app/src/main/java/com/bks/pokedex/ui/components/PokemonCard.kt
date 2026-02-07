package com.bks.pokedex.ui.components

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.bks.pokedex.R
import com.bks.pokedex.domain.model.Pokemon
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun PokemonCard(
    pokemon: Pokemon,
    sharedTransitionScope: SharedTransitionScope,
    animVisibilityScope: AnimatedVisibilityScope,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    onToggleFavorite: (() -> Unit)? = null
) {
    val haptic = LocalHapticFeedback.current
    var showFeedbackOverlay by remember { mutableStateOf(false) }
    val isCurrentlyFavorite by rememberUpdatedState(pokemon.isFavorite)
    var isRemovingAnimation by remember { mutableStateOf(false) }

    LaunchedEffect(showFeedbackOverlay) {
        if (showFeedbackOverlay) {
            delay(600)
            showFeedbackOverlay = false
        }
    }

    Card(
        modifier = modifier
            .padding(4.dp)
            .height(112.dp)
            .fillMaxWidth()
            .shadow(elevation = 3.dp, shape = RoundedCornerShape(8.dp))
            .pointerInput(pokemon.id) {
                detectTapGestures(
                    onTap = {
                        onClick()
                    },
                    onDoubleTap = {
                        isRemovingAnimation = isCurrentlyFavorite
                        if (isRemovingAnimation) {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        } else {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        }
                        showFeedbackOverlay = true
                        onToggleFavorite?.invoke()
                    }
                )
            },
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.fillMaxSize()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    Text(
                        text = "#${pokemon.id.toString().padStart(3, '0')}",
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(top = 4.dp, end = 8.dp),
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color(0xFF666666),
                            fontSize = 8.sp
                        )
                    )

                    with(sharedTransitionScope) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(pokemon.imageUrl)
                                .crossfade(true)
                                .build(),
                            placeholder = painterResource(id = R.drawable.pokeball),
                            contentDescription = pokemon.name,
                            modifier = Modifier
                                .sharedElement(
                                    rememberSharedContentState(key = "image-${pokemon.id}"),
                                    animVisibilityScope
                                )
                                .size(72.dp)
                                .align(Alignment.Center)
                                .padding(top = 4.dp),
                            contentScale = ContentScale.Fit
                        )
                    }

                    if (pokemon.isFavorite) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = null,
                            tint = Color(0xFFDC0A2D),
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .padding(4.dp)
                                .size(14.dp)
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(24.dp)
                        .background(Color(0xFFEFEFEF)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = pokemon.name.replaceFirstChar { it.uppercase() },
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color(0xFF1D1D1D),
                            fontSize = 10.sp
                        ),
                        textAlign = TextAlign.Center
                    )
                }
            }

            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                androidx.compose.animation.AnimatedVisibility(
                    visible = showFeedbackOverlay,
                    enter = fadeIn() + scaleIn(
                        initialScale = 0.3f,
                        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
                    ),
                    exit = fadeOut() + scaleOut(targetScale = 1.5f)
                ) {
                    if (isRemovingAnimation) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .background(Color(0xFFDC0A2D).copy(alpha = 0.9f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    } else {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = null,
                            tint = Color(0xFFDC0A2D).copy(alpha = 0.8f),
                            modifier = Modifier.size(56.dp)
                        )
                    }
                }
            }
        }
    }
}
