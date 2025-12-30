package com.example.statspos.data.remote.purchase

import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface PurchaseOrderItemsApi {
    @POST("purchaseOrderItems/loadPurchaseOrderItems")
    suspend fun loadPurchaseOrderItems(@Body body: JsonObject): Response<JsonObject>

    @POST("purchaseOrderItems/insertPurchaseOrderItem")
    suspend fun insertPurchaseOrderItem(@Body body: JsonObject): Response<JsonObject>

    @POST("purchaseOrderItems/updatePurchaseOrderItem")
    suspend fun updatePurchaseOrderItem(@Body body: JsonObject): Response<JsonObject>

    @POST("purchaseOrderItems/deletePurchaseOrderItem")
    suspend fun deletePurchaseOrderItem(@Body body: JsonObject): Response<JsonObject>

    @POST("purchaseOrderItems/getPurchaseOrderItem")
    suspend fun getPurchaseOrderItem(@Body body: JsonObject): Response<JsonObject>
}