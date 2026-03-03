package com.example.statspos.data.remote.reports

import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface SalesReportsApi {
    @POST("salesReports/mainReport")
    suspend fun mainReport(@Body body: JsonObject): Response<JsonObject>

    @POST("salesReports/billWiseReport")
    suspend fun billWiseReport(@Body body: JsonObject): Response<JsonObject>

    @POST("salesReports/itemsReport")
    suspend fun itemsReport(@Body body: JsonObject): Response<JsonObject>

    @POST("salesReports/briefReport")
    suspend fun briefReport(@Body body: JsonObject): Response<JsonObject>

    @POST("salesReports/chartDaily")
    suspend fun chartDaily(@Body body: JsonObject): Response<JsonObject>

    @POST("salesReports/chartWeekly")
    suspend fun chartWeekly(@Body body: JsonObject): Response<JsonObject>

    @POST("salesReports/chartMonthly")
    suspend fun chartMonthly(@Body body: JsonObject): Response<JsonObject>

    @POST("salesReports/chartYearly")
    suspend fun chartYearly(@Body body: JsonObject): Response<JsonObject>
}