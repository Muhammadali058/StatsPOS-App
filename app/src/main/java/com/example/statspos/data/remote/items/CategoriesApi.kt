package com.example.statspos.data.remote.items

import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface CategoriesApi {
    // region Categories
    @POST("categories/loadCategories")
    suspend fun loadCategories(@Body body: JsonObject): Response<JsonObject>

    @POST("categories/insertCategory")
    suspend fun insertCategory(@Body body: JsonObject): Response<JsonObject>

    @POST("categories/updateCategory")
    suspend fun updateCategory(@Body body: JsonObject): Response<JsonObject>

    @POST("categories/deleteCategory")
    suspend fun deleteCategory(@Body body: JsonObject): Response<JsonObject>

    @POST("categories/getCategory")
    suspend fun getCategory(@Body body: JsonObject): Response<JsonObject>
    // endregion

    // region Sub-Categories
    @POST("subCategories/loadSubCategories")
    suspend fun loadSubCategories(@Body body: JsonObject): Response<JsonObject>

    @POST("subCategories/insertSubCategory")
    suspend fun insertSubCategory(@Body body: JsonObject): Response<JsonObject>

    @POST("subCategories/updateSubCategory")
    suspend fun updateSubCategory(@Body body: JsonObject): Response<JsonObject>

    @POST("subCategories/deleteSubCategory")
    suspend fun deleteSubCategory(@Body body: JsonObject): Response<JsonObject>

    @POST("subCategories/getSubCategory")
    suspend fun getSubCategory(@Body body: JsonObject): Response<JsonObject>
    // endregion
}