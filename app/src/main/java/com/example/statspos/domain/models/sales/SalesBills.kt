package com.example.statspos.domain.models.sales

data class SalesBills(
    var id: Long? = null,
    var invoiceNo: Int? = null,
    var customerName: String? = null,
    var total: Double? = null,

    var date: String? = null,
    var salesOn: String? = null,
    var salesType: String? = null,
    var mop: String? = null,
    var type: String? = null,

    var username: String? = null,
)