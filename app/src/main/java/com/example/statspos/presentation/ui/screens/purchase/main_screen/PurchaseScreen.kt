package com.example.statspos.presentation.ui.screens.purchase.main_screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.statspos.domain.models.purchase.PurchaseBills
import com.example.statspos.presentation.ui.components.TabLayout
import com.example.statspos.presentation.viewmodels.SharedViewModel

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
//    }
}


//private sealed class Routes : NavKey {
//    @Serializable
//    data object Home : Routes()
//
//    @Serializable
//    data class ViewBillItems(val purchaseBill: PurchaseBills) : Routes()
//
//    @Serializable
//    data class AddUpdatePurchase(
//        val updateId: Long,
//        val isPendingBill: Boolean,
//        val isPostedBill: Boolean,
//        val purchaseBill: PurchaseBills?
//    ) : Routes()
//}


//@Composable
//fun PurchaseScreen1(
//    sharedViewModel: SharedViewModel,
//    onViewClick:(PurchaseBills) -> Unit,
//    onAddUpdateButtonClick: (Long, Boolean, Boolean, PurchaseBills?) -> Unit,
////    onBack: () -> Unit,
//) {
//    val backStack = rememberNavBackStack(Routes.Home)
//    fun navigate(key: NavKey) {
//        if (backStack.lastOrNull() != key) {
//            backStack.add(key)
//        }
//    }
//    NavDisplay(
//        backStack = backStack,
//        entryDecorators = listOf(
//            rememberSaveableStateHolderNavEntryDecorator(),
//            rememberViewModelStoreNavEntryDecorator()
//        ),
//        entryProvider = entryProvider {
//            entry<Routes.Home> {
//                Home(
//                    sharedViewModel = sharedViewModel,
//                    onBack = {
////                        onBack()
//                    },
//                    onViewClick = { purchaseBill ->
//                        navigate(Routes.ViewBillItems(purchaseBill))
//                    },
//                    onAddUpdateButtonClick = { updateId, isPendingBill, isPostedBill, purchaseBill ->
//                        navigate(
//                            Routes.AddUpdatePurchase(
//                                updateId,
//                                isPendingBill,
//                                isPostedBill,
//                                purchaseBill
//                            )
//                        )
//                    }
//                )
//            }
//            entry<Routes.AddUpdatePurchase> { key ->
//                PurchaseBillScreen(
//                    sharedViewModel = sharedViewModel,
//                    invoiceId = key.updateId,
//                    isPendingBill = key.isPendingBill,
//                    isPostedBill = key.isPostedBill,
//                    purchaseBill = key.purchaseBill,
//                    onBack = {
//                        backStack.removeLastOrNull()
//                    }
//                )
//            }
//            entry<Routes.ViewBillItems> { key ->
//                ViewPurchaseBillItemsScreen(
//                    invoiceId = key.purchaseBill.id!!,
//                    isPostedBill = true,
//                    onBack = {
//                        backStack.removeLastOrNull()
//                    }
//                )
//            }
//        }
//    )
//}
//
//@Composable
//private fun Home(
//    sharedViewModel: SharedViewModel,
//    onBack: () -> Unit,
//    onViewClick: (PurchaseBills) -> Unit,
//    onAddUpdateButtonClick: (Long, Boolean, Boolean, PurchaseBills?) -> Unit,
//) {
//    val tabs = listOf("Pending Bills", "Posted Bills")
//    val pagerState = rememberPagerState(
//        initialPage = 0,
//        pageCount = { tabs.size }
//    )
//
//    val snackbarHostState = remember { SnackbarHostState() }
//    Scaffold(
//        snackbarHost = {
//            AppSnackbarHost(
//                snackbarHostState = snackbarHostState,
//            )
//        },
//        topBar = {
//            TopAppBar(
//                onNavigationClick = {
//                    onBack()
//                },
//                title = "Purchase",
//            )
//        }
//    ) { innerPadding ->
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(innerPadding)
//        ) {
//            TabLayout(
//                pagerState = pagerState,
//                tabs = tabs,
//            )
//
//            HorizontalPager(
//                state = pagerState,
//                modifier = Modifier
//                    .fillMaxSize(),
//            ) { page ->
//                when (page) {
//                    0 ->
//                        PurchasePendingBillsBody(
//                            sharedViewModel = sharedViewModel,
//                            onAddUpdateButtonClick = { invoiceId, isPendingBill, purchaseBill ->
//                                onAddUpdateButtonClick(
//                                    invoiceId,
//                                    isPendingBill,
//                                    false,
//                                    purchaseBill
//                                )
//                            },
//                        )
//
//                    1 ->
//                        PurchasePostedBillsBody(
//                            sharedViewModel = sharedViewModel,
//                            onViewClick = onViewClick,
//                            onAddUpdateButtonClick = { invoiceId, isPostedBill, purchaseBill ->
//                                onAddUpdateButtonClick(invoiceId, false, isPostedBill, purchaseBill)
//                            },
//                        )
//                }
//            }
//        }
//    }
//}
