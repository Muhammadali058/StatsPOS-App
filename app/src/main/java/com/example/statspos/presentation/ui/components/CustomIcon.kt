package com.example.statspos.presentation.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource

@Composable
fun CustomIcon(
    modifier: Modifier = Modifier,
    @DrawableRes icon: Int,
    contentDescription: String? = null,
    tint: Color = MaterialTheme.colorScheme.onPrimaryContainer
) {
    Icon(
        painterResource(icon),
        contentDescription = contentDescription,
        tint = tint,
        modifier = modifier,
    )
}


@Composable
fun CustomIcon(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    contentDescription: String? = null,
    tint: Color = MaterialTheme.colorScheme.onPrimaryContainer
) {
    Icon(
        icon,
        contentDescription = contentDescription,
        tint = tint,
        modifier = modifier,
    )
}