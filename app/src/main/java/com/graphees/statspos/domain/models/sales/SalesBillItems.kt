package com.graphees.statspos.domain.models.sales

import kotlinx.serialization.Serializable

@Serializable
data class SalesBillItems(
    var sr: Int? = null,
    var id: Long? = null,
    var itemname: String? = null,
    var barcode: String? = null,

    var qty: Double? = null,
    var crtn: Int? = null,
    var rate: Double? = null,
    var crtnRate: Double? = null,
    var disc: Double? = null,
    var total: Double? = null,
    var cost: Double? = null,
    var profit: Double? = null,
    var imageUrl: String? = null,
    var crtnSize: Int? = null,
)