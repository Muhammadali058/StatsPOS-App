package com.graphees.statspos.data.repository.main

import com.graphees.statspos.data.remote.main.MainApi
import com.graphees.statspos.domain.repository.main.MainRepository
import com.graphees.statspos.utils.DB
import com.graphees.statspos.utils.HP
import com.graphees.statspos.utils.Resource
import com.graphees.statspos.utils.safeApiCall
import com.google.gson.JsonObject
import okhttp3.MultipartBody
import javax.inject.Inject

class MainRepositoryImpl @Inject constructor(
    private val api: MainApi
) : MainRepository {

    override suspend fun loadDashboardReport(body: JsonObject): Resource<JsonObject> {
        body.addProperty("branchGroupId", HP.branchGroupId)

        return safeApiCall {
            api.loadDashboardReport(
                DB.addParams(body)
            )
        }
    }

    override suspend fun loadData(): Resource<JsonObject> {
        val body = JsonObject().apply {
            addProperty("branchGroupId", HP.branchGroupId)
        }

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

    override suspend fun getUrduText(text:String): Resource<JsonObject> {
        val body = JsonObject().apply {
            addProperty("text", text)
        }

        return safeApiCall {
            api.getUrduText(
                DB.addParams(body)
            )
        }
    }

}
