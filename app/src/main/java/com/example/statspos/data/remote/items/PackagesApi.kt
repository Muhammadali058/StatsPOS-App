package com.example.statspos.data.remote.items

import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface PackagesApi {
    // region Packages
    @POST("packages/loadPackages")
    suspend fun loadPackages(@Body body: JsonObject): Response<JsonObject>

    @POST("packages/insertPackage")
    suspend fun insertPackage(@Body body: JsonObject): Response<JsonObject>

    @POST("packages/updatePackage")
    suspend fun updatePackage(@Body body: JsonObject): Response<JsonObject>

    @POST("packages/deletePackage")
    suspend fun deletePackage(@Body body: JsonObject): Response<JsonObject>

    @POST("packages/getPackage")
    suspend fun getPackage(@Body body: JsonObject): Response<JsonObject>

    @POST("packages/generatePackage")
    suspend fun generatePackage(@Body body: JsonObject): Response<JsonObject>
    // endregion

    // region Sub-Packages
    @POST("packageItems/loadSubPackages")
    suspend fun loadSubPackages(@Body body: JsonObject): Response<JsonObject>

    @POST("packageItems/insertSubPackage")
    suspend fun insertSubPackage(@Body body: JsonObject): Response<JsonObject>

    @POST("packageItems/updateSubPackage")
    suspend fun updateSubPackage(@Body body: JsonObject): Response<JsonObject>

    @POST("packageItems/deleteSubPackage")
    suspend fun deleteSubPackage(@Body body: JsonObject): Response<JsonObject>

    @POST("packageItems/getSubPackage")
    suspend fun getSubPackage(@Body body: JsonObject): Response<JsonObject>
    // endregion
}