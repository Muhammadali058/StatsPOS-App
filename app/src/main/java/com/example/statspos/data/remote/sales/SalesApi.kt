package com.example.statspos.data.remote.sales

import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface SalesApi {
    @POST("sales/insertSales")
    suspend fun insertSales(@Body body: JsonObject): Response<JsonObject>

    @POST("sales/updateSales")
    suspend fun updateSales(@Body body: JsonObject): Response<JsonObject>

    @POST("sales/deleteSales")
    suspend fun deleteSales(@Body body: JsonObject): Response<JsonObject>

    @POST("sales/getSales")
    suspend fun getSales(@Body body: JsonObject): Response<JsonObject>

    @POST("sales/tempClose")
    suspend fun tempClose(@Body body: JsonObject): Response<JsonObject>

    @POST("sales/getInvoiceId")
    suspend fun getInvoiceId(@Body body: JsonObject): Response<JsonObject>

    @POST("sales/getBill")
    suspend fun getBill(@Body body: JsonObject): Response<JsonObject>

    @POST("sales/loadPendingBills")
    suspend fun loadPendingBills(@Body body: JsonObject): Response<JsonObject>

    @POST("sales/loadPendingBillItems")
    suspend fun loadPendingBillItems(@Body body: JsonObject): Response<JsonObject>

    @POST("sales/loadPostedBills")
    suspend fun loadPostedBills(@Body body: JsonObject): Response<JsonObject>

    @POST("sales/loadPostedBillItems")
    suspend fun loadPostedBillItems(@Body body: JsonObject): Response<JsonObject>

    @POST("sales/changeBillType")
    suspend fun changeBillType(@Body body: JsonObject): Response<JsonObject>

    @POST("sales/gererateReturnBill")
    suspend fun generateReturnBill(@Body body: JsonObject): Response<JsonObject>

}