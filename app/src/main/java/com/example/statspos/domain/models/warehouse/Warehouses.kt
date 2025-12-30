package com.example.statspos.domain.models.warehouse

data class Warehouses(
    var id: Long? = null,
    var warehouseName: String? = null,
    var remarks: String? = null,

    val clientId:Int? = null,
    val branchId:Int? = null,
)