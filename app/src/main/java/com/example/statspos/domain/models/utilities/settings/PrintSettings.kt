package com.example.statspos.domain.models.utilities.settings

data class PrintSettings(
    var id: Long? = null,
    var showUrdu: Boolean? = null,
    var showLogo: Boolean? = null,

    var imageUrl: String? = null,

    val clientId:Int? = null,
    val branchId:Int? = null,
)