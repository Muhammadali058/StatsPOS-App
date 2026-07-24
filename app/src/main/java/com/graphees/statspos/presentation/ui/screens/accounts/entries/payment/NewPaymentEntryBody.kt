package com.graphees.statspos.presentation.ui.screens.accounts.entries.payment

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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.graphees.statspos.domain.models.DropdownItem
import com.graphees.statspos.presentation.ui.components.AppCircularProgressIndicator
import com.graphees.statspos.presentation.ui.components.AppSwitch
import com.graphees.statspos.presentation.ui.components.AppText
import com.graphees.statspos.presentation.ui.components.BalanceBox
import com.graphees.statspos.presentation.ui.components.DateTextbox
import com.graphees.statspos.presentation.ui.components.Dropdown
import com.graphees.statspos.presentation.ui.components.ErrorDialog
import com.graphees.statspos.presentation.ui.components.MOPSection
import com.graphees.statspos.presentation.ui.components.PlaceHolder
import com.graphees.statspos.presentation.ui.components.ProgressBarLayout
import com.graphees.statspos.presentation.ui.components.SaveButton
import com.graphees.statspos.presentation.ui.components.TextboxOutlined
import com.graphees.statspos.presentation.ui.utils.ConstantPaddings
import com.graphees.statspos.presentation.viewmodels.SharedViewModel
import com.graphees.statspos.presentation.viewmodels.accounts.entries.payment.NewPaymentEntryViewModel
import com.graphees.statspos.utils.HP
import com.graphees.statspos.utils.UiEvent
import com.graphees.statspos.utils.checkEvent
import com.graphees.statspos.utils.showToast
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield
import java.time.LocalDate

@Composable
fun NewPaymentEntryBody(
    sharedViewModel: SharedViewModel,
    snackbarHostState: SnackbarHostState
) {
    val scope = rememberCoroutineScope()
    val keyboardController = LocalSoftwareKeyboardController.current
    val context = LocalContext.current
    val viewModel = hiltViewModel<NewPaymentEntryViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val event by viewModel.event.collectAsState(UiEvent.Idle)
    var showErrorDialog by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()
    val accountFocusRequester = remember { FocusRequester() }
    val amountFocusRequester = remember { FocusRequester() }

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
                Body(
                    accountFocusRequester = accountFocusRequester,
                    amountFocusRequester = amountFocusRequester,
                    customerName = state.customerName,
                    vendorName = state.vendorName,
                    isCustomer = state.isCustomer,
                    amount = state.amount,
                    date = state.date,
                    naration = state.naration,
                    balance = state.balance,
                    remarks = state.remarks,
                    onCustomerNameChange = viewModel::onCustomerNameChange,
                    onVendorNameChange = viewModel::onVendorNameChange,
                    onCustomerSelected = { customer ->
                        viewModel.onCustomerIdChange(customer.id)
                        scope.launch {
                            yield()
                            amountFocusRequester.requestFocus()
                        }
                    },
                    onVendorSelected = { vendor ->
                        viewModel.onVendorIdChange(vendor.id)
                        scope.launch {
                            yield()
                            amountFocusRequester.requestFocus()
                        }
                    },
                    onIsVendorChanged = viewModel::onIsVendorChange,
                    onAmountChanged = viewModel::onAmountChange,
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
//                            accountFocusRequester.requestFocus()
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
    accountFocusRequester: FocusRequester?,
    amountFocusRequester: FocusRequester?,
    customerName: String,
    vendorName: String,
    isCustomer: Boolean,
    amount: String,
    date: LocalDate,
    naration: String,
    balance: String,
    remarks: String,
    onCustomerNameChange: (String) -> Unit,
    onVendorNameChange: (String) -> Unit,
    onCustomerSelected: (DropdownItem) -> Unit,
    onVendorSelected: (DropdownItem) -> Unit,
    onIsVendorChanged: (Boolean) -> Unit,
    onAmountChanged: (String) -> Unit,
    onDateChanged: (LocalDate) -> Unit,
    onNarationChanged: (String) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(ConstantPaddings.BODY_HORIZONTAL)
    ) {
        Spacer(Modifier.height(2.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AppText(
                text = remarks,
                style = MaterialTheme.typography.labelSmall
            )
            AppSwitch(
                checked = isCustomer,
                onCheckedChange = onIsVendorChanged,
                label = "Customer"
            )
        }

        Spacer(Modifier.height(4.dp))
        if (isCustomer) {
            Dropdown(
                value = customerName,
                onValueChange = onCustomerNameChange,
                items = HP.customers,
                onItemSelected = onCustomerSelected,
                label = {
                    Text("Customer")
                },
                outlined = true,
                padding = PaddingValues(top = 4.dp),
                focusRequester = accountFocusRequester,
            )
        }
        if (!isCustomer) {
            Dropdown(
                value = vendorName,
                onValueChange = onVendorNameChange,
                items = HP.vendors,
                onItemSelected = onVendorSelected,
                label = {
                    Text("Vendor")
                },
                outlined = true,
                padding = PaddingValues(top = 4.dp),
                focusRequester = accountFocusRequester,
            )
        }
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
                focusRequester = amountFocusRequester,
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

@Preview(showBackground = true)
@Composable
private fun Prev() {
    Column(
        Modifier
            .fillMaxSize(),
    ) {
        Body(
            null,
            null,
            customerName = "",
            vendorName = "",
            isCustomer = false,
            amount = "",
            date = LocalDate.now(),
            naration = "",
            balance = "Balance: 0 (R)",
            remarks = "",
            {},
            {},
            {},
            {},
            {},
            {},
            {},
            {},
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
}