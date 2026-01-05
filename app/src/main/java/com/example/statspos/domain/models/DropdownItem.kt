package com.example.statspos.domain.models

data class DropdownItem(
    var id:Long,
    var name:String,
    var mainId:Long = 0,
    var type:String = "",
)
