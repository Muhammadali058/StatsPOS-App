package com.example.statspos.data.repository.sales

import com.example.statspos.data.remote.sales.SalesApi
import com.example.statspos.domain.models.sales.Sales
import com.example.statspos.domain.repository.sales.SalesRepository
import com.example.statspos.utils.DB
import com.example.statspos.utils.HP
import com.example.statspos.utils.Resource
import com.example.statspos.utils.safeApiCall
import com.google.gson.JsonObject
import javax.inject.Inject

class SalesRepositoryImpl @Inject constructor(
    private val api: SalesApi
) : SalesRepository {
    override suspend fun insertSales(sale: Sales): Resource<JsonObject> {
        val body = DB.getJsonObject(sale)
        body.addProperty("userId", HP.user.id)

        return safeApiCall {
            api.insertSales(
                DB.addParams(body)
            )
        }
    }

    override suspend fun updateSales(sale: Sales): Resource<JsonObject> {
        val body = DB.getJsonObject(sale)
        body.addProperty("userId", HP.user.id)

        return safeApiCall {
            api.updateSales(
                DB.addParams(body)
            )
        }
    }

    override suspend fun deleteSales(id: Long, isPostedBill: Boolean): Resource<JsonObject> {
        val body = JsonObject().apply {
            addProperty("id", id)
            addProperty("userId", HP.user.id)
            addProperty("isPostedBill", isPostedBill)
        }
        return safeApiCall {
            api.deleteSales(
                DB.addParams(body)
            )
        }
    }

    override suspend fun getSales(id: Long, isPostedBill: Boolean): Resource<JsonObject> {
        val body = JsonObject().apply {
            addProperty("id", id)
            addProperty("userId", HP.user.id)
            addProperty("isPostedBill", isPostedBill)
        }
        return safeApiCall {
            api.getSales(
                DB.addParams(body)
            )
        }
    }

    override suspend fun tempClose(sale: Sales): Resource<JsonObject> {
        val body = DB.getJsonObject(sale)
        body.addProperty("userId", HP.user.id)

        return safeApiCall {
            api.tempClose(
                DB.addParams(body)
            )
        }
    }

    override suspend fun getInvoiceId(): Resource<JsonObject> {
        val body = JsonObject().apply {
            addProperty("userId", HP.user.id)
        }
        return safeApiCall {
            api.getInvoiceId(
                DB.addParams(body)
            )
        }
    }

    override suspend fun getBill(body: JsonObject): Resource<JsonObject> {
        body.addProperty("userId", HP.user.id)

        return safeApiCall {
            api.getBill(
                DB.addParams(body)
            )
        }
    }

    override suspend fun loadPendingBills(body: JsonObject): Resource<JsonObject> {
        body.addProperty("userId", HP.user.id)

        return safeApiCall {
            api.loadPendingBills(
                DB.addParams(body)
            )
        }
    }

    override suspend fun loadPendingBillItems(salesId: Long): Resource<JsonObject> {
        val body = JsonObject().apply {
            addProperty("salesId", salesId)
            addProperty("userId", HP.user.id)
        }
        return safeApiCall {
            api.loadPendingBillItems(
                DB.addParams(body)
            )
        }
    }

    override suspend fun loadPostedBills(body: JsonObject): Resource<JsonObject> {
        return safeApiCall {
            api.loadPostedBills(
                DB.addParams(body)
            )
        }
    }

    override suspend fun loadPostedBillItems(salesId: Long): Resource<JsonObject> {
        val body = JsonObject().apply {
            addProperty("salesId", salesId)
        }
        return safeApiCall {
            api.loadPostedBillItems(
                DB.addParams(body)
            )
        }
    }

    override suspend fun changeBillType(body: JsonObject): Resource<JsonObject> {
        body.addProperty("userId", HP.user.id)

        return safeApiCall {
            api.changeBillType(
                DB.addParams(body)
            )
        }
    }

    override suspend fun generateReturnBill(body: JsonObject): Resource<JsonObject> {
        return safeApiCall {
            api.generateReturnBill(
                DB.addParams(body)
            )
        }
    }
}
