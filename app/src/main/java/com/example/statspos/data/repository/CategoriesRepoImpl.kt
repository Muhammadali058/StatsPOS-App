package com.example.statspos.data.repository

import com.example.statspos.data.remote.CategoriesApi
import com.example.statspos.domain.repository.CategoriesRepo
import com.google.gson.JsonObject
import okhttp3.MultipartBody
import retrofit2.Response
import javax.inject.Inject

class CategoriesRepoImpl @Inject constructor(
    private val categoriesApi: CategoriesApi
): CategoriesRepo {
    override suspend fun loadCategories(body: JsonObject): Response<JsonObject> {
        return categoriesApi.loadCategories(body)
    }

    override suspend fun uploadImage(
        image: MultipartBody.Part,
        body: MultipartBody.Part
    ): Response<JsonObject> {
        return categoriesApi.uploadImage(image, body)
    }
}