package com.example.statspos.domain.repository.sales

import com.example.statspos.domain.models.sales.SalesItems
import com.example.statspos.utils.Resource
import com.google.gson.JsonObject

interface SalesItemsRepository {
    suspend fun loadSalesItems(body: JsonObject): Resource<JsonObject>

    suspend fun insertSalesItem(salesItem: SalesItems): Resource<JsonObject>

    suspend fun updateSalesItem(salesItem: SalesItems): Resource<JsonObject>

    suspend fun deleteSalesItem(id: Long, isPostedBill: Boolean): Resource<JsonObject>

    suspend fun getSalesItem(id: Long, isPostedBill: Boolean): Resource<JsonObject>

    suspend fun isBarcodeExists(body: JsonObject): Resource<JsonObject>
}