package com.graphees.statspos.domain.models.items

data class ItemsRateChanged(
    var id: Long? = null,
    var itemId: Long? = null,
    var userId: Long? = null,

    var oldCost: Double? = null,
    var oldRetail: Double? = null,
    var oldWholesale: Double? = null,
    var oldCrtnRate: Double? = null,
    var oldCrtnSize: Int? = null,

    var newCost: Double? = null,
    var newRetail: Double? = null,
    var newWholesale: Double? = null,
    var newCrtnRate: Double? = null,
    var newCrtnSize: Int? = null,

    var date: String? = null,

    val clientId:Int? = null,
    val branchId:Int? = null,
)