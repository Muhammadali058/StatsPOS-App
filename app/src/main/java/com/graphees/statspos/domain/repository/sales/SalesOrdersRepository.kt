package com.graphees.statspos.domain.repository.sales

import com.graphees.statspos.domain.models.sales.SalesOrders
import com.graphees.statspos.utils.Resource
import com.google.gson.JsonObject

interface SalesOrdersRepository {
    suspend fun loadSalesOrders(jsonObject: JsonObject): Resource<JsonObject>
    suspend fun orderValidation(jsonObject: JsonObject): Resource<JsonObject>
    suspend fun updateSalesOrder(salesOrder: SalesOrders): Resource<JsonObject>
    suspend fun generateBill(salesOrderId: Long): Resource<JsonObject>
}