package com.graphees.statspos.domain.models.items

data class Stock(
    var id: Long? = null,
    var itemId: Long? = null,
    var stockPcs: Double? = null,
    var stockCrtn: Long? = null,

    val clientId:Int? = null,
    val branchId:Int? = null,
)