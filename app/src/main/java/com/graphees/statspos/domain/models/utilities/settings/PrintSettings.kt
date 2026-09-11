package com.graphees.statspos.domain.models.utilities.settings

data class PrintSettings(
    var id: Long? = null,

    var shopName: String? = null,
    var address: String? = null,
    var contact: String? = null,

    var showUrdu: Boolean? = null,
    var showLogo: Boolean? = null,
    var showItemDiscPercent: Boolean? = null,
    var showItemDisc: Boolean? = null,
    var showTotalDisc: Boolean? = null,

    var defaultPrintSize: Int? = null,
    var imageUrl: String? = null,

    val clientId:Int? = null,
    val branchId:Int? = null,
)