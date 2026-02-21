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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
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
import com.example.statspos.presentation.ui.components.ProgressBarLayout
import com.example.statspos.presentation.ui.components.SaveButton
import com.example.statspos.presentation.ui.components.Textbox
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
                title = "Total: ${HP.formatDecimal(state.total, mustDecimals = 1)}",
                actions = {
                    Row {
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
                        crtn = state.crtn,
                        rate = state.rate,
                        crtnRate = state.crtnRate,
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
    crtn: String,
    rate: String,
    crtnRate: String,
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
            value = if (HP.getDoubleValue(qty) > 0.0) qty else "",
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
    if(HP.settings.saleCartons == true) {
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
            )
        }
    }
    if (HP.userRights.discount == true) {
        DiscountTextbox(
            value = if (HP.getDoubleValue(disc) > 0.0) disc else "",
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
                text = "Rs. $totalDisc"
            )
        }
        Spacer(Modifier.height(8.dp))
    }
}

@Preview(showBackground = true)
@Composable
private fun Prev() {
    Column(
        Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Body(
            "",
            "",
            "",
            "",
            "",
            true,
            0.0,
            {},
            {},
            {},
            {},
            {},
            {},
        )
    }
}