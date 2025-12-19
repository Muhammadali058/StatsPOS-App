package com.example.statspos.data.remote.main

import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ClientsApi {
    @POST("clients/clientLogin")
    suspend fun clientLogin(@Body body: JsonObject): Response<JsonObject>

    @POST("clients/localClientLogin")
    suspend fun localClientLogin(@Body body: JsonObject): Response<JsonObject>
}