package com.example.statspos.domain.models.purchase

import kotlinx.serialization.Serializable

@Serializable
data class Purchase(
    var id: Long? = null,
    var invoiceNo: Int? = null,
    var total: Double? = null,

    var isDiscRsPer: Boolean? = null,
    var disc: Double? = null,
    var totalDisc: Double? = null,

    var expenses: Double? = null,
    var refInvoiceNo: String? = null,
    var date: String? = null,
    var time: String? = null,

    val purchaseOn:Int? = null,
    val purchaseType:Int? = null,
    var isMopCashBank: Boolean? = null,

    var bankId: Long? = null,
    var subBankId: Long? = null,
    var vendorId: Long? = null,
    var supplierId: Long? = null,
    var userId: Long? = null,
    var warehouseId: Long? = null,

    var oldBalance: Double? = null,
    var newBalance: Double? = null,
    var naration: String? = null,

    val clientId:Int? = null,
    val branchId:Int? = null,

    // Extras not part of database
    var purchaseId: Long? = null,
    var currentShiftId: Long? = null,
    var updateVendor: Boolean? = null,
    var isPostedBill: Boolean? = null,
    var isPendingBill: Boolean? = null,
    var totalItems: Int? = null,
)