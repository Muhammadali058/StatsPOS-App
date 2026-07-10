package com.graphees.statspos.presentation.ui.screens.warehouse.stock_transfer

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
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.graphees.statspos.presentation.ui.components.AppSnackbarHost
import com.graphees.statspos.presentation.ui.components.TabLayout
import com.graphees.statspos.presentation.ui.components.TopAppBar
import com.graphees.statspos.presentation.ui.screens.items.SearchItemsScreen
import com.graphees.statspos.presentation.viewmodels.SharedViewModel
import kotlinx.serialization.Serializable

private sealed class Routes : NavKey {
    @Serializable
    data object Home : Routes()

    @Serializable
    data class AddUpdateStockTransferItem(
        val updateId: Long,
        val isUpdate: Boolean,
        val warehouseId: Long
    ) : Routes()

    @Serializable
    data class WarehouseEntryItems(val warehouseEntryId: Long) : Routes()

    @Serializable
    data object SearchItem : Routes()
}

@Composable
fun StockTransferScreen(
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
                    addUpdateStockTransferItemClick = { updateId, isUpdated, warehouseId ->
                        navigate(
                            Routes.AddUpdateStockTransferItem(
                                updateId,
                                isUpdated,
                                warehouseId
                            )
                        )
                    },
                    onWarehouseEntryClick = { warehouseEntryId ->
                        navigate(Routes.WarehouseEntryItems(warehouseEntryId))
                    }
                )
            }
            entry<Routes.AddUpdateStockTransferItem> { key ->
                AddUpdateStockTransferItemScreen(
                    sharedViewModel = sharedViewModel,
                    updateId = key.updateId,
                    isUpdate = key.isUpdate,
                    warehouseId = key.warehouseId,
                    onSearchItemClick = {
                        navigate(Routes.SearchItem)
                    },
                    onBack = {
                        backStack.removeLastOrNull()
                    }
                )
            }
            entry<Routes.WarehouseEntryItems> { key ->
                WarehouseEntryItemsBody(
                    warehouseEntryId = key.warehouseEntryId,
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
    addUpdateStockTransferItemClick: (Long, Boolean, Long) -> Unit,
    onWarehouseEntryClick: (Long) -> Unit,
) {
    val tabs = listOf("Transfer Entry", "Posted Entries")
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
                title = "Stock Transfer",
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
                        NewStockTransferEntryBody(
                            sharedViewModel = sharedViewModel,
                            snackbarHostState = snackbarHostState,
                            onAddButtonClick = { updateId, isUpdated, warehouseId ->
                                addUpdateStockTransferItemClick(updateId, isUpdated, warehouseId)
                            },
                        )

                    1 ->
                        WarehouseEntriesBody(
                            sharedViewModel = sharedViewModel,
                            snackbarHostState = snackbarHostState,
                            onClick = { warehouseEntryId ->
                                onWarehouseEntryClick(warehouseEntryId)
                            },
                        )
                }
            }
        }
    }
}
