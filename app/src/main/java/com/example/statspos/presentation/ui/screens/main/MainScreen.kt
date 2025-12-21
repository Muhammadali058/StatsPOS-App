package com.example.statspos.presentation.ui.screens.main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.statspos.presentation.ui.components.CustomSnackbarHost
import com.example.statspos.presentation.ui.components.ErrorDialog
import com.example.statspos.presentation.viewmodels.main.LocalDataViewModel
import com.example.statspos.presentation.viewmodels.main.MainViewModel
import com.example.statspos.utils.ThemeMode
import com.example.statspos.utils.UiEvent
import com.example.statspos.utils.checkEvent

@Composable
fun MainScreen() {
    val settingsViewModel = hiltViewModel<LocalDataViewModel>()
    val viewModel = hiltViewModel<MainViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val event by viewModel.event.collectAsState(UiEvent.Idle)

    var showErrorDialog by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(event) {
        checkEvent(
            event = event,
            snackbarHostState = snackbarHostState,
            viewModelIdleEvent = viewModel::onEvent
        ) {
            showErrorDialog = true
        }
    }

    if (showErrorDialog) {
        ErrorDialog(
            text = state.error!!
        ) {
            showErrorDialog = false
        }
    } else {
        Scaffold(
            snackbarHost = {
                CustomSnackbarHost(snackbarHostState = snackbarHostState)
            }
        ) { innerPadding ->
//            Box(
//                                modifier = Modifier
//                    .padding(innerPadding)
//                    .fillMaxSize()
//            ){
//                Text("Main Screen")
//            }

            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Button(
                    onClick = { settingsViewModel.setTheme(ThemeMode.LIGHT) }
                ) {
                    Text("Light")
                }
                Button(
                    onClick = { settingsViewModel.setTheme(ThemeMode.DARK) }
                ) {
                    Text("Dark")
                }
                Button(
                    onClick = { settingsViewModel.setTheme(ThemeMode.SYSTEM) }
                ) {
                    Text("System")
                }
            }
        }
    }


}