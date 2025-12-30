package com.example.statspos.data.remote.accounts

import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface EmployeesApi {
    @POST("employees/loadEmployees")
    suspend fun loadEmployees(@Body body: JsonObject): Response<JsonObject>

    @POST("employees/insertEmployee")
    suspend fun insertEmployee(@Body body: JsonObject): Response<JsonObject>

    @POST("employees/updateEmployee")
    suspend fun updateEmployee(@Body body: JsonObject): Response<JsonObject>

    @POST("employees/deleteEmployee")
    suspend fun deleteEmployee(@Body body: JsonObject): Response<JsonObject>

    @POST("employees/getEmployee")
    suspend fun getEmployee(@Body body: JsonObject): Response<JsonObject>
}