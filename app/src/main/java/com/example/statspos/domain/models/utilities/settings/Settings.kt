package com.example.statspos.domain.models.utilities.settings

data class Settings(
    var id: Long? = null,
    var saleUnderStock: Boolean? = null,
    var costWarning: Boolean? = null,
    var stockWarning: Boolean? = null,
    var autoCreditSelect: Boolean? = null,
    var showItemStock: Boolean? = null,
    var loadAutoCompleteItems: Boolean? = null,
    var paymentNotifications: Boolean? = null,
    var editOldCreditBill: Boolean? = null,
    var autoRetailChange: Boolean? = null,
    var instantSearch: Boolean? = null,
    var useUrdu: Boolean? = null,
    var showLedgerInBill: Boolean? = null,
    var showLedgerInVoucher: Boolean? = null,

    var qtyChangeable: Boolean? = null,
    var saleCartons: Boolean? = null,
    var fourRateSystem: Boolean? = null,
    var sameDateBillEdit: Boolean? = null,
    var showCustomerLastRate: Boolean? = null,
    var alwaysUseLastRate: Boolean? = null,
    var allowManyDuplicateBillPrints: Boolean? = null,
    var isPaymentNecessary: Boolean? = null,
    var shiftWiseSales: Boolean? = null,
    var shiftWisePurchase: Boolean? = null,

    var isDefaultRateRetail: Boolean? = null,
    var printLanguage: Int? = null,
    var isDefaultDiscRs: Boolean? = null,
    var printSize: Int? = null,

    var showBillPreview: Boolean? = null,
    var itemExistsInSalesWarning: Boolean? = null,
    var fullWindowReports: Boolean? = null,

    var shopName: String? = null,
    var address: String? = null,
    var contact: String? = null,

    val clientId:Int? = null,
    val branchId:Int? = null,
)