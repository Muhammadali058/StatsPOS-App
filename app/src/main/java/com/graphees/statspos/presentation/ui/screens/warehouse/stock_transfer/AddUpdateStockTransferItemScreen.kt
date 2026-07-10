package com.graphees.statspos.presentation.ui.screens.warehouse.stock_transfer

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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.graphees.statspos.presentation.ui.components.AppCircularProgressIndicator
import com.graphees.statspos.presentation.ui.components.AppSnackbarHost
import com.graphees.statspos.presentation.ui.components.BarcodeScannerDialog
import com.graphees.statspos.presentation.ui.components.ConfirmDialog
import com.graphees.statspos.presentation.ui.components.Dropdown
import com.graphees.statspos.presentation.ui.components.ErrorDialog
import com.graphees.statspos.presentation.ui.components.HeadingMedium
import com.graphees.statspos.presentation.ui.components.LabelMedium
import com.graphees.statspos.presentation.ui.components.ProgressBarLayout
import com.graphees.statspos.presentation.ui.components.SaveButton
import com.graphees.statspos.presentation.ui.components.SearchItemBox
import com.graphees.statspos.presentation.ui.components.Textbox
import com.graphees.statspos.presentation.ui.components.TopAppBar
import com.graphees.statspos.presentation.ui.utils.ConstantPaddings
import com.graphees.statspos.presentation.viewmodels.SharedViewModel
import com.graphees.statspos.presentation.viewmodels.warehouse.stock_transfer.AddUpdateStockTransferItemViewModel
import com.graphees.statspos.utils.HP
import com.graphees.statspos.utils.UiEvent
import com.graphees.statspos.utils.checkEvent
import com.graphees.statspos.utils.showToast

@Composable
fun AddUpdateStockTransferItemScreen(
    sharedViewModel: SharedViewModel,
    updateId: Long = 0,
    isUpdate: Boolean = false,
    warehouseId: Long = 0,
    onSearchItemClick: () -> Unit,
    onBack: () -> Unit,
) {
    val context = LocalContext.current

    fun goBackWithResult() {
        sharedViewModel.notifyDataChanged()
        onBack()
    }

    val keyboardController = LocalSoftwareKeyboardController.current
    val viewModel = hiltViewModel<AddUpdateStockTransferItemViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val event by viewModel.event.collectAsState(UiEvent.Idle)
    val snackbarHostState = remember { SnackbarHostState() }
    var showErrorDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showBarcodeScanner by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()
    val itemFocusRequester = remember { FocusRequester() }
    val qtyFocusRequester = remember { FocusRequester() }

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
                warehouseId = warehouseId,
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
                viewModel.getItem(itemname!!){
                    qtyFocusRequester.requestFocus()
                }
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
            text = "Are you sure to delete this item",
            onDismiss = {
                showDeleteDialog = false
            },
            onConfirm = {
                showDeleteDialog = false
                viewModel.deleteData(updateId) {
                    context.showToast("Item deleted successfully")
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
                viewModel.getItem(it){
                    qtyFocusRequester.requestFocus()
                }
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
                title = if (isUpdate) "Update Item" else "Add Item",
//                actions = {
//                    Row {
//                        if (isUpdate && HP.userRights.deleteAnything == true) {
//                            IconButton(onClick = {
//                                showDeleteDialog = true
//                            }) {
//                                AppIcon(
//                                    icon = Icons.Default.Delete,
//                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
//                                )
//                            }
//                        }
//                    }
//                }
            )
        }
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
                        value = state.warehouseName,
                        onValueChange = viewModel::onWarehouseNameChange,
                        items = HP.warehouses,
                        onItemSelected = { dropdownItem ->
                            viewModel.onWarehouseIdChange(dropdownItem.id)
                        },
                        label = {
                            Text("Warehouse")
                        },
                        enabled = false,
                    )
                    SearchItemBox(
                        itemFocusRequester = itemFocusRequester,
                        value = state.itemname,
                        onValueChange = viewModel::onItemnameChange,
                        onItemSelected = {
                            viewModel.getItem(it){
                                qtyFocusRequester.requestFocus()
                            }
//                            keyboardController?.hide()
                        },
                        onSearchClick = {
                            viewModel.getItem(it){
                                qtyFocusRequester.requestFocus()
                            }
//                            keyboardController?.hide()
                        },
                        onEndIconClick = {
                            viewModel.onItemnameChange("")
                            itemFocusRequester.requestFocus()
                        },
                        onBarcodeClick = {
                            showBarcodeScanner = true
                        },
                        onSearchItemClick = onSearchItemClick
                    )
                    Body(
                        qtyFocusRequester = qtyFocusRequester,
                        stockPcs = state.stockPcs,
                        stockCrtn = state.stockCrtn,
                        wStockPcs = state.wStockPcs,
                        wStockCrtn = state.wStockCrtn,
                        qty = state.qty,
                        crtn = state.crtn,
                        qtyEnabled = state.qtyEnabled,
                        crtnEnabled = state.crtnEnabled,
                        onQtyChange = viewModel::onQtyChange,
                        onCrtnChange = viewModel::onCrtnChange,
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
                            viewModel.insertOrUpdateData {
                                sharedViewModel.notifyDataChanged()
                                itemFocusRequester.requestFocus()
//                                goBackWithResult()
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
    qtyFocusRequester: FocusRequester? = null,
    stockPcs: Double,
    stockCrtn: Long,
    wStockPcs: Double,
    wStockCrtn: Long,
    qty: String,
    crtn: String,
    qtyEnabled: Boolean,
    crtnEnabled: Boolean,
    onQtyChange: (String) -> Unit,
    onCrtnChange: (String) -> Unit,
) {
    Text(
        modifier = Modifier
            .fillMaxWidth(),
        text = "Stock Here",
        textAlign = TextAlign.Center,
        style = MaterialTheme.typography.titleMedium.copy(
            color = MaterialTheme.colorScheme.primary,
        ),
    )
    Column(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
        ) {
            HeadingMedium("Stock Pcs", Modifier.fillMaxWidth(.5f), textAlign = TextAlign.Center)
            HeadingMedium("Stock Crtn", Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
        }
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
        ) {
            LabelMedium(
                HP.formatDecimal(stockPcs),
                Modifier.fillMaxWidth(.5f),
                textAlign = TextAlign.Center
            )
            LabelMedium(
                stockCrtn.toString(),
                Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }
    }

    Spacer(Modifier.height(8.dp))
    Text(
        modifier = Modifier
            .fillMaxWidth(),
        text = "Stock in Warehouse",
        textAlign = TextAlign.Center,
        style = MaterialTheme.typography.titleMedium.copy(
            color = MaterialTheme.colorScheme.primary,
        ),
    )
    Column(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
        ) {
            HeadingMedium("Stock Pcs", Modifier.fillMaxWidth(.5f), textAlign = TextAlign.Center)
            HeadingMedium("Stock Crtn", Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
        }
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
        ) {
            LabelMedium(
                HP.formatDecimal(wStockPcs),
                Modifier.fillMaxWidth(.5f),
                textAlign = TextAlign.Center
            )
            LabelMedium(
                wStockCrtn.toString(),
                Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }
    }
    Spacer(Modifier.height(8.dp))
    Row {
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
            enabled = qtyEnabled,
            focusRequester = qtyFocusRequester,
        )
        if (HP.settings.saleCartons == true) {
            Spacer(Modifier.width(8.dp))
            Textbox(
                value = crtn,
                onValueChange = onCrtnChange,
                modifier = Modifier
                    .weight(1f),
                label = {
                    Text("Crtn")
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal
                ),
                enabled = crtnEnabled,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun Prev() {
    Column(
        Modifier.fillMaxSize()
    ) {
        Body(
            null,
            0.0,
            0L,
            0.0,
            0L,
            "",
            "",
            true,
            true,
            {},
            {},
        )
    }
}