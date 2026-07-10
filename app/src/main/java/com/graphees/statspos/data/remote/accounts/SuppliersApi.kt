package com.graphees.statspos.data.remote.accounts

import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface SuppliersApi {
    @POST("suppliers/loadSuppliers")
    suspend fun loadSuppliers(@Body body: JsonObject): Response<JsonObject>

    @POST("suppliers/insertSupplier")
    suspend fun insertSupplier(@Body body: JsonObject): Response<JsonObject>

    @POST("suppliers/updateSupplier")
    suspend fun updateSupplier(@Body body: JsonObject): Response<JsonObject>

    @POST("suppliers/deleteSupplier")
    suspend fun deleteSupplier(@Body body: JsonObject): Response<JsonObject>

    @POST("suppliers/getSupplier")
    suspend fun getSupplier(@Body body: JsonObject): Response<JsonObject>
}