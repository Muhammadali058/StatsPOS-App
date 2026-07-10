package com.graphees.statspos.domain.models.accounts

data class UserCashEntries(
    var id: Long? = null,
    var userId: Long? = null,
    var amount: Double? = null,
    var type: String? = null,

    val clientId:Int? = null,
    val branchId:Int? = null,
)