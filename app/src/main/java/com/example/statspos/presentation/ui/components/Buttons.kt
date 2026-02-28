package com.example.statspos.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun SmallButton(
    text: String,
    modifier: Modifier = Modifier,
    size: Dp = 28.dp,
    onClick: () -> Unit,
) {
    Box(
        modifier = modifier
            .background(MaterialTheme.colorScheme.primary)
            .size(size)
            .clickable {
                onClick()
            },
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            style = TextStyle(
                color = MaterialTheme.colorScheme.onPrimary
            )
        )
    }
}

@Composable
fun SaveButton(
    modifier: Modifier = Modifier
        .fillMaxWidth(),
    text: String = "Save",
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .height(52.dp),
        shape = RoundedCornerShape(8.dp),
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}

@Composable
fun AppFloatingActionButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    FloatingActionButton(
        modifier = modifier,
        onClick = onClick,
        shape = CircleShape,
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = null,
        )
    }
}

@Composable
fun ReportButton(
    text:String = "Show",
    modifier: Modifier = Modifier,
//    shape: Shape = RectangleShape,
    shape: Shape = RoundedCornerShape(4.dp),
    onClick: () -> Unit,
) {
    Button(
        onClick = onClick,
        shape = shape,
        modifier = modifier
            .height(40.dp)
            .defaultMinSize(minHeight = 0.dp, minWidth = 70.dp),
        contentPadding = PaddingValues(vertical = 0.dp, horizontal = 12.dp),
    ) {
        Text(text)
    }
}