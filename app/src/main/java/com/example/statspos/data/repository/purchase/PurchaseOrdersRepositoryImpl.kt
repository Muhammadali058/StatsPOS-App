package com.example.statspos.data.repository.purchase

import com.example.statspos.data.remote.purchase.PurchaseOrdersApi
import com.example.statspos.domain.models.purchase.PurchaseOrders
import com.example.statspos.domain.repository.purchase.PurchaseOrdersRepository
import com.example.statspos.utils.DB
import com.example.statspos.utils.HP
import com.example.statspos.utils.Resource
import com.example.statspos.utils.safeApiCall
import com.google.gson.JsonObject
import javax.inject.Inject

class PurchaseOrdersRepositoryImpl @Inject constructor(
    private val api: PurchaseOrdersApi
) : PurchaseOrdersRepository {
    override suspend fun loadPurchaseOrders(body: JsonObject): Resource<JsonObject> {
        return safeApiCall {
            api.loadPurchaseOrders(
                DB.addParams(body)
            )
        }
    }

    override suspend fun insertPurchaseOrder(purchaseOrder: PurchaseOrders): Resource<JsonObject> {
        val body = DB.getJsonObject(purchaseOrder)
        return safeApiCall {
            api.insertPurchaseOrder(
                DB.addParams(body)
            )
        }
    }

    override suspend fun updatePurchaseOrder(purchaseOrder: PurchaseOrders): Resource<JsonObject> {
        val body = DB.getJsonObject(purchaseOrder)
        return safeApiCall {
            api.updatePurchaseOrder(
                DB.addParams(body)
            )
        }
    }

    override suspend fun deletePurchaseOrder(id: Long): Resource<JsonObject> {
        val body = JsonObject().apply {
            addProperty("id", id)
            addProperty("userId", HP.user.id)
        }
        return safeApiCall {
            api.deletePurchaseOrder(
                DB.addParams(body)
            )
        }
    }

    override suspend fun getPurchaseOrder(id: Long): Resource<JsonObject> {
        val body = JsonObject().apply {
            addProperty("id", id)
        }
        return safeApiCall {
            api.getPurchaseOrder(
                DB.addParams(body)
            )
        }
    }

    override suspend fun getOrder(id: Long): Resource<JsonObject> {
        val body = JsonObject().apply {
            addProperty("id", id)
        }
        return safeApiCall {
            api.getOrder(
                DB.addParams(body)
            )
        }
    }
}
