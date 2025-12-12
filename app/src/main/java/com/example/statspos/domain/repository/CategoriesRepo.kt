package com.example.statspos.domain.repository

import com.google.gson.JsonObject
import okhttp3.MultipartBody
import retrofit2.Response

interface CategoriesRepo {
    suspend fun loadCategories(body: JsonObject): Response<JsonObject>

    suspend fun uploadImage(image: MultipartBody.Part, body: MultipartBody.Part): Response<JsonObject>
}