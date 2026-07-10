package com.graphees.statspos.domain.models.warehouse

data class WarehouseStock(
    var id: Long? = null,
    var warehouseEntryId: Long? = null,
    var itemId: Long? = null,

    var stockPcs: Double? = null,
    var stockCrtn: Int? = null,

    val clientId:Int? = null,
    val branchId:Int? = null,
)