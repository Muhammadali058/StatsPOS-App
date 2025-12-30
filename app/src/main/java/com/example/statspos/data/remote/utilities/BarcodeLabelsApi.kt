package com.example.statspos.data.remote.utilities

import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface BarcodeLabelsApi {
    @POST("barcodeLabels/loadBarcodeLabels")
    suspend fun loadBarcodeLabels(@Body body: JsonObject): Response<JsonObject>

    @POST("barcodeLabels/insertBarcodeLabel")
    suspend fun insertBarcodeLabel(@Body body: JsonObject): Response<JsonObject>

    @POST("barcodeLabels/updateBarcodeLabel")
    suspend fun updateBarcodeLabel(@Body body: JsonObject): Response<JsonObject>

    @POST("barcodeLabels/deleteBarcodeLabel")
    suspend fun deleteBarcodeLabel(@Body body: JsonObject): Response<JsonObject>

    @POST("barcodeLabels/getBarcodeLabel")
    suspend fun getBarcodeLabel(@Body body: JsonObject): Response<JsonObject>

    @POST("barcodeLabels/clearBarcodeLabels")
    suspend fun clearBarcodeLabels(@Body body: JsonObject): Response<JsonObject>

    @POST("barcodeLabels/getBarcodeLabels")
    suspend fun getBarcodeLabels(@Body body: JsonObject): Response<JsonObject>

    @POST("barcodeLabels/getBarcodeLabelForPreview")
    suspend fun getBarcodeLabelForPreview(@Body body: JsonObject): Response<JsonObject>
}