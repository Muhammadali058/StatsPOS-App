package com.example.statspos.data.remote.sales

import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface SalesOrdersApi {

    @POST("salesOrders/loadSalesOrders")
    suspend fun loadSalesOrders(@Body body: JsonObject): Response<JsonObject>

    @POST("salesOrders/orderValidation")
    suspend fun orderValidation(@Body body: JsonObject): Response<JsonObject>

    @POST("salesOrders/insertSalesOrder")
    suspend fun insertSalesOrder(@Body body: JsonObject): Response<JsonObject>

    @POST("salesOrders/updateSalesOrder")
    suspend fun updateSalesOrder(@Body body: JsonObject): Response<JsonObject>
}