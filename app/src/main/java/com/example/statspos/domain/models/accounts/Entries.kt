package com.example.statspos.domain.models.accounts

data class Entries(
    var id: Long? = null,
    var accountId: Long? = null,
    var amount: Double? = null,
    var naration: String? = null,

    var date: String? = null,
    var time: String? = null,

    var entryType: Int? = null,
    var isMopCashBank: Boolean? = null,
    var userId: Long? = null,

    var oldBalance: Double? = null,
    var newBalance: Double? = null,
    var debitAccountId: Long? = null,
    var creditAccountId: Long? = null,
    var currentShiftId: Long? = null,

    val clientId:Int? = null,
    val branchId:Int? = null,

    // Extras not part of database
    var itemId: Long? = null,
    var stock: Double? = null,
    var accountName: String? = null,
    var mop: String? = null,
    var username: String? = null,
    var debitAccountName: String? = null,
    var creditAccountName: String? = null,
)