package com.example.statspos.presentation.ui.screens

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

sealed class BottomRoutes : NavKey{
    @Serializable
    data object Home : BottomRoutes()

    @Serializable
    data object Items : BottomRoutes()

    @Serializable
    data object Sales : BottomRoutes()

    @Serializable
    data object Reports : BottomRoutes()

}

sealed class TopRoutes : NavKey{
    @Serializable
    data object Home : TopRoutes()

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

    @Serializable
    data object AddSales : BottomRoutes()

//    Accounts
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

//    Entries
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


//    Warehouse
    @Serializable
    data object Warehouses : TopRoutes()

}