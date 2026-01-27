package com.example.statspos.utils

import com.google.gson.Gson
import com.google.gson.JsonObject
import retrofit2.Response

object DB {
    const val IS_ONLINE_MODE = true

    var HOST = "http://192.168.100.28:8000/"
    var API = "${HOST}api/"

    fun setBaseUrl(host: String){
        HOST = host
        API = "${host}api/"
    }

    fun addParams(jsonObject: JsonObject): JsonObject{
        jsonObject.addProperty("clientId", HP.clientId)
        jsonObject.addProperty("branchId", HP.branchId)
        return jsonObject
    }

    fun getJsonObject(jsonObject: Any): JsonObject{
        val body = Gson().toJsonTree(jsonObject).asJsonObject
        return body
    }
}

sealed class Resource<T> {
    data class Success<T>(val data: T) : Resource<T>()
    data class Error<T>(val error: String?) : Resource<T>()
    data class Information<T>(val message: String?, val data: JsonObject?) : Resource<T>()
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

            if (jsonObject.get("type").asString == ("e")) {
                Resource.Error(jsonObject.get("message").asString)
            } else {
                Resource.Information(
                    message = jsonObject.get("message").asString,
                    data = if(jsonObject.has("data")) jsonObject.get("data").asJsonObject else null
                )
            }
        }
    } catch (e: Exception) {
        Resource.Error(e.localizedMessage ?: "Network error")
    }
}