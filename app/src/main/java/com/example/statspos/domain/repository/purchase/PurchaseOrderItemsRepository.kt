package com.example.statspos.domain.repository.purchase

import com.example.statspos.domain.models.purchase.PurchaseOrderItems
import com.example.statspos.utils.Resource
import com.google.gson.JsonObject

interface PurchaseOrderItemsRepository {
    suspend fun loadPurchaseOrderItems(body: JsonObject): Resource<JsonObject>

    suspend fun insertPurchaseOrderItem(purchaseOrderItem: PurchaseOrderItems): Resource<JsonObject>

    suspend fun updatePurchaseOrderItem(purchaseOrderItem: PurchaseOrderItems): Resource<JsonObject>

    suspend fun deletePurchaseOrderItem(id: Long): Resource<JsonObject>

    suspend fun getPurchaseOrderItem(id: Long): Resource<JsonObject>
}