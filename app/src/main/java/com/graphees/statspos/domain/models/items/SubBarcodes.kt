package com.graphees.statspos.domain.models.items

data class SubBarcodes(
    var id: Long? = null,
    var itemId: Long? = null,
    var subBarcode: String? = null,
    var isCrtn: Boolean? = null,

    val clientId:Int? = null,
    val branchId:Int? = null,
    val branchGroupId:Int? = null,
)