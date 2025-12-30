package com.example.statspos.data.remote.sales

import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface SalesItemsApi {
    @POST("salesItems/loadSalesItems")
    suspend fun loadSalesItems(@Body body: JsonObject): Response<JsonObject>

    @POST("salesItems/insertSalesItem")
    suspend fun insertSalesItem(@Body body: JsonObject): Response<JsonObject>

    @POST("salesItems/updateSalesItem")
    suspend fun updateSalesItem(@Body body: JsonObject): Response<JsonObject>

    @POST("salesItems/deleteSalesItem")
    suspend fun deleteSalesItem(@Body body: JsonObject): Response<JsonObject>

    @POST("salesItems/getSalesItem")
    suspend fun getSalesItem(@Body body: JsonObject): Response<JsonObject>

    @POST("salesItems/isBarcodeExists")
    suspend fun isBarcodeExists(@Body body: JsonObject): Response<JsonObject>
}