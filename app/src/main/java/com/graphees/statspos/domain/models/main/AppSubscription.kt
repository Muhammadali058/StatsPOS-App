package com.graphees.statspos.domain.models.main

data class AppSubscription(
    var id: Int? = null,
    var isActive: Boolean? = null,
    var expiryDate: String? = null,
    var paymentDate: String? = null,
    var paymentRequest: Boolean? = null,

    var clientId: Int? = null,
    var branchId: Int? = null,

    // Extras
    var expiryDays: Int? = null,
)