package com.example.statspos.domain.repository.warehouse

import com.example.statspos.domain.models.warehouse.StockEntries
import com.example.statspos.utils.Resource
import com.google.gson.JsonObject

interface StockEntriesRepository {
    suspend fun loadStockEntries(body: JsonObject): Resource<JsonObject>

    suspend fun insertStockEntry(stockEntry: StockEntries): Resource<JsonObject>

    suspend fun updateStockEntry(stockEntry: StockEntries): Resource<JsonObject>

    suspend fun deleteStockEntry(id: Long): Resource<JsonObject>

    suspend fun getStockEntry(id: Long): Resource<JsonObject>

    suspend fun loadWarehouseEntries(body: JsonObject): Resource<JsonObject>

    suspend fun loadWarehouseEntryItems(body: JsonObject): Resource<JsonObject>

    suspend fun stockTransferToWarehouse(warehouseId: Long): Resource<JsonObject>

    suspend fun stockReceiveFromWarehouse(warehouseId: Long): Resource<JsonObject>

    suspend fun generateStockEntries(body: JsonObject): Resource<JsonObject>

    suspend fun isBarcodeExists(warehouseId: Long, barcode: String): Resource<JsonObject>
}