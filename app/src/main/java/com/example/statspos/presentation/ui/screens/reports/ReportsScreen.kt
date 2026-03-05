package com.example.statspos.presentation.ui.screens.reports

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavKey
import com.example.statspos.presentation.ui.components.AppIcon
import com.example.statspos.presentation.ui.components.AppText
import com.example.statspos.presentation.ui.components.PullToRefreshList
import com.example.statspos.presentation.ui.components.ReportCard
import com.example.statspos.presentation.ui.components.TopItem
import com.example.statspos.presentation.ui.screens.TopRoutes
import com.example.statspos.presentation.ui.screens.reports.sales.TodayBox
import com.example.statspos.presentation.ui.utils.ConstantPaddings

private val reports = listOf(
    TopItem("Sales", TopRoutes.SalesReports),
    TopItem("Purchase", TopRoutes.PurchaseReports),
    TopItem("Profit", TopRoutes.ProfitReports),
    TopItem("Stock", TopRoutes.StockReports),
    TopItem("Accounts", TopRoutes.AccountsReports),
    TopItem("Items", TopRoutes.ItemsReports),
)

@Composable
fun ReportsScreen(
    onTopRouteClick: (NavKey) -> Unit,
) {
    val scrollState = rememberScrollState()

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
                isRefreshing = false,
                onRefresh = {  },
            ) {
                item {
                    Spacer(Modifier.height(8.dp))
                    TodaySales(
                        cashSales = 0.0,
                        creditSales = 0.0,
                        salesReturns = 0.0,
                        totalSales = 0.0,
                        totalBills = 0,
                    )
                    Spacer(Modifier.height(12.dp))
                    TodaySales(
                        cashSales = 0.0,
                        creditSales = 0.0,
                        salesReturns = 0.0,
                        totalSales = 0.0,
                        totalBills = 0,
                    )
                    Spacer(Modifier.height(12.dp))
                    TodaySales(
                        cashSales = 0.0,
                        creditSales = 0.0,
                        salesReturns = 0.0,
                        totalSales = 0.0,
                        totalBills = 0,
                    )
                    Spacer(Modifier.height(12.dp))
                    TodaySales(
                        cashSales = 0.0,
                        creditSales = 0.0,
                        salesReturns = 0.0,
                        totalSales = 0.0,
                        totalBills = 0,
                    )
                    Spacer(Modifier.height(12.dp))
                    TodaySales(
                        cashSales = 0.0,
                        creditSales = 0.0,
                        salesReturns = 0.0,
                        totalSales = 0.0,
                        totalBills = 0,
                    )
                    Spacer(Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
private fun TodaySales(
    modifier: Modifier = Modifier,
    cashSales: Double = 0.0,
    creditSales: Double = 0.0,
    salesReturns: Double = 0.0,
    totalSales: Double = 0.0,
    totalBills: Int = 0,
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
        items(items) { item ->
            Card(
                modifier = Modifier
                    .width(100.dp)
                    .height(112.dp)
                    .padding(vertical = 6.dp),
                onClick = { onClick(item.screen) },
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 2.dp
                ),
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
