package com.graphees.statspos.domain.repository.sales

import com.graphees.statspos.utils.Resource
import com.google.gson.JsonObject

interface SalesOrderItemsRepository {
    suspend fun loadSalesOrderItems(salesOrderId: Long): Resource<JsonObject>
}