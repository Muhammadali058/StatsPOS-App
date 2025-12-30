package com.example.statspos.domain.models.accounts

data class EntryTypes(
    var id: Long? = null,
    var description: String? = null,

    val clientId:Int? = null,
    val branchId:Int? = null,
)