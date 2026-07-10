package com.graphees.statspos.data.remote.items

import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface LinkedItemsApi {
    @POST("linkedItems/loadLinkedItems")
    suspend fun loadLinkedItems(@Body body: JsonObject): Response<JsonObject>

    @POST("linkedItems/insertLinkedItem")
    suspend fun insertLinkedItem(@Body body: JsonObject): Response<JsonObject>

    @POST("linkedItems/updateLinkedItem")
    suspend fun updateLinkedItem(@Body body: JsonObject): Response<JsonObject>

    @POST("linkedItems/deleteLinkedItem")
    suspend fun deleteLinkedItem(@Body body: JsonObject): Response<JsonObject>

    @POST("linkedItems/getLinkedItem")
    suspend fun getLinkedItem(@Body body: JsonObject): Response<JsonObject>
}