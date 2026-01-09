package com.example.statspos.domain.models.sales

data class SalesItems(
    var id: Long? = null,
    var salesId: Long? = null,
    var invoiceNo: Int? = null,
    var itemId: Long? = null,
    var itemname: String? = null,
    var urduname: String? = null,

    var qty: Double? = null,
    var crtn: Int? = null,

    var cost: Double? = null,
    var rate: Double? = null,
    var retail: Double? = null,
    var wholesale: Double? = null,
    var rate3: Double? = null,
    var rate4: Double? = null,
    var crtnRate: Double? = null,
    var crtnSize: Int? = null,

    var isDiscRsPer: Boolean? = null,
    var disc: Double? = null,
    var calculatedDisc: Double? = null,
    var totalDisc: Double? = null,

    var total: Double? = null,
    var totalCost: Double? = null,
    var profit: Double? = null,

    val itemNo:Int? = null,
    val isRetail: Boolean? = null,
    val userId:Long? = null,

    val clientId:Int? = null,
    val branchId:Int? = null,

    // Extras not part of database
    var isPostedBill: Boolean? = null,
    var salesType: Int? = null,
    var isEstimated: Boolean? = null,
)