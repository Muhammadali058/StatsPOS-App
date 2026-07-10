package com.graphees.statspos.data.remote.purchase

import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface RejectedItemsApi {
    @POST("rejectedItems/loadRejectedItems")
    suspend fun loadRejectedItems(@Body body: JsonObject): Response<JsonObject>

    @POST("rejectedItems/insertRejectedItem")
    suspend fun insertRejectedItem(@Body body: JsonObject): Response<JsonObject>

    @POST("rejectedItems/updateRejectedItem")
    suspend fun updateRejectedItem(@Body body: JsonObject): Response<JsonObject>

    @POST("rejectedItems/deleteRejectedItem")
    suspend fun deleteRejectedItem(@Body body: JsonObject): Response<JsonObject>

    @POST("rejectedItems/getRejectedItem")
    suspend fun getRejectedItem(@Body body: JsonObject): Response<JsonObject>
}