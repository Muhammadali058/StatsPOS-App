package com.graphees.statspos.presentation.ui.screens.purchase.purchase_bill

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.graphees.statspos.R
import com.graphees.statspos.domain.models.DropdownItem
import com.graphees.statspos.presentation.ui.components.BalanceBox
import com.graphees.statspos.presentation.ui.components.ComboBox
import com.graphees.statspos.presentation.ui.components.DateTextbox
import com.graphees.statspos.presentation.ui.components.DiscountTextbox
import com.graphees.statspos.presentation.ui.components.Dropdown
import com.graphees.statspos.presentation.ui.components.ErrorDialog
import com.graphees.statspos.presentation.ui.components.ExpandableSection
import com.graphees.statspos.presentation.ui.components.MOPSection
import com.graphees.statspos.presentation.ui.components.PlaceHolder
import com.graphees.statspos.presentation.ui.components.ProgressBarLayout
import com.graphees.statspos.presentation.ui.components.SaveButton
import com.graphees.statspos.presentation.ui.components.TextboxOutlined
import com.graphees.statspos.presentation.ui.utils.ConstantPaddings
import com.graphees.statspos.presentation.viewmodels.SharedViewModel
import com.graphees.statspos.presentation.viewmodels.purchase.purchase_bill.AddUpdatePurchaseViewModel
import com.graphees.statspos.utils.HP
import com.graphees.statspos.utils.UiEvent
import com.graphees.statspos.utils.checkEvent
import java.time.LocalDate

@Composable
fun PurchaseBillBody(
    sharedViewModel: SharedViewModel,
    purchaseViewModel: AddUpdatePurchaseViewModel,
    snackbarHostState: SnackbarHostState,
) {
    val state by purchaseViewModel.state.collectAsStateWithLifecycle()
    val event by purchaseViewModel.event.collectAsState(UiEvent.Idle)
    var showErrorDialog by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    LaunchedEffect(event) {
        checkEvent(
            event = event,
            snackbarHostState = snackbarHostState,
            viewModelIdleEvent = purchaseViewModel::onEvent,
            onError = {
                showErrorDialog = true
            }
        )
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
            .background(MaterialTheme.colorScheme.surface)
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
                Basic(
                    vendorName = state.vendorName,
                    balance = state.balance,
                    isDiscRsPer = state.isDiscRsPer,
                    disc = state.disc,
                    totalDisc = state.totalDisc,
                    date = state.date,
                    purchaseOn = state.purchaseOn,
                    purchaseType = state.purchaseType,
                    onVendorNameChange = purchaseViewModel::onVendorNameChange,
                    onVendorSelected = { vendor ->
                        purchaseViewModel.onVendorIdChange(vendor.id)
                    },
                    onDiscChange = purchaseViewModel::onDiscChange,
                    onIsDiscRsPerChange = purchaseViewModel::onIsDiscRsPerChange,
                    onPurchaseOnChange = purchaseViewModel::onPurchaseOnChange,
                    onPurchaseTypeChange = purchaseViewModel::onPurchaseTypeChange,
                    onDateChange = purchaseViewModel::onDateChange,
                )
                MOPSection(
                    mop = state.mop,
                    bank = state.bank,
                    subBank = state.subBank,
                    onMOPChange = purchaseViewModel::onMOPChange,
                    onBankSelected = purchaseViewModel::onBankSelected,
                    onSubBankSelected = purchaseViewModel::onSubBankSelected,
                )
                Others(
                    remarks = state.remarks,
                    expense = state.expense,
                    refInvoiceNo = state.refInvoiceNo,
                    supplier = state.supplier,
                    onSupplierSelected = purchaseViewModel::onSupplierSelected,
                    onExpenseChange = purchaseViewModel::onExpenseChange,
                    onRefInvoiceNoChange = purchaseViewModel::onRefInvoiceNoChange,
                )
                Spacer(Modifier.height(8.dp))
            }
        }

        if (state.isLoading) {
            ProgressBarLayout()
        }
    }
}

@Composable
private fun Basic(
    vendorName: String,
    balance: String,
    disc: String,
    isDiscRsPer: Boolean,
    totalDisc: Double,
    date: LocalDate,
    purchaseOn: DropdownItem,
    purchaseType: DropdownItem,
    onVendorNameChange: (String) -> Unit,
    onVendorSelected: (DropdownItem) -> Unit,
    onDiscChange: (String) -> Unit,
    onIsDiscRsPerChange: (Boolean) -> Unit,
    onPurchaseOnChange: (DropdownItem) -> Unit,
    onPurchaseTypeChange: (DropdownItem) -> Unit,
    onDateChange: (LocalDate) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(ConstantPaddings.BODY_HORIZONTAL)
    ) {
        Dropdown(
            value = vendorName,
            onValueChange = onVendorNameChange,
            items = HP.vendors,
            onItemSelected = onVendorSelected,
            label = {
                Text("Vendor")
            },
            outlined = true,
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
        Spacer(Modifier.height(8.dp))
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
        Row(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            ComboBox(
                modifier = Modifier
                    .fillMaxWidth(0.5f),
                items = HP.purchaseOn,
                selectedItem = purchaseOn,
                onItemSelected = onPurchaseOnChange,
                label = {
                    Text(text = "Purchase On")
                },
                placeholder = {
                    PlaceHolder(text = "Purchase On")
                },
                outlined = true,
                showEndIcon = false,
                enabled = HP.userRights.creditBill == true,
            )
            Spacer(Modifier.width(8.dp))
            ComboBox(
                modifier = Modifier
                    .fillMaxWidth(),
                items = HP.purchaseType,
                selectedItem = purchaseType,
                onItemSelected = onPurchaseTypeChange,
                label = {
                    Text(text = "Purchase Type")
                },
                placeholder = {
                    PlaceHolder(text = "Purchase Type")
                },
                outlined = true,
                showEndIcon = false,
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
    }
}

@Composable
private fun Others(
    remarks: String,
    expense: String,
    refInvoiceNo: String,
    supplier: DropdownItem?,
    onSupplierSelected: (DropdownItem) -> Unit,
    onExpenseChange: (String) -> Unit,
    onRefInvoiceNoChange: (String) -> Unit,
) {
    ExpandableSection(
        title = "Others",
        icon = R.drawable.others,
    ) {
        if (HP.adminSettings.showSuppliersInPurchase == true) {
            ComboBox(
                modifier = Modifier
                    .fillMaxWidth(),
                items = HP.suppliers,
                selectedItem = supplier,
                onItemSelected = onSupplierSelected,
                label = {
                    Text(text = "Supplier")
                },
                outlined = true,
                addNone = true,
            )
        }
//        Textbox(
//            value = if (HP.getDoubleValue(expense) > 0.0) expense else "",
//            onValueChange = onExpenseChange,
//            modifier = Modifier.fillMaxWidth(),
//            label = {
//                Text("Expense")
//            }
//        )
        TextboxOutlined(
            value = refInvoiceNo,
            onValueChange = onRefInvoiceNoChange,
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text("Ref. Inv No")
            }
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
                "",
                "",
                "",
                true,
                0.0,
                LocalDate.now(),
                HP.purchaseOn[0],
                HP.purchaseType[0],
                {},
                { },
                { },
                { },
                { },
                { },
                { },
            )
            Spacer(Modifier.height(8.dp))
            Others(
                "",
                "",
                "",
                HP.getNoneDropdownItem(),
                { },
                { },
                { },
            )
            MOPSection(
                mop = HP.mop[0],
                bank = HP.getNoneDropdownItem(),
                subBank = HP.getNoneDropdownItem(),
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