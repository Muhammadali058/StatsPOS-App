package com.graphees.statspos.domain.models.reports.accounts

import kotlinx.serialization.Serializable

@Serializable
data class ShiftReport(
    var amount: Double? = null,
    var entryType: Int? = null,
    var naration: String? = null,
)