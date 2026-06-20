package com.example.statspos.presentation.ui.screens.reports

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavKey
import com.example.statspos.R
import com.example.statspos.presentation.ui.components.AccessDeniedBox
import com.example.statspos.presentation.ui.components.AppIcon
import com.example.statspos.presentation.ui.components.AppIconButton
import com.example.statspos.presentation.ui.components.AppSwitch
import com.example.statspos.presentation.ui.components.AppText
import com.example.statspos.presentation.ui.components.AutoCompleteItemsTextbox
import com.example.statspos.presentation.ui.components.DateTextbox
import com.example.statspos.presentation.ui.components.ErrorDialog
import com.example.statspos.presentation.ui.components.FilterIcon
import com.example.statspos.presentation.ui.components.PullToRefreshList
import com.example.statspos.presentation.ui.components.ReportButton
import com.example.statspos.presentation.ui.components.ReportCard
import com.example.statspos.presentation.ui.components.SearchBox
import com.example.statspos.presentation.ui.components.ShowReportIcon
import com.example.statspos.presentation.ui.components.TopItem
import com.example.statspos.presentation.ui.screens.TopRoutes
import com.example.statspos.presentation.ui.utils.ConstantPaddings
import com.example.statspos.presentation.viewmodels.SharedViewModel
import com.example.statspos.presentation.viewmodels.reports.ReportsViewModel
import com.example.statspos.utils.HP
import com.example.statspos.utils.UiEvent
import com.example.statspos.utils.checkEvent
import java.time.LocalDate

private val reports = listOf(
    TopItem(
        "Sales",
        TopRoutes.SalesReports,
        R.drawable.sales_report,
        HP.userRights.salesReport == true
    ),
    TopItem(
        "Purchase",
        TopRoutes.PurchaseReports,
        R.drawable.purchase_report,
        HP.userRights.purchaseReport == true
    ),
    TopItem(
        "Profit",
        TopRoutes.ProfitReports,
        R.drawable.profit_report,
        HP.userRights.profitReport == true
    ),
    TopItem(
        "Stock",
        TopRoutes.StockReports,
        R.drawable.stock_report,
        HP.userRights.stockReport == true
    ),
    TopItem(
        "Accounts",
        TopRoutes.AccountsReports,
        R.drawable.accounts_report,
        HP.userRights.accountsReport == true
    ),
    TopItem(
        "Items",
        TopRoutes.ItemsReports,
        R.drawable.items_report,
        HP.userRights.itemsReport == true
    ),
)

@Composable
fun ReportsScreen(
    sharedViewModel: SharedViewModel,
    onTopRouteClick: (NavKey) -> Unit,
) {
    val viewModel = hiltViewModel<ReportsViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val event by viewModel.event.collectAsState(UiEvent.Idle)
    val snackbarHostState = remember { SnackbarHostState() }
    var showErrorDialog by remember { mutableStateOf(false) }
    val sharedViewModelState by sharedViewModel.state.collectAsStateWithLifecycle()

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

    // When branch changed
    LaunchedEffect(sharedViewModelState.refreshReportsScreen) {
        if (sharedViewModelState.refreshReportsScreen) {
            viewModel.loadMainReport()
            sharedViewModel.consumeRefreshReportsScreen()
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

    if (HP.userRights.reports == true) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(ConstantPaddings.BODY_HORIZONTAL),
        ) {
            Spacer(Modifier.height(8.dp))
            ReportsGrid(reports, onTopRouteClick)
            Spacer(Modifier.height(8.dp))

            Column(
                modifier = Modifier
                    .weight(1f)
            ) {
                PullToRefreshList(
                    modifier = Modifier,
                    isRefreshing = state.isLoading,
                    onRefresh = {
                        viewModel.loadMainReport()
                    },
                ) {
                    item {
                        if (HP.userRights.salesReport == true) {
                            TodaySales(
                                cashSales = state.mainReport.cashSales,
                                creditSales = state.mainReport.creditSales,
                                salesReturns = state.mainReport.salesReturns,
                                totalSales = state.mainReport.totalSales,
                                totalSalesBills = state.mainReport.totalSalesBills,
                            )
                            Spacer(Modifier.height(12.dp))
                        }
                        if (HP.userRights.purchaseReport == true) {
                            TodayPurchase(
                                cashPurchase = state.mainReport.cashPurchase,
                                creditPurchase = state.mainReport.creditPurchase,
                                purchaseReturns = state.mainReport.purchaseReturns,
                                totalPurchase = state.mainReport.totalPurchase,
                                totalPurchaseBills = state.mainReport.totalPurchaseBills,
                            )
                            Spacer(Modifier.height(12.dp))
                        }
                        if (HP.userRights.profitReport == true) {
                            TodayProfit(
                                grossProfit = state.mainReport.grossProfit,
                                expenses = state.mainReport.expenses,
                                margin = state.mainReport.margin,
                                netProfit = state.mainReport.netProfit,
                            )
                            Spacer(Modifier.height(12.dp))
                        }
                        if (HP.userRights.stockReport == true) {
                            TodayStock(
                                stockAtCost = state.mainReport.stockAtCost,
                                stockAtRetail = state.mainReport.stockAtRetail,
                                stockAtWholesale = state.mainReport.stockAtWholesale,
                            )
                            Spacer(Modifier.height(12.dp))
                        }
                        if (HP.userRights.accountsReport == true) {
                            TodayAccounts(
                                receipts = state.mainReport.receipts,
                                payments = state.mainReport.payments,
                                debtors = state.mainReport.debtors,
                                creditors = state.mainReport.creditors,
                            )
                            Spacer(Modifier.height(12.dp))
                        }
                        if (HP.userRights.accountsReport == true) {
                            TodayCashAccount(
                                openingBalance = state.mainReport.openingBalance,
                                cashIn = state.mainReport.cashIn,
                                cashOut = state.mainReport.cashOut,
                                closingBalance = state.mainReport.closingBalance,
                            )
                            Spacer(Modifier.height(12.dp))
                        }
                    }
                }
            }
        }
    } else {
        AccessDeniedBox()
    }
}

@Composable
fun ReportButtons(
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
fun ReportsDateBox(
    fromDate: LocalDate,
    toDate: LocalDate,
    onFromDateChange: (LocalDate) -> Unit,
    onToDateChange: (LocalDate) -> Unit,
    onFilterClick: () -> Unit,
) {
    SearchBox{
        Column(
            modifier = Modifier
                .fillMaxWidth(),
        ) {
            Spacer(Modifier.height(2.dp))
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
                FilterIcon {
                    onFilterClick()
                }
            }
        }
    }
}

@Composable
fun ReportsItemnameBox(
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
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
        ) {
            AutoCompleteItemsTextbox(
                modifier = Modifier
                    .weight(1f),
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
                    ShowReportIcon {
                        onEndIconClick(value)
                    }
                },
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Go
                ),
                padding = PaddingValues(top = 4.dp),
            )
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

        Spacer(Modifier.height(16.dp))
        AppSwitch(
            checked = sum,
            onCheckedChange = onSumChange,
            label = "Sum"
        )
    }
}

@Composable
fun TodaySales(
    modifier: Modifier = Modifier,
    cashSales: Double = 0.0,
    creditSales: Double = 0.0,
    salesReturns: Double = 0.0,
    totalSales: Double = 0.0,
    totalSalesBills: Int = 0,
) {
    ReportCard(
        modifier = modifier,
        heading = "Sales",
        subHeading = "Summary of sales",
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

@Composable
fun TodayPurchase(
    modifier: Modifier = Modifier,
    cashPurchase: Double = 0.0,
    creditPurchase: Double = 0.0,
    purchaseReturns: Double = 0.0,
    totalPurchase: Double = 0.0,
    totalPurchaseBills: Int = 0,
) {
    ReportCard(
        modifier = modifier,
        heading = "Purchase",
        subHeading = "Summary of purchase",
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
        ) {
            TodayBox(
                modifier = Modifier
                    .weight(1f),
                text = "Cash Purchase",
                value = "Rs.${HP.formatDecimal(cashPurchase, numberOfDecimals = 0)}",
            )
            TodayBox(
                modifier = Modifier
                    .weight(1f),
                text = "Credit Purchase",
                value = "Rs.${HP.formatDecimal(creditPurchase, numberOfDecimals = 0)}",
            )
            TodayBox(
                modifier = Modifier
                    .weight(1f),
                text = "Returns",
                value = "Rs.${HP.formatDecimal(purchaseReturns, numberOfDecimals = 0)}",
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth(),
        ) {
            TodayBox(
                modifier = Modifier
                    .weight(1f),
                text = "Total Purchase",
                value = "Rs.${HP.formatDecimal(totalPurchase, numberOfDecimals = 0)}",
            )
            TodayBox(
                modifier = Modifier
                    .weight(1f),
                text = "Total Invoices",
                value = totalPurchaseBills.toString(),
            )
        }
    }
}

@Composable
fun TodayProfit(
    modifier: Modifier = Modifier,
    grossProfit: Double = 0.0,
    expenses: Double = 0.0,
    margin: Double = 0.0,
    netProfit: Double = 0.0,
) {
    ReportCard(
        modifier = modifier,
        heading = "Profit",
        subHeading = "Summary of profit",
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
        ) {
            TodayBox(
                modifier = Modifier
                    .weight(1f),
                text = "Gross Profit",
                value = "Rs.${HP.formatDecimal(grossProfit, numberOfDecimals = 0)}",
            )
            TodayBox(
                modifier = Modifier
                    .weight(1f),
                text = "Expenses",
                value = "Rs.${HP.formatDecimal(expenses, numberOfDecimals = 0)}",
            )
            TodayBox(
                modifier = Modifier
                    .weight(1f),
                text = "Margin",
                value = "(${HP.formatDecimal(margin, numberOfDecimals = 2)})%",
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth(),
        ) {
            TodayBox(
                modifier = Modifier
                    .weight(1f),
                text = "Net Profit",
                value = "Rs.${HP.formatDecimal(netProfit, numberOfDecimals = 0)}",
            )
        }
    }
}

@Composable
fun TodayStock(
    modifier: Modifier = Modifier,
    stockAtCost: Double = 0.0,
    stockAtRetail: Double = 0.0,
    stockAtWholesale: Double = 0.0,
) {
    ReportCard(
        modifier = modifier,
        heading = "Stock",
        subHeading = "Value of current stock",
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
        ) {
            TodayBox(
                modifier = Modifier
                    .weight(1f),
                text = "Stock at Retail",
                value = "Rs.${HP.formatDecimal(stockAtRetail, numberOfDecimals = 0)}",
            )
            TodayBox(
                modifier = Modifier
                    .weight(1f),
                text = "Stock at Wholesale",
                value = "Rs.${HP.formatDecimal(stockAtWholesale, numberOfDecimals = 0)}",
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth(),
        ) {
            TodayBox(
                modifier = Modifier
                    .weight(1f),
                text = "Stock at Cost",
                value = "Rs.${HP.formatDecimal(stockAtCost, numberOfDecimals = 0)}",
            )
        }
    }
}

@Composable
private fun TodayAccounts(
    modifier: Modifier = Modifier,
    receipts: Double = 0.0,
    payments: Double = 0.0,
    debtors: Double = 0.0,
    creditors: Double = 0.0,
) {
    ReportCard(
        modifier = modifier,
        heading = "Accounts",
        subHeading = "Summary of daily accounts",
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
        ) {
            TodayBox(
                modifier = Modifier
                    .weight(1f),
                text = "Receipts",
                value = "Rs.${HP.formatDecimal(receipts, numberOfDecimals = 0)}",
            )
            TodayBox(
                modifier = Modifier
                    .weight(1f),
                text = "Payments",
                value = "Rs.${HP.formatDecimal(payments, numberOfDecimals = 0)}",
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth(),
        ) {
            TodayBox(
                modifier = Modifier
                    .weight(1f),
                text = "Receivables",
                value = "Rs.${HP.formatDecimal(debtors, numberOfDecimals = 0)}",
            )
            TodayBox(
                modifier = Modifier
                    .weight(1f),
                text = "Payables",
                value = "Rs.${HP.formatDecimal(creditors, numberOfDecimals = 0)}",
            )
        }
    }
}

@Composable
private fun TodayCashAccount(
    modifier: Modifier = Modifier,
    openingBalance: Double = 0.0,
    cashIn: Double = 0.0,
    cashOut: Double = 0.0,
    closingBalance: Double = 0.0,
) {
    ReportCard(
        modifier = modifier,
        heading = "Cash Account",
        subHeading = "Summary of daily cash",
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
        ) {
            TodayBox(
                modifier = Modifier
                    .weight(1f),
                text = "Cash In",
                value = "Rs.${HP.formatDecimal(cashIn, numberOfDecimals = 0)}",
            )
            TodayBox(
                modifier = Modifier
                    .weight(1f),
                text = "Cash Out",
                value = "Rs.${HP.formatDecimal(cashOut, numberOfDecimals = 0)}",
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth(),
        ) {
            TodayBox(
                modifier = Modifier
                    .weight(1f),
                text = "Opening Balance",
                value = "Rs.${HP.formatDecimal(openingBalance, numberOfDecimals = 0)}",
            )
            TodayBox(
                modifier = Modifier
                    .weight(1f),
                text = "Closing Balance",
                value = "Rs.${HP.formatDecimal(closingBalance, numberOfDecimals = 0)}",
            )
        }
    }
}

@Composable
fun TodayBox(
    modifier: Modifier = Modifier,
    text: String,
    value: String,
) {
    Column(
        modifier = modifier
            .padding(4.dp)
            .border(
                1.dp,
                MaterialTheme.colorScheme.onPrimaryContainer,
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
        AppText(
            text = value,
            style = TextStyle(
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
            )
        )
    }
}

@Composable
private fun ReportsGrid1(
    items: List<TopItem>,
    onClick: (TopRoutes) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = 400.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        val filteredItems = items.filter { it.access }
        items(filteredItems) { item ->
            Card(
                modifier = Modifier
                    .width(100.dp)
                    .height(112.dp)
                    .padding(vertical = 6.dp),
                onClick = { onClick(item.screen) },
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 2.dp
                ),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                ),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.SpaceEvenly,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    item.icon?.run {
                        AppIcon(
                            icon = item.icon,
                            size = 30.dp,
                        )
                    }
//                    Spacer(Modifier.height(4.dp))
                    AppText(
                        text = item.text,
                        style = TextStyle(
                            textAlign = TextAlign.Center,
                        )
                    )
                }
            }
        }
    }
}


@Composable
private fun ReportsGrid(
    items: List<TopItem>,
    onClick: (TopRoutes) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = 400.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        val filteredItems = items.filter { it.access }
        items(filteredItems) { item ->
            Card(
                modifier = Modifier
                    .width(100.dp)
                    .height(112.dp)
                    .padding(vertical = 6.dp),
                onClick = { onClick(item.screen) },
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 2.dp
                ),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                ),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.SpaceEvenly,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Spacer(Modifier.height(12.dp))
                    item.icon?.run {
                        AppIcon(
                            modifier = Modifier
                                .weight(1f),
                            icon = item.icon,
                            size = 26.dp,
                        )
                    }
                    Spacer(Modifier.height(12.dp))
                    Text(
                        modifier = Modifier
                            .weight(1f),
                        text = item.text,
                        style = TextStyle(
                            textAlign = TextAlign.Center,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun DateBox(
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
