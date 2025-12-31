package com.example.statspos.data.repository.purchase

import com.example.statspos.data.remote.purchase.PurchaseOrderItemsApi
import com.example.statspos.domain.models.purchase.PurchaseOrderItems
import com.example.statspos.domain.repository.purchase.PurchaseOrderItemsRepository
import com.example.statspos.utils.DB
import com.example.statspos.utils.Resource
import com.example.statspos.utils.safeApiCall
import com.google.gson.JsonObject
import javax.inject.Inject

class PurchaseOrderItemsRepositoryImpl @Inject constructor(
    private val api: PurchaseOrderItemsApi
) : PurchaseOrderItemsRepository {
    override suspend fun loadPurchaseOrderItems(body: JsonObject): Resource<JsonObject> {
        return safeApiCall {
            api.loadPurchaseOrderItems(
                DB.addParams(body)
            )
        }
    }

    override suspend fun insertPurchaseOrderItem(purchaseOrderItem: PurchaseOrderItems): Resource<JsonObject> {
        val body = DB.getJsonObject(purchaseOrderItem)
        return safeApiCall {
            api.insertPurchaseOrderItem(
                DB.addParams(body)
            )
        }
    }

    override suspend fun updatePurchaseOrderItem(purchaseOrderItem: PurchaseOrderItems): Resource<JsonObject> {
        val body = DB.getJsonObject(purchaseOrderItem)
        return safeApiCall {
            api.updatePurchaseOrderItem(
                DB.addParams(body)
            )
        }
    }

    override suspend fun deletePurchaseOrderItem(id: Long): Resource<JsonObject> {
        val body = JsonObject().apply {
            addProperty("id", id)
        }
        return safeApiCall {
            api.deletePurchaseOrderItem(
                DB.addParams(body)
            )
        }
    }

    override suspend fun getPurchaseOrderItem(id: Long): Resource<JsonObject> {
        val body = JsonObject().apply {
            addProperty("id", id)
        }
        return safeApiCall {
            api.getPurchaseOrderItem(
                DB.addParams(body)
            )
        }
    }
}
