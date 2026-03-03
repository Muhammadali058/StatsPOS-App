package com.example.statspos.presentation.ui.screens.reports.sales

import androidx.annotation.DrawableRes
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
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
import com.example.statspos.domain.models.reports.ChartReport
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
import com.example.statspos.presentation.ui.components.SubDropdown
import com.example.statspos.presentation.ui.components.TopAppBar
import com.example.statspos.presentation.ui.screens.items.SearchItemsScreen
import com.example.statspos.presentation.ui.utils.ConstantPaddings
import com.example.statspos.presentation.ui.utils.PdfPreviewScreen
import com.example.statspos.presentation.ui.utils.openPdf
import com.example.statspos.presentation.viewmodels.SharedViewModel
import com.example.statspos.presentation.viewmodels.reports.SalesReportsViewModel
import com.example.statspos.utils.HP
import com.example.statspos.utils.UiEvent
import com.example.statspos.utils.checkEvent
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.compose.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberAxisGuidelineComponent
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberAxisLabelComponent
import com.patrykandpatrick.vico.compose.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.compose.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.compose.cartesian.data.columnSeries
import com.patrykandpatrick.vico.compose.cartesian.layer.ColumnCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberColumnCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.marker.CartesianMarker
import com.patrykandpatrick.vico.compose.cartesian.marker.DefaultCartesianMarker
import com.patrykandpatrick.vico.compose.cartesian.marker.rememberDefaultCartesianMarker
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.common.Fill
import com.patrykandpatrick.vico.compose.common.Insets
import com.patrykandpatrick.vico.compose.common.LayeredComponent
import com.patrykandpatrick.vico.compose.common.component.ShapeComponent
import com.patrykandpatrick.vico.compose.common.component.TextComponent
import com.patrykandpatrick.vico.compose.common.component.rememberLineComponent
import com.patrykandpatrick.vico.compose.common.component.rememberShapeComponent
import com.patrykandpatrick.vico.compose.common.component.rememberTextComponent
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
                .padding(bottom = 16.dp)
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
                    TodaySales(
                        cashSales = state.mainReport.cashSales,
                        creditSales = state.mainReport.creditSales,
                        salesReturns = state.mainReport.salesReturns,
                        totalSales = state.mainReport.totalSales,
                        totalBills = state.mainReport.totalBills,
                    )
                    Spacer(Modifier.height(8.dp))

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        Card(
                            modifier = Modifier
                                .padding(top = 8.dp),
                            elevation = CardDefaults.cardElevation(
                                defaultElevation = 3.dp
                            ),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                            ),
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                            ) {
                                Spacer(Modifier.height(12.dp))
                                AppText(
                                    modifier = Modifier
                                        .padding(horizontal = 12.dp),
                                    text = "Sales Chart",
                                    style = TextStyle(
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                    )
                                )
                                Spacer(Modifier.height(2.dp))
                                AppText(
                                    modifier = Modifier
                                        .padding(horizontal = 12.dp),
                                    text = "Sales trend chart for last 7 days",
                                    style = TextStyle(
                                        fontSize = 12.sp,
                                    )
                                )
                                Spacer(Modifier.height(8.dp))
                                HorizontalDivider(thickness = 1.dp, color = Color.LightGray)
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp)
                                ) {
                                    DailySalesChart(state.chartReport)
                                }
                            }
                        }
                    }


                    Spacer(Modifier.height(16.dp))
                    Title("PDF Reports", R.drawable.reports)
                    Spacer(Modifier.height(8.dp))
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
                    Spacer(Modifier.height(8.dp))
                }
            }

            if (state.isLoading) {
                ProgressBarLayout()
            }
        }
    }
}

@Composable
fun DailySalesChart(data: List<ChartReport>) {
    if (data.isNotEmpty()) {
        val modelProducer = remember { CartesianChartModelProducer() }

        LaunchedEffect(data) {
            modelProducer.runTransaction {
                columnSeries {
                    series(
                        data.map { it.total }
                    )
                }
            }
        }

        val startAxis = VerticalAxis.rememberStart(
            label = rememberAxisLabelComponent(
                style = TextStyle(
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontSize = 12.sp
                )
            )
        )

        val bottomAxis = HorizontalAxis.rememberBottom(
            valueFormatter = remember(data) {
                CartesianValueFormatter { _, value, _ ->
                    val index = value.toInt()
                    data.getOrNull(index)?.date ?: ""
                }
            },
            label = rememberAxisLabelComponent(
                style = TextStyle(
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontSize = 12.sp
                )
            )
        )

        CartesianChartHost(
            chart = rememberCartesianChart(
                rememberColumnCartesianLayer(
                    columnProvider = ColumnCartesianLayer.ColumnProvider.series(
                        rememberLineComponent(
                            fill = Fill(MaterialTheme.colorScheme.primary),
                            thickness = 16.dp,
                            shape = RectangleShape
                        )
                    )
                ),
                startAxis = startAxis,
                bottomAxis = bottomAxis,
                marker = rememberMarker(
                    valueFormatter = DefaultCartesianMarker.ValueFormatter.default(
                        prefix = "Rs."
                    ),
                    showIndicator = true
                ),
            ),
            modelProducer = modelProducer,
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
        )
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
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                modifier = Modifier
                    .weight(1f)
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
                    }
                )
            }
            Spacer(Modifier.width(8.dp))
            ReportButton { onCategoryClick() }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                modifier = Modifier
                    .weight(1f)
            ) {
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
                    }
                )
            }
            Spacer(Modifier.width(8.dp))
            ReportButton { onSubCategoryClick() }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                modifier = Modifier
                    .weight(1f)
            ) {
                Dropdown(
                    value = vendorName,
                    onValueChange = onVendorNameChange,
                    items = HP.vendors,
                    onItemSelected = { dropdownItem ->
                        onVendorIdChange(dropdownItem.id)
                    },
                    label = {
                        Text(text = "Vendor")
                    }
                )
            }
            Spacer(Modifier.width(8.dp))
            ReportButton { onVendorClick() }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                modifier = Modifier
                    .weight(1f)
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
                    }
                )
            }
            Spacer(Modifier.width(8.dp))
            ReportButton { onCustomerClick() }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                modifier = Modifier
                    .weight(1f)
            ) {
                Dropdown(
                    value = accountCategoryName,
                    onValueChange = onAccountCategoryNameChange,
                    items = HP.accountCategories,
                    onItemSelected = { dropdownItem ->
                        onAccountCategoryIdChange(dropdownItem.id)
                    },
                    label = {
                        Text(text = "Customer Category")
                    }
                )
            }
            Spacer(Modifier.width(8.dp))
            ReportButton { onAccountCategoryClick() }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                modifier = Modifier
                    .weight(1f)
            ) {
                Dropdown(
                    value = supplierName,
                    onValueChange = onSupplierNameChange,
                    items = HP.suppliers,
                    onItemSelected = { dropdownItem ->
                        onSupplierIdChange(dropdownItem.id)
                    },
                    label = {
                        Text(text = "Supplier")
                    }
                )
            }
            Spacer(Modifier.width(8.dp))
            ReportButton { onSupplierClick() }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                modifier = Modifier
                    .weight(1f)
            ) {
                Dropdown(
                    value = username,
                    onValueChange = onUsernameChange,
                    items = HP.users,
                    onItemSelected = { dropdownItem ->
                        onUserIdChange(dropdownItem.id)
                    },
                    label = {
                        Text(text = "User")
                    }
                )
            }
            Spacer(Modifier.width(8.dp))
            ReportButton { onUserClick() }
        }
    }
}

@Composable
private fun Title(
    title: String,
    @DrawableRes icon: Int? = null,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start,
    ) {
//        if (icon != null) {
//            AppIcon(
//                icon = icon,
//                size = 24.dp,
//            )
//            Spacer(Modifier.width(16.dp))
//        }
        AppText(
            text = title,
            style = MaterialTheme.typography.titleMedium,
        )
    }
}

//@Preview(showBackground = true)
@Composable
private fun Prev() {
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        DateBox(
            LocalDate.now(),
            LocalDate.now(),
            {},
            {},
            {},
        )
        Spacer(Modifier.height(8.dp))
        ReportButtons(
            {},
            {},
            {},
        )
        Spacer(Modifier.height(8.dp))
        Dropdowns(
            "",
            "",
            0L,
            "",
            "",
            "",
            "",
            "",
            {},
            {},
            {},
            {},
            {},
            {},
            {},
            {},
            {},
            {},
            {},
            {},
            {},
            {},
            {},
            {},
            {},
            {},
            {},
            {},
            {},
        )
        ItemnameBox(
            "",
            {},
            {},
            {},
            {},
            {},
            {},
            {},
            false,
            {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun TodaySales(
    cashSales: Double = 0.0,
    creditSales: Double = 0.0,
    salesReturns: Double = 0.0,
    totalSales: Double = 0.0,
    totalBills: Int = 0,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Card(
            modifier = Modifier
                .padding(top = 16.dp),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 3.dp
            ),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
            ),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Spacer(Modifier.height(12.dp))
                AppText(
                    modifier = Modifier
                        .padding(horizontal = 12.dp),
                    text = "Today",
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                    )
                )
                Spacer(Modifier.height(2.dp))
                AppText(
                    modifier = Modifier
                        .padding(horizontal = 12.dp),
                    text = "Summary of daily sales",
                    style = TextStyle(
                        fontSize = 12.sp,
                    )
                )
                Spacer(Modifier.height(8.dp))
                HorizontalDivider(thickness = 1.dp, color = Color.LightGray)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                    ) {
                        TodayBox(
                            modifier = Modifier
                                .weight(1f),
                            text = "Cash Sales",
                            value = cashSales,
                        )
                        TodayBox(
                            modifier = Modifier
                                .weight(1f),
                            text = "Credit Sales",
                            value = creditSales,
                        )
                        TodayBox(
                            modifier = Modifier
                                .weight(1f),
                            text = "Returns",
                            value = salesReturns,
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
                            value = totalSales,
                        )
                        TodayBox(
                            modifier = Modifier
                                .weight(1f),
                            text = "Total Invoices",
                            value = totalBills.toDouble(),
                            addRs = false,
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TodayBox(
    modifier: Modifier = Modifier,
    text: String,
    value: Double,
    addRs: Boolean = true,
) {
    Column(
        modifier = modifier
            .padding(4.dp)
            .border(
                0.5.dp,
                MaterialTheme.colorScheme.primary,
                RoundedCornerShape(8.dp)
            )
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        AppText(
            text = text,
            style = TextStyle(
                fontSize = 12.sp,
            )
        )
        Spacer(Modifier.height(8.dp))

        val temp = if (addRs)
            "Rs.${HP.formatDecimal(value, numberOfDecimals = 0)}"
        else
            HP.formatDecimal(value, numberOfDecimals = 0)
        AppText(
            text = temp,
            style = TextStyle(
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
            )
        )
    }
}


@Composable
internal fun rememberMarker(
    valueFormatter: DefaultCartesianMarker.ValueFormatter =
        DefaultCartesianMarker.ValueFormatter.default(),
    showIndicator: Boolean = true,
): CartesianMarker {
    val labelBackground =
        rememberShapeComponent(
            fill = Fill(MaterialTheme.colorScheme.background),
            shape = CircleShape,
            strokeFill = Fill(MaterialTheme.colorScheme.outline),
            strokeThickness = 1.dp,
        )
    val label =
        rememberTextComponent(
            style =
                TextStyle(
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center,
                    fontSize = 12.sp,
                ),
            padding = Insets(8.dp, 4.dp),
            background = labelBackground,
            minWidth = TextComponent.MinWidth.fixed(40.dp),
        )
    val indicatorFrontComponent =
        rememberShapeComponent(Fill(MaterialTheme.colorScheme.surface), CircleShape)
    val guideline = rememberAxisGuidelineComponent()

    return rememberDefaultCartesianMarker(
        label = label,
        valueFormatter = valueFormatter,
        indicator =
            if (showIndicator) {
                { color ->
                    LayeredComponent(
                        back = ShapeComponent(Fill(color.copy(alpha = 0.15f)), CircleShape),
                        front =
                            LayeredComponent(
                                back = ShapeComponent(fill = Fill(color), shape = CircleShape),
                                front = indicatorFrontComponent,
                                padding = Insets(5.dp),
                            ),
                        padding = Insets(10.dp),
                    )
                }
            } else {
                null
            },
        indicatorSize = 36.dp,
        guideline = guideline,
    )
}
