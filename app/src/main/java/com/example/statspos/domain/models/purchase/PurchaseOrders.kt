package com.example.statspos.domain.models.purchase

data class PurchaseOrders(
    var id: Long? = null,
    var purchaseOrderName: String? = null,
    var remarks: String? = null,

    val clientId:Int? = null,
    val branchId:Int? = null,
)