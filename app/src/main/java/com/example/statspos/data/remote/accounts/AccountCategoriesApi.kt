package com.example.statspos.data.remote.accounts

import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AccountCategoriesApi {
    @POST("accountCategories/loadAccountCategories")
    suspend fun loadAccountCategories(@Body body: JsonObject): Response<JsonObject>

    @POST("accountCategories/insertAccountCategory")
    suspend fun insertAccountCategory(@Body body: JsonObject): Response<JsonObject>

    @POST("accountCategories/updateAccountCategory")
    suspend fun updateAccountCategory(@Body body: JsonObject): Response<JsonObject>

    @POST("accountCategories/deleteAccountCategory")
    suspend fun deleteAccountCategory(@Body body: JsonObject): Response<JsonObject>

    @POST("accountCategories/getAccountCategory")
    suspend fun getAccountCategory(@Body body: JsonObject): Response<JsonObject>
}