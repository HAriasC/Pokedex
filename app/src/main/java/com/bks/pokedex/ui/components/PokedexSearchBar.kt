package com.bks.pokedex.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SortByAlpha
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import com.bks.pokedex.ui.screens.home.HomeContract

@Composable
fun PokedexSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    sortType: HomeContract.SortType,
    onSortIconClick: () -> Unit,
    placeholder: String = "Search",
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        BasicTextField(
            value = query,
            onValueChange = onQueryChange,
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
                        if (query.isEmpty()) {
                            Text(
                                text = placeholder,
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
                .clickable { onSortIconClick() },
            shape = RoundedCornerShape(16.dp),
            color = Color.White
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = if (sortType == HomeContract.SortType.NUMBER) Icons.Default.Tag else Icons.Default.SortByAlpha,
                    contentDescription = "Sort",
                    tint = Color(0xFFDC0A2D),
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}
