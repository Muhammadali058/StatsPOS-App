package com.example.statspos.domain.models.main

data class Clients(
    var id: Long,
    var clientName: String,
    var username: String = "",
    var password: String = "",
    var contact: String = "",
    var city: String = "",
    var email: String = "",
    var address: String = "",
)