package com.graphees.statspos.data.remote.main

import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ClientsApi {
    @POST("clients/clientLogin")
    suspend fun clientLogin(@Body body: JsonObject): Response<JsonObject>

    @POST("clients/clientSignup")
    suspend fun clientSignup(@Body body: JsonObject): Response<JsonObject>

    @POST("clients/localClientLogin")
    suspend fun localClientLogin(@Body body: JsonObject): Response<JsonObject>

    @POST("clients/getBranches")
    suspend fun getBranches(@Body body: JsonObject): Response<JsonObject>

    @POST("clients/getClient")
    suspend fun getClient(@Body body: JsonObject): Response<JsonObject>

    @POST("clients/updateFCMToken")
    suspend fun updateFCMToken(@Body body: JsonObject): Response<JsonObject>

    @POST("clients/updateAppSubscription")
    suspend fun updateAppSubscription(@Body body: JsonObject): Response<JsonObject>
}