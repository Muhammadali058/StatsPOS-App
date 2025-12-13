package com.example.statspos.data.repository

import com.example.statspos.data.remote.CategoriesApi
import com.example.statspos.domain.models.Categories
import com.example.statspos.domain.repository.CategoriesRepository
import com.example.statspos.utils.Resource
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.coroutines.delay
import okhttp3.MultipartBody
import retrofit2.Response
import javax.inject.Inject

class CategoriesRepositoryImpl @Inject constructor(
    private val categoriesApi: CategoriesApi
) : CategoriesRepository {
    override suspend fun loadCategories(body: JsonObject): Resource<List<Categories>> {
        val result = safeApiCall { categoriesApi.loadCategories(body) }

        return when (result) {
            is Resource.Error -> Resource.Error(result.message)
            is Resource.Information -> Resource.Information(result.infoMessage)
            is Resource.Success -> {
                val jsonArray =
                    result.data.getAsJsonArray("rows") ?: return Resource.Success(emptyList())

                val categories = mutableListOf<Categories>()
                for (a in jsonArray) {
                    val cat = Gson().fromJson(a, Categories::class.java)
                    categories.add(cat)
                }

                Resource.Success(categories)
            }

            else -> Resource.Error("Unknown Error")
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
            else -> Resource.Error("Unknown Error")
        }
    }

    suspend fun <T> safeApiCall(
        apiCall: suspend () -> Response<T>
    ): Resource<T> {
        return try {
            val response = apiCall()

            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    Resource.Success(body)
                } else {
                    Resource.Error("Empty response body")
                }
            } else {
                val errorBodyString = response.errorBody()?.string()
                val jsonObject = Gson().fromJson(errorBodyString, JsonObject::class.java)

                if (jsonObject.has("result")) {
                    Resource.Information(jsonObject.get("message").asString)
                } else {
                    Resource.Error(jsonObject.get("message").asString)
                }
            }
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Network error")
        }
    }
}