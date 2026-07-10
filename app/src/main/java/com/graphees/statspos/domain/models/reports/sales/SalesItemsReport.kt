package com.graphees.statspos.domain.models.reports.sales

import kotlinx.serialization.Serializable

@Serializable
data class SalesItemsReport(
    var salesId: Long? = null,
    var invoiceNo: Int? = null,
    var date: String? = null,
    var itemname: String? = null,
    var urduname: String? = null,
    var customerName: String? = null,

    var qty: Double? = null,
    var crtn: Int? = null,
    var rate: Double? = null,
    var crtnRate: Double? = null,
    var totalDisc: Double? = null,
    var total: Double? = null,
)