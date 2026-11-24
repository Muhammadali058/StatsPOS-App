package com.graphees.statspos.data.remote.accounts

import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface BanksApi {
    @POST("banks/loadBanks")
    suspend fun loadBanks(@Body body: JsonObject): Response<JsonObject>

    @POST("banks/insertBank")
    suspend fun insertBank(@Body body: JsonObject): Response<JsonObject>

    @POST("banks/updateBank")
    suspend fun updateBank(@Body body: JsonObject): Response<JsonObject>

    @POST("banks/deleteBank")
    suspend fun deleteBank(@Body body: JsonObject): Response<JsonObject>

    @POST("banks/getBank")
    suspend fun getBank(@Body body: JsonObject): Response<JsonObject>
}