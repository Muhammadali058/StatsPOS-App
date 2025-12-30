package com.example.statspos.domain.models.purchase

data class RejectedItems(
    var id: Long? = null,
    var itemId: Long? = null,

    var qty: Double? = null,
    var crtn: Int? = null,
    var date: String? = null,

    val clientId:Int? = null,
    val branchId:Int? = null,

    // Extras not part of database
    var itemname: String? = null,
    var vendorName: String? = null,
)