package com.graphees.statspos.data.remote.accounts

import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface VendorsApi {
    @POST("vendors/loadVendors")
    suspend fun loadVendors(@Body body: JsonObject): Response<JsonObject>

    @POST("vendors/insertVendor")
    suspend fun insertVendor(@Body body: JsonObject): Response<JsonObject>

    @POST("vendors/updateVendor")
    suspend fun updateVendor(@Body body: JsonObject): Response<JsonObject>

    @POST("vendors/deleteVendor")
    suspend fun deleteVendor(@Body body: JsonObject): Response<JsonObject>

    @POST("vendors/getVendor")
    suspend fun getVendor(@Body body: JsonObject): Response<JsonObject>
}