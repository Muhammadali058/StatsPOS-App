package com.example.statspos.data.remote.utilities

import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface SettingsApi {
    @POST("settings/updateSettings")
    suspend fun updateSettings(@Body body: JsonObject): Response<JsonObject>

    @POST("settings/updateAdminSettings")
    suspend fun updateAdminSettings(@Body body: JsonObject): Response<JsonObject>
}