package com.example.statspos.domain.models.reports

import kotlinx.serialization.Serializable

@Serializable
data class TotalReport(
    var branchName: String? = null,
    var branchGroupName: String? = null,
    var accountName: String? = null,
    var contact: String? = null,
    var address: String? = null,

    var totalDebit: Double? = null,
    var totalCredit: Double? = null,
    var grandTotal: Double? = null,
    var oldBalance: Double? = null,
    var newBalance: Double? = null,
    var cgs: Double? = null,
    var sales: Double? = null,
    var grossProfit: Double? = null,
    var totalExpenses: Double? = null,
    var netProfit: Double? = null,
    var totalSales: Double? = null,
    var totalReceipts: Double? = null,
    var totalBalance: Double? = null,
    var totalEntries: Int? = null,

    // Sales, Profit
    var total: Double? = null,
    var totalQty: Double? = null,
    var totalCrtn: Double? = null,
    var totalDisc: Double? = null,
    var totalProfit: Double? = null,
    var totalMargin: Double? = null,
    var totalCost: Double? = null,
    var totalBills: Int? = null,
    var totalItems: Int? = null,

    // Stock
    var totalStockPcs: Double? = null,
    var totalStockCrtn: Int? = null,
)