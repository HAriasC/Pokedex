package com.bks.pokedex.ui.screens.detail.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.zIndex
import coil.compose.AsyncImage
import com.bks.pokedex.R
import com.bks.pokedex.domain.model.Evolution
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin

private data class Family(
    val parent: Evolution? = null,
    val siblings: List<Evolution> = emptyList(),
    val children: List<Evolution> = emptyList()
)

@Composable
fun EvolutionDialog(
    currentPokemonName: String,
    currentPokemonImageUrl: String,
    evolutionChain: Evolution?,
    themeColor: Color,
    onEvolutionClick: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var showContent by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (showContent) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "dialogScale"
    )

    val alpha by animateFloatAsState(
        targetValue = if (showContent) 1f else 0f,
        animationSpec = tween(300),
        label = "dialogAlpha"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "rotation")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(20000, easing = LinearEasing)),
        label = "bgRotation"
    )

    LaunchedEffect(Unit) {
        showContent = true
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable(enabled = true, onClick = onDismiss),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                        this.alpha = alpha
                        transformOrigin = androidx.compose.ui.graphics.TransformOrigin(0.5f, 1f)
                    }
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(Color.White, Color(0xFFF5F5F5)),
                            radius = 1000f
                        )
                    )
                    .border(BorderStroke(2.dp, themeColor.copy(alpha = 0.5f)), CircleShape)
                    .clickable(enabled = false) {},
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.pokeball),
                    contentDescription = null,
                    tint = themeColor.copy(alpha = 0.20f),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(30.dp)
                        .rotate(rotation)
                )

                val family = remember(evolutionChain) {
                    evolutionChain?.let { findFamily(it, currentPokemonName) } ?: Family()
                }

                val radius = 115.dp

                // Calcula los ángulos una sola vez para que nodos y líneas coincidan perfectamente
                val nodesWithAngles = remember(family) {
                    val list = mutableListOf<Pair<Evolution, Float>>()
                    if (family.parent == null && family.siblings.isEmpty() && family.children.size > 3) {
                        val angleStep = 360f / family.children.size
                        family.children.forEachIndexed { i, c -> list.add(c to (i * angleStep + 90f)) }
                    } else {
                        family.parent?.let { list.add(it to -90f) }
                        if (family.children.isNotEmpty()) {
                            val arcSpan = 160f
                            val startAngle = 90f - (arcSpan / 2f)
                            val step =
                                if (family.children.size > 1) arcSpan / (family.children.size - 1) else 0f
                            family.children.forEachIndexed { i, c -> list.add(c to (if (family.children.size > 1) startAngle + i * step else 90f)) }
                        }
                        family.siblings.forEachIndexed { i, s -> list.add(s to (if (i % 2 == 0) 0f else 180f)) }
                    }
                    list
                }

                // Dibuja líneas de conexión usando los mismos ángulos que los nodos
                FamilyConnections(
                    angles = nodesWithAngles.map { it.second },
                    radius = radius,
                    color = themeColor
                )

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.zIndex(2f)
                ) {
                    AsyncImage(
                        model = currentPokemonImageUrl,
                        contentDescription = currentPokemonName,
                        modifier = Modifier
                            .size(100.dp)
                            .scale(scale)
                            .clip(CircleShape)
                            .background(Color.White)
                            .border(BorderStroke(1.dp, themeColor.copy(alpha = 0.1f)), CircleShape)
                            .padding(8.dp),
                        contentScale = ContentScale.Fit
                    )
                    Text(
                        text = currentPokemonName,
                        fontWeight = FontWeight.Black,
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center,
                        color = themeColor
                    )
                }

                nodesWithAngles.forEachIndexed { index, pair ->
                    AnimatedEvolutionNode(
                        evolution = pair.first,
                        radius = radius,
                        angleDegrees = pair.second,
                        themeColor = themeColor,
                        delayMillis = index * 100,
                        onClick = onEvolutionClick
                    )
                }
            }
        }
    }
}

@Composable
private fun FamilyConnections(
    angles: List<Float>,
    radius: androidx.compose.ui.unit.Dp,
    color: Color
) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val center = Offset(size.width / 2, size.height / 2)
        val radiusPx = radius.toPx()

        angles.forEach { angleDegrees ->
            val rad = Math.toRadians(angleDegrees.toDouble())
            val target = Offset(
                center.x + radiusPx * cos(rad).toFloat(),
                center.y + radiusPx * sin(rad).toFloat()
            )
            drawLine(
                color = color.copy(alpha = 0.25f),
                start = center,
                end = target,
                strokeWidth = 2.dp.toPx(),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
            )
        }
    }
}

@Composable
private fun AnimatedEvolutionNode(
    evolution: Evolution,
    radius: androidx.compose.ui.unit.Dp,
    angleDegrees: Float,
    themeColor: Color,
    delayMillis: Int,
    onClick: (String) -> Unit
) {
    var isVisible by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "nodeScale"
    )

    LaunchedEffect(Unit) {
        delay(delayMillis.toLong())
        isVisible = true
    }

    val angleRad = Math.toRadians(angleDegrees.toDouble())
    val xOffset = (radius.value * cos(angleRad)).dp
    val yOffset = (radius.value * sin(angleRad)).dp

    Box(
        modifier = Modifier
            .offset(x = xOffset, y = yOffset)
            .size(75.dp)
            .scale(scale)
            .clip(CircleShape)
            .background(Color.White)
            .border(BorderStroke(1.dp, themeColor.copy(alpha = 0.3f)), CircleShape)
            .clickable { onClick(evolution.name) }
            .padding(4.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            AsyncImage(
                model = evolution.imageUrl,
                contentDescription = evolution.name,
                modifier = Modifier.size(45.dp),
                contentScale = ContentScale.Fit
            )
            Text(
                text = evolution.name,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                lineHeight = 10.sp,
                color = Color.DarkGray,
                maxLines = 1
            )
        }
    }
}

private fun findFamily(root: Evolution, currentName: String): Family {
    if (root.name.equals(currentName, ignoreCase = true)) {
        return Family(children = root.evolvesTo)
    }
    val current = root.evolvesTo.find { it.name.equals(currentName, ignoreCase = true) }
    if (current != null) {
        return Family(
            parent = root.copy(evolvesTo = emptyList()),
            siblings = root.evolvesTo.filter { it.name != current.name },
            children = current.evolvesTo
        )
    }
    for (child in root.evolvesTo) {
        val found = findFamily(child, currentName)
        if (found.parent != null || found.children.isNotEmpty() || found.siblings.isNotEmpty()) return found
    }
    return Family()
}
