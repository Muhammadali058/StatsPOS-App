package com.graphees.statspos.domain.repository.sales

import com.graphees.statspos.domain.models.sales.Sales
import com.graphees.statspos.utils.Resource
import com.google.gson.JsonObject

interface SalesRepository {
    suspend fun insertSales(sale: Sales): Resource<JsonObject>

    suspend fun updateSales(sale: Sales): Resource<JsonObject>

    suspend fun deleteSales(id: Long, isPostedBill: Boolean): Resource<JsonObject>

    suspend fun getSales(id: Long, isPostedBill: Boolean): Resource<JsonObject>

    suspend fun tempClose(sale: Sales): Resource<JsonObject>

    suspend fun getInvoiceId(): Resource<JsonObject>

    suspend fun getBill(body: JsonObject): Resource<JsonObject>

    suspend fun loadPendingBills(body: JsonObject): Resource<JsonObject>

    suspend fun loadPendingBillItems(salesId: Long): Resource<JsonObject>

    suspend fun loadPostedBills(body: JsonObject): Resource<JsonObject>

    suspend fun loadPostedBillItems(salesId: Long): Resource<JsonObject>

    suspend fun changeBillType(body: JsonObject): Resource<JsonObject>

    suspend fun generateReturnBill(body: JsonObject): Resource<JsonObject>

}