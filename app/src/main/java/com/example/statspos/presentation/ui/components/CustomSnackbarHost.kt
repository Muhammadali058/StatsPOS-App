package com.example.statspos.presentation.ui.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.statspos.utils.SnackbarType

@Composable
fun CustomSnackbarHost(
    snackbarHostState: SnackbarHostState,
    currentSnackbarType: SnackbarType = SnackbarType.INFORMATION
) {
    SnackbarHost(
        hostState = snackbarHostState,
        modifier = Modifier
            .fillMaxSize()
            .wrapContentSize(Alignment.TopCenter),
    )
    { data ->
        val backgroundColor = when (currentSnackbarType) {
            SnackbarType.INFORMATION -> MaterialTheme.colorScheme.primary
            SnackbarType.ERROR -> Color.Red
        }

        Snackbar(
            snackbarData = data,
            modifier = Modifier
                .statusBarsPadding(),
            containerColor = backgroundColor,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            shape = RoundedCornerShape(16.dp),
        )
    }
}