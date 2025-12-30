package com.example.statspos.data.remote.items

import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface SubBarcodesApi {
    @POST("subBarcodes/loadSubBarcodes")
    suspend fun loadSubBarcodes(@Body body: JsonObject): Response<JsonObject>

    @POST("subBarcodes/insertSubBarcode")
    suspend fun insertSubBarcode(@Body body: JsonObject): Response<JsonObject>

    @POST("subBarcodes/updateSubBarcode")
    suspend fun updateSubBarcode(@Body body: JsonObject): Response<JsonObject>

    @POST("subBarcodes/deleteSubBarcode")
    suspend fun deleteSubBarcode(@Body body: JsonObject): Response<JsonObject>

    @POST("subBarcodes/getSubBarcode")
    suspend fun getSubBarcode(@Body body: JsonObject): Response<JsonObject>
}