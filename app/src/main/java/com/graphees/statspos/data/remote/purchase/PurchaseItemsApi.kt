package com.graphees.statspos.data.remote.purchase

import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface PurchaseItemsApi {
    @POST("purchaseItems/loadPurchaseItems")
    suspend fun loadPurchaseItems(@Body body: JsonObject): Response<JsonObject>

    @POST("purchaseItems/insertPurchaseItem")
    suspend fun insertPurchaseItem(@Body body: JsonObject): Response<JsonObject>

    @POST("purchaseItems/updatePurchaseItem")
    suspend fun updatePurchaseItem(@Body body: JsonObject): Response<JsonObject>

    @POST("purchaseItems/deletePurchaseItem")
    suspend fun deletePurchaseItem(@Body body: JsonObject): Response<JsonObject>

    @POST("purchaseItems/getPurchaseItem")
    suspend fun getPurchaseItem(@Body body: JsonObject): Response<JsonObject>

    @POST("purchaseItems/isBarcodeExists")
    suspend fun isBarcodeExists(@Body body: JsonObject): Response<JsonObject>
}