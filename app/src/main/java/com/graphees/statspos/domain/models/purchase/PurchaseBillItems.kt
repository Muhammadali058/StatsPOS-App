package com.graphees.statspos.domain.models.purchase

import kotlinx.serialization.Serializable

@Serializable
data class PurchaseBillItems(
    var sr: Int? = null,
    var id: Long? = null,
    var itemname: String? = null,

    var qty: Double? = null,
    var crtn: Int? = null,
    var finalCost: Double? = null,
    var cost: Double? = null,
    var calculatedDisc: Double? = null,
    var tax: Double? = null,
    var total: Double? = null,
    var crtnSize: Int? = null,
    var imageUrl: String? = null,
)