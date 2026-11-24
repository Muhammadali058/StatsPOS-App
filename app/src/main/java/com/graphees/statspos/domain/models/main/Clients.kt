package com.graphees.statspos.domain.models.main

import kotlinx.serialization.Serializable

@Serializable
data class Clients(
    var id: Int? = null,
    var clientName: String? = null,
    var username: String? = null,
    var password: String? = null,
    var contact: String? = null,
    var city: String? = null,
    var email: String? = null,
    var address: String? = null,
    var isRegisteredWeek: Boolean? = null,
    var isRegisteredMonth: Boolean? = null,
    var isOnline: Boolean? = null,
    var hasShoppingApp: Boolean? = null,

    var fcmToken: String? = null,
)