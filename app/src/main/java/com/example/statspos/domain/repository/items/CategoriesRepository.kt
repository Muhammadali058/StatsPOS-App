package com.example.statspos.domain.repository.items

import com.example.statspos.domain.models.items.Categories
import com.example.statspos.domain.models.items.SubCategories
import com.example.statspos.utils.Resource
import com.google.gson.JsonObject
import okhttp3.MultipartBody

interface CategoriesRepository {
    // region Categories
    suspend fun loadCategories(body: JsonObject): Resource<JsonObject>

    suspend fun insertCategory(category: Categories): Resource<JsonObject>

    suspend fun updateCategory(category: Categories): Resource<JsonObject>

    suspend fun deleteCategory(id: Long): Resource<JsonObject>

    suspend fun getCategory(id: Long): Resource<JsonObject>
    // endregion

    // region Sub-Categories
    suspend fun loadSubCategories(body: JsonObject): Resource<JsonObject>

    suspend fun insertSubCategory(subCategory: SubCategories): Resource<JsonObject>

    suspend fun updateSubCategory(subCategory: SubCategories): Resource<JsonObject>

    suspend fun deleteSubCategory(id: Long): Resource<JsonObject>

    suspend fun getSubCategory(id: Long): Resource<JsonObject>
    // endregion
}