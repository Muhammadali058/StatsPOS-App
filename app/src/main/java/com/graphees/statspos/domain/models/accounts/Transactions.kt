package com.graphees.statspos.domain.models.accounts

data class Transactions(
    var id: Long? = null,
    var accountId: Long? = null,
    var debit: Double? = null,
    var credit: Double? = null,

    var date: String? = null,
    var time: String? = null,
    var naration: String? = null,
    var isDebit: Boolean? = null,

    var entryId: Long? = null,
    var salesId: Long? = null,
    var purchaseId: Long? = null,
    var userId: Long? = null,
    var entryType: Int? = null,

    var currentShiftId: Long? = null,

    val clientId:Int? = null,
    val branchId:Int? = null,
)