package com.example.statspos.domain.models.utilities.settings

data class AdminSettings(
    var id: Long? = null,
    var showSuppliersInPurchase: Boolean? = null,
    var searchInSales: Boolean? = null,
    var searchInPurchase: Boolean? = null,
    var estimatedBill: Boolean? = null,
    var useWeightScale: Boolean? = null,
    var showDashboard: Boolean? = null,
    var detailedSearchInPOS: Boolean? = null,

    val clientId:Int? = null,
    val branchId:Int? = null,
)