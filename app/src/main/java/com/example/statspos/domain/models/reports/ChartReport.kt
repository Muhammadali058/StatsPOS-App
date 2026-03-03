package com.example.statspos.domain.models.reports

import kotlinx.serialization.Serializable

@Serializable
data class ChartReport(
    var total: Double = 0.0,
    var date: String = "",
)