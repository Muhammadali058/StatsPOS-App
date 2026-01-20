package com.example.statspos.presentation.ui.components

import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign

@Composable
fun AppText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.onPrimaryContainer,
    style: TextStyle = LocalTextStyle.current,
    textAlign: TextAlign? = null,
) {
    Text(
        modifier = modifier,
        text = text,
        color = color,
        style = style,
        textAlign = textAlign,
    )
}

@Composable
fun LabelMedium(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.onPrimaryContainer,
    style: TextStyle = MaterialTheme.typography.labelMedium,
) {
    Text(
        modifier = modifier,
        text = text,
        color = color,
        style = style
    )
}

@Composable
fun LabelLarge(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.onPrimaryContainer,
    style: TextStyle = MaterialTheme.typography.labelLarge,
) {
    Text(
        modifier = modifier,
        text = text,
        color = color,
        style = style
    )
}


@Composable
fun HeadingMedium(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.onPrimaryContainer,
    style: TextStyle = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
) {
    Text(
        modifier = modifier,
        text = text,
        color = color,
        style = style
    )
}

@Composable
fun HeadingLarge(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.onPrimaryContainer,
    style: TextStyle = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
) {
    Text(
        modifier = modifier,
        text = text,
        color = color,
        style = style
    )
}
