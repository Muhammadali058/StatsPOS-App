package com.example.statspos.presentation.ui.screens.purchase.purchase_bill

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
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.statspos.domain.models.DropdownItem
import com.example.statspos.domain.models.purchase.Purchase
import com.example.statspos.domain.models.sales.Sales
import com.example.statspos.presentation.ui.components.AppCircularProgressIndicator
import com.example.statspos.presentation.ui.components.AppIcon
import com.example.statspos.presentation.ui.components.AppIconButton
import com.example.statspos.presentation.ui.components.AppSnackbarHost
import com.example.statspos.presentation.ui.components.AppSwitch
import com.example.statspos.presentation.ui.components.AutoCompleteItemsTextbox
import com.example.statspos.presentation.ui.components.BalanceBox
import com.example.statspos.presentation.ui.components.BarcodeScannerDialog
import com.example.statspos.presentation.ui.components.CalculatorTB
import com.example.statspos.presentation.ui.components.ConfirmDialog
import com.example.statspos.presentation.ui.components.DateTextbox
import com.example.statspos.presentation.ui.components.DiscountTextbox
import com.example.statspos.presentation.ui.components.ErrorDialog
import com.example.statspos.presentation.ui.components.ExpandableSection
import com.example.statspos.presentation.ui.components.HeadingLarge
import com.example.statspos.presentation.ui.components.HeadingMedium
import com.example.statspos.presentation.ui.components.LabelLarge
import com.example.statspos.presentation.ui.components.LabelMedium
import com.example.statspos.presentation.ui.components.ProgressBarLayout
import com.example.statspos.presentation.ui.components.SaveButton
import com.example.statspos.presentation.ui.components.SearchItemBox
import com.example.statspos.presentation.ui.components.Textbox
import com.example.statspos.presentation.ui.components.TextboxCB
import com.example.statspos.presentation.ui.components.TopAppBar
import com.example.statspos.presentation.ui.utils.ConstantPaddings
import com.example.statspos.presentation.viewmodels.SharedViewModel
import com.example.statspos.presentation.viewmodels.purchase.purchase_bill.AddUpdatePurchaseItemViewModel
import com.example.statspos.presentation.viewmodels.sales.sales_bill.AddUpdateSalesItemViewModel
import com.example.statspos.utils.HP
import com.example.statspos.utils.UiEvent
import com.example.statspos.utils.checkEvent
import com.example.statspos.utils.showToast
import java.time.LocalDate

@Composable
fun AddUpdatePurchaseItemScreen(
    sharedViewModel: SharedViewModel,
    updateId: Long,
    isUpdate: Boolean,
    purchase: Purchase,
    onSearchItemClick: () -> Unit,
    onBack: () -> Unit,
) {
    val context = LocalContext.current

    fun goBackWithResult() {
        sharedViewModel.notifyDataChanged()
        onBack()
    }

    val keyboardController = LocalSoftwareKeyboardController.current
    val viewModel = hiltViewModel<AddUpdatePurchaseItemViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val event by viewModel.event.collectAsState(UiEvent.Idle)
    val snackbarHostState = remember { SnackbarHostState() }
    var showErrorDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var confirmDialogText by remember { mutableStateOf("") }
    var confirmDialogType by remember { mutableIntStateOf(0) }
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
    LaunchedEffect(Unit) {
        if (!state.hasLoadedOnce) {
            viewModel.updateInitialState(
                isUpdate = isUpdate,
                updateId = updateId,
                purchase = purchase,
            )

            if (isUpdate) {
                viewModel.editData(updateId)
            }

            itemFocusRequester.requestFocus()
            viewModel.setHasLoadedOnce(true)
        }
    }

    // For search item
    val sharedViewModelState by sharedViewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(sharedViewModelState.dataChanged) {
        if (sharedViewModelState.dataChanged) {
            val item = sharedViewModelState.item
            item?.run {
                viewModel.onItemnameChange(itemname!!)
                viewModel.getItem(itemname!!) {
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
                    viewModel.setStockWarningResult(true)
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
                viewModel.getItem(it) {
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
//                actions = {
//                    Row {
//                        if (isUpdate) {
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
                        modifier = Modifier
                            .padding(ConstantPaddings.BODY_HORIZONTAL),
                        itemFocusRequester = itemFocusRequester,
                        value = state.itemname,
                        onValueChange = viewModel::onItemnameChange,
                        onItemSelected = {
                            viewModel.getItem(it) {
                                qtyFocusRequester.requestFocus()
                            }
//                            keyboardController?.hide()
                        },
                        onSearchClick = {
                            viewModel.getItem(it) {
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
                        cost = state.cost,
                        crtn = state.crtn,
                        crtnSize = state.crtnSize,
                        qtyEnabled = state.qtyEnabled,
                        crtnEnabled = state.crtnEnabled,
                        stockPcs = state.stockPcs,
                        stockCrtn = state.stockCrtn,
                        warehouseStock = state.warehouseStock,
                        oldRates = state.oldRates,
                        isDiscRsPer = state.isDiscRsPer,
                        disc = state.disc,
                        calculatedDisc = state.calculatedDisc,
                        tax = state.tax,
                        totalDisc = state.totalDisc,
                        totalTax = state.totalTax,
                        finalCost = state.finalCost,
                        costCrtn = state.costCrtn,
                        grossTotal = state.grossTotal,
                        calculatedTax = state.calculatedTax,
                        freezeDisc = state.freezeDisc,
                        freezeTax = state.freezeTax,
                        location = state.location,
                        packing = state.packing,
                        onQtyChange = viewModel::onQtyChange,
                        onCostChange = viewModel::onCostChange,
                        onCrtnChange = viewModel::onCrtnChange,
                        onDiscChange = viewModel::onDiscChange,
                        onIsDiscRsPerChange = viewModel::onIsDiscRsPerChange,
                        onTaxChange = viewModel::onTaxChange,
                        onFreezeDiscChange = viewModel::onFreezeDiscChange,
                        onFreezeTaxChange = viewModel::onFreezeTaxChange,
                    )
                    SaleRates(
                        retail = state.retail,
                        wholesale = state.wholesale,
                        rate3 = state.rate3,
                        rate4 = state.rate4,
                        crtnRate = state.crtnRate,
                        marketPrice = state.marketPrice,
                        retailEnabled = state.retailEnabled,
                        wholesaleEnabled = state.wholesaleEnabled,
                        crtnRateEnabled = state.crtnRateEnabled,
                        onRetailChange = viewModel::onRetailChange,
                        onWholesaleChange = viewModel::onWholesaleChange,
                        onRate3Change = viewModel::onRate3Change,
                        onRate4Change = viewModel::onRate4Change,
                        onCrtnRateChange = viewModel::onCrtnRateChange,
                        onMarketPriceChange = viewModel::onMarketPriceChange,
                    )
                    Others(
                        isNewStock = state.isNewStock,
                        lockPcs = state.lockPcs,
                        lockCrtn = state.lockCrtn,
                        expiry = state.expiry,
                        onIsNewStockChange = viewModel::onIsNewStockChange,
                        onLockPcsChange = viewModel::onLockPcsChange,
                        onLockCrtnChange = viewModel::onLockCrtnChange,
                        onExpiryChange = viewModel::onExpiryChange,
                    )
                }

                Box(
                    modifier = Modifier
                        .padding(ConstantPaddings.BODY_HORIZONTAL)
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
    cost: String,
    crtn: String,
    crtnSize: Int,
    qtyEnabled: Boolean,
    crtnEnabled: Boolean,
    stockPcs: Double,
    stockCrtn: Long,
    warehouseStock: String,
    oldRates: String,
    disc: String,
    isDiscRsPer: Boolean,
    calculatedDisc: Double,
    tax: String,
    totalDisc: Double,
    totalTax: Double,
    finalCost: Double,
    costCrtn: Double,
    grossTotal: Double,
    calculatedTax: Double,
    freezeDisc: Boolean,
    freezeTax: Boolean,
    location:String,
    packing:String,
    onQtyChange: (String) -> Unit,
    onCostChange: (String) -> Unit,
    onCrtnChange: (String) -> Unit,
    onDiscChange: (String) -> Unit,
    onIsDiscRsPerChange: (Boolean) -> Unit,
    onTaxChange: (String) -> Unit,
    onFreezeDiscChange: (Boolean) -> Unit,
    onFreezeTaxChange: (Boolean) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(ConstantPaddings.BODY_HORIZONTAL),
    ) {
        Row {
            CalculatorTB(
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
            CalculatorTB(
                value = cost,
                onValueChange = onCostChange,
                modifier = Modifier
                    .weight(1f),
//                trailingIcon = {
//                    AppIconButton(
//                        icon = R.drawable.calculate,
//                        onClick = {
//                            onCostChange(HP.evaluateExpression(cost))
//                        },
//                        size = 20.dp,
//                    )
//                },
                label = {
                    Text("Cost")
                },
            )
        }
        if (HP.settings.saleCartons == true) {
            Row {
                CalculatorTB(
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
                CalculatorTB(
                    value = crtnSize.toString(),
                    onValueChange = {},
                    modifier = Modifier
                        .weight(1f),
                    label = {
                        Text("PCS in Carton")
                    },
                    readOnly = true,
                )
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth(),
        ) {
            Column(
                modifier = Modifier
                    .weight(1f),
            ) {
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
                        text = "Rs. ${HP.formatDecimal(calculatedDisc)}",
                    )
                }
            }
            Spacer(Modifier.width(8.dp))
            AppSwitch(
                modifier = Modifier.padding(top = 16.dp),
                checked = freezeDisc,
                onCheckedChange = onFreezeDiscChange,
                label = ""
            )
        }
        Spacer(Modifier.height(8.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth(),
        ) {
            Column(
                modifier = Modifier
                    .weight(1f),
            ) {
                Textbox(
                    value = tax,
                    onValueChange = onTaxChange,
                    modifier = Modifier
                        .fillMaxWidth(),
                    label = {
                        Text("Tax")
                    },
                    padding = PaddingValues(top = 4.dp),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal
                    ),
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    BalanceBox(
                        text = "Rs. ${HP.formatDecimal(calculatedTax)}",
                    )
                }
            }
            Spacer(Modifier.width(8.dp))
            AppSwitch(
                modifier = Modifier.padding(top = 16.dp),
                checked = freezeTax,
                onCheckedChange = onFreezeTaxChange,
                label = ""
            )
        }
        Spacer(Modifier.height(8.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth(),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(0.5f),
            ) {
                HeadingMedium("Total Disc: ")
                LabelMedium(HP.formatDecimal(totalDisc))
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
            ) {
                HeadingMedium("Total Tax: ")
                LabelMedium(HP.formatDecimal(totalTax))
            }
        }
        Spacer(Modifier.height(8.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth(),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(0.5f),
            ) {
                HeadingMedium("Final Cost: ")
                LabelMedium(HP.formatDecimal(finalCost))
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
            ) {
                HeadingMedium("Cost Crtn: ")
                LabelMedium(HP.formatDecimal(costCrtn))
            }
        }
        Spacer(Modifier.height(8.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth(),
        ) {
            HeadingLarge("Gross Total: ")
            LabelLarge(HP.formatDecimal(grossTotal))
        }
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
        if(location.isNotEmpty()) {
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
            ) {
                HeadingMedium("Location: ")
                LabelMedium(location)
            }
        }
        if(packing.isNotEmpty()) {
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
            ) {
                HeadingMedium("Packing: ")
                LabelMedium(packing)
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
        if (oldRates.isNotEmpty()) {
            Spacer(Modifier.height(8.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
            ) {
                HeadingMedium("Last Purchases: ")
                Spacer(Modifier.height(2.dp))
                LabelMedium(oldRates)
            }
        }
    }
}

@Composable
private fun SaleRates(
    retail: String,
    wholesale: String,
    rate3: String,
    rate4: String,
    crtnRate: String,
    marketPrice: String,
    retailEnabled: Boolean,
    wholesaleEnabled: Boolean,
    crtnRateEnabled: Boolean,

    onRetailChange: (String) -> Unit,
    onWholesaleChange: (String) -> Unit,
    onRate3Change: (String) -> Unit,
    onRate4Change: (String) -> Unit,
    onCrtnRateChange: (String) -> Unit,
    onMarketPriceChange: (String) -> Unit,
) {
    ExpandableSection(
        title = "Sale Rates",
        initiallyExpanded = true,
    ) {
        Spacer(Modifier.height(8.dp))
        // Retail & Wholesale
        Row {
            Textbox(
                value = retail,
                onValueChange = onRetailChange,
                modifier = Modifier.weight(1f),
                label = {
                    Text(
                        text = if (HP.settings.fourRateSystem == true) "Rate 1" else "Retail"
                    )
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal
                ),
                enabled = retailEnabled,
            )
            Spacer(Modifier.width(8.dp))
            Textbox(
                value = wholesale,
                onValueChange = onWholesaleChange,
                modifier = Modifier.weight(1f),
                label = {
                    Text(
                        text = if (HP.settings.fourRateSystem == true) "Rate 2" else "Wholesale"
                    )
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal
                ),
                enabled = wholesaleEnabled,
            )
        }

        // Rate3 & Rate4
        if (HP.settings.fourRateSystem == true) {
            Row {
                Textbox(
                    value = rate3,
                    onValueChange = onRate3Change,
                    modifier = Modifier.weight(1f),
                    label = {
                        Text("Rate 3")
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal
                    ),
                )
                Spacer(Modifier.width(8.dp))
                Textbox(
                    value = rate4,
                    onValueChange = onRate4Change,
                    modifier = Modifier.weight(1f),
                    label = {
                        Text("Rate 3")
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal
                    ),
                )
            }
        }

        // Carton Rate & PCS in Carton
        if (HP.settings.saleCartons == true) {
            Row {
                Textbox(
                    value = crtnRate,
                    onValueChange = onCrtnRateChange,
                    modifier = Modifier.weight(1f),
                    label = {
                        Text("Crtn Rate")
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal
                    ),
                    enabled = crtnRateEnabled,
                )
                Spacer(Modifier.width(8.dp))
                Textbox(
                    value = marketPrice,
                    onValueChange = onMarketPriceChange,
                    modifier = Modifier.weight(1f),
                    label = {
                        Text("Market Price")
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal
                    ),
                )
            }
        }
    }
}

@Composable
private fun Others(
    isNewStock: Boolean,
    lockPcs: Boolean,
    lockCrtn: Boolean,
    expiry: LocalDate,
    onIsNewStockChange: (Boolean) -> Unit,
    onLockPcsChange: (Boolean) -> Unit,
    onLockCrtnChange: (Boolean) -> Unit,
    onExpiryChange: (LocalDate) -> Unit,
) {
    ExpandableSection(
        title = "Others",
        initiallyExpanded = true,
    ) {
        Spacer(Modifier.height(8.dp))
        AppSwitch(
            checked = isNewStock,
            onCheckedChange = onIsNewStockChange,
            label = "Sale current stock first"
        )
        Spacer(Modifier.height(16.dp))
        Row {
            AppSwitch(
                modifier = Modifier.weight(1f),
                checked = lockPcs,
                onCheckedChange = onLockPcsChange,
                label = "Lock PCS"
            )
            AppSwitch(
                modifier = Modifier.weight(1f),
                checked = lockCrtn,
                onCheckedChange = onLockCrtnChange,
                label = "Lock CRTN"
            )
        }
        Spacer(Modifier.height(16.dp))
        DateTextbox(
            modifier = Modifier
                .fillMaxWidth(),
            date = expiry,
            onDateChange = onExpiryChange,
            label = "Expiry"
        )
        Spacer(Modifier.height(8.dp))
    }
}