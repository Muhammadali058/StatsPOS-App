package com.example.statspos.domain.repository.purchase

import com.example.statspos.domain.models.purchase.Purchase
import com.example.statspos.utils.Resource
import com.google.gson.JsonObject

interface PurchaseRepository {
    suspend fun insertPurchase(purchase: Purchase): Resource<JsonObject>

    suspend fun updatePurchase(purchase: Purchase): Resource<JsonObject>

    suspend fun deletePurchase(id: Long, isPostedBill: Boolean): Resource<JsonObject>

    suspend fun getPurchase(id: Long, isPostedBill: Boolean): Resource<JsonObject>

    suspend fun tempClose(purchase: Purchase): Resource<JsonObject>

    suspend fun getInvoiceId(): Resource<JsonObject>

    suspend fun isReplaceExists(vendorId: Long): Resource<JsonObject>

    suspend fun getBill(body: JsonObject): Resource<JsonObject>

    suspend fun generateBarcodeLabels(body: JsonObject): Resource<JsonObject>

    suspend fun loadPendingBills(body: JsonObject): Resource<JsonObject>

    suspend fun loadPendingBillItems(purchaseId: Long): Resource<JsonObject>

    suspend fun loadPostedBills(body: JsonObject): Resource<JsonObject>

    suspend fun loadPostedBillItems(purchaseId: Long): Resource<JsonObject>
}