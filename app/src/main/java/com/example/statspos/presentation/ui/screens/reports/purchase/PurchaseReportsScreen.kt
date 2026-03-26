package com.example.statspos.presentation.ui.screens.reports.purchase

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.imePadding
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
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
import com.example.statspos.domain.models.reports.purchase.PurchaseBillWiseReport
import com.example.statspos.domain.models.reports.purchase.PurchaseItemsReport
import com.example.statspos.presentation.ui.components.AppIcon
import com.example.statspos.presentation.ui.components.AppIconButton
import com.example.statspos.presentation.ui.components.AppSnackbarHost
import com.example.statspos.presentation.ui.components.AppSwitch
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
import com.example.statspos.presentation.ui.screens.reports.ReportButtons
import com.example.statspos.presentation.ui.screens.reports.ReportsDateBox
import com.example.statspos.presentation.ui.screens.reports.ReportsItemnameBox
import com.example.statspos.presentation.ui.screens.reports.TodayPurchase
import com.example.statspos.presentation.ui.utils.ConstantPaddings
import com.example.statspos.presentation.ui.utils.openPdf
import com.example.statspos.presentation.viewmodels.SharedViewModel
import com.example.statspos.presentation.viewmodels.reports.PurchaseReportsViewModel
import com.example.statspos.utils.HP
import com.example.statspos.utils.UiEvent
import com.example.statspos.utils.checkEvent
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import java.time.LocalDate

private sealed class Routes : NavKey {
    @Serializable
    data object Home : Routes()

    @Serializable
    data object SearchItem : Routes()
}

@Composable
fun PurchaseReportsScreen(
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
    val viewModel = hiltViewModel<PurchaseReportsViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val event by viewModel.event.collectAsState(UiEvent.Idle)
    val snackbarHostState = remember { SnackbarHostState() }
    var showErrorDialog by remember { mutableStateOf(false) }
    var showBarcodeScanner by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
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
        purchaseBillWiseReport: List<PurchaseBillWiseReport>,
        totalReport: TotalReport
    ) {
        val file = purchaseBillWiseReport(
            context = context,
            fromDate = HP.getFormatedDate(state.fromDate),
            toDate = HP.getFormatedDate(state.toDate),
            billWiseReport = purchaseBillWiseReport,
            totalReport = totalReport,
        )

        openPdf(context, file)
    }

    fun showItemsReport(
        purchaseItemsReport: List<PurchaseItemsReport>,
        totalReport: TotalReport
    ) {
        if (state.sum) {
            val file = purchaseItemsSumReport(
                context = context,
                fromDate = HP.getFormatedDate(state.fromDate),
                toDate = HP.getFormatedDate(state.toDate),
                itemsReport = purchaseItemsReport,
                totalReport = totalReport,
            )

            openPdf(context, file)
        } else {
            val file = purchaseItemsReport(
                context = context,
                fromDate = HP.getFormatedDate(state.fromDate),
                toDate = HP.getFormatedDate(state.toDate),
                itemsReport = purchaseItemsReport,
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
                title = "Purchase Reports",
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
                    items = HP.purchaseType,
                    selectedItem = state.purchaseType,
                    onItemSelected = { item ->
                        viewModel.onPurchaseTypeChange(item)
                    },
                    label = {
                        Text(text = "Purchase Type")
                    },
                    addNone = true,
                    noneText = "Both",
                )
                ComboBox(
                    modifier = Modifier
                        .fillMaxWidth(),
                    items = HP.purchaseOn,
                    selectedItem = state.purchaseOn,
                    onItemSelected = { item ->
                        viewModel.onPurchaseOnChange(item)
                    },
                    label = {
                        Text(text = "Purchase On")
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
                        .verticalScroll(scrollState)
                        .imePadding(),
                ) {
                    Spacer(Modifier.height(12.dp))
                    TodayPurchase(
                        cashPurchase = state.mainReport.cashPurchase,
                        creditPurchase = state.mainReport.creditPurchase,
                        purchaseReturns = state.mainReport.purchaseReturns,
                        totalPurchase = state.mainReport.totalPurchase,
                        totalPurchaseBills = state.mainReport.totalPurchaseBills,
                    )
                    Spacer(Modifier.height(12.dp))
                    TrendChart(
                        chartFor = "Purchase",
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
                                    accountCategoryName = state.accountCategoryName,
                                    warehouseName = state.warehouseName,
                                    supplierName = state.supplierName,
                                    username = state.username,
                                    onCategoryNameChange = viewModel::onCategoryNameChange,
                                    onSubCategoryNameChange = viewModel::onSubCategoryNameChange,
                                    onVendorNameChange = viewModel::onVendorNameChange,
                                    onAccountCategoryNameChange = viewModel::onAccountCategoryNameChange,
                                    onWarehouseNameChange = viewModel::onWarehouseNameChange,
                                    onSupplierNameChange = viewModel::onSupplierNameChange,
                                    onUsernameChange = viewModel::onUsernameChange,
                                    onCategoryIdChange = viewModel::onCategoryIdChange,
                                    onSubCategoryIdChange = viewModel::onSubCategoryIdChange,
                                    onVendorIdChange = viewModel::onVendorIdChange,
                                    onAccountCategoryIdChange = viewModel::onAccountCategoryIdChange,
                                    onWarehouseIdChange = viewModel::onWarehouseIdChange,
                                    onSupplierIdChange = viewModel::onSupplierIdChange,
                                    onUserIdChange = viewModel::onUserIdChange,
                                    onCategoryClick = {
                                        viewModel.onCategoryClick { purchaseItemsReport, totalReport ->
                                            showItemsReport(purchaseItemsReport, totalReport)
                                        }
                                    },
                                    onSubCategoryClick = {
                                        viewModel.onSubCategoryClick { purchaseItemsReport, totalReport ->
                                            showItemsReport(purchaseItemsReport, totalReport)
                                        }
                                    },
                                    onVendorClick = {
                                        viewModel.onVendorClick { purchaseItemsReport, totalReport ->
                                            showItemsReport(purchaseItemsReport, totalReport)
                                        }
                                    },
                                    onAccountCategoryClick = {
                                        viewModel.onAccountCategoryClick { purchaseBillWiseReport, totalReport ->
                                            showBillWiseReport(purchaseBillWiseReport, totalReport)
                                        }
                                    },
                                    onWarehouseClick = {
                                        viewModel.onWarehouseClick { purchaseBillWiseReport, totalReport ->
                                            showBillWiseReport(purchaseBillWiseReport, totalReport)
                                        }
                                    },
                                    onSupplierClick = {
                                        viewModel.onSupplierClick { purchaseBillWiseReport, totalReport ->
                                            showBillWiseReport(purchaseBillWiseReport, totalReport)
                                        }
                                    },
                                    onUserClick = {
                                        viewModel.onUserClick { purchaseBillWiseReport, totalReport ->
                                            showBillWiseReport(purchaseBillWiseReport, totalReport)
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
//                                        viewModel.onItemnameChange("")
                                        viewModel.onItemClick { purchaseItemsReport, totalReport ->
                                            showItemsReport(purchaseItemsReport, totalReport)
                                        }
                                    },
                                    onBarcodeClick = {
                                        showBarcodeScanner = true
                                    },
                                    onSearchItemClick = onSearchItemClick,
                                    onItemClick = {
                                        viewModel.onItemClick { purchaseItemsReport, totalReport ->
                                            showItemsReport(purchaseItemsReport, totalReport)
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
    accountCategoryName: String,
    warehouseName: String,
    supplierName: String,
    username: String,
    onCategoryNameChange: (String) -> Unit,
    onSubCategoryNameChange: (String) -> Unit,
    onVendorNameChange: (String) -> Unit,
    onAccountCategoryNameChange: (String) -> Unit,
    onWarehouseNameChange: (String) -> Unit,
    onSupplierNameChange: (String) -> Unit,
    onUsernameChange: (String) -> Unit,
    onCategoryIdChange: (Long) -> Unit,
    onSubCategoryIdChange: (Long) -> Unit,
    onVendorIdChange: (Long) -> Unit,
    onAccountCategoryIdChange: (Long) -> Unit,
    onWarehouseIdChange: (Long) -> Unit,
    onSupplierIdChange: (Long) -> Unit,
    onUserIdChange: (Long) -> Unit,
    onCategoryClick: () -> Unit,
    onSubCategoryClick: () -> Unit,
    onVendorClick: () -> Unit,
    onAccountCategoryClick: () -> Unit,
    onWarehouseClick: () -> Unit,
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
            value = accountCategoryName,
            onValueChange = onAccountCategoryNameChange,
            items = HP.accountCategories,
            onItemSelected = { dropdownItem ->
                onAccountCategoryIdChange(dropdownItem.id)
            },
            label = {
                Text(text = "Vendor Category")
            },
            trailingIcon = {
                ShowReportIcon {
                    onAccountCategoryClick()
                }
            },
            changeIdOnEmpty = true,
        )
        Dropdown(
            value = warehouseName,
            onValueChange = onWarehouseNameChange,
            items = HP.warehouses,
            onItemSelected = { dropdownItem ->
                onWarehouseIdChange(dropdownItem.id)
            },
            label = {
                Text(text = "Warehouse")
            },
            trailingIcon = {
                ShowReportIcon {
                    onWarehouseClick()
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
