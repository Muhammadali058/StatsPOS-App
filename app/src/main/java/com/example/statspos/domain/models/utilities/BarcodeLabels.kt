package com.example.statspos.domain.models.utilities

data class BarcodeLabels(
    var id: Long? = null,
    var itemId: Long? = null,
    var barcode: String? = null,
    var itemname: String? = null,

    var qty: Int? = null,
    var rate: Double? = null,
    var crtnRate: Double? = null,
    var marketPrice: Double? = null,
    val expiry:String? = null,
    var userId: Long? = null,

    val clientId:Int? = null,
    val branchId:Int? = null,

    // Extras not part of database
    val urduname:String? = null,
    val originalItemname:String? = null,
)