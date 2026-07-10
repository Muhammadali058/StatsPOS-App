package com.graphees.statspos.domain.repository.purchase

import com.graphees.statspos.domain.models.purchase.PurchaseOrders
import com.graphees.statspos.utils.Resource
import com.google.gson.JsonObject

interface PurchaseOrdersRepository {
    suspend fun loadPurchaseOrders(body: JsonObject): Resource<JsonObject>

    suspend fun insertPurchaseOrder(purchaseOrder: PurchaseOrders): Resource<JsonObject>

    suspend fun updatePurchaseOrder(purchaseOrder: PurchaseOrders): Resource<JsonObject>

    suspend fun deletePurchaseOrder(id: Long): Resource<JsonObject>

    suspend fun getPurchaseOrder(id: Long): Resource<JsonObject>

    suspend fun getOrder(id: Long): Resource<JsonObject>
}