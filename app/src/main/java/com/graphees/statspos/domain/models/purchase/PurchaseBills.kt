package com.graphees.statspos.domain.models.purchase

import kotlinx.serialization.Serializable

@Serializable
data class PurchaseBills(
    var id: Long? = null,
    var invoiceNo: Int? = null,
    var vendorName: String? = null,
    var total: Double? = null,

    var grossTotal: Double? = null,
    var isDiscRsPer: Boolean? = null,
    var disc: Double? = null,
    var totalDisc: Double? = null,
    var localDate: String? = null,

    var date: String? = null,
    var purchaseOn: String? = null,
    var purchaseType: String? = null,
    var mop: String? = null,

    var warehouseName: String? = null,
    var username: String? = null,
    var refInvoiceNo: String? = null,
    var warehouseId: Long? = null,
)