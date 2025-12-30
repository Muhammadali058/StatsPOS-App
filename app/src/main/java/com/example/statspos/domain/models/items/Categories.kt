package com.example.statspos.domain.models.items

data class Categories(
    var id: Long? = null,
    var categoryName: String? = null,
    var remarks: String? = null,
    var imageUrl: String? = null,

    val clientId:Int? = null,
    val branchId:Int? = null,
    val branchGroupId:Int? = null,
)