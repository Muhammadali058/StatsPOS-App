package com.example.statspos.domain.models.warehouse

data class WarehouseEntries(
    var id: Long? = null,
    var warehouseId: Long? = null,

    var type: String? = null,
    var date: String? = null,
    var time: String? = null,
    var userId: Long? = null,

    val clientId:Int? = null,
    val branchId:Int? = null,
)