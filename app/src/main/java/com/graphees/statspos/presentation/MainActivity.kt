package com.graphees.statspos.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.graphees.statspos.presentation.ui.theme.StatsPOSTheme
import com.graphees.statspos.presentation.viewmodels.main.LocalDataViewModel
import com.graphees.statspos.utils.HP
import com.graphees.statspos.utils.ThemeMode
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val viewModel = hiltViewModel<LocalDataViewModel>()
            val themeMode by viewModel.getTheme().collectAsStateWithLifecycle(ThemeMode.LIGHT)

            val darkTheme = when(themeMode){
                ThemeMode.LIGHT -> false
                ThemeMode.DARK -> true
                ThemeMode.SYSTEM -> isSystemInDarkTheme()
            }

            HP.darkTheme = darkTheme

            StatsPOSTheme(
                darkTheme = darkTheme
            ) {
                App()
            }
        }
    }
}