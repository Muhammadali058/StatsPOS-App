package com.example.statspos.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = darkBlue,
    secondary = Color.White,
    tertiary = darkBlue,

    background = background,
    surface = background,
    onPrimary = Color.White,
    onSecondary = textColor,
    onTertiary = Color.White,
    onBackground = textColor,
    onSurface = textColor,
)

private val LightColorScheme = lightColorScheme(
    primary = darkBlue,
    secondary = Color.White,
    tertiary = darkBlue,

    background = background,
    surface = background,
    onPrimary = Color.White,
    onSecondary = textColor,
    onTertiary = Color.White,
    onBackground = textColor,
    onSurface = textColor,
)

@Composable
fun StatsPOSTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
//        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
//            val context = LocalContext.current
//            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
//        }

        darkTheme -> LightColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if(!view.isInEditMode){
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = if(darkTheme) Color.Black.toArgb() else Color.White.toArgb()
            window.navigationBarColor = if(darkTheme) Color.Black.toArgb() else Color.White.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}