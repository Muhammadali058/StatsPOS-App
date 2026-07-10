package com.graphees.statspos.domain.repository.warehouse

import com.graphees.statspos.domain.models.warehouse.Warehouses
import com.graphees.statspos.utils.Resource
import com.google.gson.JsonObject

interface WarehousesRepository {
    suspend fun loadWarehouses(body: JsonObject): Resource<JsonObject>

    suspend fun insertWarehouse(warehouse: Warehouses): Resource<JsonObject>

    suspend fun updateWarehouse(warehouse: Warehouses): Resource<JsonObject>

    suspend fun deleteWarehouse(id: Long): Resource<JsonObject>

    suspend fun getWarehouse(id: Long): Resource<JsonObject>
}