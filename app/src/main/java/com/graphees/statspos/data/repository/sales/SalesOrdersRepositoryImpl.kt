package com.graphees.statspos.data.repository.sales

import com.graphees.statspos.data.remote.sales.SalesOrdersApi
import com.graphees.statspos.domain.models.sales.SalesOrders
import com.graphees.statspos.domain.repository.sales.SalesOrdersRepository
import com.graphees.statspos.utils.DB
import com.graphees.statspos.utils.HP
import com.graphees.statspos.utils.Resource
import com.graphees.statspos.utils.safeApiCall
import com.google.gson.JsonObject
import javax.inject.Inject

class SalesOrdersRepositoryImpl @Inject constructor(
    private val api: SalesOrdersApi
) : SalesOrdersRepository {

    override suspend fun loadSalesOrders(jsonObject: JsonObject): Resource<JsonObject> {
        return safeApiCall {
            api.loadSalesOrders(
                DB.addParams(jsonObject)
            )
        }
    }

    override suspend fun orderValidation(jsonObject: JsonObject): Resource<JsonObject> {
        return safeApiCall {
            api.orderValidation(
                DB.addParams(jsonObject)
            )
        }
    }

    override suspend fun updateSalesOrder(salesOrder: SalesOrders): Resource<JsonObject> {
        val body = DB.getJsonObject(salesOrder)

        return safeApiCall {
            api.updateSalesOrder(
                DB.addParams(body)
            )
        }
    }

    override suspend fun generateBill(salesOrderId: Long): Resource<JsonObject> {
        val body = JsonObject().apply {
            addProperty("salesOrderId", salesOrderId)
            addProperty("userId", HP.user.id)
        }

        return safeApiCall {
            api.generateBill(
                DB.addParams(body)
            )
        }
    }
}