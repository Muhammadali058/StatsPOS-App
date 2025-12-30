package com.example.statspos.data.remote.purchase

import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface PurchaseOrdersApi {
    @POST("purchaseOrders/loadPurchaseOrders")
    suspend fun loadPurchaseOrders(@Body body: JsonObject): Response<JsonObject>

    @POST("purchaseOrders/insertPurchaseOrder")
    suspend fun insertPurchaseOrder(@Body body: JsonObject): Response<JsonObject>

    @POST("purchaseOrders/updatePurchaseOrder")
    suspend fun updatePurchaseOrder(@Body body: JsonObject): Response<JsonObject>

    @POST("purchaseOrders/deletePurchaseOrder")
    suspend fun deletePurchaseOrder(@Body body: JsonObject): Response<JsonObject>

    @POST("purchaseOrders/getPurchaseOrder")
    suspend fun getPurchaseOrder(@Body body: JsonObject): Response<JsonObject>

    @POST("purchaseOrders/getOrder")
    suspend fun getOrder(@Body body: JsonObject): Response<JsonObject>
}