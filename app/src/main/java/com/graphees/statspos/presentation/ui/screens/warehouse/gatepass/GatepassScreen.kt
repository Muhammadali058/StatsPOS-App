package com.graphees.statspos.presentation.ui.screens.warehouse.gatepass

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
    data class AddUpdateGatepass(val updateId: Long, val isUpdate: Boolean, val warehouseId: Long, val date: String) : Routes()

    @Serializable
    data class AddUpdateGatepassItem(val updateId: Long, val isUpdate: Boolean, val gatepassId: Long) : Routes()

    @Serializable
    data object SearchItem : Routes()
}

@Composable
fun GatepassScreen(
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
                    addUpdateGatepassClick = { updateId, isUpdated, warehouseId, date ->
                        navigate(Routes.AddUpdateGatepass(updateId, isUpdated, warehouseId, date))
                    },
                    addUpdateGatepassItemClick = { updateId, isUpdated, packageId ->
                        navigate(Routes.AddUpdateGatepassItem(updateId, isUpdated, packageId))
                    }
                )
            }
            entry<Routes.AddUpdateGatepass> { key ->
                AddUpdateGatepassScreen(
                    sharedViewModel = sharedViewModel,
                    updateId = key.updateId,
                    isUpdate = key.isUpdate,
                    date = key.date,
                    warehouseId = key.warehouseId,
                    onBack = {
                        backStack.removeLastOrNull()
                    }
                )
            }
            entry<Routes.AddUpdateGatepassItem> { key ->
                AddUpdateGatepassItemScreen(
                    sharedViewModel = sharedViewModel,
                    updateId = key.updateId,
                    isUpdate = key.isUpdate,
                    gatepassId = key.gatepassId,
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
    addUpdateGatepassClick: (Long, Boolean, Long, String) -> Unit,
    addUpdateGatepassItemClick: (Long, Boolean, Long) -> Unit,
) {
    val tabs = listOf("Gatepass", "Gatepass Items")
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
                title = "Gatepass",
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
                        GatepassBody(
                            sharedViewModel = sharedViewModel,
                            snackbarHostState = snackbarHostState,
                            onAddButtonClick = { updateId, isUpdated, warehouseId, date ->
                                addUpdateGatepassClick(updateId, isUpdated, warehouseId, date)
                            },
                        )

                    1 ->
                        GatepassItemsBody(
                            sharedViewModel = sharedViewModel,
                            snackbarHostState = snackbarHostState,
                            onAddButtonClick = { updateId, isUpdated, packageId ->
                                addUpdateGatepassItemClick(updateId, isUpdated, packageId)
                            },
                        )
                }
            }
        }
    }
}
