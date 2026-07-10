package com.graphees.statspos.domain.models.utilities.users

data class UserTypes(
    var id: Long? = null,
    var description: String? = null,

    val clientId:Int? = null,
    val branchId:Int? = null,
)