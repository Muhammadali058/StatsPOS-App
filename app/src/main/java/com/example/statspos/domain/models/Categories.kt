package com.example.statspos.domain.models

data class Categories(
    var id: Long,
    var categoryName: String,
    var remarks: String,
    var imageUrl: String,

    val clientId:Int,
    val branchId:Int,
    val branchGroupId:Int,
)
