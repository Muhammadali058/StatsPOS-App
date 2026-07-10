package com.graphees.statspos.data.remote.accounts

import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AccountsApi {
    @POST("accounts/getBalance")
    suspend fun getBalance(@Body body: JsonObject): Response<JsonObject>

    @POST("accounts/loadEntries")
    suspend fun loadEntries(@Body body: JsonObject): Response<JsonObject>

    @POST("accounts/passEntry")
    suspend fun passEntry(@Body body: JsonObject): Response<JsonObject>

    @POST("accounts/deleteEntry")
    suspend fun deleteEntry(@Body body: JsonObject): Response<JsonObject>

    @POST("accounts/getEntry")
    suspend fun getEntry(@Body body: JsonObject): Response<JsonObject>

    @POST("accounts/loadDuePayments")
    suspend fun loadDuePayments(@Body body: JsonObject): Response<JsonObject>

    @POST("accounts/deleteDuePayment")
    suspend fun deleteDuePayment(@Body body: JsonObject): Response<JsonObject>

}