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

    // region Package-Items
    @POST("packageItems/loadPackageItems")
    suspend fun loadPackageItems(@Body body: JsonObject): Response<JsonObject>

    @POST("packageItems/insertPackageItem")
    suspend fun insertPackageItem(@Body body: JsonObject): Response<JsonObject>

    @POST("packageItems/updatePackageItem")
    suspend fun updatePackageItem(@Body body: JsonObject): Response<JsonObject>

    @POST("packageItems/deletePackageItem")
    suspend fun deletePackageItem(@Body body: JsonObject): Response<JsonObject>

    @POST("packageItems/getPackageItem")
    suspend fun getPackageItem(@Body body: JsonObject): Response<JsonObject>
    // endregion
}