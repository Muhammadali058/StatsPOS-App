package com.graphees.statspos.data.repository.sales

import com.graphees.statspos.data.remote.sales.SalesOrderItemsApi
import com.graphees.statspos.utils.DB
import com.graphees.statspos.utils.Resource
import com.graphees.statspos.utils.safeApiCall
import com.graphees.statspos.domain.repository.sales.SalesOrderItemsRepository
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