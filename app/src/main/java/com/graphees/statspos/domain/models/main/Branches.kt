package com.graphees.statspos.domain.models.main

data class Branches(
    var id: Int? = null,
    var clientId: Int? = null,
    var branchName: String? = null,
    var city: String? = null,
    var address: String? = null,

    var fbrIntegrated: Boolean? = null,
    var posId: Int? = null,
    var accessToken: String? = null,
    var ntn: String? = null,
    var cnic: String? = null,

    var taxRate: Double? = null,
    var baseUrl: String? = null,
)