package com.graphees.statspos.domain.models.sales

data class DuePayments(
    var id: Long? = null,
    var accountId: Long? = null,
    var dueDate: String? = null,

    val clientId:Int? = null,
    val branchId:Int? = null,
)