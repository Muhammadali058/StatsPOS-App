package com.graphees.statspos.data.remote.accounts

import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface CustomersApi {
    @POST("customers/loadCustomers")
    suspend fun loadCustomers(@Body body: JsonObject): Response<JsonObject>

    @POST("customers/insertCustomer")
    suspend fun insertCustomer(@Body body: JsonObject): Response<JsonObject>

    @POST("customers/updateCustomer")
    suspend fun updateCustomer(@Body body: JsonObject): Response<JsonObject>

    @POST("customers/deleteCustomer")
    suspend fun deleteCustomer(@Body body: JsonObject): Response<JsonObject>

    @POST("customers/getCustomer")
    suspend fun getCustomer(@Body body: JsonObject): Response<JsonObject>
}