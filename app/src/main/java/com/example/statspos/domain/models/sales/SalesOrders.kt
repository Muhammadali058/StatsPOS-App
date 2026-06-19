package com.example.statspos.domain.models.sales

data class SalesOrders(
    var id: Long? = null,
    var appUserId: Long? = null,

    var totalBill: Double? = null,
    var totalProfit: Double? = null,
    var deliveryCharges: Double? = null,
    var totalItems: Int? = null,
    var totalQty: Int? = null,

    var status: String? = null,
    var date: String? = null,
    var time: String? = null,

    val clientId:Int? = null,
    val branchId:Int? = null,
)