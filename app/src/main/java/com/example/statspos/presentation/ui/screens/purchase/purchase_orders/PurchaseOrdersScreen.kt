package com.example.statspos.presentation.ui.screens.purchase.purchase_orders

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.example.statspos.presentation.ui.components.TabLayout
import com.example.statspos.presentation.ui.components.TopAppBar
import com.example.statspos.presentation.ui.screens.items.SearchItemsScreen
import com.example.statspos.presentation.viewmodels.SharedViewModel
import kotlinx.serialization.Serializable

private sealed class Routes : NavKey {
    @Serializable
    data object Home : Routes()

    @Serializable
    data class AddUpdatePurchaseOrder(val updateId: Long, val isUpdate: Boolean) : Routes()

    @Serializable
    data class AddUpdatePurchaseOrderItem(val updateId: Long, val isUpdate: Boolean, val purchaseOrderId: Long) : Routes()

    @Serializable
    data object SearchItem : Routes()
}

@Composable
fun PurchaseOrdersScreen(
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
                    addUpdatePurchaseOrderClick = { updateId, isUpdated ->
                        navigate(Routes.AddUpdatePurchaseOrder(updateId, isUpdated))
                    },
                    addUpdatePurchaseOrderItemClick = { updateId, isUpdated, purchaseOrderId ->
                        navigate(Routes.AddUpdatePurchaseOrderItem(updateId, isUpdated, purchaseOrderId))
                    }
                )
            }
            entry<Routes.AddUpdatePurchaseOrder> { key ->
                AddUpdatePurchaseOrderScreen(
                    sharedViewModel = sharedViewModel,
                    updateId = key.updateId,
                    isUpdate = key.isUpdate,
                    onBack = {
                        backStack.removeLastOrNull()
                    }
                )
            }
            entry<Routes.AddUpdatePurchaseOrderItem> { key ->
                AddUpdatePurchaseOrderItemScreen (
                    sharedViewModel = sharedViewModel,
                    updateId = key.updateId,
                    isUpdate = key.isUpdate,
                    purchaseOrderId = key.purchaseOrderId,
                    onSearchItemClick = {
                        navigate(Routes.SearchItem)
                    },
                    onBack = {
                        backStack.removeLastOrNull()
                    }
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

@Composable
private fun Home(
    sharedViewModel: SharedViewModel,
    onBack: () -> Unit,
    addUpdatePurchaseOrderClick: (Long, Boolean) -> Unit,
    addUpdatePurchaseOrderItemClick: (Long, Boolean, Long) -> Unit,
) {
    val tabs = listOf("Orders", "Order Items")
    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { tabs.size }
    )

    Scaffold(
        topBar = {
            TopAppBar(
                onNavigationClick = {
                    onBack()
                },
                title = "Purchase Orders",
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
                        PurchaseOrdersBody (
                            sharedViewModel = sharedViewModel,
                            onAddButtonClick = { updateId, isUpdated ->
                                addUpdatePurchaseOrderClick(updateId, isUpdated)
                            },
                        )

                    1 ->
                        PurchaseOrderItemsBody (
                            sharedViewModel = sharedViewModel,
                            onAddButtonClick = { updateId, isUpdated, purchaseOrderId ->
                                addUpdatePurchaseOrderItemClick(updateId, isUpdated, purchaseOrderId)
                            },
                        )
                }
            }
        }
    }
}
