package com.graphees.statspos.domain.models.purchase

import kotlinx.serialization.Serializable

@Serializable
data class PurchaseOrderVoucher(
    var id: Long? = null,
    var purchaseOrderName: String? = null,
    var itemname: String? = null,
    var urduname: String? = null,

    var retail: Double? = null,
    var wholesale: Double? = null,
    var crtnRate: Double? = null,
    var crtnSize: Int? = null,
    var categoryName: String? = null,

    var qty: Double? = null,
    var crtn: Int? = null,
    var cost: Double? = null,
    var costCrtn: Double? = null,
    var total: Double? = null,
    var grandTotal: Double? = null,

    var remarks: String? = null,
)