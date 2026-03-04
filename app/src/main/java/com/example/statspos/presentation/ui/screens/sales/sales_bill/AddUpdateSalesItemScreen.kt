package com.example.statspos.presentation.ui.screens.sales.sales_bill

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.statspos.R
import com.example.statspos.domain.models.sales.Sales
import com.example.statspos.presentation.ui.components.AppCircularProgressIndicator
import com.example.statspos.presentation.ui.components.AppIcon
import com.example.statspos.presentation.ui.components.AppIconButton
import com.example.statspos.presentation.ui.components.AppSnackbarHost
import com.example.statspos.presentation.ui.components.AutoCompleteItemsTextbox
import com.example.statspos.presentation.ui.components.BalanceBox
import com.example.statspos.presentation.ui.components.BarcodeScannerDialog
import com.example.statspos.presentation.ui.components.ConfirmDialog
import com.example.statspos.presentation.ui.components.DiscountTextbox
import com.example.statspos.presentation.ui.components.ErrorDialog
import com.example.statspos.presentation.ui.components.HeadingMedium
import com.example.statspos.presentation.ui.components.LabelMedium
import com.example.statspos.presentation.ui.components.ProgressBarLayout
import com.example.statspos.presentation.ui.components.SaveButton
import com.example.statspos.presentation.ui.components.SearchItemBox
import com.example.statspos.presentation.ui.components.Textbox
import com.example.statspos.presentation.ui.components.TextboxCB
import com.example.statspos.presentation.ui.components.TopAppBar
import com.example.statspos.presentation.ui.utils.ConstantPaddings
import com.example.statspos.presentation.viewmodels.SharedViewModel
import com.example.statspos.presentation.viewmodels.sales.sales_bill.AddUpdateSalesItemViewModel
import com.example.statspos.utils.HP
import com.example.statspos.utils.UiEvent
import com.example.statspos.utils.checkEvent
import com.example.statspos.utils.showToast

@Composable
fun AddUpdateSalesItemScreen(
    sharedViewModel: SharedViewModel,
    updateId: Long,
    isUpdate: Boolean,
    sales: Sales,
    onSearchItemClick: () -> Unit,
    onBack: () -> Unit,
) {
    val context = LocalContext.current

    fun goBackWithResult() {
        sharedViewModel.notifyDataChanged()
        onBack()
    }

    val keyboardController = LocalSoftwareKeyboardController.current
    val viewModel = hiltViewModel<AddUpdateSalesItemViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val event by viewModel.event.collectAsState(UiEvent.Idle)
    val snackbarHostState = remember { SnackbarHostState() }
    var showErrorDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var confirmDialogText by remember { mutableStateOf("") }
    var confirmDialogType by remember { mutableStateOf(0) }
    var showConfirmDialog by remember { mutableStateOf(false) }
    var showBarcodeScanner by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()
    val itemFocusRequester = remember { FocusRequester() }
    val qtyFocusRequester = remember { FocusRequester() }

    fun onSaveButtonClick() {
        viewModel.insertOrUpdateData {
            sharedViewModel.notifyDataChanged()
            itemFocusRequester.requestFocus()
//            goBackWithResult()
        }
    }

    LaunchedEffect(event) {
        checkEvent(
            event = event,
            snackbarHostState = snackbarHostState,
            viewModelIdleEvent = viewModel::onEvent,
            onError = {
                showErrorDialog = true
            },
            onConfirm = { text, type ->
                keyboardController?.hide()

                confirmDialogText = text
                confirmDialogType = type
                showConfirmDialog = true
            }
        )
    }

    // Edit data when update
    var hasLoadedOnce by rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        if (!hasLoadedOnce) {
            viewModel.updateInitialState(
                isUpdate = isUpdate,
                updateId = updateId,
                sales = sales,
            )

            if (isUpdate) {
                viewModel.editData(updateId)
            }

            hasLoadedOnce = true
        }
    }

    // For search item
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

    if (showConfirmDialog) {
        ConfirmDialog(
            text = confirmDialogText,
            onDismiss = {
                showConfirmDialog = false
            },
            onConfirm = {
                showConfirmDialog = false
                if (confirmDialogType == 1) {
                    viewModel.setIsExistsResult(true)
                    onSaveButtonClick()
                }
                if (confirmDialogType == 2) {
                    viewModel.setExpirableResult(true)
                    onSaveButtonClick()
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
                title = "Total: ${HP.formatDecimal(state.total, mustDecimals = 1)}",
                actions = {
                    Row {
                        if (isUpdate) {
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
                        onSearchItemClick = onSearchItemClick,
                    )
                    Body(
                        qtyFocusRequester = qtyFocusRequester,
                        qty = state.qty,
                        crtn = state.crtn,
                        rate = state.rate,
                        crtnRate = state.crtnRate,
                        qtyEnabled = state.qtyEnabled,
                        crtnEnabled = state.crtnEnabled,
                        rateEnabled = state.rateEnabled,
                        crtnRateEnabled = state.crtnRateEnabled,
                        customerId = state.sales.customerId ?: 0L,
                        lastRate = state.lastRate,
                        lastCrtnRate = state.lastCrtnRate,
                        stockPcs = state.stockPcs,
                        stockCrtn = state.stockCrtn,
                        warehouseStock = state.warehouseStock,
                        cost = state.cost,
                        crtnSize = state.crtnSize,
                        rates = state.rates,
                        isDiscRsPer = state.isDiscRsPer,
                        disc = state.disc,
                        totalDisc = state.totalDisc,
                        onQtyChange = viewModel::onQtyChange,
                        onCrtnChange = viewModel::onCrtnChange,
                        onRateChange = viewModel::onRateChange,
                        onCrtnRateChange = viewModel::onCrtnRateChange,
                        onDiscChange = viewModel::onDiscChange,
                        onIsDiscRsPerChange = viewModel::onIsDiscRsPerChange,
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
                            onSaveButtonClick()
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
    qty: String,
    crtn: String,
    rate: String,
    crtnRate: String,
    qtyEnabled: Boolean,
    crtnEnabled: Boolean,
    rateEnabled: Boolean,
    crtnRateEnabled: Boolean,
    customerId: Long,
    lastRate: Double,
    lastCrtnRate: Double,
    stockPcs: Double,
    stockCrtn: Long,
    warehouseStock: String,
    cost: Double,
    crtnSize: Int,
    rates: List<String>,
    disc: String,
    isDiscRsPer: Boolean,
    totalDisc: Double,
    onQtyChange: (String) -> Unit,
    onCrtnChange: (String) -> Unit,
    onRateChange: (String) -> Unit,
    onCrtnRateChange: (String) -> Unit,
    onDiscChange: (String) -> Unit,
    onIsDiscRsPerChange: (Boolean) -> Unit,
) {
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
        Spacer(Modifier.width(8.dp))
        TextboxCB(
            value = rate,
            onValueChange = onRateChange,
            modifier = Modifier
                .weight(1f),
            label = {
                Text("Rate")
            },
            items = rates,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Decimal
            ),
            enabled = rateEnabled,
            readOnly = HP.userRights.changeRates == false,
        )
    }
    if (HP.settings.saleCartons == true) {
        Row {
            Textbox(
                value = if (HP.getIntValue(crtn) > 0) crtn else "",
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
            Spacer(Modifier.width(8.dp))
            Textbox(
                value = crtnRate,
                onValueChange = onCrtnRateChange,
                modifier = Modifier
                    .weight(1f),
                label = {
                    Text("Crtn Rate")
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal
                ),
                enabled = crtnRateEnabled,
                readOnly = HP.userRights.changeRates == false,
            )
        }
    }
    if (HP.userRights.discount == true) {
        DiscountTextbox(
            value = disc,
            onValueChange = onDiscChange,
            isDiscRsPer = isDiscRsPer,
            onIsDiscRsPerChange = onIsDiscRsPerChange,
            modifier = Modifier
                .fillMaxWidth(),
            padding = PaddingValues(top = 4.dp)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
        ) {
            BalanceBox(
                text = "Rs. ${HP.formatDecimal(totalDisc)}"
            )
        }
        Spacer(Modifier.height(8.dp))
    }
    if (HP.settings.showItemStock == true) {
        Spacer(Modifier.height(8.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth(),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(if (HP.settings.saleCartons == true) 0.5f else 1f),
            ) {
                HeadingMedium("Stock Pcs: ")
                LabelMedium(HP.formatDecimal(stockPcs))
            }
            if (HP.settings.saleCartons == true) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                ) {
                    HeadingMedium("Stock Crtn: ")
                    LabelMedium(stockCrtn.toString())
                }
            }
        }
    }
    if (HP.settings.showCustomerLastRate == true && customerId != 0L) {
        Spacer(Modifier.height(8.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth(),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(if (HP.settings.saleCartons == true) 0.5f else 1f),
            ) {
                HeadingMedium("Last Rate: ")
                LabelMedium(HP.formatDecimal(lastRate))
            }
            if (HP.settings.saleCartons == true) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                ) {
                    HeadingMedium("Last Crtn Rate: ")
                    LabelMedium(HP.formatDecimal(lastCrtnRate))
                }
            }
        }
    }
    if (HP.userRights.seeCost == true) {
        Spacer(Modifier.height(8.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth(),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(if (HP.settings.saleCartons == true) 0.5f else 1f),
            ) {
                HeadingMedium("Cost: ")
                LabelMedium(HP.formatDecimal(cost))
            }
            if (HP.settings.saleCartons == true) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                ) {
                    HeadingMedium("Cost Crtn: ")
                    LabelMedium(HP.formatDecimal(cost * crtnSize))
                }
            }
        }
    }
    if (warehouseStock.isNotEmpty()) {
        Spacer(Modifier.height(8.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth(),
        ) {
            HeadingMedium("Stock in warehouse: ")
            Spacer(Modifier.height(2.dp))
            LabelMedium(warehouseStock)
        }
    }
}
