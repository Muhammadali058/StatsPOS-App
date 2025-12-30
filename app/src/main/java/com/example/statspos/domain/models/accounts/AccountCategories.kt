package com.example.statspos.domain.models.accounts

data class AccountCategories(
    var id: Long? = null,
    var categoryName: String? = null,
    var remarks: String? = null,

    val clientId:Int? = null,
    val branchId:Int? = null,
)