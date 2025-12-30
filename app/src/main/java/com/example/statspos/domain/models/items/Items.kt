package com.example.statspos.domain.models.items

data class Items(
    var id: Long? = null,
    var itemname: String? = null,
    var barcode: String? = null,
    var urduname: String? = null,

    var lastCost: Double? = null,
    var cost: Double? = null,
    var retail: Double? = null,
    var wholesale: Double? = null,
    var rate3: Double? = null,
    var rate4: Double? = null,
    var crtnRate: Double? = null,
    var crtnSize: Int? = null,
    var marketPrice: Double? = null,
    var refCode: String? = null,

    var isDiscRsPer: Boolean? = null,
    var disc: Double? = null,
    var expirable: Boolean? = null,
    var expiry: String? = null,

    val stockWarningMin:Int? = null,
    val stockWarningMax:Int? = null,
    val maxSalePcs:Int? = null,
    val maxSaleCrtn:Int? = null,

    var openingCost: Double? = null,
    var openingStockPcs: Double? = null,
    var openingStockCrtn: Long? = null,
    val openingCrtnSize:Int? = null,

    var repeatable: Boolean? = null,
    var searchable: Boolean? = null,
    var changeable: Boolean? = null,
    var button: Boolean? = null,
    var lockPcs: Boolean? = null,
    var lockCrtn: Boolean? = null,
    var saleUnderStock: Boolean? = null,

    var categoryId: Long? = null,
    var subCategoryId: Long? = null,

    var packing: String? = null,
    var location: String? = null,

    var newCost: Double? = null,
    var newRetail: Double? = null,
    var newWholesale: Double? = null,
    var newRate3: Double? = null,
    var newRate4: Double? = null,
    var newCrtnRate: Double? = null,
    var newCrtnSize: Int? = null,
    var newMarketPrice: Double? = null,
    var newStockPcs: Double? = null,
    var newStockCrtn: Long? = null,

    var imageUrl: String? = null,
    var date: String? = null,
    var isAudited: Boolean? = null,

    val clientId:Int? = null,
    val branchId:Int? = null,
    val branchGroupId:Int? = null,

    // Extras not part of database
    var stockPcs: Double? = null,
    var stockCrtn: Long? = null,
    var wStockPcs: Double? = null,
    var wStockCrtn: Long? = null,
    var totalStockPcs: Double? = null,
    var totalStockCrtn: Long? = null,

    var isCrtnBarcode: Boolean? = null,
    var crtnBarcode: String? = null,
    var isExists: Boolean? = null,
    var isExistsInOrder: Boolean? = null,
    var isReplaceExists: Boolean? = null,

    var vendorId: Long? = null,
    var userId: Long? = null,
    var oldRates: String? = null, // it will be used in purchase
    var warehouseStock: String? = null,

    var lastRate: Double? = null, // these will be used in sales to show customer last rates
    var lastCrtnRate: Double? = null,
)