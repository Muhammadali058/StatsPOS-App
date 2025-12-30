package com.example.statspos.data.remote.warehouse

import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface GatepassItemsApi {
    @POST("gatepassItems/loadGatepassItems")
    suspend fun loadGatepassItems(@Body body: JsonObject): Response<JsonObject>

    @POST("gatepassItems/insertGatepassItem")
    suspend fun insertGatepassItem(@Body body: JsonObject): Response<JsonObject>

    @POST("gatepassItems/updateGatepassItem")
    suspend fun updateGatepassItem(@Body body: JsonObject): Response<JsonObject>

    @POST("gatepassItems/deleteGatepassItem")
    suspend fun deleteGatepassItem(@Body body: JsonObject): Response<JsonObject>

    @POST("gatepassItems/getGatepassItem")
    suspend fun getGatepassItem(@Body body: JsonObject): Response<JsonObject>
}