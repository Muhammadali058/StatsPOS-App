package com.graphees.statspos.presentation.ui.screens.utilities.settings

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
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.graphees.statspos.presentation.ui.components.AppCircularProgressIndicator
import com.graphees.statspos.presentation.ui.components.AppSnackbarHost
import com.graphees.statspos.presentation.ui.components.AppSwitch
import com.graphees.statspos.presentation.ui.components.ErrorDialog
import com.graphees.statspos.presentation.ui.components.ProgressBarLayout
import com.graphees.statspos.presentation.ui.components.SaveButton
import com.graphees.statspos.presentation.ui.components.Textbox
import com.graphees.statspos.presentation.ui.components.TopAppBar
import com.graphees.statspos.presentation.ui.components.UploadImageView
import com.graphees.statspos.presentation.ui.utils.ConstantPaddings
import com.graphees.statspos.presentation.viewmodels.SharedViewModel
import com.graphees.statspos.presentation.viewmodels.utilities.settings.PrintSettingsViewModel
import com.graphees.statspos.utils.UiEvent
import com.graphees.statspos.utils.checkEvent
import okhttp3.MultipartBody

@Composable
fun PrintSettingsScreen(
    sharedViewModel: SharedViewModel,
    onBack: () -> Unit,
) {
    fun goBackWithResult() {
//        sharedViewModel.notifyDataChanged()
        onBack()
    }

    val viewModel = hiltViewModel<PrintSettingsViewModel>()
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
                title = "Print Settings",
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
                    ShopData(
                        shopName = state.shopName,
                        contact = state.contact,
                        address = state.address,
                        onShopNameChange = viewModel::onShopNameChange,
                        onContactChange = viewModel::onContactChange,
                        onAddressChange = viewModel::onAddressChange,
                    )
                    Spacer(Modifier.height(12.dp))
                    Body(
                        showUrdu = state.showUrdu,
                        showLogo = state.showLogo,
                        onShowUrduChange = viewModel::onShowUrduChange,
                        onShowLogoChange = viewModel::onShowLogoChange,
                    )
                    Spacer(Modifier.height(12.dp))
                    ImageExpandable(
                        isUploadingImage = state.isUploadingImage,
                        imageUrl = state.imageUrl,
                        onImageUrlChange = {
                            viewModel.uploadImage(it)
                        },
                        onClear = viewModel::deleteImage,
                    )

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
                            viewModel.updatePrintSettings {
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
private fun ShopData(
    shopName: String,
    contact: String,
    address: String,
    onShopNameChange: (String) -> Unit,
    onContactChange: (String) -> Unit,
    onAddressChange: (String) -> Unit,
) {
    Textbox(
        value = shopName,
        onValueChange = onShopNameChange,
        modifier = Modifier
            .fillMaxWidth(),
        label = {
            Text("Shop Name")
        }
    )
    Textbox(
        value = contact,
        onValueChange = onContactChange,
        modifier = Modifier
            .fillMaxWidth(),
        label = {
            Text("Contact")
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number
        )
    )
    Textbox(
        value = address,
        onValueChange = onAddressChange,
        modifier = Modifier
            .fillMaxWidth()
            .height(84.dp),
        label = {
            Text("Address")
        },
        singleLine = false,
    )
}

@Composable
private fun Body(
    showUrdu: Boolean,
    showLogo: Boolean,
    onShowUrduChange: (Boolean) -> Unit,
    onShowLogoChange: (Boolean) -> Unit,
) {
    Row (
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround,
    ){
        AppSwitch(
            checked = showUrdu,
            onCheckedChange = onShowUrduChange,
            label = "Show Urdu"
        )
        AppSwitch(
            checked = showLogo,
            onCheckedChange = onShowLogoChange,
            label = "Show Logo"
        )
    }
}


@Composable
private fun ImageExpandable(
    isUploadingImage: Boolean,
    imageUrl: String,
    onImageUrlChange: (MultipartBody.Part) -> Unit,
    onClear: (String) -> Unit,
) {
//    ExpandableSection(
//        title = "Logo",
//        initiallyExpanded = false,
//    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            if (isUploadingImage) {
                AppCircularProgressIndicator()
            } else {
                UploadImageView(
                    imageUrl = imageUrl,
                    onImageUrlChange = onImageUrlChange,
                    onClear = onClear,
                )
            }
        }
//    }
}

