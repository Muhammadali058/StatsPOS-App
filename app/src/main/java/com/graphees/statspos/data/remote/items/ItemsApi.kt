package com.graphees.statspos.data.remote.items

import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ItemsApi {
    @POST("items/loadItems")
    suspend fun loadItems(@Body body: JsonObject): Response<JsonObject>

    @POST("items/insertItem")
    suspend fun insertItem(@Body body: JsonObject): Response<JsonObject>

    @POST("items/updateItem")
    suspend fun updateItem(@Body body: JsonObject): Response<JsonObject>

    @POST("items/deleteItem")
    suspend fun deleteItem(@Body body: JsonObject): Response<JsonObject>

    @POST("items/getItem")
    suspend fun getItem(@Body body: JsonObject): Response<JsonObject>

    @POST("items/isBarcodeExists")
    suspend fun isBarcodeExists(@Body body: JsonObject): Response<JsonObject>

    @POST("items/getBarcode")
    suspend fun getBarcode(@Body body: JsonObject): Response<JsonObject>

}