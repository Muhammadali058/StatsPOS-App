package com.example.statspos.domain.models.reports

import kotlinx.serialization.Serializable

@Serializable
data class MainReport(
    var totalSales: Double = 0.0,
    var cashSales: Double = 0.0,
    var creditSales: Double = 0.0,
    var salesReturns: Double = 0.0,
    var totalBills: Int = 0,
)