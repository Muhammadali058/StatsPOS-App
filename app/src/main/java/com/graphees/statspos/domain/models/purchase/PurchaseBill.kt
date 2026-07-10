package com.graphees.statspos.domain.models.purchase

import kotlinx.serialization.Serializable

@Serializable
data class PurchaseBill(
    var id: Long? = null,
    var invoiceNo: Int? = null,
    var itemname: String? = null,
    var urduname: String? = null,
    var barcode: String? = null,
    var refCode: String? = null,

    var grossCost: Double? = null,
    var cost: Double? = null,
    var costCrtn: Double? = null,
    var qty: Double? = null,
    var crtn: Int? = null,
    var retail: Double? = null,
    var wholesale: Double? = null,
    var rate3: Double? = null,
    var rate4: Double? = null,
    var crtnRate: Double? = null,
    var crtnSize: Int? = null,
    var isDiscRsPer: Boolean? = null,
    var disc: Double? = null,
    var totalDisc: Double? = null,
    var total: Double? = null,

    var date: String? = null,
    var time: String? = null,
    var billType: String? = null,

    var grandTotal: Double? = null,
    var grandDisc: Double? = null,
    var grandIsDiscRsPer: Boolean? = null,
    var grandTotalDisc: Double? = null,

    var refInvoiceNo: String? = null,
    var oldBalance: Double? = null,
    var newBalance: Double? = null,
    var purchaseOn: String? = null,
    var purchaseType: String? = null,
    var mop: String? = null,
    var saleCartons: Boolean? = null,

    var supplierName: String? = null,
    var supplierContact: String? = null,
    var supplierAddress: String? = null,
    var vendorName: String? = null,
    var vendorContact: String? = null,
    var vendorAddress: String? = null,
    var vendorEmail: String? = null,
    var vendorCity: String? = null,
    var user: String? = null,
    var userPhone: String? = null,
    var userAddress: String? = null,
    var bankName: String? = null,
    var subBankName: String? = null,
)