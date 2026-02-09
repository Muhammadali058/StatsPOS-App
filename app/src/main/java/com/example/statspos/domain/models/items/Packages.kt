package com.example.statspos.domain.models.items

data class Packages(
    var id: Long? = null,
    var packageName: String? = null,
    var remarks: String? = null,

    val clientId:Int? = null,
    val branchId:Int? = null,

    // Extras
    val total: Double? = null,
)