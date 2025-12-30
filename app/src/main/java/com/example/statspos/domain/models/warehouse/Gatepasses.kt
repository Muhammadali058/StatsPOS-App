package com.example.statspos.domain.models.warehouse

data class Gatepasses(
    var id: Long? = null,
    var warehouseId: Long? = null,
    var gatepassName: String? = null,
    var remarks: String? = null,
    var date: String? = null,
    var time: String? = null,

    val clientId:Int? = null,
    val branchId:Int? = null,
)