package com.graphees.statspos.domain.models.purchase

data class PurchaseOrderItems(
    var id: Long? = null,
    var purchaseOrderId: Long? = null,
    var itemId: Long? = null,

    var qty: Double? = null,
    var crtn: Int? = null,
    var cost: Double? = null,
    var total: Double? = null,

    val clientId:Int? = null,
    val branchId:Int? = null,

    // Extras not part of database
    var itemname: String? = null,
    var stockPcs: Double? = null,
    var stockCrtn: Long? = null,
    val crtnSize:Int? = null,
    var imageUrl: String? = null,
)