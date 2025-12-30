package com.example.statspos.data.remote.purchase

import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface PurchaseApi {
    @POST("purchase/insertPurchase")
    suspend fun insertPurchase(@Body body: JsonObject): Response<JsonObject>

    @POST("purchase/updatePurchase")
    suspend fun updatePurchase(@Body body: JsonObject): Response<JsonObject>

    @POST("purchase/deletePurchase")
    suspend fun deletePurchase(@Body body: JsonObject): Response<JsonObject>

    @POST("purchase/getPurchase")
    suspend fun getPurchase(@Body body: JsonObject): Response<JsonObject>

    @POST("purchase/tempClose")
    suspend fun tempClose(@Body body: JsonObject): Response<JsonObject>

    @POST("purchase/getInvoiceId")
    suspend fun getInvoiceId(@Body body: JsonObject): Response<JsonObject>

    @POST("purchase/isReplaceExists")
    suspend fun isReplaceExists(@Body body: JsonObject): Response<JsonObject>

    @POST("purchase/getBill")
    suspend fun getBill(@Body body: JsonObject): Response<JsonObject>

    @POST("purchase/generateBarcodeLabels")
    suspend fun generateBarcodeLabels(@Body body: JsonObject): Response<JsonObject>

    @POST("purchase/loadPendingBills")
    suspend fun loadPendingBills(@Body body: JsonObject): Response<JsonObject>

    @POST("purchase/loadPendingBillItems")
    suspend fun loadPendingBillItems(@Body body: JsonObject): Response<JsonObject>

    @POST("purchase/loadPostedBills")
    suspend fun loadPostedBills(@Body body: JsonObject): Response<JsonObject>

    @POST("purchase/loadPostedBillItems")
    suspend fun loadPostedBillItems(@Body body: JsonObject): Response<JsonObject>
}