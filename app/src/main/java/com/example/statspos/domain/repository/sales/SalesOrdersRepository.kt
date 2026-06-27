package com.example.statspos.domain.repository.sales

import com.example.statspos.domain.models.sales.SalesOrders
import com.example.statspos.utils.Resource
import com.google.gson.JsonObject

interface SalesOrdersRepository {
    suspend fun loadSalesOrders(jsonObject: JsonObject): Resource<JsonObject>
    suspend fun orderValidation(jsonObject: JsonObject): Resource<JsonObject>
    suspend fun updateSalesOrder(salesOrder: SalesOrders): Resource<JsonObject>
}