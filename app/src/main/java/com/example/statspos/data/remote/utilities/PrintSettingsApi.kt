package com.example.statspos.data.remote.utilities

import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface PrintSettingsApi {
    @POST("settings/updatePrintSettings")
    suspend fun updatePrintSettings(@Body body: JsonObject): Response<JsonObject>
}