package com.graphees.statspos.presentation.ui.screens.main.login

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import com.graphees.statspos.presentation.ui.components.AppText

@Composable
fun CloseAppScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center,
    ){
        AppText(
            text = "Completely close this app and login again.",
            style = TextStyle(
                textAlign = TextAlign.Center,
            )
        )
    }
}