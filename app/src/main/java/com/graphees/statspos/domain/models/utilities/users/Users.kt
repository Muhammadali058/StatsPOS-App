package com.graphees.statspos.domain.models.utilities.users

data class Users(
    var id: Long? = null,
    var username: String? = null,
    var password: String? = null,
    var contact: String? = null,
    var email: String? = null,
    var address: String? = null,

    var dateOfBirth: String? = null,
    val userType:Int? = null,
    val shift:Int? = null,
    var currentShiftId: Long? = null,

    var imageUrl: String? = null,
    var isMainUser: Boolean? = null,

    val clientId:Int? = null,
    val branchId:Int? = null,
    val branchGroupId:Int? = null,

    // Extras not part of database
    var userTypeDescription: String? = null,
    var shiftDescription: String? = null,
)