package com.graphees.statspos.presentation.ui.screens.reports.stock

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
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
import com.graphees.statspos.R
import com.graphees.statspos.domain.models.DropdownItem
import com.graphees.statspos.domain.models.reports.TotalReport
import com.graphees.statspos.domain.models.reports.stock.StockItemsReport
import com.graphees.statspos.presentation.ui.components.AppIconButton
import com.graphees.statspos.presentation.ui.components.AppSnackbarHost
import com.graphees.statspos.presentation.ui.components.AutoCompleteItemsTextbox
import com.graphees.statspos.presentation.ui.components.BarcodeScannerDialog
import com.graphees.statspos.presentation.ui.components.ComboBox
import com.graphees.statspos.presentation.ui.components.Dropdown
import com.graphees.statspos.presentation.ui.components.ErrorDialog
import com.graphees.statspos.presentation.ui.components.ProgressBarLayout
import com.graphees.statspos.presentation.ui.components.ReportButton
import com.graphees.statspos.presentation.ui.components.ReportCard
import com.graphees.statspos.presentation.ui.components.ShowReportIcon
import com.graphees.statspos.presentation.ui.components.SubDropdown
import com.graphees.statspos.presentation.ui.components.TopAppBar
import com.graphees.statspos.presentation.ui.screens.items.SearchItemsScreen
import com.graphees.statspos.presentation.ui.screens.reports.TodayStock
import com.graphees.statspos.presentation.ui.utils.ConstantPaddings
import com.graphees.statspos.presentation.ui.utils.openPdf
import com.graphees.statspos.presentation.viewmodels.SharedViewModel
import com.graphees.statspos.presentation.viewmodels.reports.StockReportsViewModel
import com.graphees.statspos.utils.HP
import com.graphees.statspos.utils.UiEvent
import com.graphees.statspos.utils.checkEvent
import kotlinx.serialization.Serializable

private sealed class Routes : NavKey {
    @Serializable
    data object Home : Routes()

    @Serializable
    data object SearchItem : Routes()
}

@Composable
fun StockReportsScreen(
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
    val viewModel = hiltViewModel<StockReportsViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val event by viewModel.event.collectAsState(UiEvent.Idle)
    val snackbarHostState = remember { SnackbarHostState() }
    var showErrorDialog by remember { mutableStateOf(false) }
    var showBarcodeScanner by remember { mutableStateOf(false) }
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

    fun showItemsReport(
        stockItemsReport: List<StockItemsReport>,
        totalReport: TotalReport
    ) {
        val file = stockItemsReport(
            context = context,
            costHeading = state.costHeading,
            itemsReport = stockItemsReport,
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
                title = "Stock Reports",
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
                        .verticalScroll(scrollState)
                        .imePadding(),
                ) {
                    Spacer(Modifier.height(12.dp))
                    TodayStock(
                        stockAtCost = state.mainReport.stockAtCost,
                        stockAtRetail = state.mainReport.stockAtRetail,
                        stockAtWholesale = state.mainReport.stockAtWholesale,
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
                            ) {
                                ReportButtons(
                                    onTotalStockClick = {
                                        viewModel.onTotalClick { stockItemsReport, totalReport ->
                                            showItemsReport(stockItemsReport, totalReport)
                                        }
                                    },
                                    onFilterReportClick = {
                                        viewModel.onFilterClick { stockItemsReport, totalReport ->
                                            showItemsReport(stockItemsReport, totalReport)
                                        }
                                    },
                                )
                                Spacer(Modifier.height(12.dp))
                                Dropdowns(
                                    stockShowing = state.stockShowing,
                                    stockAt = state.stockAt,
                                    stockExpiry = state.stockExpiry,
                                    stockType = state.stockType,
                                    categoryName = state.categoryName,
                                    subCategoryName = state.subCategoryName,
                                    categoryId = state.categoryId,
                                    vendorName = state.vendorName,
                                    warehouse = state.warehouse,
                                    onCategoryNameChange = viewModel::onCategoryNameChange,
                                    onSubCategoryNameChange = viewModel::onSubCategoryNameChange,
                                    onVendorNameChange = viewModel::onVendorNameChange,
                                    onWarehouseChange = viewModel::onWarehouseChange,
                                    onStockShowingChange = viewModel::onStockShowingChange,
                                    onStockAtChange = viewModel::onStockAtChange,
                                    onStockExpiryChange = viewModel::onStockExpiryChange,
                                    onStockTypeChange = viewModel::onStockTypeChange,
                                    onCategoryIdChange = viewModel::onCategoryIdChange,
                                    onSubCategoryIdChange = viewModel::onSubCategoryIdChange,
                                    onVendorIdChange = viewModel::onVendorIdChange,
                                    onCategoryClick = {
                                        viewModel.onCategoryClick { stockItemsReport, totalReport ->
                                            showItemsReport(stockItemsReport, totalReport)
                                        }
                                    },
                                    onSubCategoryClick = {
                                        viewModel.onSubCategoryClick { stockItemsReport, totalReport ->
                                            showItemsReport(stockItemsReport, totalReport)
                                        }
                                    },
                                    onVendorClick = {
                                        viewModel.onVendorClick { stockItemsReport, totalReport ->
                                            showItemsReport(stockItemsReport, totalReport)
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
//                                        viewModel.onItemnameChange("")
                                        viewModel.onItemClick { stockItemsReport, totalReport ->
                                            showItemsReport(stockItemsReport, totalReport)
                                        }
                                    },
                                    onBarcodeClick = {
                                        showBarcodeScanner = true
                                    },
                                    onSearchItemClick = onSearchItemClick,
                                    onItemClick = {
                                        viewModel.onItemClick { stockItemsReport, totalReport ->
                                            showItemsReport(stockItemsReport, totalReport)
                                        }
                                    },
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
private fun ReportButtons(
    onTotalStockClick: () -> Unit,
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
            ReportButton("Total Stock", Modifier.width(120.dp)) {
                onTotalStockClick()
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
                    onGoClick = onSearchClick,
                    label = {
                        Text(
                            text = "Select Item"
                        )
                    },
                    trailingIcon = {
                        ShowReportIcon {
                            onEndIconClick(value)
                        }
//                        IconButton(onClick = {
//                            onEndIconClick(value)
//                        }) {
//                            AppIcon(
//                                icon = Icons.Default.Clear,
//                                size = 20.dp
//                            )
//                        }
                    },
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Go
                    ),
                    padding = PaddingValues(top = 4.dp),
                )
//                ReportButton(
//                    modifier = Modifier.offset(y = (-1).dp),
//                    shape = RoundedCornerShape(bottomEnd = 4.dp, bottomStart = 4.dp)
//                ) {
//                    onItemClick()
//                }
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
            }
        }
    }
}

@Composable
private fun Dropdowns(
    stockShowing: DropdownItem,
    stockAt: DropdownItem,
    stockExpiry: DropdownItem,
    stockType: DropdownItem,
    categoryName: String,
    subCategoryName: String,
    categoryId: Long,
    vendorName: String,
    warehouse: DropdownItem,
    onCategoryNameChange: (String) -> Unit,
    onSubCategoryNameChange: (String) -> Unit,
    onVendorNameChange: (String) -> Unit,
    onWarehouseChange: (DropdownItem) -> Unit,
    onStockShowingChange: (DropdownItem) -> Unit,
    onStockAtChange: (DropdownItem) -> Unit,
    onStockExpiryChange: (DropdownItem) -> Unit,
    onStockTypeChange: (DropdownItem) -> Unit,
    onCategoryIdChange: (Long) -> Unit,
    onSubCategoryIdChange: (Long) -> Unit,
    onVendorIdChange: (Long) -> Unit,
    onCategoryClick: () -> Unit,
    onSubCategoryClick: () -> Unit,
    onVendorClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
        ) {
            ComboBox(
                modifier = Modifier
                    .fillMaxWidth(0.5f),
                items = HP.stockShowing,
                selectedItem = stockShowing,
                onItemSelected = onStockShowingChange,
                label = {
                    Text(text = "Showing")
                },
                showEndIcon = false,
            )
            Spacer(Modifier.width(8.dp))
            ComboBox(
                modifier = Modifier
                    .fillMaxWidth(),
                items = HP.stockAt,
                selectedItem = stockAt,
                onItemSelected = onStockAtChange,
                label = {
                    Text(text = "Stock At")
                },
                showEndIcon = false,
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth(),
        ) {
            ComboBox(
                modifier = Modifier
                    .fillMaxWidth(0.5f),
                items = HP.stockExpiry,
                selectedItem = stockExpiry,
                onItemSelected = onStockExpiryChange,
                label = {
                    Text(text = "Expiry")
                },
                showEndIcon = false,
            )
            Spacer(Modifier.width(8.dp))
            ComboBox(
                modifier = Modifier
                    .fillMaxWidth(),
                items = HP.stockType,
                selectedItem = stockType,
                onItemSelected = onStockTypeChange,
                label = {
                    Text(text = "Type")
                },
                showEndIcon = false,
            )
        }
        ComboBox(
            modifier = Modifier
                .fillMaxWidth(),
            items = HP.warehouses,
            selectedItem = warehouse,
            onItemSelected = onWarehouseChange,
            label = {
                Text(text = "Warehouse")
            },
            addNone = true,
        )
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
    }
}
