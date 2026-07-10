package com.graphees.statspos.domain.models.reports.purchase

import kotlinx.serialization.Serializable

@Serializable
data class PurchaseItemsReport(
    var purchaseId: Long? = null,
    var invoiceNo: Int? = null,
    var date: String? = null,
    var itemname: String? = null,
    var urduname: String? = null,
    var vendorName: String? = null,

    var qty: Double? = null,
    var crtn: Int? = null,
    var crtnSize: Int? = null,
    var cost: Double? = null,
    var costCrtn: Double? = null,
    var totalDisc: Double? = null,
    var totalTax: Double? = null,
    var total: Double? = null,
)