package com.example.statspos.presentation.ui.screens.items.packages

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.IconButton
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.statspos.R
import com.example.statspos.domain.models.DropdownItem
import com.example.statspos.presentation.ui.components.AppCircularProgressIndicator
import com.example.statspos.presentation.ui.components.AppIcon
import com.example.statspos.presentation.ui.components.AppIconButton
import com.example.statspos.presentation.ui.components.AppSnackbarHost
import com.example.statspos.presentation.ui.components.AutoCompleteItemsTextbox
import com.example.statspos.presentation.ui.components.BarcodeScannerDialog
import com.example.statspos.presentation.ui.components.ConfirmDialog
import com.example.statspos.presentation.ui.components.Dropdown
import com.example.statspos.presentation.ui.components.ErrorDialog
import com.example.statspos.presentation.ui.components.ProgressBarLayout
import com.example.statspos.presentation.ui.components.SaveButton
import com.example.statspos.presentation.ui.components.Textbox
import com.example.statspos.presentation.ui.components.TopAppBar
import com.example.statspos.presentation.ui.utils.ConstantPaddings
import com.example.statspos.presentation.viewmodels.SharedViewModel
import com.example.statspos.presentation.viewmodels.items.packages.AddUpdatePackageItemViewModel
import com.example.statspos.utils.HP
import com.example.statspos.utils.UiEvent
import com.example.statspos.utils.checkEvent
import com.example.statspos.utils.showToast

@Composable
fun AddUpdatePackageItemScreen(
    sharedViewModel: SharedViewModel,
    updateId: Long = 0,
    isUpdate: Boolean = false,
    packageId: Long = 0,
    onSearchItemClick: () -> Unit,
    onBack: () -> Unit,
) {
    val context = LocalContext.current

    fun goBackWithResult() {
        sharedViewModel.notifyDataChanged()
        onBack()
    }

    val keyboardController = LocalSoftwareKeyboardController.current
    val viewModel = hiltViewModel<AddUpdatePackageItemViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val event by viewModel.event.collectAsState(UiEvent.Idle)
    val snackbarHostState = remember { SnackbarHostState() }
    var showErrorDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showBarcodeScanner by remember { mutableStateOf(false) }
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
            viewModel.updateInitialState(
                isUpdate = isUpdate,
                updateId = updateId,
                packageId = packageId,
            )

            if (isUpdate) {
                viewModel.editData(updateId)
            }

            viewModel.setHasLoadedOnce(true)
        }
    }

    val sharedViewModelState by sharedViewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(sharedViewModelState.dataChanged) {
        if (sharedViewModelState.dataChanged) {
            val item = sharedViewModelState.item
            item?.run {
                viewModel.onItemnameChange(itemname!!)
                viewModel.getItem(itemname!!)
                sharedViewModel.consumeDataChanged()
            }
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

    if (showDeleteDialog) {
        ConfirmDialog(
            text = "Are you sure to delete this package item",
            onDismiss = {
                showDeleteDialog = false
            },
            onConfirm = {
                showDeleteDialog = false
                viewModel.deleteData(updateId) {
                    context.showToast("Package item deleted successfully")
                    goBackWithResult()
                }
            }
        )
    }

    if (showBarcodeScanner) {
        BarcodeScannerDialog(
            onDismiss = {
                showBarcodeScanner = false
            },
            onScanned = {
                viewModel.onItemnameChange(it)
                viewModel.getItem(it)
                showBarcodeScanner = false
            }
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
                title = if (isUpdate) "Update Package Item" else "Add Package Item",
                actions = {
                    Row {
                        if (isUpdate && HP.userRights.deleteAnything == true) {
                            IconButton(onClick = {
                                showDeleteDialog = true
                            }) {
                                AppIcon(
                                    icon = Icons.Default.Delete,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
                .padding(ConstantPaddings.BODY_HORIZONTAL)
                .padding(vertical = 16.dp)
        ){
            Column(
                Modifier
                    .fillMaxSize()
                    .imePadding(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Column(
                    Modifier
                        .weight(1f)
                        .verticalScroll(scrollState)
                        .imePadding(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Dropdown(
                        value = state.packageName,
                        onValueChange = viewModel::onPackageNameChange,
                        items = HP.packages,
                        onItemSelected = { dropdownItem ->
                            viewModel.onPackageIdChange(dropdownItem.id)
                        },
                        label = {
                            Text("Package")
                        },
                        enabled = false,
                    )
                    ItemnameBox(
                        value = state.itemname,
                        onValueChange = viewModel::onItemnameChange,
                        onItemSelected = {
                            viewModel.getItem(it)
                            keyboardController?.hide()
                        },
                        onSearchClick = {
                            viewModel.getItem(it)
                            keyboardController?.hide()
                        },
                        onEndIconClick = {
                            viewModel.onItemnameChange("")
                        },
                        onBarcodeClick = {
                            showBarcodeScanner = true
                        },
                        onSearchItemClick = onSearchItemClick
                    )
                    Body(
                        qty = state.qty,
                        rate = state.rate,
                        total = state.total,
                        onQtyChange = viewModel::onQtyChange,
                        onRateChange = viewModel::onRateChange,
                    )
                }

                Box(
                    modifier = Modifier
                        .windowInsetsPadding(
                            WindowInsets.navigationBars
                                .union(WindowInsets.ime)
                        )
                ){
                    if (state.isSaving) {
                        AppCircularProgressIndicator()
                    } else {
                        SaveButton {
                            viewModel.insertOrUpdateData {
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
private fun ItemnameBox(
    value: String,
    onValueChange: (String) -> Unit,
    onItemSelected: (String) -> Unit,
    onSearchClick: (String) -> Unit,
    onEndIconClick: (String) -> Unit,
    onBarcodeClick: () -> Unit,
    onSearchItemClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AutoCompleteItemsTextbox(
            modifier = Modifier
                .weight(1f),
            value = value,
            onValueChange = onValueChange,
            onItemSelected = onItemSelected,
            onEndIconClick = onEndIconClick,
            onSearchClick = onSearchClick,
            label = {
                Text(
                    text = "Select Item"
                )
            },
            trailingIcon = {
                IconButton(onClick = {
                    onEndIconClick(value)
                }) {
                    AppIcon(
                        icon = Icons.Default.Clear,
                        size = 20.dp
                    )
                }
            },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Go
            )
        )
        Spacer(Modifier.width(4.dp))
        AppIconButton(
            onClick = {
                onBarcodeClick()
            },
            icon = R.drawable.ic_barcode,
            buttonSize = 32.dp,
            size = 26.dp
        )
        Spacer(Modifier.width(4.dp))
        AppIconButton(
            onClick = {
                onSearchItemClick()
            },
            icon = Icons.Default.Search,
            buttonSize = 32.dp,
            size = 26.dp
        )
    }
}

@Composable
private fun Body(
    qty: String,
    rate: String,
    total: String,
    onQtyChange: (String) -> Unit,
    onRateChange: (String) -> Unit,
) {
    Row{
        Textbox(
            value = qty,
            onValueChange = onQtyChange,
            modifier = Modifier
                .weight(1f),
            label = {
                Text("Qty")
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Decimal
            ),
        )
        Spacer(Modifier.width(8.dp))
        Textbox(
            value = rate,
            onValueChange = onRateChange,
            modifier = Modifier
                .weight(1f),
            label = {
                Text("Rate")
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Decimal
            ),
        )
    }
    Textbox(
        value = total,
        onValueChange = { },
        modifier = Modifier
            .fillMaxWidth(),
        label = {
            Text("Total")
        },
        readOnly = true,
    )
}
