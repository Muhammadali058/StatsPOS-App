package com.example.statspos.data.remote.reports

import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface StockReportsApi {
    @POST("stockReports/stockReport")
    suspend fun stockReport(@Body body: JsonObject): Response<JsonObject>
}