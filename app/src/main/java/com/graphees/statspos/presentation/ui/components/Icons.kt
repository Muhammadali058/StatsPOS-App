package com.graphees.statspos.presentation.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.graphees.statspos.R
import com.graphees.statspos.presentation.ui.utils.ConstantSize

@Composable
fun AppIconBox(
    @DrawableRes icon: Int,
    modifier: Modifier = Modifier,
    size: Dp = ConstantSize.DEFAULT_ICON,
    contentDescription: String? = null,
    tint: Color = MaterialTheme.colorScheme.primary,
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(tint.copy(.1f))
            .padding(10.dp),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            painterResource(icon),
            contentDescription = contentDescription,
            tint = tint,
            modifier = modifier
                .size(size),
        )
    }
}

@Composable
fun AppIconButtonBox(
    @DrawableRes icon: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    buttonSize: Dp = 42.dp,
    size: Dp = ConstantSize.DEFAULT_ICON,
    enabled: Boolean = true,
    tint: Color = MaterialTheme.colorScheme.primary,
    colors: IconButtonColors = IconButtonDefaults.iconButtonColors(),
    interactionSource: MutableInteractionSource? = null,
) {
    IconButton(
        modifier = modifier
            .size(buttonSize),
        onClick = onClick,
        enabled = enabled,
        colors = colors,
        interactionSource = interactionSource,
        shape = RectangleShape,
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(12.dp))
                .background(tint.copy(.1f))
//                .padding(4.dp)
            ,
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painterResource(icon),
                contentDescription = null,
                tint = tint,
                modifier = modifier
                    .size(size),
            )
        }
    }
}

@Composable
fun AppIcon(
    @DrawableRes icon: Int,
    modifier: Modifier = Modifier,
    size: Dp = ConstantSize.DEFAULT_ICON,
    contentDescription: String? = null,
    tint: Color = MaterialTheme.colorScheme.onPrimaryContainer
) {
    Icon(
        painterResource(icon),
        contentDescription = contentDescription,
        tint = tint,
        modifier = modifier
            .size(size),
    )
}


@Composable
fun AppIcon(
    icon: ImageVector,
    modifier: Modifier = Modifier,
    size: Dp = ConstantSize.DEFAULT_ICON,
    contentDescription: String? = null,
    tint: Color = MaterialTheme.colorScheme.onPrimaryContainer
) {
    Icon(
        icon,
        contentDescription = contentDescription,
        tint = tint,
        modifier = modifier
            .size(size),
    )
}

@Composable
fun AppIconButton(
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    buttonSize: Dp = ConstantSize.DEFAULT_ICON_BUTTON,
    size: Dp = ConstantSize.DEFAULT_ICON,
    tint: Color = MaterialTheme.colorScheme.onPrimaryContainer,
    enabled: Boolean = true,
    colors: IconButtonColors = IconButtonDefaults.iconButtonColors(),
    interactionSource: MutableInteractionSource? = null,
    shape: Shape = IconButtonDefaults.standardShape,
) {
    IconButton(
        modifier = modifier
            .size(buttonSize),
        onClick = onClick,
        enabled = enabled,
        colors = colors,
        interactionSource = interactionSource,
        shape = shape,
    ) {
        AppIcon(
            icon = icon,
            size = size,
            tint = tint,
            modifier = Modifier
        )
    }
}


@Composable
fun AppIconButton(
    @DrawableRes icon: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    buttonSize: Dp = ConstantSize.DEFAULT_ICON_BUTTON,
    size: Dp = ConstantSize.DEFAULT_ICON,
    enabled: Boolean = true,
    tint: Color = MaterialTheme.colorScheme.onPrimaryContainer,
    colors: IconButtonColors = IconButtonDefaults.iconButtonColors(),
    interactionSource: MutableInteractionSource? = null,
    shape: Shape = IconButtonDefaults.standardShape,
) {
    IconButton(
        modifier = modifier
            .size(buttonSize),
        onClick = onClick,
        enabled = enabled,
        colors = colors,
        interactionSource = interactionSource,
        shape = shape,
    ) {
        AppIcon(
            icon = icon,
            size = size,
            tint = tint,
            modifier = Modifier
        )
    }
}

@Composable
fun ShowReportIcon(
    modifier: Modifier = Modifier,
) {
    AppIcon(
        icon = Icons.Default.Download,
        modifier = modifier.size(20.dp)
    )
}

@Composable
fun ShowReportIcon(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    AppIconButton(
        modifier = modifier,
        icon = Icons.Default.Download,
        onClick = onClick,
    )
//    IconButton(
//        modifier = modifier,
//        onClick = onClick,
//    ) {
//        ShowReportIcon()
//    }
}

@Composable
fun DeleteIcon(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    AppIconButton(
        icon = Icons.Default.Delete,
        onClick = onClick,
        modifier = modifier,
        size = 22.dp
    )
}

@Composable
fun FilterIcon(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    AppIconButtonBox(
        modifier = modifier,
        icon = R.drawable.filter,
        size = 18.dp,
        onClick = {
            onClick()
        }
    )
//    AppIconButton(
//        icon = R.drawable.filter,
//        onClick = onClick,
//        modifier = modifier,
//        size = 18.dp,
//        buttonSize = 26.dp,
//    )
}
