package com.example.statspos.presentation.ui.screens.sales.main_screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.example.statspos.domain.models.sales.SalesBills
import com.example.statspos.presentation.ui.components.AppSnackbarHost
import com.example.statspos.presentation.ui.components.TabLayout
import com.example.statspos.presentation.viewmodels.SharedViewModel

@Composable
fun SalesScreen(
    sharedViewModel: SharedViewModel,
    onViewClick:(SalesBills) -> Unit,
    onAddUpdateButtonClick: (Long, Boolean, Boolean, SalesBills?) -> Unit,
) {
    val tabs = listOf("Pending Bills", "Posted Bills")
    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { tabs.size }
    )

    val snackbarHostState = remember { SnackbarHostState() }
    Scaffold(
        snackbarHost = {
            AppSnackbarHost(
                snackbarHostState = snackbarHostState,
            )
        },
    ) { innerPadding ->
        Box(Modifier.padding(innerPadding))

        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            TabLayout(
                pagerState = pagerState,
                tabs = tabs,
            )

            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxSize(),
            ) { page ->
                when (page) {
                    0 ->
                        SalesPendingBillsBody(
                            sharedViewModel = sharedViewModel,
                            onAddUpdateButtonClick = { invoiceId, isPendingBill, salesBills ->
                                onAddUpdateButtonClick(invoiceId, isPendingBill, false, salesBills)
                            },
                        )

                    1 ->
                        SalesPostedBillsBody(
                            sharedViewModel = sharedViewModel,
                            onViewClick = onViewClick,
                            onAddUpdateButtonClick = { invoiceId, isPostedBill, salesBills ->
                                onAddUpdateButtonClick(invoiceId, false, isPostedBill, salesBills)
                            },
                        )
                }
            }
        }
    }
}
