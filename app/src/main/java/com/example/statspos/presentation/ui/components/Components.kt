package com.example.statspos.presentation.ui.components

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.example.statspos.presentation.ui.theme.backgroundDark
import com.example.statspos.presentation.ui.theme.backgroundLight
import com.example.statspos.presentation.ui.theme.primaryLight

@Composable
fun ChangeStatusBarColor(darkTheme: Boolean = false) {
    val colorScheme =  MaterialTheme.colorScheme
    val view = LocalView.current
    SideEffect {
        val window = (view.context as Activity).window
//        window.navigationBarColor = if(darkTheme) colorScheme.onSurface.toArgb() else backgroundLight.toArgb()
//        window.statusBarColor = if(darkTheme) colorScheme.onSurface.toArgb() else backgroundLight.toArgb()
        window.navigationBarColor = colorScheme.surface.toArgb()
        window.statusBarColor = colorScheme.surfaceVariant.toArgb()
        WindowCompat.getInsetsController(window, view).apply {
            isAppearanceLightNavigationBars = !darkTheme
            isAppearanceLightStatusBars = false
        }
    }
}