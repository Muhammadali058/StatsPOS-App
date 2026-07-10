package com.graphees.statspos.domain.models.main

data class LocalClients(
    var id: Int,
    var clientName: String,
    var username: String = "",
    var password: String = "",
    var isSingleServer: Boolean = false,

    var contact: String = "",
    var city: String = "",
    var email: String = "",
    var address: String = "",
)