package com.example.statspos.utils

import com.google.gson.Gson
import com.google.gson.JsonObject
import retrofit2.Response

object DB {
    const val HOST = "http://192.168.100.28:8000/"
    const val API = "${HOST}api/"
}

sealed class Resource<T> {
    data class Success<T>(val data: T) : Resource<T>()
    data class Error<T>(val message: String?) : Resource<T>()
    data class Information<T>(val infoMessage: String?) : Resource<T>()
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