package com.example.statspos.presentation.ui.screens.main.main

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.example.statspos.presentation.ui.screens.TopRoutes
import com.example.statspos.presentation.ui.screens.accounts.account_categories.AccountCategoriesScreen
import com.example.statspos.presentation.ui.screens.accounts.banks.BanksScreen
import com.example.statspos.presentation.ui.screens.accounts.customers.CustomersScreen
import com.example.statspos.presentation.ui.screens.accounts.expenses.ExpensesScreen
import com.example.statspos.presentation.ui.screens.accounts.suppliers.SuppliersScreen
import com.example.statspos.presentation.ui.screens.accounts.vendors.VendorsScreen
import com.example.statspos.presentation.ui.screens.items.AddUpdateItemScreen
import com.example.statspos.presentation.ui.screens.items.categories.CategoriesScreen
import com.example.statspos.presentation.ui.screens.items.packages.PackagesScreen
import com.example.statspos.presentation.ui.screens.purchase.PurchaseScreen
import com.example.statspos.presentation.ui.screens.purchase.purchase_orders.PurchaseOrdersScreen
import com.example.statspos.presentation.viewmodels.SharedViewModel

@Composable
fun MainScreen() {
    val sharedViewModel = hiltViewModel<SharedViewModel>()

    val backStack = rememberNavBackStack(TopRoutes.Home)
    val activity = LocalActivity.current as Activity
    BackHandler {
        if (backStack.size == 1) {
            activity.finish()
        }
    }
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
            entry<TopRoutes.Home> {
                HomeScreen(
                    sharedViewModel = sharedViewModel,
                    onTopRouteClick = { key ->
                        navigate(key)
                    }
                )
            }
            entry<TopRoutes.AddUpdateItem> { key ->
                AddUpdateItemScreen(
                    sharedViewModel = sharedViewModel,
                    updateId = key.updateId,
                    isUpdate = key.isUpdate,
                    onBack = {
                        backStack.removeLastOrNull()
                    },
                )
            }
            entry<TopRoutes.Categories> {
                CategoriesScreen(
                    sharedViewModel = sharedViewModel,
                    onBack = {
                        backStack.removeLastOrNull()
                    }
                )
            }
            entry<TopRoutes.Packages> {
                PackagesScreen(
                    sharedViewModel = sharedViewModel,
                    onBack = {
                        backStack.removeLastOrNull()
                    }
                )
            }
            entry<TopRoutes.PurchaseOrders> {
                PurchaseOrdersScreen(
                    sharedViewModel = sharedViewModel,
                    onBack = {
                        backStack.removeLastOrNull()
                    }
                )
            }
            entry<TopRoutes.Purchase> {
                PurchaseScreen()
            }

//            Accounts
            entry<TopRoutes.Customers> {
                CustomersScreen(
                    sharedViewModel = sharedViewModel,
                    onBack = {
                        backStack.removeLastOrNull()
                    }
                )
            }
            entry<TopRoutes.Vendors> {
                VendorsScreen(
                    sharedViewModel = sharedViewModel,
                    onBack = {
                        backStack.removeLastOrNull()
                    }
                )
            }
            entry<TopRoutes.Suppliers> {
                SuppliersScreen(
                    sharedViewModel = sharedViewModel,
                    onBack = {
                        backStack.removeLastOrNull()
                    }
                )
            }
            entry<TopRoutes.Banks> {
                BanksScreen(
                    sharedViewModel = sharedViewModel,
                    onBack = {
                        backStack.removeLastOrNull()
                    }
                )
            }
            entry<TopRoutes.Expenses> {
                ExpensesScreen(
                    sharedViewModel = sharedViewModel,
                    onBack = {
                        backStack.removeLastOrNull()
                    }
                )
            }
            entry<TopRoutes.AccountCategories> {
                AccountCategoriesScreen(
                    sharedViewModel = sharedViewModel,
                    onBack = {
                        backStack.removeLastOrNull()
                    }
                )
            }
            entry<TopRoutes.AddSales> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Blue),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Add Sales")
                }
            }
        }
    )
}
