package com.graphees.statspos.domain.models.accounts

data class StockAdjustment(
    var id: Long? = null,
    var itemId: Long? = null,
    var stock: Double? = null,

    val clientId:Int? = null,
    val branchId:Int? = null,
)