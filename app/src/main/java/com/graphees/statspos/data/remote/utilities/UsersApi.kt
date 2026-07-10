package com.graphees.statspos.data.remote.utilities

import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface UsersApi {
    @POST("users/login")
    suspend fun login(@Body body: JsonObject): Response<JsonObject>

    @POST("users/loadUsers")
    suspend fun loadUsers(@Body body: JsonObject): Response<JsonObject>

    @POST("users/insertUser")
    suspend fun insertUser(@Body body: JsonObject): Response<JsonObject>

    @POST("users/updateUser")
    suspend fun updateUser(@Body body: JsonObject): Response<JsonObject>

    @POST("users/deleteUser")
    suspend fun deleteUser(@Body body: JsonObject): Response<JsonObject>

    @POST("users/getUser")
    suspend fun getUser(@Body body: JsonObject): Response<JsonObject>

}