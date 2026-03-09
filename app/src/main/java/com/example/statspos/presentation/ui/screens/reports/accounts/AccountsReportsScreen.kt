package com.example.statspos.presentation.ui.screens.reports.accounts

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.statspos.domain.models.DropdownItem
import com.example.statspos.domain.models.reports.TotalReport
import com.example.statspos.domain.models.reports.accounts.AccountReport
import com.example.statspos.presentation.ui.components.AppSnackbarHost
import com.example.statspos.presentation.ui.components.ComboBox
import com.example.statspos.presentation.ui.components.Dropdown
import com.example.statspos.presentation.ui.components.ErrorDialog
import com.example.statspos.presentation.ui.components.ProgressBarLayout
import com.example.statspos.presentation.ui.components.ReportButton
import com.example.statspos.presentation.ui.components.ReportCard
import com.example.statspos.presentation.ui.components.ShowReportIcon
import com.example.statspos.presentation.ui.components.SubComboBox
import com.example.statspos.presentation.ui.components.TopAppBar
import com.example.statspos.presentation.ui.utils.ConstantPaddings
import com.example.statspos.presentation.ui.utils.openPdf
import com.example.statspos.presentation.viewmodels.SharedViewModel
import com.example.statspos.presentation.viewmodels.reports.AccountReportsViewModel
import com.example.statspos.utils.HP
import com.example.statspos.utils.UiEvent
import com.example.statspos.utils.checkEvent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountsReportsScreen(
    sharedViewModel: SharedViewModel,
    onBack: () -> Unit,
) {
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val viewModel = hiltViewModel<AccountReportsViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val event by viewModel.event.collectAsState(UiEvent.Idle)
    val snackbarHostState = remember { SnackbarHostState() }
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

    fun showLedger(
        accountReport: List<AccountReport>,
        totalReport: TotalReport
    ) {
        val file = ledgerReport(
            context = context,
            fromDate = HP.getFormatedDate(state.fromDate),
            toDate = HP.getFormatedDate(state.toDate),
            accountReport = accountReport,
            totalReport = totalReport,
        )

        openPdf(context, file)
    }

    fun showBankStatement(
        accountReport: List<AccountReport>,
        totalReport: TotalReport
    ) {
        val file = bankStatementReport(
            context = context,
            fromDate = HP.getFormatedDate(state.fromDate),
            toDate = HP.getFormatedDate(state.toDate),
            accountReport = accountReport,
            totalReport = totalReport,
        )

        openPdf(context, file)
    }

    fun showExpenses(
        accountReport: List<AccountReport>,
        totalReport: TotalReport
    ) {
        val file = expensesReport(
            context = context,
            fromDate = HP.getFormatedDate(state.fromDate),
            toDate = HP.getFormatedDate(state.toDate),
            accountReport = accountReport,
            totalReport = totalReport,
        )

        openPdf(context, file)
    }

    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing,
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
                title = "Accounts Reports",
            )
        }
    ) { innerPadding ->
        Box(
            Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
                .padding(ConstantPaddings.BODY_HORIZONTAL)
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
                    Spacer(Modifier.height(16.dp))
                    ReportCard(
                        heading = "Ledger",
                        subHeading = "Detailed ledgers of accounts",
                        content = {
                            Ledger(
                                customerName = state.customerName,
                                vendorName = state.vendorName,
                                onCustomerNameChange = viewModel::onCustomerNameChange,
                                onVendorNameChange = viewModel::onVendorNameChange,
                                onCustomerIdChange = viewModel::onCustomerIdChange,
                                onVendorIdChange = viewModel::onVendorIdChange,
                                onCustomerClick = {
                                    viewModel.onCustomerLedgerClick { accountReport, totalReport ->
                                        showLedger(accountReport, totalReport)
                                    }
                                },
                                onVendorClick = {
                                    viewModel.onVendorLedgerClick { accountReport, totalReport ->
                                        showLedger(accountReport, totalReport)
                                    }
                                },
                            )
                        }
                    )
                    Spacer(Modifier.height(16.dp))
                    ReportCard(
                        heading = "Bank Statement",
                        subHeading = "Details of bank receipts & payments",
                        content = {
                            BankStatement(
                                bank = state.bank,
                                subBank = state.subBank,
                                onBankChange = viewModel::onBankChange,
                                onSubBankChange = viewModel::onSubBankChange,
                                onShowClick = {
                                    viewModel.onBankStatementClick { accountReport, totalReport ->
                                        showBankStatement(accountReport, totalReport)
                                    }
                                }
                            )
                        }
                    )
                    Spacer(Modifier.height(16.dp))
                    ReportCard(
                        heading = "Expenses",
                        subHeading = "Summary of expenses",
                        content = {
                            Expenses(
                                expense = state.expense,
                                subExpense = state.subExpense,
                                onExpenseChange = viewModel::onExpenseChange,
                                onSubExpenseChange = viewModel::onSubExpenseChange,
                                onExpensesClick = {
                                    viewModel.onExpensesClick { accountReport, totalReport ->
                                        showExpenses(accountReport, totalReport)
                                    }
                                },
                                onSubExpensesClick = {
                                    viewModel.onSubExpensesClick { accountReport, totalReport ->
                                        showExpenses(accountReport, totalReport)
                                    }
                                },
                                onTotalExpensesClick = {
                                    viewModel.onTotalExpensesClick { accountReport, totalReport ->
                                        showExpenses(accountReport, totalReport)
                                    }
                                }
                            )
                        }
                    )
                    Spacer(Modifier.height(16.dp))
                }
            }

            if (state.isLoading) {
                ProgressBarLayout()
            }
        }
    }
}

@Composable
private fun Ledger(
    customerName: String,
    vendorName: String,
    onCustomerNameChange: (String) -> Unit,
    onVendorNameChange: (String) -> Unit,
    onCustomerIdChange: (Long) -> Unit,
    onVendorIdChange: (Long) -> Unit,
    onCustomerClick: () -> Unit,
    onVendorClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Dropdown(
            value = customerName,
            onValueChange = onCustomerNameChange,
            items = HP.customers,
            onItemSelected = { dropdownItem ->
                onCustomerIdChange(dropdownItem.id)
            },
            label = {
                Text(text = "Customer")
            },
            trailingIcon = {
                ShowReportIcon {
                    onCustomerClick()
                }
            },
            changeIdOnEmpty = true,
        )
        Dropdown(
            value = vendorName,
            onValueChange = onVendorNameChange,
            items = HP.vendors,
            onItemSelected = { dropdownItem ->
                onVendorIdChange(dropdownItem.id)
            },
            label = {
                Text(text = "Vendor")
            },
            trailingIcon = {
                ShowReportIcon {
                    onVendorClick()
                }
            },
            changeIdOnEmpty = true,
        )
    }
}

@Composable
private fun BankStatement(
    bank: DropdownItem,
    subBank: DropdownItem,
    onBankChange: (DropdownItem) -> Unit,
    onSubBankChange: (DropdownItem) -> Unit,
    onShowClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        ComboBox(
            modifier = Modifier
                .fillMaxWidth(),
            items = HP.banks,
            selectedItem = bank,
            onItemSelected = onBankChange,
            label = {
                Text("Bank")
            },
            addNone = true,
        )
        SubComboBox(
            modifier = Modifier
                .fillMaxWidth(),
            items = HP.subBanks,
            selectedItem = subBank,
            onItemSelected = onSubBankChange,
            label = {
                Text("Bank Account")
            },
            addNone = true,
            mainId = bank.id
        )
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
        ) {
            ReportButton{
                onShowClick()
            }
        }
    }
}

@Composable
private fun Expenses(
    expense: DropdownItem,
    subExpense: DropdownItem,
    onExpenseChange: (DropdownItem) -> Unit,
    onSubExpenseChange: (DropdownItem) -> Unit,
    onExpensesClick: () -> Unit,
    onSubExpensesClick: () -> Unit,
    onTotalExpensesClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        ComboBox(
            modifier = Modifier
                .fillMaxWidth(),
            items = HP.expenses,
            selectedItem = expense,
            onItemSelected = onExpenseChange,
            label = {
                Text("Expense")
            },
            addNone = true,
            trailingIcon = {
                ShowReportIcon {
                    onExpensesClick()
                }
            },
        )
        SubComboBox(
            modifier = Modifier
                .fillMaxWidth(),
            items = HP.subExpenses,
            selectedItem = subExpense,
            onItemSelected = onSubExpenseChange,
            label = {
                Text("Sub-Expense")
            },
            addNone = true,
            mainId = expense.id,
            trailingIcon = {
                ShowReportIcon {
                    onSubExpensesClick()
                }
            },
        )
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
        ) {
            ReportButton("Total Expenses"){
                onTotalExpensesClick()
            }
        }
    }
}
