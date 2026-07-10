package com.graphees.statspos.domain.models.warehouse

data class StockEntries(
    var id: Long? = null,
    var warehouseId: Long? = null,
    var itemId: Long? = null,
    var qty: Double? = null,
    var crtn: Int? = null,

    val clientId:Int? = null,
    val branchId:Int? = null,

    // Extras not part of database
    var itemname: String? = null,
    var imageUrl: String? = null,
    var stockPcs: Double? = null,
    var stockCrtn: Long? = null,
    var wStockPcs: Double? = null,
    var wStockCrtn: Long? = null,
    val crtnSize:Int? = null,
)