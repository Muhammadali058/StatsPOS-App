package com.example.statspos.domain.models.sales

data class Sales(
    var id: Long? = null,
    var invoiceNo: Int? = null,
    var total: Double? = null,
    var cost: Double? = null,
    var profit: Double? = null,

    var isDiscRsPer: Boolean? = null,
    var disc: Double? = null,
    var totalDisc: Double? = null,

    var totalItems: Int? = null,
    var payment: Int? = null,
    var change: Int? = null,
    var date: String? = null,
    var dueDate: String? = null,

    var salesOn: Int? = null,
    var salesType: Int? = null,
    var isMopCashBank: Boolean? = null,
    var bankId: Long? = null,
    var subBankId: Long? = null,
    var customerId: Long? = null,
    var supplierId: Long? = null,
    var userId: Long? = null,

    var customerName: String? = null,
    var remarks: String? = null,
    var isRetail: Boolean? = null,
    var isPaid: Boolean? = null,
    var isDelivered: Boolean? = null,
    var isEstimatedBill: Boolean? = null,

    var oldBalance: Double? = null,
    var newBalance: Double? = null,
    var naration: String? = null,
    var fbrInvoiceNo: String? = null,
    var qrcode: String? = null,

    val clientId: Int? = null,
    val branchId: Int? = null,

    // Extras not part of database
    var salesId: Long? = null,
    var currentShiftId: Long? = null,
    var isPendingBill: Boolean? = null,
)