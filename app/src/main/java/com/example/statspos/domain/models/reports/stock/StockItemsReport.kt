package com.example.statspos.domain.models.reports.stock

import kotlinx.serialization.Serializable

@Serializable
data class StockItemsReport(
    var id: Long? = null,
    var barcode: String? = null,
    var itemname: String? = null,
    var urduname: String? = null,

    var stockPcs: Double? = null,
    var stockCrtn: Int? = null,
    var cost: Double? = null,
    var retail: Double? = null,
    var wholesale: Double? = null,
    var total: Double? = null,

    var warehouseName: String? = null,
    var stockAt: String? = null,
    var categoryName: String? = null,
    var location: String? = null,
)