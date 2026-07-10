package com.graphees.statspos.presentation.ui.screens.reports.accounts

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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.width
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
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.graphees.statspos.domain.models.DropdownItem
import com.graphees.statspos.domain.models.reports.TotalReport
import com.graphees.statspos.domain.models.reports.accounts.AccountReport
import com.graphees.statspos.presentation.ui.components.AppSnackbarHost
import com.graphees.statspos.presentation.ui.components.ComboBox
import com.graphees.statspos.presentation.ui.components.DateTextbox
import com.graphees.statspos.presentation.ui.components.Dropdown
import com.graphees.statspos.presentation.ui.components.ErrorDialog
import com.graphees.statspos.presentation.ui.components.ProgressBarLayout
import com.graphees.statspos.presentation.ui.components.ReportButton
import com.graphees.statspos.presentation.ui.components.ReportCard
import com.graphees.statspos.presentation.ui.components.SearchBox
import com.graphees.statspos.presentation.ui.components.ShowReportIcon
import com.graphees.statspos.presentation.ui.components.SubComboBox
import com.graphees.statspos.presentation.ui.components.TopAppBar
import com.graphees.statspos.presentation.ui.screens.reports.accounts.manage_cash.ManageCashScreen
import com.graphees.statspos.presentation.ui.screens.utilities.shift.ManageShiftsScreen
import com.graphees.statspos.presentation.ui.utils.ConstantPaddings
import com.graphees.statspos.presentation.ui.utils.openPdf
import com.graphees.statspos.presentation.viewmodels.SharedViewModel
import com.graphees.statspos.presentation.viewmodels.reports.AccountReportsViewModel
import com.graphees.statspos.utils.HP
import com.graphees.statspos.utils.UiEvent
import com.graphees.statspos.utils.checkEvent
import kotlinx.serialization.Serializable
import java.time.LocalDate

private sealed class Routes : NavKey {
    @Serializable
    data object Home : Routes()

    @Serializable
    data object ManageShift : Routes()

    @Serializable
    data object ManageCash : Routes()
}

@Composable
fun AccountsReportsScreen(
    sharedViewModel: SharedViewModel,
    onBack: () -> Unit,
) {
    val backStack = rememberNavBackStack(Routes.Home)
    fun navigate(key: NavKey) {
        if (backStack.lastOrNull() != key) {
            backStack.add(key)
        }
    }
    NavDisplay(
        backStack = backStack,
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        entryProvider = entryProvider {
            entry<Routes.Home> {
                Home(
                    sharedViewModel = sharedViewModel,
                    onManageShiftsClick = {
                        navigate(Routes.ManageShift)
                    },
                    onManageCashClick = {
                        navigate(Routes.ManageCash)
                    },
                    onBack = {
                        onBack()
                    },
                )
            }
            entry<Routes.ManageShift> { key ->
                ManageShiftsScreen(
                    sharedViewModel = sharedViewModel,
                    onBack = {
                        backStack.removeLastOrNull()
                    },
                )
            }
            entry<Routes.ManageCash> { key ->
                ManageCashScreen (
                    sharedViewModel = sharedViewModel,
                    onBack = {
                        backStack.removeLastOrNull()
                    },
                )
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Home(
    sharedViewModel: SharedViewModel,
    onManageShiftsClick: () -> Unit,
    onManageCashClick: () -> Unit,
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

    fun showReceipts(
        accountReport: List<AccountReport>,
        totalReport: TotalReport
    ) {
        val file = receiptsReport(
            context = context,
            fromDate = HP.getFormatedDate(state.fromDate),
            toDate = HP.getFormatedDate(state.toDate),
            accountReport = accountReport,
            totalReport = totalReport,
        )

        openPdf(context, file)
    }

    fun showPayments(
        accountReport: List<AccountReport>,
        totalReport: TotalReport
    ) {
        val file = paymentsReport(
            context = context,
            fromDate = HP.getFormatedDate(state.fromDate),
            toDate = HP.getFormatedDate(state.toDate),
            accountReport = accountReport,
            totalReport = totalReport,
        )

        openPdf(context, file)
    }

    fun showCustomers(
        accountReport: List<AccountReport>,
        totalReport: TotalReport
    ) {
        val file = customersReport(
            context = context,
            accountReport = accountReport,
            totalReport = totalReport,
        )

        openPdf(context, file)
    }

    fun showVendors(
        accountReport: List<AccountReport>,
        totalReport: TotalReport
    ) {
        val file = vendorsReport(
            context = context,
            accountReport = accountReport,
            totalReport = totalReport,
        )

        openPdf(context, file)
    }

    fun showCustomersBalanceList(
        accountReport: List<AccountReport>,
        totalReport: TotalReport
    ) {
        val file = customersBalanceListReport(
            context = context,
            fromDate = HP.getFormatedDate(state.fromDate),
            toDate = HP.getFormatedDate(state.toDate),
            accountReport = accountReport,
            totalReport = totalReport,
        )

        openPdf(context, file)
    }

    fun showIncomeStatement(
        accountReport: List<AccountReport>,
        totalReport: TotalReport
    ) {
        val file = incomeStatementReport(
            context = context,
            fromDate = HP.getFormatedDate(state.fromDate),
            toDate = HP.getFormatedDate(state.toDate),
            accountReport = accountReport,
            totalReport = totalReport,
        )

        openPdf(context, file)
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
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
            ) {
                SearchBox {
                    DateBox(
                        fromDate = state.fromDate,
                        toDate = state.toDate,
                        onFromDateChange = viewModel::onFromDateChange,
                        onToDateChange = viewModel::onToDateChange,
                    )
                }

                Column(
                    Modifier
                        .weight(1f)
                        .verticalScroll(scrollState)
                        .padding(ConstantPaddings.BODY_HORIZONTAL)
                        .imePadding(),
                ) {
                    Spacer(Modifier.height(12.dp))
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
                    Spacer(Modifier.height(12.dp))
                    ReportCard(
                        heading = "Bank statement",
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
                    Spacer(Modifier.height(12.dp))
                    ReportCard(
                        heading = "Receipts and payments",
                        subHeading = "Summary of receipts and payments",
                        content = {
                            ReceiptsAndPayments(
                                mop = state.mop,
                                username = state.username,
                                onMopChange = viewModel::onMopChange,
                                onUsernameChange = viewModel::onUsernameChange,
                                onUserIdChange = viewModel::onUserIdChange,
                                onReceiptsClick = {
                                    viewModel.onReceiptsClick { accountReport, totalReport ->
                                        showReceipts(accountReport, totalReport)
                                    }
                                },
                                onPaymentsClick = {
                                    viewModel.onPaymentsClick { accountReport, totalReport ->
                                        showPayments(accountReport, totalReport)
                                    }
                                },
                            )
                        }
                    )
                    Spacer(Modifier.height(12.dp))
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
                    Spacer(Modifier.height(12.dp))
                    ReportCard(
                        heading = "Receivable and payable",
                        subHeading = "Summary of cash receivable and payable",
                        content = {
                            ReceivableAndPayable(
                                listType = state.listType,
                                supplierName = state.supplierName,
                                customerCategory = state.customerCategory,
                                vendorCategory = state.vendorCategory,
                                onListTypeChange = viewModel::onListTypeChange,
                                onSupplierNameChange = viewModel::onSupplierNameChange,
                                onCustomerCategoryChange = viewModel::onCustomerCategoryChange,
                                onVendorCategoryChange = viewModel::onVendorCategoryChange,
                                onSupplierIdChange = viewModel::onSupplierIdChange,
                                onCustomersClick = {
                                    viewModel.onCustomersClick { accountReport, totalReport ->
                                        if (state.listType.id == 1L)
                                            showCustomers(accountReport, totalReport)
                                        else
                                            showCustomersBalanceList(accountReport, totalReport)
                                    }
                                },
                                onVendorsClick = {
                                    viewModel.onVendorsClick { accountReport, totalReport ->
                                        showVendors(accountReport, totalReport)
                                    }
                                },
                                onSupplierClick = {
                                    viewModel.onSuppliersClick { accountReport, totalReport ->
                                        if (state.listType.id == 1L)
                                            showCustomers(accountReport, totalReport)
                                        else
                                            showCustomersBalanceList(accountReport, totalReport)
                                    }
                                },
                                onCustomerCategoryClick = {
                                    viewModel.onCustomerCategoryClick { accountReport, totalReport ->
                                        if (state.listType.id == 1L)
                                            showCustomers(accountReport, totalReport)
                                        else
                                            showCustomersBalanceList(accountReport, totalReport)
                                    }
                                },
                                onVendorCategoryClick = {
                                    viewModel.onVendorCategoryClick { accountReport, totalReport ->
                                        showVendors(accountReport, totalReport)
                                    }
                                },
                            )
                        }
                    )
                    Spacer(Modifier.height(12.dp))
                    ReportCard(
                        heading = "Others",
                        subHeading = "Income Statement, cash account",
                        content = {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth(),
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Start,
                                ) {
                                    ReportButton("Income Statement") {
                                        viewModel.onIncomeStatementClick { accountReport, totalReport ->
                                            showIncomeStatement(accountReport, totalReport)
                                        }
                                    }
                                    Spacer(Modifier.width(8.dp))
                                    ReportButton("Cash Account") {
                                        viewModel.onCashAccountClick { accountReport, totalReport ->
                                            showCashAccount(accountReport, totalReport)
                                        }
                                    }
                                }
                                Spacer(Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Start,
                                ) {
                                    ReportButton("Manage Cash") {
                                        onManageCashClick()
                                    }
                                    Spacer(Modifier.width(8.dp))
                                    ReportButton("Manage Shifts") {
                                        onManageShiftsClick()
                                    }
                                }
                            }
                        }
                    )
                    Spacer(Modifier.height(12.dp))
                }
            }

            if (state.isLoading) {
                ProgressBarLayout()
            }
        }
    }
}

@Composable
private fun DateBox(
    fromDate: LocalDate,
    toDate: LocalDate,
    onFromDateChange: (LocalDate) -> Unit,
    onToDateChange: (LocalDate) -> Unit,
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
        Spacer(Modifier.height(2.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
        ) {
            ReportButton {
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
                ShowReportIcon()
            },
            onTrailingIconClick = {
                onExpensesClick()
            }
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
                ShowReportIcon()
            },
            onTrailingIconClick = {
                onSubExpensesClick()
            }
        )
        Spacer(Modifier.height(2.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
        ) {
            ReportButton("Total Expenses") {
                onTotalExpensesClick()
            }
        }
    }
}

@Composable
private fun ReceiptsAndPayments(
    mop: DropdownItem,
    username: String,
    onMopChange: (DropdownItem) -> Unit,
    onUsernameChange: (String) -> Unit,
    onUserIdChange: (Long) -> Unit,
    onReceiptsClick: () -> Unit,
    onPaymentsClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        ComboBox(
            modifier = Modifier
                .fillMaxWidth(),
            items = HP.mop,
            selectedItem = mop,
            onItemSelected = onMopChange,
            label = {
                Text("M.O.P")
            },
            addNone = true,
            noneText = "Both",
        )
        Dropdown(
            value = username,
            onValueChange = onUsernameChange,
            items = HP.users,
            onItemSelected = { dropdownItem ->
                onUserIdChange(dropdownItem.id)
            },
            label = {
                Text(text = "User")
            },
            changeIdOnEmpty = true,
        )
        Spacer(Modifier.height(2.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
        ) {
            ReportButton("Receipts") {
                onReceiptsClick()
            }
            Spacer(Modifier.width(8.dp))
            ReportButton("Payments") {
                onPaymentsClick()
            }
        }
    }
}

@Composable
private fun ReceivableAndPayable(
    listType: DropdownItem,
    supplierName: String,
    customerCategory: DropdownItem,
    vendorCategory: DropdownItem,
    onListTypeChange: (DropdownItem) -> Unit,
    onSupplierNameChange: (String) -> Unit,
    onCustomerCategoryChange: (DropdownItem) -> Unit,
    onVendorCategoryChange: (DropdownItem) -> Unit,
    onSupplierIdChange: (Long) -> Unit,
    onCustomersClick: () -> Unit,
    onVendorsClick: () -> Unit,
    onSupplierClick: () -> Unit,
    onCustomerCategoryClick: () -> Unit,
    onVendorCategoryClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
        ) {
            ReportButton("Customers") {
                onCustomersClick()
            }
            Spacer(Modifier.width(8.dp))
            ReportButton("Vendors") {
                onVendorsClick()
            }
        }
        Spacer(Modifier.height(8.dp))
        ComboBox(
            modifier = Modifier
                .fillMaxWidth(),
            items = HP.listType,
            selectedItem = listType,
            onItemSelected = onListTypeChange,
            label = {
                Text("Type")
            },
        )
        Dropdown(
            value = supplierName,
            onValueChange = onSupplierNameChange,
            items = HP.suppliers,
            onItemSelected = { dropdownItem ->
                onSupplierIdChange(dropdownItem.id)
            },
            label = {
                Text(text = "Customers by supplier")
            },
            trailingIcon = {
                ShowReportIcon {
                    onSupplierClick()
                }
            },
            changeIdOnEmpty = true,
        )
        ComboBox(
            modifier = Modifier
                .fillMaxWidth(),
            items = HP.accountCategories,
            selectedItem = customerCategory,
            onItemSelected = onCustomerCategoryChange,
            label = {
                Text("Customers by category")
            },
            addNone = true,
            trailingIcon = {
                ShowReportIcon()
            },
            onTrailingIconClick = {
                onCustomerCategoryClick()
            }
        )
        ComboBox(
            modifier = Modifier
                .fillMaxWidth(),
            items = HP.accountCategories,
            selectedItem = vendorCategory,
            onItemSelected = onVendorCategoryChange,
            label = {
                Text("Vendors by category")
            },
            addNone = true,
            trailingIcon = {
                ShowReportIcon()
            },
            onTrailingIconClick = {
                onVendorCategoryClick()
            }
        )
    }
}
