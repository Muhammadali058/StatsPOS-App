package com.example.statspos.presentation.ui.screens.reports.sales

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.example.statspos.R
import com.example.statspos.domain.models.reports.TotalReport
import com.example.statspos.domain.models.reports.sales.SalesBillWiseReport
import com.example.statspos.domain.models.reports.sales.SalesItemsReport
import com.example.statspos.presentation.ui.components.AppIcon
import com.example.statspos.presentation.ui.components.AppIconButton
import com.example.statspos.presentation.ui.components.AppSnackbarHost
import com.example.statspos.presentation.ui.components.AppSwitch
import com.example.statspos.presentation.ui.components.AppText
import com.example.statspos.presentation.ui.components.AutoCompleteItemsTextbox
import com.example.statspos.presentation.ui.components.BarcodeScannerDialog
import com.example.statspos.presentation.ui.components.BottomSheet
import com.example.statspos.presentation.ui.components.ComboBox
import com.example.statspos.presentation.ui.components.DateTextbox
import com.example.statspos.presentation.ui.components.Dropdown
import com.example.statspos.presentation.ui.components.ErrorDialog
import com.example.statspos.presentation.ui.components.ProgressBarLayout
import com.example.statspos.presentation.ui.components.ReportButton
import com.example.statspos.presentation.ui.components.ReportCard
import com.example.statspos.presentation.ui.components.ShowReportIcon
import com.example.statspos.presentation.ui.components.SubDropdown
import com.example.statspos.presentation.ui.components.TopAppBar
import com.example.statspos.presentation.ui.components.TrendChart
import com.example.statspos.presentation.ui.screens.items.SearchItemsScreen
import com.example.statspos.presentation.ui.screens.reports.TodayBox
import com.example.statspos.presentation.ui.screens.reports.TodaySales
import com.example.statspos.presentation.ui.utils.ConstantPaddings
import com.example.statspos.presentation.ui.utils.PdfPreviewScreen
import com.example.statspos.presentation.ui.utils.openPdf
import com.example.statspos.presentation.viewmodels.SharedViewModel
import com.example.statspos.presentation.viewmodels.reports.SalesReportsViewModel
import com.example.statspos.utils.HP
import com.example.statspos.utils.UiEvent
import com.example.statspos.utils.checkEvent
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import java.io.File
import java.time.LocalDate

private sealed class Routes : NavKey {
    @Serializable
    data object Home : Routes()

    @Serializable
    data object SearchItem : Routes()

    @Serializable
    data class ViewReport(val filePath: String) : Routes()
}

@Composable
fun SalesReportsScreen(
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
            entry<Routes.ViewReport> { key ->
                val file = File(key.filePath)
                PdfPreviewScreen(
                    file = file,
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
    val viewModel = hiltViewModel<SalesReportsViewModel>()
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
        salesBillWiseReport: List<SalesBillWiseReport>,
        totalReport: TotalReport
    ) {
        val file = salesBillWiseReport(
            context = context,
            fromDate = HP.getFormatedDate(state.fromDate),
            toDate = HP.getFormatedDate(state.toDate),
            billWiseReport = salesBillWiseReport,
            totalReport = totalReport,
        )

        openPdf(context, file)
    }

    fun showItemsReport(
        salesItemsReport: List<SalesItemsReport>,
        totalReport: TotalReport
    ) {
        if (state.sum) {
            val file = salesItemsSumReport(
                context = context,
                fromDate = HP.getFormatedDate(state.fromDate),
                toDate = HP.getFormatedDate(state.toDate),
                itemsReport = salesItemsReport,
                totalReport = totalReport,
            )

            openPdf(context, file)
        } else {
            val file = salesItemsReport(
                context = context,
                fromDate = HP.getFormatedDate(state.fromDate),
                toDate = HP.getFormatedDate(state.toDate),
                itemsReport = salesItemsReport,
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
                title = "Sales Reports",
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
                    Spacer(Modifier.height(16.dp))
                    TodaySales(
                        cashSales = state.mainReport.cashSales,
                        creditSales = state.mainReport.creditSales,
                        salesReturns = state.mainReport.salesReturns,
                        totalSales = state.mainReport.totalSales,
                        totalSalesBills = state.mainReport.totalSalesBills,
                    )
                    Spacer(Modifier.height(16.dp))
                    TrendChart(
                        chartFor = "Sales",
                        chartReport = state.chartReport,
                        chartDuration = state.chartDuration,
                        onChartDurationChange = viewModel::onChartDurationChange
                    )
                    Spacer(Modifier.height(16.dp))

                    ReportCard(
                        heading = "Detailed Reports",
                        subHeading = "Save & share pdf reports"
                    )
                    {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(4.dp)
                        ){
                            DateBox(
                                fromDate = state.fromDate,
                                toDate = state.toDate,
                                onFromDateChange = viewModel::onFromDateChange,
                                onToDateChange = viewModel::onToDateChange,
                                onFilterClick = {
                                    showBottomSheet = true
                                },
                            )
                            Spacer(Modifier.height(8.dp))
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
                            Spacer(Modifier.height(8.dp))
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
private fun DateBox(
    fromDate: LocalDate,
    toDate: LocalDate,
    onFromDateChange: (LocalDate) -> Unit,
    onToDateChange: (LocalDate) -> Unit,
    onFilterClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
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
            Spacer(Modifier.width(4.dp))
            AppIconButton(
                onClick = {
                    onFilterClick()
                },
                icon = Icons.Default.FilterList,
                buttonSize = 32.dp,
                size = 26.dp
            )
        }
    }
}

@Composable
private fun ReportButtons(
    onTotalBillsClick: () -> Unit,
    onTotalItemsClick: () -> Unit,
    onFilterReportClick: () -> Unit,
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(scrollState),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ReportButton("Total Bills", Modifier.width(120.dp)) {
                onTotalBillsClick()
            }
            Spacer(Modifier.width(8.dp))
            ReportButton("Total Items", Modifier.width(120.dp)) {
                onTotalItemsClick()
            }
            Spacer(Modifier.width(8.dp))
            ReportButton("Filter Report", Modifier.width(120.dp)) {
                onFilterReportClick()
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
    onItemClick: () -> Unit,
    sum: Boolean,
    onSumChange: (Boolean) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
        ) {
            Column(
                modifier = Modifier
                    .weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                AutoCompleteItemsTextbox(
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
                    ),
                    padding = PaddingValues(top = 4.dp),
                )
                ReportButton(
                    modifier = Modifier.offset(y = (-1).dp),
                    shape = RoundedCornerShape(bottomEnd = 4.dp, bottomStart = 4.dp)
                ) {
                    onItemClick()
                }
            }
            Column {
                Row {
                    Spacer(Modifier.width(4.dp))
                    AppIconButton(
                        modifier = Modifier
                            .padding(top = 8.dp),
                        onClick = {
                            onBarcodeClick()
                        },
                        icon = R.drawable.ic_barcode,
                        buttonSize = 32.dp,
                        size = 26.dp
                    )
                    Spacer(Modifier.width(4.dp))
                    AppIconButton(
                        modifier = Modifier
                            .padding(top = 8.dp),
                        onClick = {
                            onSearchItemClick()
                        },
                        icon = Icons.Default.Search,
                        buttonSize = 32.dp,
                        size = 26.dp
                    )
                }
                Spacer(Modifier.height(16.dp))
                AppSwitch(
                    checked = sum,
                    onCheckedChange = onSumChange,
                    label = "Sum"
                )
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

@Composable
private fun TodaySales1(
    modifier: Modifier = Modifier,
    cashSales: Double = 0.0,
    creditSales: Double = 0.0,
    salesReturns: Double = 0.0,
    totalSales: Double = 0.0,
    totalSalesBills: Int = 0,
) {
    ReportCard(
        modifier = modifier,
        heading = "Today",
        subHeading = "Summary of daily sales",
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
        ) {
            TodayBox(
                modifier = Modifier
                    .weight(1f),
                text = "Cash Sales",
                value = "Rs.${HP.formatDecimal(cashSales, numberOfDecimals = 0)}",
            )
            TodayBox(
                modifier = Modifier
                    .weight(1f),
                text = "Credit Sales",
                value = "Rs.${HP.formatDecimal(creditSales, numberOfDecimals = 0)}",
            )
            TodayBox(
                modifier = Modifier
                    .weight(1f),
                text = "Returns",
                value = "Rs.${HP.formatDecimal(salesReturns, numberOfDecimals = 0)}",
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth(),
        ) {
            TodayBox(
                modifier = Modifier
                    .weight(1f),
                text = "Total Sales",
                value = "Rs.${HP.formatDecimal(totalSales, numberOfDecimals = 0)}",
            )
            TodayBox(
                modifier = Modifier
                    .weight(1f),
                text = "Total Invoices",
                value = totalSalesBills.toString(),
            )
        }
    }
}
