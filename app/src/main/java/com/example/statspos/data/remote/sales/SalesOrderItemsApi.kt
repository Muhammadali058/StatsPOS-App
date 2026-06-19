package com.example.statspos.data.remote.sales

import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface SalesOrderItemsApi {

    @POST("salesOrderItems/loadSalesOrderItems")
    suspend fun loadSalesOrderItems(@Body body: JsonObject): Response<JsonObject>

}