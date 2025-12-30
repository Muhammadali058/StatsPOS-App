package com.example.statspos.domain.models.utilities.settings

data class Passwords(
    var id: Long? = null,
    var deleteItem: String? = null,
    var deleteAccount: String? = null,
    var editSalesBill: String? = null,
    var editPurchaseBill: String? = null,
    var deleteSalesBill: String? = null,
    var deletePurchaseBill: String? = null,
    var deleteEntry: String? = null,

    var useDeleteItem: Boolean? = null,
    var useDeleteAccount: Boolean? = null,
    var useEditSalesBill: Boolean? = null,
    var useEditPurchaseBill: Boolean? = null,
    var useDeleteSalesBill: Boolean? = null,
    var useDeletePurchaseBill: Boolean? = null,
    var useDeleteEntry: Boolean? = null,

    val clientId:Int? = null,
    val branchId:Int? = null,
)