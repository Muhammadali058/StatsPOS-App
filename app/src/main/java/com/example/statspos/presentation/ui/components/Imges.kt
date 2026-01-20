package com.example.statspos.presentation.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.ContentScale.Companion
import androidx.compose.ui.res.painterResource
import coil3.compose.rememberAsyncImagePainter
import com.example.statspos.R
import com.example.statspos.utils.HP

@Composable
fun ImageView(
    imageUrl: String?,
    modifier: Modifier = Modifier,
    showIfNull: Boolean = false,
    error: Painter? = painterResource(R.drawable.item),
    contentScale: ContentScale = ContentScale.Fit,
    content: @Composable ColumnScope.() -> Unit = {},
) {
    if (showIfNull) {
        Image(
            painter = rememberAsyncImagePainter(
                model = HP.getImageUrl(imageUrl!!),
                error = error,
            ),
            contentDescription = null,
            modifier = modifier,
            contentScale = contentScale,
        )
        Column(content = content)
    } else {
        imageUrl?.let {
            if (it.isNotEmpty()) {
                Image(
                    painter = rememberAsyncImagePainter(
                        model = HP.getImageUrl(imageUrl),
                    ),
                    contentDescription = null,
                    modifier = modifier,
                    contentScale = contentScale,
                )
                Column(content = content)
            }
        }
    }
}