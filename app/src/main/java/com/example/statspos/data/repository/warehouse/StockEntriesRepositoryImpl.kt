package com.example.statspos.data.repository.warehouse

import com.example.statspos.data.remote.warehouse.StockEntriesApi
import com.example.statspos.domain.models.warehouse.StockEntries
import com.example.statspos.domain.repository.warehouse.StockEntriesRepository
import com.example.statspos.utils.DB
import com.example.statspos.utils.HP
import com.example.statspos.utils.Resource
import com.example.statspos.utils.safeApiCall
import com.google.gson.JsonObject
import javax.inject.Inject

class StockEntriesRepositoryImpl @Inject constructor(
    private val api: StockEntriesApi
) : StockEntriesRepository {
    override suspend fun loadStockEntries(body: JsonObject): Resource<JsonObject> {
        body.addProperty("userId", HP.user.id)

        return safeApiCall {
            api.loadStockEntries(
                DB.addParams(body)
            )
        }
    }

    override suspend fun insertStockEntry(stockEntry: StockEntries): Resource<JsonObject> {
        val body = DB.getJsonObject(stockEntry)
        body.addProperty("userId", HP.user.id)

        return safeApiCall {
            api.insertStockEntry(
                DB.addParams(body)
            )
        }
    }

    override suspend fun updateStockEntry(stockEntry: StockEntries): Resource<JsonObject> {
        val body = DB.getJsonObject(stockEntry)
        body.addProperty("userId", HP.user.id)

        return safeApiCall {
            api.updateStockEntry(
                DB.addParams(body)
            )
        }
    }

    override suspend fun deleteStockEntry(id: Long): Resource<JsonObject> {
        val body = JsonObject().apply {
            addProperty("id", id)
        }
        return safeApiCall {
            api.deleteStockEntry(
                DB.addParams(body)
            )
        }
    }

    override suspend fun getStockEntry(id: Long): Resource<JsonObject> {
        val body = JsonObject().apply {
            addProperty("id", id)
        }
        return safeApiCall {
            api.getStockEntry(
                DB.addParams(body)
            )
        }
    }

    override suspend fun loadWarehouseEntries(body: JsonObject): Resource<JsonObject> {
        return safeApiCall {
            api.loadWarehouseEntries(
                DB.addParams(body)
            )
        }
    }

    override suspend fun loadWarehouseEntryItems(body: JsonObject): Resource<JsonObject> {
        return safeApiCall {
            api.loadWarehouseEntryItems(
                DB.addParams(body)
            )
        }
    }

    override suspend fun stockTransferToWarehouse(warehouseId: Long): Resource<JsonObject> {
        val body = JsonObject().apply {
            addProperty("warehouseId", warehouseId)
            addProperty("userId", HP.user.id)
        }
        return safeApiCall {
            api.stockTransferToWarehouse(
                DB.addParams(body)
            )
        }
    }

    override suspend fun stockReceiveFromWarehouse(warehouseId: Long): Resource<JsonObject> {
        val body = JsonObject().apply {
            addProperty("warehouseId", warehouseId)
            addProperty("userId", HP.user.id)
        }
        return safeApiCall {
            api.stockReceiveFromWarehouse(
                DB.addParams(body)
            )
        }
    }

    override suspend fun generateStockEntries(body: JsonObject): Resource<JsonObject> {
        body.addProperty("userId", HP.user.id)

        return safeApiCall {
            api.generateStockEntries(
                DB.addParams(body)
            )
        }
    }

    override suspend fun isBarcodeExists(warehouseId: Long, barcode: String): Resource<JsonObject> {
        val body = JsonObject().apply {
            addProperty("barcode", barcode)
            addProperty("warehouseId", warehouseId)
            addProperty("branchGroupId", HP.branchGroupId)
        }
        return safeApiCall {
            api.isBarcodeExists(
                DB.addParams(body)
            )
        }
    }
}
