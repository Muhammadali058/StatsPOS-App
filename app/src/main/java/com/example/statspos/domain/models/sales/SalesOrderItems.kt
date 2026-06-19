package com.example.statspos.domain.models.sales

data class SalesOrderItems(
    var id: Long? = null,
    var salesOrderId: Long? = null,
    var itemId: Long? = null,

    var qty: Double? = null,
    var rate: Double? = null,
    var marketPrice: Double? = null,
    var isDiscRsPer: Boolean? = null,
    var disc: Double? = null,
    var total: Double? = null,

    val clientId:Int? = null,
    val branchId:Int? = null,

    // Extras
    var itemname: String? = null,
    var imageUrl: String? = null,
)