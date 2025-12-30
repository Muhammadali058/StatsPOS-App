package com.example.statspos.domain.models.purchase

data class PurchaseItems(
    var id: Long? = null,
    var purchaseId: Long? = null,
    var invoiceNo: Int? = null,
    var itemId: Long? = null,
    var itemname: String? = null,

    var qty: Double? = null,
    var crtn: Int? = null,

    var cost: Double? = null,
    var finalCost: Double? = null,
    var retail: Double? = null,
    var wholesale: Double? = null,
    var rate3: Double? = null,
    var rate4: Double? = null,
    var crtnRate: Double? = null,
    var crtnSize: Int? = null,
    var marketPrice: Double? = null,

    var isDiscRsPer: Boolean? = null,
    var disc: Double? = null,
    var calculatedDisc: Double? = null,
    var totalDisc: Double? = null,

    var tax: Double? = null,
    var totalTax: Double? = null,

    var total: Double? = null,
    var expiry: String? = null,

    var isNewStock: Boolean? = null,
    var lockPcs: Boolean? = null,
    var lockCrtn: Boolean? = null,

    val itemNo:Int? = null,
    val userId:Long? = null,

    val clientId:Int? = null,
    val branchId:Int? = null,

    // Extras not part of database
    var isPostedBill: Boolean? = null,
)