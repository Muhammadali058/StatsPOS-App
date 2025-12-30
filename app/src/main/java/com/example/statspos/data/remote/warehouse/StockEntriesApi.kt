package com.example.statspos.data.remote.warehouse

import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface StockEntriesApi {
    @POST("warehouses/loadStockEntries")
    suspend fun loadStockEntries(@Body body: JsonObject): Response<JsonObject>

    @POST("warehouses/insertStockEntry")
    suspend fun insertStockEntry(@Body body: JsonObject): Response<JsonObject>

    @POST("warehouses/updateStockEntry")
    suspend fun updateStockEntry(@Body body: JsonObject): Response<JsonObject>

    @POST("warehouses/deleteStockEntry")
    suspend fun deleteStockEntry(@Body body: JsonObject): Response<JsonObject>

    @POST("warehouses/getStockEntry")
    suspend fun getStockEntry(@Body body: JsonObject): Response<JsonObject>

    @POST("warehouses/loadWarehouseEntries")
    suspend fun loadWarehouseEntries(@Body body: JsonObject): Response<JsonObject>

    @POST("warehouses/loadWarehouseEntryItems")
    suspend fun loadWarehouseEntryItems(@Body body: JsonObject): Response<JsonObject>

    @POST("warehouses/stockTransferToWarehouse")
    suspend fun stockTransferToWarehouse(@Body body: JsonObject): Response<JsonObject>

    @POST("warehouses/stockReceiveFromWarehouse")
    suspend fun stockReceiveFromWarehouse(@Body body: JsonObject): Response<JsonObject>

    @POST("warehouses/generateStockEntries")
    suspend fun generateStockEntries(@Body body: JsonObject): Response<JsonObject>

    @POST("warehouses/isBarcodeExists")
    suspend fun isBarcodeExists(@Body body: JsonObject): Response<JsonObject>
}