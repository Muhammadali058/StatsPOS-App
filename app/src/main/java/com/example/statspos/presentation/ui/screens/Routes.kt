package com.example.statspos.presentation.ui.screens

import androidx.navigation3.runtime.NavKey
import com.example.statspos.domain.models.sales.Sales
import com.example.statspos.domain.models.sales.SalesBills
import kotlinx.serialization.Serializable

sealed class BottomRoutes : NavKey {
    @Serializable
    data object Home : BottomRoutes()

    @Serializable
    data object Items : BottomRoutes()

    @Serializable
    data object Sales : BottomRoutes()

    @Serializable
    data object Reports : BottomRoutes()

}

sealed class TopRoutes : NavKey {
    @Serializable
    data object Home : TopRoutes()

    // region Top
    @Serializable
    data object SearchItem : BottomRoutes()

    @Serializable
    data class AddUpdateItem(val updateId: Long, val isUpdate: Boolean) : TopRoutes()

    @Serializable
    data object Purchase : TopRoutes()

    @Serializable
    data object Categories : TopRoutes()

    @Serializable
    data object Packages : TopRoutes()

    @Serializable
    data object PurchaseOrders : TopRoutes()

    @Serializable
    data object Users : TopRoutes()

    @Serializable
    data object Settings : TopRoutes()

    // endregion
    // region Sales
    @Serializable
    data class AddUpdateSales(
        val updateId: Long,
        val isPendingBill: Boolean,
        val isPostedBill: Boolean,
        val salesBill: SalesBills?
    ) : TopRoutes()

    @Serializable
    data class ViewBillItems(val salesBill: SalesBills) : TopRoutes()

    // endregion
    // region Accounts
    @Serializable
    data object Customers : TopRoutes()

    @Serializable
    data object Vendors : TopRoutes()

    @Serializable
    data object Suppliers : TopRoutes()

    @Serializable
    data object Banks : TopRoutes()

    @Serializable
    data object Expenses : TopRoutes()

    @Serializable
    data object AccountCategories : TopRoutes()

    // endregion
    // region Entries
    @Serializable
    data object ReceiptEntry : TopRoutes()

    @Serializable
    data object PaymentEntry : TopRoutes()

    @Serializable
    data object ExpenseEntry : TopRoutes()

    @Serializable
    data object JournalEntry : TopRoutes()

    @Serializable
    data object StockEntry : TopRoutes()

    // endregion
    // region Warehouse
    @Serializable
    data object Warehouses : TopRoutes()

    @Serializable
    data object TransferStock : TopRoutes()

    @Serializable
    data object Gatepass : TopRoutes()
    // endregion
    // region Reports
    @Serializable
    data object SalesReports : TopRoutes()

    @Serializable
    data object PurchaseReports : TopRoutes()

    @Serializable
    data object ProfitReports : TopRoutes()

    @Serializable
    data object StockReports : TopRoutes()

    @Serializable
    data object AccountsReports : TopRoutes()

    @Serializable
    data object ItemsReports : TopRoutes()

    @Serializable
    data object AuditReports : TopRoutes()
    // endregion
}