package com.example.statspos.presentation.ui.screens.main.main

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
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
import com.example.statspos.presentation.ui.screens.accounts.entries.expense.ExpenseEntryScreen
import com.example.statspos.presentation.ui.screens.accounts.entries.journal.JournalEntryScreen
import com.example.statspos.presentation.ui.screens.accounts.entries.payment.PaymentEntryScreen
import com.example.statspos.presentation.ui.screens.accounts.entries.receipt.ReceiptEntryScreen
import com.example.statspos.presentation.ui.screens.accounts.entries.stock.StockEntryScreen
import com.example.statspos.presentation.ui.screens.accounts.expenses.ExpensesScreen
import com.example.statspos.presentation.ui.screens.accounts.suppliers.SuppliersScreen
import com.example.statspos.presentation.ui.screens.accounts.vendors.VendorsScreen
import com.example.statspos.presentation.ui.screens.items.AddUpdateItemScreen
import com.example.statspos.presentation.ui.screens.items.SearchItemsScreen
import com.example.statspos.presentation.ui.screens.items.categories.CategoriesScreen
import com.example.statspos.presentation.ui.screens.items.packages.PackagesScreen
import com.example.statspos.presentation.ui.screens.main.login.CloseAppScreen
import com.example.statspos.presentation.ui.screens.purchase.purchase_bill.PurchaseBillScreen
import com.example.statspos.presentation.ui.screens.purchase.purchase_bill.ViewPurchaseBillItemsScreen
import com.example.statspos.presentation.ui.screens.purchase.purchase_orders.PurchaseOrdersScreen
import com.example.statspos.presentation.ui.screens.reports.accounts.AccountsReportsScreen
import com.example.statspos.presentation.ui.screens.reports.items.ItemsReportsScreen
import com.example.statspos.presentation.ui.screens.reports.profit.ProfitReportsScreen
import com.example.statspos.presentation.ui.screens.reports.purchase.PurchaseReportsScreen
import com.example.statspos.presentation.ui.screens.reports.sales.SalesReportsScreen
import com.example.statspos.presentation.ui.screens.reports.stock.StockReportsScreen
import com.example.statspos.presentation.ui.screens.sales.sales_bill.SalesBillScreen
import com.example.statspos.presentation.ui.screens.sales.sales_bill.ViewSalesBillItemsScreen
import com.example.statspos.presentation.ui.screens.utilities.settings.SettingsScreen
import com.example.statspos.presentation.ui.screens.utilities.users.UpdateUserScreen
import com.example.statspos.presentation.ui.screens.utilities.users.UsersScreen
import com.example.statspos.presentation.ui.screens.warehouse.gatepass.GatepassScreen
import com.example.statspos.presentation.ui.screens.warehouse.stock_transfer.StockTransferScreen
import com.example.statspos.presentation.ui.screens.warehouse.warehouse.WarehousesScreen
import com.example.statspos.presentation.viewmodels.SharedViewModel
import com.example.statspos.presentation.viewmodels.sales.orders.SalesOrdersViewModel

@Composable
fun MainScreen(
    onLogout: () -> Unit,
) {
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
                    onLogout = onLogout,
                    onTopRouteClick = { key ->
                        navigate(key)
                    },
                )
            }
            entry<TopRoutes.CloseApp> {
                CloseAppScreen()
            }
            entry<TopRoutes.SearchItem> {
                SearchItemsScreen(
                    sharedViewModel = sharedViewModel,
                    onBack = {
                        backStack.removeLastOrNull()
                    }
                )
            }
            entry<TopRoutes.UpdateUser> {
                UpdateUserScreen(
                    sharedViewModel = sharedViewModel,
                    onBack = {
                        backStack.removeLastOrNull()
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
            entry<TopRoutes.Users> {
                UsersScreen(
                    sharedViewModel = sharedViewModel,
                    onBack = {
                        backStack.removeLastOrNull()
                    }
                )
            }
            entry<TopRoutes.Settings> {
                SettingsScreen(
                    sharedViewModel = sharedViewModel,
                    onBack = {
                        backStack.removeLastOrNull()
                    }
                )
            }

            // region Sales
            entry<TopRoutes.AddUpdateSales> { key ->
                SalesBillScreen(
                    sharedViewModel = sharedViewModel,
                    invoiceId = key.updateId,
                    isPendingBill = key.isPendingBill,
                    isPostedBill = key.isPostedBill,
                    salesBill = key.salesBill,
                    onBack = {
                        backStack.removeLastOrNull()
                    }
                )
            }
            entry<TopRoutes.ViewSalesBillItems> { key ->
                ViewSalesBillItemsScreen(
                    invoiceId = key.salesBill.id!!,
                    isPostedBill = true,
                    onBack = {
                        backStack.removeLastOrNull()
                    }
                )
            }
            // endregion
            // region Purchase
            entry<TopRoutes.AddUpdatePurchase> { key ->
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
            entry<TopRoutes.ViewPurchaseBillItems> { key ->
                ViewPurchaseBillItemsScreen(
                    invoiceId = key.purchaseBill.id!!,
                    isPostedBill = true,
                    onBack = {
                        backStack.removeLastOrNull()
                    }
                )
            }
            // endregion
            // region Accounts
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
            // endregion
            // region Entries
            entry<TopRoutes.ReceiptEntry> {
                ReceiptEntryScreen(
                    onBack = {
                        backStack.removeLastOrNull()
                    }
                )
            }
            entry<TopRoutes.PaymentEntry> {
                PaymentEntryScreen(
                    onBack = {
                        backStack.removeLastOrNull()
                    }
                )
            }
            entry<TopRoutes.ExpenseEntry> {
                ExpenseEntryScreen(
                    onBack = {
                        backStack.removeLastOrNull()
                    }
                )
            }
            entry<TopRoutes.JournalEntry> {
                JournalEntryScreen(
                    onBack = {
                        backStack.removeLastOrNull()
                    }
                )
            }
            entry<TopRoutes.StockEntry> {
                StockEntryScreen(
                    mainSharedViewModel = sharedViewModel,
                    onSearchItemClick = {
                        navigate(TopRoutes.SearchItem)
                    },
                    onBack = {
                        backStack.removeLastOrNull()
                    }
                )
            }
            // endregion
            // region Warehouse
            entry<TopRoutes.Warehouses> {
                WarehousesScreen(
                    sharedViewModel = sharedViewModel,
                    onBack = {
                        backStack.removeLastOrNull()
                    }
                )
            }
            entry<TopRoutes.TransferStock> {
                StockTransferScreen(
                    sharedViewModel = sharedViewModel,
                    onBack = {
                        backStack.removeLastOrNull()
                    }
                )
            }
            entry<TopRoutes.Gatepass> {
                GatepassScreen(
                    sharedViewModel = sharedViewModel,
                    onBack = {
                        backStack.removeLastOrNull()
                    }
                )
            }
            // endregion
            // region Reports
            entry<TopRoutes.SalesReports> {
                SalesReportsScreen(
                    sharedViewModel = sharedViewModel,
                    onBack = {
                        backStack.removeLastOrNull()
                    }
                )
            }
            entry<TopRoutes.PurchaseReports> {
                PurchaseReportsScreen(
                    sharedViewModel = sharedViewModel,
                    onBack = {
                        backStack.removeLastOrNull()
                    }
                )
            }
            entry<TopRoutes.ProfitReports> {
                ProfitReportsScreen(
                    sharedViewModel = sharedViewModel,
                    onBack = {
                        backStack.removeLastOrNull()
                    }
                )
            }
            entry<TopRoutes.StockReports> {
                StockReportsScreen(
                    sharedViewModel = sharedViewModel,
                    onBack = {
                        backStack.removeLastOrNull()
                    }
                )
            }
            entry<TopRoutes.AccountsReports> {
                AccountsReportsScreen(
                    sharedViewModel = sharedViewModel,
                    onBack = {
                        backStack.removeLastOrNull()
                    }
                )
            }
            entry<TopRoutes.ItemsReports> {
                ItemsReportsScreen(
                    sharedViewModel = sharedViewModel,
                    onBack = {
                        backStack.removeLastOrNull()
                    }
                )
            }
            // endregion
        }
    )
}
