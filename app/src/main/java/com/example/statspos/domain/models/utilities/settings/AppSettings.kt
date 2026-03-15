package com.example.statspos.domain.models.utilities.settings

data class AppSettings(
    var id: Long? = null,

    var instantSearch: Boolean? = null,
    var innerItemSearch: Boolean? = null,
    var itemSuggestions: Boolean? = null,

    val clientId:Int? = null,
    val branchId:Int? = null,
)