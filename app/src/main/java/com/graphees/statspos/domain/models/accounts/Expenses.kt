package com.graphees.statspos.domain.models.accounts

data class Expenses(
    var id: Long? = null,
    var expenseName: String? = null,
    var remarks: String? = null,

    val clientId:Int? = null,
    val branchId:Int? = null,
)