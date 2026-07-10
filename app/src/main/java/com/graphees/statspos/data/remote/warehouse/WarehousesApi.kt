package com.graphees.statspos.data.remote.warehouse

import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface WarehousesApi {
    @POST("warehouses/loadWarehouses")
    suspend fun loadWarehouses(@Body body: JsonObject): Response<JsonObject>

    @POST("warehouses/insertWarehouse")
    suspend fun insertWarehouse(@Body body: JsonObject): Response<JsonObject>

    @POST("warehouses/updateWarehouse")
    suspend fun updateWarehouse(@Body body: JsonObject): Response<JsonObject>

    @POST("warehouses/deleteWarehouse")
    suspend fun deleteWarehouse(@Body body: JsonObject): Response<JsonObject>

    @POST("warehouses/getWarehouse")
    suspend fun getWarehouse(@Body body: JsonObject): Response<JsonObject>
}