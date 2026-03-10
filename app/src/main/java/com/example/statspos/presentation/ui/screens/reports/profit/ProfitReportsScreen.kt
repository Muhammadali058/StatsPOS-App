package com.example.statspos.presentation.ui.screens.reports.profit

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.example.statspos.domain.models.reports.TotalReport
import com.example.statspos.domain.models.reports.profit.ProfitBillWiseReport
import com.example.statspos.domain.models.reports.profit.ProfitItemsReport
import com.example.statspos.presentation.ui.components.AppSnackbarHost
import com.example.statspos.presentation.ui.components.BarcodeScannerDialog
import com.example.statspos.presentation.ui.components.BottomSheet
import com.example.statspos.presentation.ui.components.ComboBox
import com.example.statspos.presentation.ui.components.Dropdown
import com.example.statspos.presentation.ui.components.ErrorDialog
import com.example.statspos.presentation.ui.components.ProgressBarLayout
import com.example.statspos.presentation.ui.components.ReportCard
import com.example.statspos.presentation.ui.components.ShowReportIcon
import com.example.statspos.presentation.ui.components.SubDropdown
import com.example.statspos.presentation.ui.components.TopAppBar
import com.example.statspos.presentation.ui.components.TrendChart
import com.example.statspos.presentation.ui.screens.items.SearchItemsScreen
import com.example.statspos.presentation.ui.screens.reports.ReportButtons
import com.example.statspos.presentation.ui.screens.reports.ReportsDateBox
import com.example.statspos.presentation.ui.screens.reports.ReportsItemnameBox
import com.example.statspos.presentation.ui.screens.reports.TodayProfit
import com.example.statspos.presentation.ui.screens.reports.TodaySales
import com.example.statspos.presentation.ui.screens.reports.sales.salesItemsReport
import com.example.statspos.presentation.ui.screens.reports.sales.salesItemsSumReport
import com.example.statspos.presentation.ui.utils.ConstantPaddings
import com.example.statspos.presentation.ui.utils.openPdf
import com.example.statspos.presentation.viewmodels.SharedViewModel
import com.example.statspos.presentation.viewmodels.reports.ProfitReportsViewModel
import com.example.statspos.utils.HP
import com.example.statspos.utils.UiEvent
import com.example.statspos.utils.checkEvent
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

private sealed class Routes : NavKey {
    @Serializable
    data object Home : Routes()

    @Serializable
    data object SearchItem : Routes()
}

@Composable
fun ProfitReportsScreen(
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
                    onBack = {
                        onBack()
                    },
                    onSearchItemClick = {
                        navigate(Routes.SearchItem)
                    },
                )
            }
            entry<Routes.SearchItem> { key ->
                SearchItemsScreen(
                    sharedViewModel = sharedViewModel,
                    onBack = {
                        backStack.removeLastOrNull()
                    }
                )
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Home(
    sharedViewModel: SharedViewModel,
    onBack: () -> Unit,
    onSearchItemClick: () -> Unit,
) {
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val viewModel = hiltViewModel<ProfitReportsViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val event by viewModel.event.collectAsState(UiEvent.Idle)
    val snackbarHostState = remember { SnackbarHostState() }
    var showErrorDialog by remember { mutableStateOf(false) }
    var showBarcodeScanner by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showBottomSheet by remember { mutableStateOf(false) }

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

    // For search item
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

    fun showBillWiseReport(
        profitBillWiseReport: List<ProfitBillWiseReport>,
        totalReport: TotalReport
    ) {
        val file = profitBillWiseReport(
            context = context,
            fromDate = HP.getFormatedDate(state.fromDate),
            toDate = HP.getFormatedDate(state.toDate),
            billWiseReport = profitBillWiseReport,
            totalReport = totalReport,
        )

        openPdf(context, file)
    }

    fun showItemsReport(
        profitItemsReport: List<ProfitItemsReport>,
        totalReport: TotalReport
    ) {
        if (state.sum) {
            val file = profitItemsSumReport(
                context = context,
                fromDate = HP.getFormatedDate(state.fromDate),
                toDate = HP.getFormatedDate(state.toDate),
                itemsReport = profitItemsReport,
                totalReport = totalReport,
            )

            openPdf(context, file)
        } else {
            val file = profitItemsReport(
                context = context,
                fromDate = HP.getFormatedDate(state.fromDate),
                toDate = HP.getFormatedDate(state.toDate),
                itemsReport = profitItemsReport,
                totalReport = totalReport,
            )

            openPdf(context, file)
        }
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
                title = "Profit Reports",
            )
        }
    ) { innerPadding ->

        // Bottom Sheet
        if (showBottomSheet) {
            BottomSheet(
                modifier = Modifier
                    .fillMaxWidth(),
                sheetState = sheetState,
                onDismissRequest = {
                    showBottomSheet = false
                },
            ) {
                ComboBox(
                    modifier = Modifier
                        .fillMaxWidth(),
                    items = HP.salesType,
                    selectedItem = state.salesType,
                    onItemSelected = { item ->
                        viewModel.onSalesTypeChange(item)
                    },
                    label = {
                        Text(text = "Sales Type")
                    },
                    addNone = true,
                    noneText = "Both",
                )
                ComboBox(
                    modifier = Modifier
                        .fillMaxWidth(),
                    items = HP.salesOn,
                    selectedItem = state.salesOn,
                    onItemSelected = { item ->
                        viewModel.onSalesOnChange(item)
                    },
                    label = {
                        Text(text = "Sales On")
                    },
                    addNone = true,
                    noneText = "Both",
                )
                ComboBox(
                    modifier = Modifier
                        .fillMaxWidth(),
                    items = HP.mop,
                    selectedItem = state.mop,
                    onItemSelected = { item ->
                        viewModel.onMOPChange(item)
                    },
                    label = {
                        Text(text = "M.O.P")
                    },
                    addNone = true,
                    noneText = "Both",
                )
                ComboBox(
                    modifier = Modifier
                        .fillMaxWidth(),
                    items = HP.salesRetailType,
                    selectedItem = state.salesRetailType,
                    onItemSelected = { item ->
                        viewModel.onSalesRetailTypeChange(item)
                    },
                    label = {
                        Text(text = "Type")
                    },
                    addNone = true,
                    noneText = "Both",
                )

                Button(onClick = {
                    scope.launch { sheetState.hide() }.invokeOnCompletion {
                        if (!sheetState.isVisible) {
                            showBottomSheet = false
                        }
                    }
                }) {
                    Text("OK")
                }
            }
        }

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
                    Spacer(Modifier.height(12.dp))
                    TodayProfit(
                        grossProfit = state.mainReport.grossProfit,
                        expenses = state.mainReport.expenses,
                        margin = state.mainReport.margin,
                        netProfit = state.mainReport.netProfit,
                    )
                    Spacer(Modifier.height(12.dp))
                    TrendChart(
                        chartFor = "Profit",
                        chartReport = state.chartReport,
                        chartDuration = state.chartDuration,
                        onChartDurationChange = viewModel::onChartDurationChange
                    )
                    Spacer(Modifier.height(12.dp))
                    ReportCard(
                        heading = "Detailed Reports",
                        subHeading = "Save & share pdf reports",
                        content = {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(4.dp)
                            ){
                                ReportsDateBox(
                                    fromDate = state.fromDate,
                                    toDate = state.toDate,
                                    onFromDateChange = viewModel::onFromDateChange,
                                    onToDateChange = viewModel::onToDateChange,
                                    onFilterClick = {
                                        showBottomSheet = true
                                    },
                                )
                                Spacer(Modifier.height(4.dp))
                                ReportButtons(
                                    onTotalBillsClick = {
                                        viewModel.onTotalBillsClick { salesBillWiseReport, totalReport ->
                                            showBillWiseReport(salesBillWiseReport, totalReport)
                                        }
                                    },
                                    onTotalItemsClick = {
                                        viewModel.onTotalItemsClick { salesItemsReport, totalReport ->
                                            showItemsReport(salesItemsReport, totalReport)
                                        }
                                    },
                                    onFilterReportClick = {
                                        viewModel.onFilterClick { salesItemsReport, totalReport ->
                                            showItemsReport(salesItemsReport, totalReport)
                                        }
                                    },
                                )
                                Spacer(Modifier.height(6.dp))
                                Dropdowns(
                                    categoryName = state.categoryName,
                                    subCategoryName = state.subCategoryName,
                                    categoryId = state.categoryId,
                                    vendorName = state.vendorName,
                                    customerName = state.customerName,
                                    accountCategoryName = state.accountCategoryName,
                                    supplierName = state.supplierName,
                                    username = state.username,
                                    onCategoryNameChange = viewModel::onCategoryNameChange,
                                    onSubCategoryNameChange = viewModel::onSubCategoryNameChange,
                                    onVendorNameChange = viewModel::onVendorNameChange,
                                    onCustomerNameChange = viewModel::onCustomerNameChange,
                                    onAccountCategoryNameChange = viewModel::onAccountCategoryNameChange,
                                    onSupplierNameChange = viewModel::onSupplierNameChange,
                                    onUsernameChange = viewModel::onUsernameChange,
                                    onCategoryIdChange = viewModel::onCategoryIdChange,
                                    onSubCategoryIdChange = viewModel::onSubCategoryIdChange,
                                    onVendorIdChange = viewModel::onVendorIdChange,
                                    onCustomerIdChange = viewModel::onCustomerIdChange,
                                    onAccountCategoryIdChange = viewModel::onAccountCategoryIdChange,
                                    onSupplierIdChange = viewModel::onSupplierIdChange,
                                    onUserIdChange = viewModel::onUserIdChange,
                                    onCategoryClick = {
                                        viewModel.onCategoryClick { salesItemsReport, totalReport ->
                                            showItemsReport(salesItemsReport, totalReport)
                                        }
                                    },
                                    onSubCategoryClick = {
                                        viewModel.onSubCategoryClick { salesItemsReport, totalReport ->
                                            showItemsReport(salesItemsReport, totalReport)
                                        }
                                    },
                                    onVendorClick = {
                                        viewModel.onVendorClick { salesItemsReport, totalReport ->
                                            showItemsReport(salesItemsReport, totalReport)
                                        }
                                    },
                                    onCustomerClick = {
                                        viewModel.onCustomerClick { salesBillWiseReport, totalReport ->
                                            showBillWiseReport(salesBillWiseReport, totalReport)
                                        }
                                    },
                                    onAccountCategoryClick = {
                                        viewModel.onAccountCategoryClick { salesBillWiseReport, totalReport ->
                                            showBillWiseReport(salesBillWiseReport, totalReport)
                                        }
                                    },
                                    onSupplierClick = {
                                        viewModel.onSupplierClick { salesBillWiseReport, totalReport ->
                                            showBillWiseReport(salesBillWiseReport, totalReport)
                                        }
                                    },
                                    onUserClick = {
                                        viewModel.onUserClick { salesBillWiseReport, totalReport ->
                                            showBillWiseReport(salesBillWiseReport, totalReport)
                                        }
                                    },
                                )
                                ReportsItemnameBox(
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
                                    onSearchItemClick = onSearchItemClick,
                                    onItemClick = {
                                        viewModel.onItemClick { salesItemsReport, totalReport ->
                                            showItemsReport(salesItemsReport, totalReport)
                                        }
                                    },
                                    sum = state.sum,
                                    onSumChange = viewModel::onSumChange,
                                )
                                Spacer(Modifier.height(4.dp))
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
private fun Dropdowns(
    categoryName: String,
    subCategoryName: String,
    categoryId: Long,
    vendorName: String,
    customerName: String,
    accountCategoryName: String,
    supplierName: String,
    username: String,
    onCategoryNameChange: (String) -> Unit,
    onSubCategoryNameChange: (String) -> Unit,
    onVendorNameChange: (String) -> Unit,
    onCustomerNameChange: (String) -> Unit,
    onAccountCategoryNameChange: (String) -> Unit,
    onSupplierNameChange: (String) -> Unit,
    onUsernameChange: (String) -> Unit,
    onCategoryIdChange: (Long) -> Unit,
    onSubCategoryIdChange: (Long) -> Unit,
    onVendorIdChange: (Long) -> Unit,
    onCustomerIdChange: (Long) -> Unit,
    onAccountCategoryIdChange: (Long) -> Unit,
    onSupplierIdChange: (Long) -> Unit,
    onUserIdChange: (Long) -> Unit,
    onCategoryClick: () -> Unit,
    onSubCategoryClick: () -> Unit,
    onVendorClick: () -> Unit,
    onCustomerClick: () -> Unit,
    onAccountCategoryClick: () -> Unit,
    onSupplierClick: () -> Unit,
    onUserClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Dropdown(
            value = categoryName,
            onValueChange = onCategoryNameChange,
            items = HP.categories,
            onItemSelected = { dropdownItem ->
                onCategoryIdChange(dropdownItem.id)
            },
            label = {
                Text(text = "Category")
            },
            trailingIcon = {
                ShowReportIcon {
                    onCategoryClick()
                }
            },
            changeIdOnEmpty = true,
        )
        SubDropdown(
            value = subCategoryName,
            onValueChange = onSubCategoryNameChange,
            items = HP.subCategories,
            mainId = categoryId,
            onItemSelected = { dropdownItem ->
                onSubCategoryIdChange(dropdownItem.id)
            },
            label = {
                Text(text = "Sub-Category")
            },
            trailingIcon = {
                ShowReportIcon {
                    onSubCategoryClick()
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
            value = accountCategoryName,
            onValueChange = onAccountCategoryNameChange,
            items = HP.accountCategories,
            onItemSelected = { dropdownItem ->
                onAccountCategoryIdChange(dropdownItem.id)
            },
            label = {
                Text(text = "Customer Category")
            },
            trailingIcon = {
                ShowReportIcon {
                    onAccountCategoryClick()
                }
            },
            changeIdOnEmpty = true,
        )
        Dropdown(
            value = supplierName,
            onValueChange = onSupplierNameChange,
            items = HP.suppliers,
            onItemSelected = { dropdownItem ->
                onSupplierIdChange(dropdownItem.id)
            },
            label = {
                Text(text = "Supplier")
            },
            trailingIcon = {
                ShowReportIcon {
                    onSupplierClick()
                }
            },
            changeIdOnEmpty = true,
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
            trailingIcon = {
                ShowReportIcon {
                    onUserClick()
                }
            },
            changeIdOnEmpty = true,
        )
    }
}
