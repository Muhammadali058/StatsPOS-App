package com.example.statspos.domain.models.items

data class Categories(
    var id: Long,
    var categoryName: String,
    var remarks: String? = "",
    var imageUrl: String? = "",

    val clientId:Int? = 0,
    val branchId:Int? = 0,
    val branchGroupId:Int? = 0,
)