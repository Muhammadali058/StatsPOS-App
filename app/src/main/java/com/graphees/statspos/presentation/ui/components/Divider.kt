package com.graphees.statspos.presentation.ui.components

import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun AppHorizontalDivider(
    modifier: Modifier = Modifier,
    thickness: Dp = 1.dp,
    color: Color = MaterialTheme.colorScheme.surfaceDim,
) {
    HorizontalDivider(
        modifier = modifier,
        thickness = thickness,
        color = color,
    )
}
@Composable
fun ListHorizontalDivider(
    modifier: Modifier = Modifier,
    thickness: Dp = 0.5.dp,
    color: Color = MaterialTheme.colorScheme.onPrimaryContainer.copy(0.2f),
) {
    HorizontalDivider(
        modifier = modifier,
        thickness = thickness,
        color = color
    )
}