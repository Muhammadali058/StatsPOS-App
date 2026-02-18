package com.example.statspos.presentation.ui.screens.sales

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
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.example.statspos.presentation.ui.components.AppSnackbarHost
import com.example.statspos.presentation.ui.components.TabLayout
import com.example.statspos.presentation.ui.components.TopAppBar
import com.example.statspos.presentation.ui.screens.items.SearchItemsScreen
import com.example.statspos.presentation.ui.screens.items.packages.AddUpdatePackageItemScreen
import com.example.statspos.presentation.ui.screens.items.packages.AddUpdatePackageScreen
import com.example.statspos.presentation.ui.screens.items.packages.PackageItemsBody
import com.example.statspos.presentation.ui.screens.items.packages.PackagesBody
import com.example.statspos.presentation.viewmodels.SharedViewModel
import kotlinx.serialization.Serializable

private sealed class Routes : NavKey {
    @Serializable
    data object Home : Routes()

    @Serializable
    data class AddUpdatePackage(val updateId: Long, val isUpdate: Boolean) : Routes()

    @Serializable
    data class AddUpdatePackageItem(
        val updateId: Long,
        val isUpdate: Boolean,
        val packageId: Long
    ) : Routes()

    @Serializable
    data object SearchItem : Routes()
}

@Composable
fun SalesScreen(
    sharedViewModel: SharedViewModel,
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
                    addUpdatePackageClick = { updateId, isUpdated ->
                        navigate(Routes.AddUpdatePackage(updateId, isUpdated))
                    },
                    addUpdatePackageItemClick = { updateId, isUpdated, packageId ->
                        navigate(Routes.AddUpdatePackageItem(updateId, isUpdated, packageId))
                    }
                )
            }
            entry<Routes.AddUpdatePackage> { key ->
                AddUpdatePackageScreen(
                    sharedViewModel = sharedViewModel,
                    updateId = key.updateId,
                    isUpdate = key.isUpdate,
                    onBack = {
                        backStack.removeLastOrNull()
                    }
                )
            }
            entry<Routes.AddUpdatePackageItem> { key ->
                AddUpdatePackageItemScreen(
                    sharedViewModel = sharedViewModel,
                    updateId = key.updateId,
                    isUpdate = key.isUpdate,
                    packageId = key.packageId,
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
    addUpdatePackageClick: (Long, Boolean) -> Unit,
    addUpdatePackageItemClick: (Long, Boolean, Long) -> Unit,
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
                        PendingBillsBody(
                            sharedViewModel = sharedViewModel,
                            onItemClick = { salesBill ->

                            },
                            onAddButtonClick = { updateId, isUpdated ->
                                addUpdatePackageClick(updateId, isUpdated)
                            },
                        )

                    1 ->
                        PostedBillsBody(
                            sharedViewModel = sharedViewModel,
                            onItemClick = { salesBill ->

                            },
                            onAddButtonClick = { updateId, isUpdated ->
                                addUpdatePackageClick(updateId, isUpdated)
                            },
                        )
                }
            }
        }
    }
}
