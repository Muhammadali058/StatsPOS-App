package com.example.statspos.domain.repository.sales

import com.example.statspos.utils.Resource
import com.google.gson.JsonObject

interface SalesOrderItemsRepository {
    suspend fun loadSalesOrderItems(salesOrderId: Long): Resource<JsonObject>
}