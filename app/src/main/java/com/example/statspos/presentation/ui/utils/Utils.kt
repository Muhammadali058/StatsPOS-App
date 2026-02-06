package com.example.statspos.presentation.ui.utils

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.unit.dp

object ConstantPaddings {
    val DEFAULT_TEXTBOX_INSIDE = PaddingValues(
        16.dp
    )
    val CUSTOM_TEXTBOX_OUTSIDE = PaddingValues(
        vertical = 4.dp
    )
    val CUSTOM_TEXTBOX_INSIDE = PaddingValues(
        start = 15.dp,
        top = 10.dp,
        end = 10.dp,
        bottom = 10.dp,
    )
    val BODY_HORIZONTAL = PaddingValues(
        horizontal = 16.dp
    )

    val LIST_PADDING_VERTICAL = 8.dp
}

object ConstantSize {
    val DEFAULT_ICON_BUTTON = 30.dp
    val DEFAULT_ICON = 24.dp
    val DEFAULT_TEXTBOX_HEIGHT = 42.dp
    val ORIGINAL_TEXTBOX_HEIGHT = 52.dp
}