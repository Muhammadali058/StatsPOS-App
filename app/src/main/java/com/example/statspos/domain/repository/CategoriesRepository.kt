package com.example.statspos.domain.repository

import com.example.statspos.domain.models.Categories
import com.example.statspos.utils.Resource
import com.google.gson.JsonObject
import okhttp3.MultipartBody

interface CategoriesRepository {
    suspend fun loadCategories(body: JsonObject): Resource<List<Categories>>

    suspend fun uploadImage(image: MultipartBody.Part, body: MultipartBody.Part): Resource<JsonObject>
}