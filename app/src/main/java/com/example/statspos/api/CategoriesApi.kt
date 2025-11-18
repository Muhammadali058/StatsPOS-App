package com.example.statspos.api

import com.google.gson.JsonObject
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface CategoriesApi {
    @POST("categories/loadCategories")
    suspend fun loadCategories(@Body body: JsonObject): Response<JsonObject>

    @Multipart
    @POST("main/uploadImage")
    suspend fun uploadImage(
        @Part image: MultipartBody.Part,
        @Part body: MultipartBody.Part
    ): Response<JsonObject>
}