package com.example.statspos.data.remote.reports

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface PurchaseReportsApi {
    @POST("purchaseReports/mainReport")
    suspend fun mainReport(@Body body: JsonObject): Response<JsonObject>

    @POST("purchaseReports/billWiseReport")
    suspend fun billWiseReport(@Body body: JsonObject): Response<JsonObject>

    @POST("purchaseReports/itemsReport")
    suspend fun itemsReport(@Body body: JsonObject): Response<JsonObject>

    @POST("purchaseReports/briefReport")
    suspend fun briefReport(@Body body: JsonObject): Response<JsonObject>

    @POST("purchaseReports/chartDaily")
    suspend fun chartDaily(@Body body: JsonObject): Response<JsonArray>

    @POST("purchaseReports/chartWeekly")
    suspend fun chartWeekly(@Body body: JsonObject): Response<JsonArray>

    @POST("purchaseReports/chartMonthly")
    suspend fun chartMonthly(@Body body: JsonObject): Response<JsonArray>

    @POST("purchaseReports/chartYearly")
    suspend fun chartYearly(@Body body: JsonObject): Response<JsonArray>
}