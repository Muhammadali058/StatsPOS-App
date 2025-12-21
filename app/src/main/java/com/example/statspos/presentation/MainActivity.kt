package com.example.statspos.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.statspos.presentation.ui.theme.StatsPOSTheme
import com.example.statspos.presentation.viewmodels.main.LocalDataViewModel
import com.example.statspos.utils.ThemeMode
import com.example.statspos.utils.showToast
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

            StatsPOSTheme(
                darkTheme = darkTheme
            ) {
                App()
            }
        }
    }
}