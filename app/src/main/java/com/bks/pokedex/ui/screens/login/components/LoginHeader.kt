package com.bks.pokedex.ui.screens.login.components

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bks.pokedex.R

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun LoginHeader(
    rotationValue: Float,
    sharedTransitionScope: SharedTransitionScope,
    animVisibilityScope: AnimatedVisibilityScope,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(60.dp))

        with(sharedTransitionScope) {
            Image(
                painter = painterResource(id = R.drawable.pokeball),
                contentDescription = null,
                modifier = Modifier
                    .sharedElement(
                        rememberSharedContentState(key = "pokeball-logo"),
                        animVisibilityScope
                    )
                    .size(100.dp)
                    .rotate(rotationValue)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "POKÉDEX",
            style = MaterialTheme.typography.headlineLarge.copy(
                color = Color.White,
                fontSize = 36.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = (-1).sp
            )
        )
    }
}
