package com.example.statspos.presentation.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

@Composable
fun ListCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(10.dp),
    elevation: CardElevation = CardDefaults.cardElevation(
        defaultElevation = 3.dp
    ),
    border: BorderStroke? = null,
    onClick: () -> Unit = {},
    content: @Composable ColumnScope.() -> Unit,
) {
    Card(
        modifier = modifier,
        elevation = elevation,
        shape = shape,
        border = border,
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
        ),
        content = {
            Column(
                modifier = Modifier
                    .padding(8.dp),
            ) {
                content()
            }
        }
    )
}