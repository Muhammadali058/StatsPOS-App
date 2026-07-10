package com.graphees.statspos.data.remote.reports

import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ItemsReportsApi {
    @POST("itemsReports/itemsList")
    suspend fun itemsList(@Body body: JsonObject): Response<JsonObject>

    @POST("itemsReports/itemsRateChangedList")
    suspend fun itemsRateChangedList(@Body body: JsonObject): Response<JsonObject>
}