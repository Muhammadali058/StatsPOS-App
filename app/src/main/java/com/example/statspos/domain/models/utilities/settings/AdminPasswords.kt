package com.example.statspos.domain.models.utilities.settings

data class AdminPasswords(
    var id: Long? = null,
    var printDuplicates: String? = null,
    var audit: String? = null,

    var usePrintDuplicates: Boolean? = null,
    var useAudit: Boolean? = null,

    val clientId:Int? = null,
    val branchId:Int? = null,
)