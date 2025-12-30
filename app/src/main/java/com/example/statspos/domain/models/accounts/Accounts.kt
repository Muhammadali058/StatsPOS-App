package com.example.statspos.domain.models.accounts

data class Accounts(
    var id: Long? = null,
    var accountName: String? = null,
    var remarks: String? = null,
    var address: String? = null,
    var city: String? = null,
    var contact: String? = null,
    var email: String? = null,
    var ntn: String? = null,
    var stn: String? = null,
    var cnic: String? = null, // till Customers

    var bankId: Long? = null,
    var accountNumber: String? = null,
    var branchCode: String? = null,
    var branchAddress: String? = null, // till Banks

    var expenseId: Long? = null,
    var supplierId: Long? = null,

    val employeeId: Int? = null,
    val salary: Int? = null,

    val accountType: Int? = null,
    var isRetail: Boolean? = null,
    var categoryId: Long? = null,

    var disc: Double? = null,
    var isDiscRsPer: Boolean? = null,
    val dueDays: Int? = null,
    var isCredit: Boolean? = null,

    var imageUrl: String? = null,

    val clientId: Int? = null,
    val branchId: Int? = null,

    // Extras not part of database
    var openingBalance: Double? = null,
)