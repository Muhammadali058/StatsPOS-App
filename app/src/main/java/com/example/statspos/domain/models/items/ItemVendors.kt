package com.example.statspos.domain.models.items

data class ItemVendors(
    var id: Long? = null,
    var itemId: Long? = null,
    var vendorId: Long? = null,

    val clientId:Int? = null,
    val branchId:Int? = null,
)