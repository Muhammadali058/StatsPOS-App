package com.graphees.statspos.data.remote.main

import com.google.gson.JsonObject
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface MainApi {
    @POST("main/loadDashboardReport")
    suspend fun loadDashboardReport(@Body body: JsonObject): Response<JsonObject>

    @POST("main/loadData")
    suspend fun loadData(@Body body: JsonObject): Response<JsonObject>

    @Multipart
    @POST("main/uploadImage")
    suspend fun uploadImage(
        @Part image: MultipartBody.Part,
        @Part body: MultipartBody.Part
    ): Response<JsonObject>

    @POST("main/deleteImage")
    suspend fun deleteImage(@Body body: JsonObject): Response<JsonObject>

    @POST("main/getUrduText")
    suspend fun getUrduText(@Body body: JsonObject): Response<JsonObject>

}