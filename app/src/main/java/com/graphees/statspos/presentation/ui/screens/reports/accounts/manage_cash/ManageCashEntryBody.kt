package com.graphees.statspos.presentation.ui.screens.reports.accounts.manage_cash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.graphees.statspos.domain.models.DropdownItem
import com.graphees.statspos.domain.models.reports.TotalReport
import com.graphees.statspos.domain.models.reports.accounts.AccountReport
import com.graphees.statspos.presentation.ui.components.AppCircularProgressIndicator
import com.graphees.statspos.presentation.ui.components.ComboBox
import com.graphees.statspos.presentation.ui.components.DateTextbox
import com.graphees.statspos.presentation.ui.components.ErrorDialog
import com.graphees.statspos.presentation.ui.components.ProgressBarLayout
import com.graphees.statspos.presentation.ui.components.ReportButton
import com.graphees.statspos.presentation.ui.components.ReportCard
import com.graphees.statspos.presentation.ui.components.SaveButton
import com.graphees.statspos.presentation.ui.components.TextboxOutlined
import com.graphees.statspos.presentation.ui.screens.reports.accounts.cashAccountReport
import com.graphees.statspos.presentation.ui.utils.ConstantPaddings
import com.graphees.statspos.presentation.ui.utils.openPdf
import com.graphees.statspos.presentation.viewmodels.SharedViewModel
import com.graphees.statspos.presentation.viewmodels.reports.manage_cash.ManageCashEntryViewModel
import com.graphees.statspos.utils.HP
import com.graphees.statspos.utils.UiEvent
import com.graphees.statspos.utils.checkEvent
import com.graphees.statspos.utils.showToast
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageCashEntryBody(
    sharedViewModel: SharedViewModel,
    snackbarHostState: SnackbarHostState,
) {
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val viewModel = hiltViewModel<ManageCashEntryViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val event by viewModel.event.collectAsState(UiEvent.Idle)
    var showErrorDialog by remember { mutableStateOf(false) }
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

    if (showErrorDialog) {
        ErrorDialog(
            error = state.error,
            onDismiss = {
                showErrorDialog = false
            },
        )
    }

    fun showCashAccount(
        accountReport: List<AccountReport>,
        totalReport: TotalReport
    ) {
        val file = cashAccountReport(
            context = context,
            fromDate = HP.getFormatedDate(state.fromDate),
            toDate = HP.getFormatedDate(state.toDate),
            accountReport = accountReport,
            totalReport = totalReport,
        )

        openPdf(context, file)
    }

        Box(
            Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(ConstantPaddings.BODY_HORIZONTAL)
                .padding(vertical = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
            ) {
                Column(
                    Modifier
                        .weight(1f)
                        .verticalScroll(scrollState),
                ) {
                    Basic(
                        entryTypes = state.entryTypes,
                        entryType = state.entryType,
                        account = state.account,
                        amount = state.amount,
                        date = state.date,
                        naration = state.naration,
                        onEntryTypeChange = viewModel::onEntryTypeChange,
                        onAccountChange = viewModel::onAccountChange,
                        onAmountChanged = viewModel::onAmountChange,
                        onDateChanged = viewModel::onDateChange,
                        onNarationChanged = viewModel::onNarationChange,
                    )
                    Spacer(Modifier.height(12.dp))
                    ReportCard(
                        heading = "Cash account",
                        subHeading = "Summary of cash",
                        content = {
                            CashAccount(
                                fromDate = state.fromDate,
                                toDate = state.toDate,
                                onFromDateChange = viewModel::onFromDateChange,
                                onToDateChange = viewModel::onToDateChange,
                                onCashAccountClick = {
                                    viewModel.onCashAccountClick { accountReport, totalReport ->
                                        showCashAccount(accountReport, totalReport)
                                    }
                                }
                            )
                        }
                    )
                    Spacer(Modifier.height(12.dp))
                }

                // Post Button
                Box(
                    modifier = Modifier
                        .windowInsetsPadding(
                            WindowInsets.ime
                                .exclude(WindowInsets.navigationBars)
                                .only(WindowInsetsSides.Bottom)
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
private fun Basic(
    entryTypes: List<DropdownItem>,
    entryType: DropdownItem,
    account: DropdownItem,
    amount: String,
    date: LocalDate,
    naration: String,
    onEntryTypeChange: (DropdownItem) -> Unit,
    onAccountChange: (DropdownItem) -> Unit,
    onAmountChanged: (String) -> Unit,
    onDateChanged: (LocalDate) -> Unit,
    onNarationChanged: (String) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        ComboBox(
            modifier = Modifier
                .fillMaxWidth(),
            items = entryTypes,
            selectedItem = entryType,
            onItemSelected = onEntryTypeChange,
            label = {
                Text("Type")
            },
        )
        ComboBox(
            modifier = Modifier
                .fillMaxWidth(),
            items = HP.fixedAccounts,
            selectedItem = account,
            onItemSelected = onAccountChange,
            label = {
                Text("Account")
            },
            addNone = true,
        )
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

@Composable
private fun CashAccount(
    fromDate: LocalDate,
    toDate: LocalDate,
    onFromDateChange: (LocalDate) -> Unit,
    onToDateChange: (LocalDate) -> Unit,
    onCashAccountClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth(),
    ) {
        Spacer(Modifier.height(2.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth(),
        ) {
            DateTextbox(
                modifier = Modifier
                    .weight(1f),
                date = fromDate,
                onDateChange = onFromDateChange,
                label = "From Date"
            )
            Spacer(Modifier.width(8.dp))
            DateTextbox(
                modifier = Modifier
                    .weight(1f),
                date = toDate,
                onDateChange = onToDateChange,
                label = "To Date"
            )
        }
        Spacer(Modifier.height(2.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
        ) {
            ReportButton("Cash Account") {
                onCashAccountClick()
            }
        }
    }
}
