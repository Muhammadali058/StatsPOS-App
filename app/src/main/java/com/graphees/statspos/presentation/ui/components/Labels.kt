package com.graphees.statspos.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

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
    textAlign: TextAlign = TextAlign.Start,
    style: TextStyle = TextStyle(fontSize = 14.sp, textAlign = textAlign),
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
    style: TextStyle = TextStyle(fontSize = 16.sp),
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
    textAlign: TextAlign = TextAlign.Start,
    style: TextStyle = TextStyle(
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        textAlign = textAlign
    ),
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
    style: TextStyle = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold),
) {
    Text(
        modifier = modifier,
        text = text,
        color = color,
        style = style
    )
}

@Composable
fun BottomHeading(
    modifier: Modifier = Modifier,
    text: String,
    value: String,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(8.dp)
    ) {
        HeadingMedium(
            text = text,
        )
        LabelMedium(
            text = value,
        )
    }
}

@Composable
fun ListMainHeading(
    text: String,
    modifier: Modifier = Modifier,
    textAlign: TextAlign = TextAlign.Start,
    color: Color = MaterialTheme.colorScheme.onPrimaryContainer,
) {
    Text(
        modifier = modifier,
        text = text,
        color = color,
        style = TextStyle(
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            textAlign = textAlign
        )
    )
}

@Composable
fun ListMainLabel(
    text: String,
    modifier: Modifier = Modifier,
    textAlign: TextAlign = TextAlign.Start,
    color: Color = MaterialTheme.colorScheme.onPrimaryContainer,
) {
    Text(
        modifier = modifier,
        text = text,
        color = color,
        style = TextStyle(
            fontSize = 14.sp,
            textAlign = textAlign
        )
    )
}

@Composable
fun ListHeading(
    text: String,
    modifier: Modifier = Modifier,
    textAlign: TextAlign = TextAlign.Start,
    color: Color = MaterialTheme.colorScheme.onPrimaryContainer,
) {
    Text(
        modifier = modifier,
        text = text,
        color = color,
        style = TextStyle(
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            textAlign = textAlign,
        )
    )
}

@Composable
fun ListLabel(
    text: String,
    modifier: Modifier = Modifier,
    textAlign: TextAlign = TextAlign.Start,
    color: Color = MaterialTheme.colorScheme.onPrimaryContainer,
) {
    Text(
        modifier = modifier,
        text = text,
        color = color,
        style = TextStyle(
            fontSize = 12.sp,
            textAlign = textAlign,
        )
    )
}
