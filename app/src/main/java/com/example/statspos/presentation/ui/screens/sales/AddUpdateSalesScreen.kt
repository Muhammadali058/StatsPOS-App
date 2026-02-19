package com.example.statspos.presentation.ui.screens.sales

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
import androidx.compose.material.icons.filled.Delete
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.statspos.domain.models.DropdownItem
import com.example.statspos.domain.models.sales.SalesBills
import com.example.statspos.presentation.ui.components.AppCircularProgressIndicator
import com.example.statspos.presentation.ui.components.AppIcon
import com.example.statspos.presentation.ui.components.AppSnackbarHost
import com.example.statspos.presentation.ui.components.AppSwitch
import com.example.statspos.presentation.ui.components.BalanceBox
import com.example.statspos.presentation.ui.components.ComboBox
import com.example.statspos.presentation.ui.components.ConfirmDialog
import com.example.statspos.presentation.ui.components.DateTextbox
import com.example.statspos.presentation.ui.components.DiscountTextbox
import com.example.statspos.presentation.ui.components.Dropdown
import com.example.statspos.presentation.ui.components.ErrorDialog
import com.example.statspos.presentation.ui.components.ExpandableSection
import com.example.statspos.presentation.ui.components.PasswordDialog
import com.example.statspos.presentation.ui.components.ProgressBarLayout
import com.example.statspos.presentation.ui.components.SaveButton
import com.example.statspos.presentation.ui.components.SubComboBox
import com.example.statspos.presentation.ui.components.Textbox
import com.example.statspos.presentation.ui.components.TopAppBar
import com.example.statspos.presentation.ui.utils.ConstantPaddings
import com.example.statspos.presentation.viewmodels.SharedViewModel
import com.example.statspos.presentation.viewmodels.sales.AddUpdateSalesViewModel
import com.example.statspos.utils.HP
import com.example.statspos.utils.PasswordFor
import com.example.statspos.utils.UiEvent
import com.example.statspos.utils.checkEvent
import com.example.statspos.utils.showToast
import java.time.LocalDate

@Composable
fun AddUpdateSalesScreen(
    sharedViewModel: SharedViewModel,
    invoiceId: Long = 0L,
    isPendingBill: Boolean = false,
    isPostedBill: Boolean = false,
    salesBill: SalesBills? = null,
    onBack: () -> Unit,
) {
    val context = LocalContext.current

    fun goBackWithResult() {
        sharedViewModel.notifyDataChanged()
        onBack()
    }

    val viewModel = hiltViewModel<AddUpdateSalesViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val event by viewModel.event.collectAsState(UiEvent.Idle)
    val snackbarHostState = remember { SnackbarHostState() }
    var showErrorDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showPasswordDialog by remember { mutableStateOf(false) }
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
        viewModel.updateInitialState(
            invoiceId = invoiceId,
            isPendingBill = isPendingBill,
            isPostedBill = isPostedBill,
            salesBill = salesBill,
        )

        if (!hasLoadedOnce) {
            if ((isPendingBill || isPostedBill)) {
                viewModel.editData(invoiceId)
            } else {
                viewModel.getInvoiceId()
            }

            hasLoadedOnce = true
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
            text = "Are you sure to delete this bill",
            onDismiss = {
                showDeleteDialog = false
            },
            onConfirm = {
                showDeleteDialog = false
                viewModel.deleteData(invoiceId) {
                    context.showToast("Bill deleted successfully")
                    goBackWithResult()
                }
            }
        )
    }

    if (showPasswordDialog) {
        PasswordDialog(
            passwordFor = PasswordFor.DELETE_SALES_BILL,
            onDismiss = {
                showPasswordDialog = false
            },
            onConfirm = {
                showPasswordDialog = false
                viewModel.deleteData(invoiceId) {
                    context.showToast("Bill deleted successfully")
                    goBackWithResult()
                }
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
                title = "Total: ${state.total}",
                actions = {
                    Row {
                        if (isPendingBill) {
                            IconButton(onClick = {
                                showDeleteDialog = true
                            }) {
                                AppIcon(
                                    icon = Icons.Default.Delete,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        } else if (isPostedBill) {
                            if (HP.userRights.deleteAnything == true) {
                                IconButton(onClick = {
                                    if (HP.passwords.useDeleteSalesBill == true) {
                                        showPasswordDialog = true
                                    } else {
                                        showDeleteDialog = true
                                    }
                                }) {
                                    AppIcon(
                                        icon = Icons.Default.Delete,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                }
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
                .padding(vertical = 16.dp)
        ) {
            Column(
                Modifier
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Column(
                    Modifier
                        .weight(1f)
                        .verticalScroll(scrollState)
                        .imePadding(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Basic(
                        customerId = state.customerId,
                        customerName = state.customerName,
                        selectedCustomerName = state.selectedCustomerName,
                        balance = state.balance,
                        isDiscRsPer = state.isDiscRsPer,
                        disc = state.disc,
                        totalDisc = state.totalDisc,
                        date = state.date,
                        dueDate = state.dueDate,
                        salesOn = state.salesOn,
                        salesType = state.salesType,
                        onCustomerNameChange = viewModel::onCustomerNameChange,
                        onSelectedCustomerNameChange = viewModel::onSelectedCustomerNameChange,
                        onCustomerSelected = { customer ->
                            viewModel.onCustomerIdChange(customer.id)
                        },
                        onDiscChange = viewModel::onDiscChange,
                        onIsDiscRsPerChange = viewModel::onIsDiscRsPerChange,
                        onSalesOnChange = viewModel::onSalesOnChange,
                        onSalesTypeChange = viewModel::onSalesTypeChange,
                        onDateChange = viewModel::onDateChange,
                        onDueDateChange = viewModel::onDueDateChange,
                    )
                    MOP(
                        mop = state.mop,
                        bank = state.bank,
                        subBank = state.subBank,
                        bankEnabled = state.bankEnabled,
                        subBankEnabled = state.subBankEnabled,
                        onMOPChange = viewModel::onMOPChange,
                        onBankSelected = viewModel::onBankSelected,
                        onSubBankSelected = viewModel::onSubBankSelected,
                    )
                    Others(
                        isRetail = state.isRetail,
                        supplier = state.supplier,
                        payment = state.payment,
                        paymentEnabled = state.paymentEnabled,
                        change = state.change,
                        remarks = state.remarks,
                        onIsRetailChange = { value ->
                            viewModel.onIsRetailChange(value)
                            viewModel.onRetailChange(value){
                                goBackWithResult()
                            }
                        },
                        onSupplierSelected = viewModel::onSupplierSelected,
                        onPaymentChange = viewModel::onPaymentChange,
                        onChangeChange = viewModel::onChangeChange,
                        onRemarksChange = viewModel::onRemarksChange,
                    )
                    Spacer(Modifier.height(8.dp))
                }

                Box(
                    Modifier
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
private fun Basic(
    customerId: Long,
    customerName: String,
    selectedCustomerName: String,
    balance: String,
    disc: String,
    isDiscRsPer: Boolean,
    totalDisc: Double,
    date: LocalDate,
    dueDate: LocalDate,
    salesOn: DropdownItem,
    salesType: DropdownItem,
    onCustomerNameChange: (String) -> Unit,
    onSelectedCustomerNameChange: (String) -> Unit,
    onCustomerSelected: (DropdownItem) -> Unit,
    onDiscChange: (String) -> Unit,
    onIsDiscRsPerChange: (Boolean) -> Unit,
    onSalesOnChange: (DropdownItem) -> Unit,
    onSalesTypeChange: (DropdownItem) -> Unit,
    onDateChange: (LocalDate) -> Unit,
    onDueDateChange: (LocalDate) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(ConstantPaddings.BODY_HORIZONTAL)
    ) {
        Dropdown(
            value = selectedCustomerName,
            onValueChange = onSelectedCustomerNameChange,
            items = HP.customers,
            onItemSelected = onCustomerSelected,
            label = {
                Text("Customer")
            },
            padding = PaddingValues(top = 4.dp)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
        ) {
            BalanceBox(
                text = balance
            )
        }
        Spacer(Modifier.height(16.dp))
        Textbox(
            value = customerName,
            onValueChange = onCustomerNameChange,
            modifier = Modifier
                .fillMaxWidth(),
            label = {
                Text("Customer Name")
            },
            enabled = customerId == 0L,
        )
        if(HP.userRights.discount == true) {
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
        Row(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            ComboBox(
                modifier = Modifier
                    .fillMaxWidth(0.5f),
                items = HP.salesOn,
                selectedItem = salesOn,
                onItemSelected = onSalesOnChange,
                label = {
                    Text(text = "Sales On")
                },
                showEndIcon = false,
                enabled = HP.userRights.creditBill == true,
            )
            Spacer(Modifier.width(8.dp))
            ComboBox(
                modifier = Modifier
                    .fillMaxWidth(),
                items = HP.salesType,
                selectedItem = salesType,
                onItemSelected = onSalesTypeChange,
                label = {
                    Text(text = "Sales Type")
                },
                showEndIcon = false,
                enabled = HP.userRights.salesReturn == true,
            )
        }
        if (HP.userRights.dateWiseSales == true) {
            DateTextbox(
                modifier = Modifier
                    .fillMaxWidth(),
                date = date,
                onDateChange = onDateChange,
                label = "Date"
            )
        }
        if (salesOn.id == 2L) {
            DateTextbox(
                modifier = Modifier
                    .fillMaxWidth(),
                date = dueDate,
                onDateChange = onDueDateChange,
                label = "Due Date"
            )
        }
    }
}

@Composable
private fun MOP(
    mop: DropdownItem,
    bank: DropdownItem,
    subBank: DropdownItem,
    bankEnabled: Boolean,
    subBankEnabled: Boolean,
    onMOPChange: (DropdownItem) -> Unit,
    onBankSelected: (DropdownItem) -> Unit,
    onSubBankSelected: (DropdownItem) -> Unit,
) {
    ExpandableSection(
        title = "M.O.P Bank",
        initiallyExpanded = false,
    ) {
        ComboBox(
            modifier = Modifier
                .fillMaxWidth(),
            items = HP.mop,
            selectedItem = mop,
            onItemSelected = onMOPChange,
            label = {
                Text("M.O.P")
            }
        )
        ComboBox(
            modifier = Modifier
                .fillMaxWidth(),
            items = HP.banks,
            selectedItem = bank,
            onItemSelected = onBankSelected,
            label = {
                Text("Bank")
            },
            addNone = true,
            enabled = bankEnabled,
        )
        SubComboBox(
            modifier = Modifier
                .fillMaxWidth(),
            items = HP.subBanks,
            selectedItem = subBank,
            onItemSelected = onSubBankSelected,
            label = {
                Text("Bank Account")
            },
            addNone = true,
            enabled = subBankEnabled,
            mainId = bank.id
        )
    }
}


@Composable
private fun Others(
    isRetail: Boolean,
    supplier: DropdownItem?,
    payment: String,
    paymentEnabled: Boolean,
    change: String,
    remarks: String,
    onIsRetailChange: (Boolean) -> Unit,
    onSupplierSelected: (DropdownItem) -> Unit,
    onPaymentChange: (String) -> Unit,
    onChangeChange: (String) -> Unit,
    onRemarksChange: (String) -> Unit,
) {
    ExpandableSection(
        title = "Others",
        initiallyExpanded = false,
    ) {
        if(HP.settings.fourRateSystem == false) {
            Spacer(Modifier.height(8.dp))
            AppSwitch(
                modifier = Modifier,
                checked = isRetail,
                onCheckedChange = onIsRetailChange,
                label = "Is Retail"
            )
            Spacer(Modifier.height(16.dp))
        }
        ComboBox(
            modifier = Modifier
                .fillMaxWidth(),
            items = HP.suppliers,
            selectedItem = supplier,
            onItemSelected = onSupplierSelected,
            label = {
                Text(text = "Supplier")
            },
            addNone = true,
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Textbox(
                value = payment,
                onValueChange = onPaymentChange,
                modifier = Modifier
                    .weight(1f),
                label = {
                    Text("Payment")
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal
                ),
                enabled = paymentEnabled,
            )
            Spacer(Modifier.width(8.dp))
            Textbox(
                value = change,
                onValueChange = onChangeChange,
                modifier = Modifier
                    .weight(1f),
                label = {
                    Text("Change")
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal
                ),
            )
        }
        Textbox(
            value = remarks,
            onValueChange = onRemarksChange,
            modifier = Modifier
                .fillMaxWidth()
                .height(84.dp),
            label = {
                Text("Remarks")
            },
            singleLine = false,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun BodyPrev() {
    val scrollState = rememberScrollState()

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
            Basic(
                0L,
                "",
                "",
                "",
                "",
                true,
                0.0,
                LocalDate.now(),
                LocalDate.now(),
                HP.salesOn[0],
                HP.salesType[0],
                { },
                { },
                { },
                { },
                { },
                { },
                { },
                { },
                { },
            )
            Spacer(Modifier.height(8.dp))
            Others(
                true,
                HP.getNoneDropdownItem(),
                "",
                true,
                "",
                "",
                {},
                {},
                {},
                {},
                {},
            )
            MOP(
                mop = HP.mop[0],
                bank = HP.getNoneDropdownItem(),
                subBank = HP.getNoneDropdownItem(),
                bankEnabled = true,
                subBankEnabled = true,
                onMOPChange = {},
                onBankSelected = {},
                onSubBankSelected = {},
            )
        }
        Box(
            modifier = Modifier
                .padding(16.dp),
        ) {
            SaveButton {}
        }

    }
}