package com.example.statspos.domain.repository.main

import com.example.statspos.utils.Resource
import com.google.gson.JsonObject
import okhttp3.MultipartBody

interface MainRepository {
    suspend fun loadDashboardReport(body: JsonObject): Resource<JsonObject>

    suspend fun loadData(): Resource<JsonObject>

    suspend fun uploadImage(image: MultipartBody.Part): Resource<JsonObject>

    suspend fun deleteImage(imageUrl: String): Resource<JsonObject>
}