package com.graphees.statspos.domain.models.warehouse

import kotlinx.serialization.Serializable

@Serializable
data class GatepassVoucher(
    var id: Long? = null,
    var itemId: Long? = null,
    var warehouseId: Long? = null,

    var gatepassName: String? = null,
    var warehouseName: String? = null,
    var itemname: String? = null,

    var date: String? = null,
    var qty: Double? = null,
    var crtn: Int? = null,
    var remarks: String? = null,
)