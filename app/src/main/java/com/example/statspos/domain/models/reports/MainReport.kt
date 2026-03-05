package com.example.statspos.domain.models.reports

import kotlinx.serialization.Serializable

@Serializable
data class MainReport(
    var totalSales: Double = 0.0,
    var cashSales: Double = 0.0,
    var creditSales: Double = 0.0,
    var salesReturns: Double = 0.0,
    var totalSalesBills: Int = 0,

    var totalPurchase: Double = 0.0,
    var cashPurchase: Double = 0.0,
    var creditPurchase: Double = 0.0,
    var purchaseReturns: Double = 0.0,
    var totalPurchaseBills: Int = 0,

    var grossProfit: Double = 0.0,
    var expenses: Double = 0.0,
    var netProfit: Double = 0.0,
    var margin: Double = 0.0,

    var stockAtCost: Double = 0.0,
    var stockAtRetail: Double = 0.0,
    var stockAtWholesale: Double = 0.0,

    var receipts: Double = 0.0,
    var payments: Double = 0.0,
    var debtors: Double = 0.0,
    var creditors: Double = 0.0,

    var openingBalance: Double = 0.0,
    var closingBalance: Double = 0.0,
    var cashIn: Double = 0.0,
    var cashOut: Double = 0.0,
)