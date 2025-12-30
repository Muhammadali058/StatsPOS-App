package com.example.statspos.data.remote.accounts

import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface FixedAccountsApi {
    @POST("fixedAccounts/loadFixedAccounts")
    suspend fun loadFixedAccounts(@Body body: JsonObject): Response<JsonObject>

    @POST("fixedAccounts/insertFixedAccount")
    suspend fun insertFixedAccount(@Body body: JsonObject): Response<JsonObject>

    @POST("fixedAccounts/updateFixedAccount")
    suspend fun updateFixedAccount(@Body body: JsonObject): Response<JsonObject>

    @POST("fixedAccounts/deleteFixedAccount")
    suspend fun deleteFixedAccount(@Body body: JsonObject): Response<JsonObject>

    @POST("fixedAccounts/getFixedAccount")
    suspend fun getFixedAccount(@Body body: JsonObject): Response<JsonObject>
}