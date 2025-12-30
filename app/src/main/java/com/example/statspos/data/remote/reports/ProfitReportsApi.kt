package com.example.statspos.data.remote.reports

import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ProfitReportsApi {
    @POST("profitReports/billWiseReport")
    suspend fun billWiseReport(@Body body: JsonObject): Response<JsonObject>

    @POST("profitReports/itemsReport")
    suspend fun itemsReport(@Body body: JsonObject): Response<JsonObject>

    @POST("profitReports/briefReport")
    suspend fun briefReport(@Body body: JsonObject): Response<JsonObject>

    @POST("profitReports/chartDaily")
    suspend fun chartDaily(@Body body: JsonObject): Response<JsonObject>

    @POST("profitReports/chartWeekly")
    suspend fun chartWeekly(@Body body: JsonObject): Response<JsonObject>

    @POST("profitReports/chartMonthly")
    suspend fun chartMonthly(@Body body: JsonObject): Response<JsonObject>

    @POST("profitReports/chartYearly")
    suspend fun chartYearly(@Body body: JsonObject): Response<JsonObject>
}