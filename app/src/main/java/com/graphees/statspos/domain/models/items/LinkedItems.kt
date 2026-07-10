package com.graphees.statspos.domain.models.items

data class LinkedItems(
    var id: Long? = null,
    var itemId: Long? = null,
    var linkedItemId: Long? = null,

    var updateCost: Boolean? = null,
    var updateRetail: Boolean? = null,
    var updateWholesale: Boolean? = null,
    var updateCrtnRate: Boolean? = null,
    var updateCrtnSize: Boolean? = null,
    var updateMarketPrice: Boolean? = null,
    var updateExpiry: Boolean? = null,
    var rateFormula: String? = null,

    val clientId:Int? = null,
    val branchId:Int? = null,
    val branchGroupId:Int? = null,

    // Extras not part of database
    var itemname: String? = null,
    var imageUrl: String? = null,
    val crtnSize:Int? = null,
)