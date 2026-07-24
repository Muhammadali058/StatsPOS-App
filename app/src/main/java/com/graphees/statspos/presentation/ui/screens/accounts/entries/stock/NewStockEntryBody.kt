package com.graphees.statspos.presentation.ui.screens.accounts.entries.stock

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.graphees.statspos.domain.models.DropdownItem
import com.graphees.statspos.presentation.ui.components.AppCircularProgressIndicator
import com.graphees.statspos.presentation.ui.components.BarcodeScannerDialog
import com.graphees.statspos.presentation.ui.components.ComboBox
import com.graphees.statspos.presentation.ui.components.DateTextbox
import com.graphees.statspos.presentation.ui.components.ErrorDialog
import com.graphees.statspos.presentation.ui.components.MOPSection
import com.graphees.statspos.presentation.ui.components.PlaceHolder
import com.graphees.statspos.presentation.ui.components.ProgressBarLayout
import com.graphees.statspos.presentation.ui.components.SaveButton
import com.graphees.statspos.presentation.ui.components.SearchItemBox
import com.graphees.statspos.presentation.ui.components.SubComboBox
import com.graphees.statspos.presentation.ui.components.TextboxOutlined
import com.graphees.statspos.presentation.ui.utils.ConstantPaddings
import com.graphees.statspos.presentation.viewmodels.SharedViewModel
import com.graphees.statspos.presentation.viewmodels.accounts.entries.stock.NewStockEntryViewModel
import com.graphees.statspos.utils.HP
import com.graphees.statspos.utils.UiEvent
import com.graphees.statspos.utils.checkEvent
import com.graphees.statspos.utils.showToast
import java.time.LocalDate

@Composable
fun NewStockEntryBody(
    mainSharedViewModel: SharedViewModel,
    sharedViewModel: SharedViewModel,
    onSearchItemClick: () -> Unit,
    snackbarHostState: SnackbarHostState
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val context = LocalContext.current
    val viewModel = hiltViewModel<NewStockEntryViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val event by viewModel.event.collectAsState(UiEvent.Idle)
    var showErrorDialog by remember { mutableStateOf(false) }
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

    val sharedViewModelState by mainSharedViewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(sharedViewModelState.dataChanged) {
        if (sharedViewModelState.dataChanged) {
            val item = sharedViewModelState.item
            item?.run {
                viewModel.onItemnameChange(itemname!!)
                viewModel.getItem(itemname!!)
                mainSharedViewModel.consumeDataChanged()
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

    Box(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .padding(top = 8.dp)
                .padding(bottom = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Column(
                Modifier
                    .weight(1f)
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                SearchItemBox(
                    modifier = Modifier
                        .padding(ConstantPaddings.BODY_HORIZONTAL),
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
                    expense = state.expense,
                    subExpense = state.subExpense,
                    qty = state.qty,
                    crtn = state.crtn,
                    amount = state.amount,
                    date = state.date,
                    naration = state.naration,
                    onExpenseSelected = viewModel::onExpenseSelected,
                    onSubExpenseSelected = viewModel::onSubExpenseSelected,
                    onAmountChanged = viewModel::onAmountChange,
                    onQtyChanged = viewModel::onQtyChange,
                    onCrtnChanged = viewModel::onCrtnChange,
                    onDateChanged = viewModel::onDateChange,
                    onNarationChanged = viewModel::onNarationChange,
                )
                MOPSection(
                    mop = state.mop,
                    bank = state.bank,
                    subBank = state.subBank,
                    onMOPChange = viewModel::onMOPChange,
                    onBankSelected = viewModel::onBankSelected,
                    onSubBankSelected = viewModel::onSubBankSelected,
                )
            }

            // Post Button
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
                    SaveButton(
                        text = "Post"
                    ) {
                        viewModel.passEntry {
                            context.showToast("Enter posted successfully")
                            sharedViewModel.notifyDataChanged()
                            keyboardController?.hide()
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

@Composable
private fun Body(
    expense: DropdownItem?,
    subExpense: DropdownItem?,
    qty: String,
    crtn: String,
    amount: String,
    date: LocalDate,
    naration: String,
    onExpenseSelected: (DropdownItem) -> Unit,
    onSubExpenseSelected: (DropdownItem) -> Unit,
    onAmountChanged: (String) -> Unit,
    onQtyChanged: (String) -> Unit,
    onCrtnChanged: (String) -> Unit,
    onDateChanged: (LocalDate) -> Unit,
    onNarationChanged: (String) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(ConstantPaddings.BODY_HORIZONTAL)
    ) {
        ComboBox(
            modifier = Modifier
                .fillMaxWidth(),
            items = HP.expenses,
            selectedItem = expense,
            onItemSelected = onExpenseSelected,
            label = {
                Text("Expense")
            },
            outlined = true,
            addNone = true,
        )
        SubComboBox(
            modifier = Modifier
                .fillMaxWidth(),
            items = HP.subExpenses,
            selectedItem = subExpense,
            onItemSelected = onSubExpenseSelected,
            label = {
                Text("Sub-Expense")
            },
            outlined = true,
            addNone = true,
            mainId = expense?.id ?: 0L
        )
        Row(
            modifier = Modifier
                .fillMaxWidth(),
        ) {
            TextboxOutlined(
                modifier = Modifier
                    .weight(1f),
                value = qty,
                onValueChange = onQtyChanged,
                label = {
                    Text("Qty")
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal
                ),
            )
            if (HP.settings.saleCartons == true) {
                Spacer(Modifier.width(8.dp))
                TextboxOutlined(
                    modifier = Modifier
                        .weight(1f),
                    value = crtn,
                    onValueChange = onCrtnChanged,
                    label = {
                        Text("Crtn")
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal
                    ),
                )
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth(),
        ) {
            TextboxOutlined(
                modifier = Modifier
                    .weight(1f),
                value = amount,
                onValueChange = onAmountChanged,
                label = {
                    Text("Amount")
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal
                ),
            )
            if (HP.userRights.dateWiseEntry == true) {
                Spacer(Modifier.width(8.dp))
                DateTextbox(
                    modifier = Modifier
                        .weight(1f),
                    date = date,
                    onDateChange = onDateChanged,
                )
            }
        }
        TextboxOutlined(
            modifier = Modifier
                .fillMaxWidth(),
            value = naration,
            onValueChange = onNarationChanged,
            label = {
                Text("Naration")
            },
        )
    }
}
