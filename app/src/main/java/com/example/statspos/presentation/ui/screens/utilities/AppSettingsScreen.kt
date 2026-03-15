package com.example.statspos.presentation.ui.screens.utilities

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.statspos.domain.models.utilities.settings.AppSettings
import com.example.statspos.presentation.ui.components.AppCircularProgressIndicator
import com.example.statspos.presentation.ui.components.AppSnackbarHost
import com.example.statspos.presentation.ui.components.AppSwitch
import com.example.statspos.presentation.ui.components.ErrorDialog
import com.example.statspos.presentation.ui.components.ExpandableSection
import com.example.statspos.presentation.ui.components.ProgressBarLayout
import com.example.statspos.presentation.ui.components.SaveButton
import com.example.statspos.presentation.ui.components.Textbox
import com.example.statspos.presentation.ui.components.TopAppBar
import com.example.statspos.presentation.ui.components.UploadImageView
import com.example.statspos.presentation.ui.utils.ConstantPaddings
import com.example.statspos.presentation.viewmodels.SharedViewModel
import com.example.statspos.presentation.viewmodels.utilities.AppSettingsViewModel
import com.example.statspos.presentation.viewmodels.utilities.PrintSettingsViewModel
import com.example.statspos.utils.UiEvent
import com.example.statspos.utils.checkEvent
import okhttp3.MultipartBody

@Composable
fun AppSettingsScreen(
    sharedViewModel: SharedViewModel,
    onBack: () -> Unit,
) {
    fun goBackWithResult() {
//        sharedViewModel.notifyDataChanged()
        onBack()
    }

    val viewModel = hiltViewModel<AppSettingsViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val event by viewModel.event.collectAsState(UiEvent.Idle)
    val snackbarHostState = remember { SnackbarHostState() }
    var showErrorDialog by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    LaunchedEffect(event) {
        checkEvent(
            event = event,
            snackbarHostState = snackbarHostState,
            viewModelIdleEvent = viewModel::onEvent,
            onError = {
                showErrorDialog = true
            }
        )
    }

    // Edit data when update
    LaunchedEffect(Unit) {
        if (!state.hasLoadedOnce) {
            viewModel.editData()

            viewModel.setHasLoadedOnce(true)
        }
    }

    if (showErrorDialog) {
        ErrorDialog(
            error = state.error,
            onDismiss = {
                showErrorDialog = false
            },
        )
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        snackbarHost = {
            AppSnackbarHost(
                snackbarHostState = snackbarHostState,
            )
        },
        topBar = {
            TopAppBar(
                onNavigationClick = {
                    onBack()
                },
                title = "App Settings",
            )
        },
    ) { innerPadding ->
        Box(
            Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.surface)
                .padding(ConstantPaddings.BODY_HORIZONTAL)
                .padding(vertical = 8.dp)
        ) {
            Column(
                Modifier
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Column(
                    Modifier
                        .weight(1f)
                        .verticalScroll(scrollState),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Spacer(Modifier.height(12.dp))
                    Body(
                        instantSearch = state.instantSearch,
                        onInstantSearchChange = viewModel::onInstantSearchChange,
                        innerItemSearch = state.innerItemSearch,
                        onInnerItemSearchChange = viewModel::onInnerItemSearchChange,
                        itemSuggestions = state.itemSuggestions,
                        onItemSuggestionsChange = viewModel::onItemSuggestionsChange,
                    )
                    Spacer(Modifier.height(12.dp))
                }

                Box(
                    modifier = Modifier
                        .windowInsetsPadding(
                            WindowInsets.navigationBars
                                .union(WindowInsets.ime)
                        )
                ) {
                    if (state.isSaving) {
                        AppCircularProgressIndicator()
                    } else {
                        SaveButton {
                            viewModel.updateAppSettings {
                                goBackWithResult()
                            }
                        }
                    }
                }
            }

            if (state.isLoading) {
                ProgressBarLayout()
            }
        }

    }
}

@Composable
private fun Body(
    instantSearch: Boolean,
    onInstantSearchChange: (Boolean) -> Unit,
    innerItemSearch: Boolean,
    onInnerItemSearchChange: (Boolean) -> Unit,
    itemSuggestions: Boolean,
    onItemSuggestionsChange: (Boolean) -> Unit,
) {
    Row{
        AppSwitch(
            modifier = Modifier.weight(1f),
            checked = instantSearch,
            onCheckedChange = onInstantSearchChange,
            label = "Instant Search"
        )
        AppSwitch(
            modifier = Modifier.weight(1f),
            checked = innerItemSearch,
            onCheckedChange = onInnerItemSearchChange,
            label = "Inner Item Search"
        )
    }
    Spacer(Modifier.height(24.dp))
    Row{
        AppSwitch(
            modifier = Modifier.weight(1f),
            checked = itemSuggestions,
            onCheckedChange = onItemSuggestionsChange,
            label = "Item Suggestions"
        )
    }
}
