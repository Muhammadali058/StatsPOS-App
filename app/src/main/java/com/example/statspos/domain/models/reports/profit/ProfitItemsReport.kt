package com.example.statspos.domain.models.reports.profit

import kotlinx.serialization.Serializable

@Serializable
data class ProfitItemsReport(
    var salesId: Long? = null,
    var invoiceNo: Int? = null,
    var date: String? = null,
    var itemname: String? = null,
    var urduname: String? = null,

    var cost: Double? = null,
    var totalCost: Double? = null,
    var totalDisc: Double? = null,
    var qty: Double? = null,
    var crtn: Int? = null,
    var profit: Double? = null,
    var margin: Double? = null,
    var total: Double? = null,
)