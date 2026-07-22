package com.graphees.statspos.presentation.ui.screens.main.main

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.graphees.statspos.presentation.ui.components.ErrorDialog
import com.graphees.statspos.presentation.ui.screens.TopRoutes
import com.graphees.statspos.presentation.ui.screens.accounts.account_categories.AccountCategoriesScreen
import com.graphees.statspos.presentation.ui.screens.accounts.banks.BanksScreen
import com.graphees.statspos.presentation.ui.screens.accounts.customers.CustomersScreen
import com.graphees.statspos.presentation.ui.screens.accounts.entries.expense.ExpenseEntryScreen
import com.graphees.statspos.presentation.ui.screens.accounts.entries.journal.JournalEntryScreen
import com.graphees.statspos.presentation.ui.screens.accounts.entries.payment.PaymentEntryScreen
import com.graphees.statspos.presentation.ui.screens.accounts.entries.receipt.ReceiptEntryScreen
import com.graphees.statspos.presentation.ui.screens.accounts.entries.stock.StockEntryScreen
import com.graphees.statspos.presentation.ui.screens.accounts.expenses.ExpensesScreen
import com.graphees.statspos.presentation.ui.screens.accounts.suppliers.SuppliersScreen
import com.graphees.statspos.presentation.ui.screens.accounts.vendors.VendorsScreen
import com.graphees.statspos.presentation.ui.screens.items.AddUpdateItemScreen
import com.graphees.statspos.presentation.ui.screens.items.SearchItemsScreen
import com.graphees.statspos.presentation.ui.screens.items.categories.CategoriesScreen
import com.graphees.statspos.presentation.ui.screens.items.packages.PackagesScreen
import com.graphees.statspos.presentation.ui.screens.main.login.CloseAppScreen
import com.graphees.statspos.presentation.ui.screens.main.main.premium.HelpScreen
import com.graphees.statspos.presentation.ui.screens.main.main.premium.PaymentScreen
import com.graphees.statspos.presentation.ui.screens.main.main.premium.SubscriptionsScreen
import com.graphees.statspos.presentation.ui.screens.purchase.purchase_bill.PurchaseBillScreen
import com.graphees.statspos.presentation.ui.screens.purchase.purchase_bill.ViewPurchaseBillItemsScreen
import com.graphees.statspos.presentation.ui.screens.purchase.purchase_orders.PurchaseOrdersScreen
import com.graphees.statspos.presentation.ui.screens.reports.accounts.AccountsReportsScreen
import com.graphees.statspos.presentation.ui.screens.reports.items.ItemsReportsScreen
import com.graphees.statspos.presentation.ui.screens.reports.profit.ProfitReportsScreen
import com.graphees.statspos.presentation.ui.screens.reports.purchase.PurchaseReportsScreen
import com.graphees.statspos.presentation.ui.screens.reports.sales.SalesReportsScreen
import com.graphees.statspos.presentation.ui.screens.reports.stock.StockReportsScreen
import com.graphees.statspos.presentation.ui.screens.sales.sales_bill.SalesBillScreen
import com.graphees.statspos.presentation.ui.screens.sales.sales_bill.ViewSalesBillItemsScreen
import com.graphees.statspos.presentation.ui.screens.shopping_app.ShoppingAppScreen
import com.graphees.statspos.presentation.ui.screens.shopping_app.sales_orders.SalesOrdersScreen
import com.graphees.statspos.presentation.ui.screens.utilities.settings.SettingsScreen
import com.graphees.statspos.presentation.ui.screens.utilities.users.UpdateUserScreen
import com.graphees.statspos.presentation.ui.screens.utilities.users.UsersScreen
import com.graphees.statspos.presentation.ui.screens.warehouse.gatepass.GatepassScreen
import com.graphees.statspos.presentation.ui.screens.warehouse.stock_transfer.StockTransferScreen
import com.graphees.statspos.presentation.ui.screens.warehouse.warehouse.WarehousesScreen
import com.graphees.statspos.presentation.viewmodels.SharedViewModel
import com.graphees.statspos.presentation.viewmodels.main.MainViewModel
import com.graphees.statspos.utils.UiEvent
import com.graphees.statspos.utils.checkEvent

@Composable
fun MainScreen(
    onLogout: () -> Unit,
) {
    // region Request permission for notifications
    val context = LocalContext.current
    val activity = LocalActivity.current as Activity
    val notificationPermissionLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted -> }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

            if (ContextCompat.checkSelfPermission(
                    activity,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                notificationPermissionLauncher.launch(
                    Manifest.permission.POST_NOTIFICATIONS
                )
            }
        }
    }
    // endregion

    val viewModel = hiltViewModel<MainViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val event by viewModel.event.collectAsState(UiEvent.Idle)
    val snackbarHostState = remember { SnackbarHostState() }
    var showErrorDialog by remember { mutableStateOf(false) }
    LaunchedEffect(event) {
        checkEvent(
            event = event,
            snackbarHostState = snackbarHostState,
            viewModelIdleEvent = viewModel::onEvent,
            onError = {
                showErrorDialog = true
            }
        )
    }

    if (showErrorDialog) {
        ErrorDialog(
            error = state.error,
            onDismiss = {
                showErrorDialog = false
            },
        )
    }

    val sharedViewModel = hiltViewModel<SharedViewModel>()
    val backStack = rememberNavBackStack(TopRoutes.Home)
//    val activity = LocalActivity.current as Activity
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
            entry<TopRoutes.Payment> {
                PaymentScreen(
                    onBack = {
                        backStack.removeLastOrNull()
                    },
                )
            }
            entry<TopRoutes.Subscriptions> {
                SubscriptionsScreen (
                    onBack = {
                        backStack.removeLastOrNull()
                    },
                    onHelpClick = {
                        navigate(TopRoutes.Help)
                    },
                    onPayNowClick = {
                        navigate(TopRoutes.Payment)
                    },
                )
            }
            entry<TopRoutes.Help> {
                HelpScreen(
                    onBack = {
                        backStack.removeLastOrNull()
                    },
                )
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
            entry<TopRoutes.ShoppingApp> {
                ShoppingAppScreen (
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
                    salesBill = key.salesBill,
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
                    purchaseBill = key.purchaseBill,
                    isPostedBill = true,
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
