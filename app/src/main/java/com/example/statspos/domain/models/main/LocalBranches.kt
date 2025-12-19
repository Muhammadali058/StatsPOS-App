package com.example.statspos.domain.models.main

data class LocalBranches(
    var id: Int,
    var localClientId: Int,
    var baseUrl: String,
    var branchName: String = "",
    var city: String? = "",
    var address: String? = "",
)