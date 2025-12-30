package com.example.statspos.domain.models.utilities.settings

data class AdminSettings(
    var id: Long? = null,
    var passwordOnPrint: Boolean? = null,
    var showSuppliersInPurchase: Boolean? = null,

    val clientId:Int? = null,
    val branchId:Int? = null,
)