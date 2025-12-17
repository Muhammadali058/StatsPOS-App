package com.example.statspos.data.repository.items

import com.example.statspos.data.remote.items.CategoriesApi
import com.example.statspos.domain.models.items.Categories
import com.example.statspos.domain.repository.items.CategoriesRepository
import com.example.statspos.utils.Resource
import com.example.statspos.utils.safeApiCall
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.coroutines.delay
import okhttp3.MultipartBody
import retrofit2.Response
import javax.inject.Inject

class CategoriesRepositoryImpl @Inject constructor(
    private val categoriesApi: CategoriesApi
) : CategoriesRepository {
    override suspend fun loadCategories(body: JsonObject): Resource<JsonObject> {
        val result = safeApiCall { categoriesApi.loadCategories(body) }

        return when (result) {
            is Resource.Error -> Resource.Error(result.message)
            is Resource.Information -> Resource.Information(result.infoMessage)
            is Resource.Success -> Resource.Success(result.data)
        }
    }

    override suspend fun uploadImage(
        image: MultipartBody.Part,
        body: MultipartBody.Part
    ): Resource<JsonObject> {
        val result = safeApiCall { categoriesApi.uploadImage(image, body) }

        return when (result) {
            is Resource.Error -> Resource.Error(result.message)
            is Resource.Information -> Resource.Information(result.infoMessage)
            is Resource.Success -> Resource.Success(result.data)
        }
    }

}
