package com.example.statspos.presentation.ui.screens.sales.main_screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.statspos.domain.models.sales.SalesBills
import com.example.statspos.presentation.ui.components.AccessDeniedBox
import com.example.statspos.presentation.ui.components.TabLayout
import com.example.statspos.presentation.viewmodels.SharedViewModel
import com.example.statspos.utils.HP

@Composable
fun SalesScreen(
    sharedViewModel: SharedViewModel,
    onViewClick: (SalesBills) -> Unit,
    onAddUpdateButtonClick: (Long, Boolean, Boolean, SalesBills?) -> Unit,
) {
    val tabs = listOf("Pending Bills", "Posted Bills")
    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { tabs.size }
    )

    val scope = rememberCoroutineScope()
    var selectedTab by remember { mutableIntStateOf(0) }


    if (HP.userRights.sales == true) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            TabLayout(
                pagerState = pagerState,
                tabs = tabs,
            )

//        SegmentedTabs(
//            tabs = listOf("Pending Bills", "Posted Bills"),
//            selectedIndex = selectedTab,
//            onTabSelected = {
//                selectedTab = it
//                scope.launch {
//                    pagerState.animateScrollToPage(selectedTab)
//                }
//            }
//        )

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
    } else {
        AccessDeniedBox()
    }
}
