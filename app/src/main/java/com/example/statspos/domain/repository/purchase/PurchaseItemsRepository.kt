package com.example.statspos.domain.repository.purchase

import com.example.statspos.domain.models.purchase.PurchaseItems
import com.example.statspos.utils.Resource
import com.google.gson.JsonObject

interface PurchaseItemsRepository {
    suspend fun loadPurchaseItems(body: JsonObject): Resource<JsonObject>

    suspend fun insertPurchaseItem(purchaseItem: PurchaseItems): Resource<JsonObject>

    suspend fun updatePurchaseItem(purchaseItem: PurchaseItems): Resource<JsonObject>

    suspend fun deletePurchaseItem(id: Long, isPostedBill: Boolean): Resource<JsonObject>

    suspend fun getPurchaseItem(id: Long, isPostedBill: Boolean): Resource<JsonObject>

    suspend fun isBarcodeExists(body: JsonObject): Resource<JsonObject>
}