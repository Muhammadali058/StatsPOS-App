package com.example.statspos.domain.repository.items

import com.example.statspos.domain.models.items.Categories
import com.example.statspos.utils.Resource
import com.google.gson.JsonObject
import okhttp3.MultipartBody

interface CategoriesRepository {
    suspend fun loadCategories(body: JsonObject): Resource<JsonObject>

    suspend fun uploadImage(image: MultipartBody.Part, body: MultipartBody.Part): Resource<JsonObject>
}