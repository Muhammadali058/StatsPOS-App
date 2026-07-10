package com.graphees.statspos.domain.models.reports.items

import kotlinx.serialization.Serializable

@Serializable
data class ItemsReport(
    var id: Long? = null,
    var itemname: String? = null,
    var urduname: String? = null,
    var barcode: String? = null,
    var refCode: String? = null,

    var cost: Double? = null,
    var retail: Double? = null,
    var wholesale: Double? = null,
    var rate3: Double? = null,
    var rate4: Double? = null,
    var crtnRate: Double? = null,
    var crtnSize: Int? = null,

    var categoryName: String? = null,
    var subCategoryName: String? = null,
    var vendorName: String? = null,
    var imageUrl: String? = null,
    var location: String? = null,
)