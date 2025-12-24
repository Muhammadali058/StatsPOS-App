package com.example.statspos.domain.models.items

data class Items(
    var id: Long,
    var itemname: String,

    val clientId:Int? = 0,
    val branchId:Int? = 0,
    val branchGroupId:Int? = 0,
)