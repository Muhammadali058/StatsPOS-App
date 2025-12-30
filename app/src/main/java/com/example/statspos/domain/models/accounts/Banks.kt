package com.example.statspos.domain.models.accounts

data class Banks(
    var id: Long? = null,
    var bankName: String? = null,
    var remarks: String? = null,

    val clientId:Int? = null,
    val branchId:Int? = null,
)