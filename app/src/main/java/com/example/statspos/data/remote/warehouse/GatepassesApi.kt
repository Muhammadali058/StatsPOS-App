package com.example.statspos.data.remote.warehouse

import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface GatepassesApi {
    @POST("gatepasses/loadGatepasses")
    suspend fun loadGatepasses(@Body body: JsonObject): Response<JsonObject>

    @POST("gatepasses/insertGatepass")
    suspend fun insertGatepass(@Body body: JsonObject): Response<JsonObject>

    @POST("gatepasses/updateGatepass")
    suspend fun updateGatepass(@Body body: JsonObject): Response<JsonObject>

    @POST("gatepasses/deleteGatepass")
    suspend fun deleteGatepass(@Body body: JsonObject): Response<JsonObject>

    @POST("gatepasses/getGatepass")
    suspend fun getGatepass(@Body body: JsonObject): Response<JsonObject>

    @POST("gatepasses/loadGatepass")
    suspend fun loadGatepass(@Body body: JsonObject): Response<JsonObject>
}