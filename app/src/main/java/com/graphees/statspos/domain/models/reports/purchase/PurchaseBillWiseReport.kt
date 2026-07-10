package com.graphees.statspos.domain.models.reports.purchase

import kotlinx.serialization.Serializable

@Serializable
data class PurchaseBillWiseReport(
    var purchaseId: Long? = null,
    var invoiceNo: Int? = null,
    var date: String? = null,
    var purchaseOn: String? = null,
    var purchaseType: String? = null,
    var mop: String? = null,

    var grossTotal: Double? = null,
    var totalDisc: Double? = null,
    var vendorName: String? = null,
    var total: Double? = null,
)