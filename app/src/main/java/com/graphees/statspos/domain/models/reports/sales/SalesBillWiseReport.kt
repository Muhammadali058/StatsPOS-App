package com.graphees.statspos.domain.models.reports.sales

import kotlinx.serialization.Serializable

@Serializable
data class SalesBillWiseReport(
    var salesId: Long? = null,
    var invoiceNo: Int? = null,
    var date: String? = null,
    var salesOn: String? = null,
    var salesType: String? = null,
    var mop: String? = null,
    var isRetail: String? = null,

    var totalDisc: Double? = null,
    var customerName: String? = null,
    var total: Double? = null,
)