package com.graphees.statspos.domain.models.sales

import kotlinx.serialization.Serializable

@Serializable
data class SalesBills(
    var id: Long? = null,
    var invoiceNo: Int? = null,
    var customerName: String? = null,
    var total: Double? = null,

    var grossTotal: Double? = null,
    var totalCost: Double? = null,
    var isDiscRsPer: Boolean? = null,
    var disc: Double? = null,
    var totalDisc: Double? = null,
    var localDate: String? = null,

    var date: String? = null,
    var salesOn: String? = null,
    var salesType: String? = null,
    var mop: String? = null,
    var type: String? = null,

    var username: String? = null,
    var imageUrl: String? = null,
    var contact: String? = null,
)