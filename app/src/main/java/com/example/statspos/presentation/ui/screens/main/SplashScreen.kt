package com.example.statspos.presentation.ui.screens.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.statspos.R
import com.example.statspos.presentation.ui.components.CustomIcon

@Preview(showBackground = true)
@Composable
fun SplashScreen() {
    Box(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.surface)
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CustomIcon(
            icon = R.drawable.statspos_circle,
            modifier = Modifier
                .size(160.dp)
                .clip(CircleShape),
            tint = MaterialTheme.colorScheme.primary
        )
    }
}