package com.example.statspos.data.repository.sales

import com.example.statspos.data.remote.sales.SalesOrderItemsApi
import com.example.statspos.utils.DB
import com.example.statspos.utils.Resource
import com.example.statspos.utils.safeApiCall
import com.example.statspos.domain.repository.sales.SalesOrderItemsRepository
import com.google.gson.JsonObject
import javax.inject.Inject

class SalesOrderItemsRepositoryImpl @Inject constructor(
    private val api: SalesOrderItemsApi
) : SalesOrderItemsRepository {

    override suspend fun loadSalesOrderItems(salesOrderId: Long): Resource<JsonObject> {
        val body = JsonObject().apply {
            addProperty("salesOrderId", salesOrderId)
        }

        return safeApiCall {
            api.loadSalesOrderItems(
                DB.addParams(body)
            )
        }
    }

}