package com.graphees.statspos.domain.models.reports.accounts

import kotlinx.serialization.Serializable

@Serializable
data class AccountReport(
    var id: Long? = null,
    var accountName: String? = null,
    var contact: String? = null,
    var address: String? = null,

    var date: String? = null,
    var naration: String? = null,

    var debit: Double? = null,
    var credit: Double? = null,
    var balance: Double? = null,

    var amount: Double? = null,
    var expense: String? = null,

    var oldBalance: Double? = null,
    var sales: Double? = null,
    var receipts: Double? = null,
    var newBalance: Double? = null,
)