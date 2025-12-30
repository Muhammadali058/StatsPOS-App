package com.example.statspos.domain.models.items

data class PackageItems(
    var id: Long? = null,
    var packageId: Long? = null,
    var itemId: Long? = null,
    var qty: Double? = null,
    var rate: Double? = null,

    val clientId:Int? = null,
    val branchId:Int? = null,

    // Extras not part of database
    var itemname: String? = null,
)