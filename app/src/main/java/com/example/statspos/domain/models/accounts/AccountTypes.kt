package com.example.statspos.domain.models.accounts

data class AccountTypes(
    var id: Long? = null,
    var description: String? = null,

    val clientId:Int? = null,
    val branchId:Int? = null,
)