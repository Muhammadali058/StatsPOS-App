package com.example.statspos.domain.models

data class Categories(
    var id: Long,
    var categoryName: String,
    var remarks: String? = null,
    var imageUrl: String? = null,

    val clientId:Int? = null,
    val branchId:Int? = null,
    val branchGroupId:Int? = null,
)
