package com.example.statspos.data.remote.accounts

import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface BanksApi {
    // region Banks
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
    // endregion

    // region Sub-Banks
    @POST("subBanks/loadSubBanks")
    suspend fun loadSubBanks(@Body body: JsonObject): Response<JsonObject>

    @POST("subBanks/insertSubBank")
    suspend fun insertSubBank(@Body body: JsonObject): Response<JsonObject>

    @POST("subBanks/updateSubBank")
    suspend fun updateSubBank(@Body body: JsonObject): Response<JsonObject>

    @POST("subBanks/deleteSubBank")
    suspend fun deleteSubBank(@Body body: JsonObject): Response<JsonObject>

    @POST("subBanks/getSubBank")
    suspend fun getSubBank(@Body body: JsonObject): Response<JsonObject>
    // endregion
}