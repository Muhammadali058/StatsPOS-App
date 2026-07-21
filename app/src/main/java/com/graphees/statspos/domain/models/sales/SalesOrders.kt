package com.graphees.statspos.domain.models.sales

import kotlinx.serialization.Serializable

@Serializable
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

    var address: String? = null,
    var latitude: Double? = null,
    var longitude: Double? = null,

    val clientId:Int? = null,
    val branchId:Int? = null,

    // Extras
    val accountName:String? = null,
    val contact:String? = null,
)