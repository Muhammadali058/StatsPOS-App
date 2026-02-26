package com.example.statspos.presentation.ui.screens.purchase.main_screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.example.statspos.domain.models.purchase.PurchaseBills
import com.example.statspos.domain.models.sales.SalesBills
import com.example.statspos.presentation.ui.components.AppSnackbarHost
import com.example.statspos.presentation.ui.components.TabLayout
import com.example.statspos.presentation.ui.components.TopAppBar
import com.example.statspos.presentation.ui.screens.TopRoutes
import com.example.statspos.presentation.ui.screens.accounts.banks.BanksBody
import com.example.statspos.presentation.ui.screens.accounts.banks.SubBanksBody
import com.example.statspos.presentation.ui.screens.purchase.purchase_bill.PurchaseBillScreen
import com.example.statspos.presentation.ui.screens.purchase.purchase_bill.ViewPurchaseBillItemsScreen
import com.example.statspos.presentation.ui.screens.sales.sales_bill.SalesBillScreen
import com.example.statspos.presentation.ui.screens.sales.sales_bill.ViewSalesBillItemsScreen
import com.example.statspos.presentation.viewmodels.SharedViewModel
import kotlinx.serialization.Serializable

private sealed class Routes : NavKey {
    @Serializable
    data object Home : Routes()

    @Serializable
    data class ViewBillItems(val purchaseBill: PurchaseBills) : Routes()

    @Serializable
    data class AddUpdatePurchase(
        val updateId: Long,
        val isPendingBill: Boolean,
        val isPostedBill: Boolean,
        val purchaseBill: PurchaseBills?
    ) : Routes()
}

@Composable
fun PurchaseScreen(
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
                    onViewClick = { purchaseBill ->
                        navigate(Routes.ViewBillItems(purchaseBill))
                    },
                    onAddUpdateButtonClick = { updateId, isPendingBill, isPostedBill, purchaseBill ->
                        navigate(
                            Routes.AddUpdatePurchase(
                                updateId,
                                isPendingBill,
                                isPostedBill,
                                purchaseBill
                            )
                        )
                    }
                )
            }
            entry<Routes.AddUpdatePurchase> { key ->
                PurchaseBillScreen(
                    sharedViewModel = sharedViewModel,
                    invoiceId = key.updateId,
                    isPendingBill = key.isPendingBill,
                    isPostedBill = key.isPostedBill,
                    purchaseBill = key.purchaseBill,
                    onBack = {
                        backStack.removeLastOrNull()
                    }
                )
            }
            entry<Routes.ViewBillItems> { key ->
                ViewPurchaseBillItemsScreen(
                    invoiceId = key.purchaseBill.id!!,
                    isPostedBill = true,
                    onBack = {
                        backStack.removeLastOrNull()
                    }
                )
            }
        }
    )
}

@Composable
private fun Home(
    sharedViewModel: SharedViewModel,
    onBack: () -> Unit,
    onViewClick: (PurchaseBills) -> Unit,
    onAddUpdateButtonClick: (Long, Boolean, Boolean, PurchaseBills?) -> Unit,
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
        topBar = {
            TopAppBar(
                onNavigationClick = {
                    onBack()
                },
                title = "Purchase",
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
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
    }
}
