package com.example.statspos.data.repository.main

import com.example.statspos.data.remote.main.MainApi
import com.example.statspos.domain.repository.main.MainRepository
import com.example.statspos.utils.DB
import com.example.statspos.utils.Resource
import com.example.statspos.utils.safeApiCall
import com.google.gson.JsonObject
import okhttp3.MultipartBody
import javax.inject.Inject

class MainRepositoryImpl @Inject constructor(
    private val api: MainApi
) : MainRepository {

    override suspend fun loadData(): Resource<JsonObject> {
        val body = JsonObject()

        return safeApiCall {
            api.loadData(
                DB.addParams(body)
            )
        }
    }

    override suspend fun uploadImage(
        image: MultipartBody.Part
    ): Resource<JsonObject> {
        val body = DB.addParams(JsonObject())

        return safeApiCall {
            api.uploadImage(
                image = image,
                body = MultipartBody.Part.createFormData("data", body.toString())
            )
        }
    }

    override suspend fun deleteImage(imageUrl: String): Resource<JsonObject> {
        val body = JsonObject().apply {
            addProperty("imageUrl", imageUrl)
        }

        return safeApiCall {
            api.deleteImage(
                DB.addParams(body)
            )
        }
    }
}
