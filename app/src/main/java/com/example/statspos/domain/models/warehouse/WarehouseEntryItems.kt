package com.example.statspos.domain.models.warehouse

data class WarehouseEntryItems(
    var id: Long? = null,
    var warehouseEntryId: Long? = null,
    var itemId: Long? = null,

    var qty: Double? = null,
    var crtn: Int? = null,

    val clientId:Int? = null,
    val branchId:Int? = null,

    // Extras not part of database
    var itemname: String? = null,
    var imageUrl: String? = null,
)