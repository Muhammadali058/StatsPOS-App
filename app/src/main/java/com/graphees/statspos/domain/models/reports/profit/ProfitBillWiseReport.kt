package com.graphees.statspos.domain.models.reports.profit

import kotlinx.serialization.Serializable

@Serializable
data class ProfitBillWiseReport(
    var salesId: Long? = null,
    var invoiceNo: Int? = null,
    var date: String? = null,
    var customerName: String? = null,

    var totalDisc: Double? = null,
    var cost: Double? = null,
    var profit: Double? = null,
    var margin: Double? = null,
    var total: Double? = null,

)