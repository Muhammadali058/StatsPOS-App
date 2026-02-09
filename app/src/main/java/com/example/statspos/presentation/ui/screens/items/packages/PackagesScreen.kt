package com.example.statspos.presentation.ui.screens.items.packages

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
    data class AddUpdatePackage(val updateId: Long, val isUpdate: Boolean) : Routes()

    @Serializable
    data class AddUpdatePackageItem(val updateId: Long, val isUpdate: Boolean, val packageId: Long) : Routes()

    @Serializable
    data object SearchItem : Routes()
}

@Composable
fun PackagesScreen(
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
                AddUpdatePackageItemScreen (
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
    onBack: () -> Unit,
    addUpdatePackageClick: (Long, Boolean) -> Unit,
    addUpdatePackageItemClick: (Long, Boolean, Long) -> Unit,
) {
    val tabs = listOf("Packages", "Package Items")
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
                title = "Packages",
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
                        PackagesBody (
                            sharedViewModel = sharedViewModel,
                            onAddButtonClick = { updateId, isUpdated ->
                                addUpdatePackageClick(updateId, isUpdated)
                            },
                        )

                    1 ->
                        PackageItemsBody (
                            sharedViewModel = sharedViewModel,
                            onAddButtonClick = { updateId, isUpdated, packageId ->
                                addUpdatePackageItemClick(updateId, isUpdated, packageId)
                            },
                        )
                }
            }
        }
    }
}
