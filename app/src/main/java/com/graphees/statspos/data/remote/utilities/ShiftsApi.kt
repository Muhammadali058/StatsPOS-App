package com.graphees.statspos.data.remote.utilities

import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ShiftsApi {
    @POST("shifts/openShift")
    suspend fun openShift(@Body body: JsonObject): Response<JsonObject>

    @POST("shifts/closeShift")
    suspend fun closeShift(@Body body: JsonObject): Response<JsonObject>

    @POST("shifts/passEntry")
    suspend fun passEntry(@Body body: JsonObject): Response<JsonObject>

    @POST("shifts/loadUserShifts")
    suspend fun loadUserShifts(@Body body: JsonObject): Response<JsonObject>

    @POST("shifts/getShiftDetails")
    suspend fun getShiftDetails(@Body body: JsonObject): Response<JsonObject>

}