package com.example.statspos.presentation.ui.screens.sales.sales_bill

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.statspos.domain.models.DropdownItem
import com.example.statspos.domain.models.sales.SalesBills
import com.example.statspos.presentation.ui.components.AppCircularProgressIndicator
import com.example.statspos.presentation.ui.components.AppSwitch
import com.example.statspos.presentation.ui.components.BalanceBox
import com.example.statspos.presentation.ui.components.ComboBox
import com.example.statspos.presentation.ui.components.DateTextbox
import com.example.statspos.presentation.ui.components.DiscountTextbox
import com.example.statspos.presentation.ui.components.Dropdown
import com.example.statspos.presentation.ui.components.ErrorDialog
import com.example.statspos.presentation.ui.components.ExpandableSection
import com.example.statspos.presentation.ui.components.ProgressBarLayout
import com.example.statspos.presentation.ui.components.SaveButton
import com.example.statspos.presentation.ui.components.SubComboBox
import com.example.statspos.presentation.ui.components.Textbox
import com.example.statspos.presentation.ui.utils.ConstantPaddings
import com.example.statspos.presentation.viewmodels.SharedViewModel
import com.example.statspos.presentation.viewmodels.sales.sales_bill.AddUpdateSalesViewModel
import com.example.statspos.presentation.viewmodels.sales.sales_bill.SalesItemsViewModel
import com.example.statspos.utils.HP
import com.example.statspos.utils.UiEvent
import com.example.statspos.utils.checkEvent
import java.time.LocalDate

@Composable
fun SalesBillBody(
    sharedViewModel: SharedViewModel,
    salesViewModel: AddUpdateSalesViewModel,
    salesItemsViewModel: SalesItemsViewModel,
    snackbarHostState: SnackbarHostState,
    invoiceId: Long,
    isPendingBill: Boolean,
    isPostedBill: Boolean,
    salesBill: SalesBills?,
    onBack: () -> Unit,
) {
    val state by salesViewModel.state.collectAsStateWithLifecycle()
    val event by salesViewModel.event.collectAsState(UiEvent.Idle)
    var showErrorDialog by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    LaunchedEffect(event) {
        checkEvent(
            event = event,
            snackbarHostState = snackbarHostState,
            viewModelIdleEvent = salesViewModel::onEvent,
            onError = {
                showErrorDialog = true
            }
        )
    }

    // Edit data when update
    var hasLoadedOnce by rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        if (!hasLoadedOnce) {
            if (isPendingBill || isPostedBill) {
                salesViewModel.editData(invoiceId)
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

    Box(
        Modifier
            .fillMaxSize()
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
//                    .imePadding()
                ,
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
                    onCustomerNameChange = salesViewModel::onCustomerNameChange,
                    onSelectedCustomerNameChange = salesViewModel::onSelectedCustomerNameChange,
                    onCustomerSelected = { customer ->
                        salesViewModel.onCustomerIdChange(customer.id)
                    },
                    onDiscChange = salesViewModel::onDiscChange,
                    onIsDiscRsPerChange = salesViewModel::onIsDiscRsPerChange,
                    onSalesOnChange = salesViewModel::onSalesOnChange,
                    onSalesTypeChange = salesViewModel::onSalesTypeChange,
                    onDateChange = salesViewModel::onDateChange,
                    onDueDateChange = salesViewModel::onDueDateChange,
                )
                MOP(
                    mop = state.mop,
                    bank = state.bank,
                    subBank = state.subBank,
                    bankEnabled = state.bankEnabled,
                    subBankEnabled = state.subBankEnabled,
                    onMOPChange = salesViewModel::onMOPChange,
                    onBankSelected = salesViewModel::onBankSelected,
                    onSubBankSelected = salesViewModel::onSubBankSelected,
                )
                Others(
                    isRetail = state.isRetail,
                    supplier = state.supplier,
                    payment = state.payment,
                    paymentEnabled = state.paymentEnabled,
                    change = state.change,
                    remarks = state.remarks,
                    onIsRetailChange = { value ->
                        salesViewModel.onIsRetailChange(value)
                        if (isPendingBill || isPostedBill) {
                            salesViewModel.changeBillType(value) {
                                salesItemsViewModel.loadData(salesViewModel::updateTotal)

                                if (isPendingBill) {
                                    salesViewModel.tempClose {
                                        sharedViewModel.notifyBillSaved()
                                    }
                                }

                                if (isPostedBill) {
                                    salesViewModel.postBill {
                                        sharedViewModel.notifyBillPosted()
                                    }
                                }
                            }
                        }
                    },
                    onSupplierSelected = salesViewModel::onSupplierSelected,
                    onPaymentChange = salesViewModel::onPaymentChange,
                    onChangeChange = salesViewModel::onChangeChange,
                    onRemarksChange = salesViewModel::onRemarksChange,
                )
                Spacer(Modifier.height(8.dp))
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(ConstantPaddings.BODY_HORIZONTAL)
            ) {
                if (!isPostedBill) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.5f),
                        contentAlignment = Alignment.Center,
                    ) {
                        if (state.isSaving) {
                            AppCircularProgressIndicator()
                        } else {
                            SaveButton(text = "Save") {
                                salesViewModel.tempClose {
                                    sharedViewModel.notifyBillSaved()
                                    onBack()
                                }
                            }
                        }
                    }
                    Spacer(Modifier.width(8.dp))
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center,
                ) {
                    if (state.isPosting) {
                        AppCircularProgressIndicator()
                    } else {
                        SaveButton(text = "Post") {
                            salesViewModel.postBill {
                                sharedViewModel.notifyBillPosted()
                                onBack()
                            }
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
        if (HP.settings.fourRateSystem == false) {
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
                readOnly = true,
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