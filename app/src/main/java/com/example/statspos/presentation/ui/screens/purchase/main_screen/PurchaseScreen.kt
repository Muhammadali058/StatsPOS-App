package com.example.statspos.presentation.ui.screens.purchase.main_screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.statspos.domain.models.purchase.PurchaseBills
import com.example.statspos.presentation.ui.components.AccessDeniedBox
import com.example.statspos.presentation.ui.components.TabLayout
import com.example.statspos.presentation.viewmodels.SharedViewModel
import com.example.statspos.utils.HP

@Composable
fun PurchaseScreen(
    sharedViewModel: SharedViewModel,
    onViewClick: (PurchaseBills) -> Unit,
    onAddUpdateButtonClick: (Long, Boolean, Boolean, PurchaseBills?) -> Unit,
) {
    val tabs = listOf("Pending Bills", "Posted Bills")
    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { tabs.size }
    )

    if (HP.userRights.purchase == true) {
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
                        PurchasePendingBillsBody(
                            sharedViewModel = sharedViewModel,
                            onAddUpdateButtonClick = { invoiceId, isPendingBill, purchaseBill ->
                                onAddUpdateButtonClick(
                                    invoiceId,
                                    isPendingBill,
                                    false,
                                    purchaseBill
                                )
                            },
                        )

                    1 ->
                        PurchasePostedBillsBody(
                            sharedViewModel = sharedViewModel,
                            onViewClick = onViewClick,
                            onAddUpdateButtonClick = { invoiceId, isPostedBill, purchaseBill ->
                                onAddUpdateButtonClick(invoiceId, false, isPostedBill, purchaseBill)
                            },
                        )
                }
            }
        }
    } else {
        AccessDeniedBox()
    }
}
