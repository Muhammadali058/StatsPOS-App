package com.graphees.statspos.domain.models.utilities.users

data class UserRights(
    var id: Long? = null,
    var userId: Long? = null,

    var items: Boolean? = null,
    var sales: Boolean? = null,
    var purchase: Boolean? = null,
    var categories: Boolean? = null,
    var warehouse: Boolean? = null,

    // Accounts
    var accounts: Boolean? = null,
    var customers: Boolean? = null,
    var vendors: Boolean? = null,
    var expenses: Boolean? = null,
    var banks: Boolean? = null,
    var suppliers: Boolean? = null,

    // Utilities
    var utilities: Boolean? = null,
    var users: Boolean? = null,
    var settings: Boolean? = null,
    var barcodeLabels: Boolean? = null,
    var employees: Boolean? = null,

    // Reports
    var reports: Boolean? = null,
    var salesReport: Boolean? = null,
    var purchaseReport: Boolean? = null,
    var profitReport: Boolean? = null,
    var stockReport: Boolean? = null,
    var accountsReport: Boolean? = null,
    var itemsReport: Boolean? = null,
    var auditReport: Boolean? = null,

    // POS
    var changeRates: Boolean? = null,
    var seeMargin: Boolean? = null,
    var salesReturn: Boolean? = null,
    var creditBill: Boolean? = null,
    var editSaleBill: Boolean? = null,
    var editCreditBill: Boolean? = null,
    var dateWiseSales: Boolean? = null,
    var payBill: Boolean? = null,
    var discount: Boolean? = null,
    var seeCost: Boolean? = null,
    var searchItems: Boolean? = null,
    var fbrInvoice: Boolean? = null,

    // Others
    var dateWiseEntry: Boolean? = null,
    var dateWisePurchase: Boolean? = null,
    var printDuplicates: Boolean? = null,
    var deleteAnything: Boolean? = null,
    var entry: Boolean? = null,
    var editPurchaseBill: Boolean? = null,

    var branchWisePurchase: Boolean? = null,
    var branchWiseReports: Boolean? = null,

    val clientId:Int? = null,
    val branchId:Int? = null,
)