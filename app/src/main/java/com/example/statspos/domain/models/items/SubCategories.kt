package com.example.statspos.domain.models.items

data class SubCategories(
    var id: Long? = null,
    var subCategoryName: String? = null,
    var categoryId: Long? = null,
    var imageUrl: String? = null,

    val clientId:Int? = null,
    val branchId:Int? = null,
    val branchGroupId:Int? = null,
)