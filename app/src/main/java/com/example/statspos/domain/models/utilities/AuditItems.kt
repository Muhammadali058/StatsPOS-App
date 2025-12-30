package com.example.statspos.domain.models.utilities

data class AuditItems(
    var id: Long? = null,
    var warehouseId: Long? = null,
    var itemId: Long? = null,

    var currentStockPcs: Double? = null,
    var currentStockCrtn: Long? = null,
    var physicalStockPcs: Double? = null,
    var physicalStockCrtn: Long? = null,
    var stockDifference: Double? = null,

    val clientId:Int? = null,
    val branchId:Int? = null,

    // Extras not part of database
    val itemname:String? = null,
    val crtnSize:Int? = null,
)