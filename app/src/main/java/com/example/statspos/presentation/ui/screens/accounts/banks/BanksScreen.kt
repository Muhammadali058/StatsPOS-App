package com.example.statspos.presentation.ui.screens.accounts.banks

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
import com.example.statspos.presentation.ui.screens.accounts.expenses.AddUpdateExpenseScreen
import com.example.statspos.presentation.ui.screens.accounts.expenses.AddUpdateSubExpenseScreen
import com.example.statspos.presentation.ui.screens.accounts.expenses.ExpensesBody
import com.example.statspos.presentation.ui.screens.accounts.expenses.SubExpensesBody
import com.example.statspos.presentation.viewmodels.SharedViewModel
import kotlinx.serialization.Serializable

private sealed class Routes : NavKey {
    @Serializable
    data object Home : Routes()

    @Serializable
    data class AddUpdateBank(val updateId: Long, val isUpdate: Boolean) : Routes()

    @Serializable
    data class AddUpdateSubBank(val updateId: Long, val isUpdate: Boolean, val bankId: Long) :
        Routes()
}

@Composable
fun BanksScreen(
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
                    addUpdateBankClick = { updateId, isUpdated ->
                        navigate(Routes.AddUpdateBank(updateId, isUpdated))
                    },
                    addUpdateSubBankClick = { updateId, isUpdated, packageId ->
                        navigate(Routes.AddUpdateSubBank(updateId, isUpdated, packageId))
                    }
                )
            }
            entry<Routes.AddUpdateBank> { key ->
                AddUpdateBankScreen(
                    sharedViewModel = sharedViewModel,
                    updateId = key.updateId,
                    isUpdate = key.isUpdate,
                    onBack = {
                        backStack.removeLastOrNull()
                    }
                )
            }
            entry<Routes.AddUpdateSubBank> { key ->
                AddUpdateSubBankScreen(
                    sharedViewModel = sharedViewModel,
                    updateId = key.updateId,
                    isUpdate = key.isUpdate,
                    bankId = key.bankId,
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
    addUpdateBankClick: (Long, Boolean) -> Unit,
    addUpdateSubBankClick: (Long, Boolean, Long) -> Unit,
) {
    val tabs = listOf("Banks", "Bank Accounts")
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
                title = "Banks",
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
                        BanksBody(
                            sharedViewModel = sharedViewModel,
                            onAddButtonClick = { updateId, isUpdated ->
                                addUpdateBankClick(updateId, isUpdated)
                            },
                        )

                    1 ->
                        SubBanksBody(
                            sharedViewModel = sharedViewModel,
                            onAddButtonClick = { updateId, isUpdated, bankId ->
                                addUpdateSubBankClick(updateId, isUpdated, bankId)
                            },
                        )
                }
            }
        }
    }
}
