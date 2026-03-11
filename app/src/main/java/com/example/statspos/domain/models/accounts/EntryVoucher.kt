package com.example.statspos.domain.models.accounts

import kotlinx.serialization.Serializable

@Serializable
data class EntryVoucher(
    var id: Long? = null,
    var accountName: String? = null,
    var accountId: Long? = null,
    var amount: Double? = null,

    var date: String? = null,
    var naration: String? = null,
    var entryType: String? = null,
    var mop: String? = null,

    var oldBalance: Double? = null,
    var newBalance: Double? = null,

    var saleCartons: Boolean? = null,
    var showLedgerInVoucher: Boolean? = null,
)