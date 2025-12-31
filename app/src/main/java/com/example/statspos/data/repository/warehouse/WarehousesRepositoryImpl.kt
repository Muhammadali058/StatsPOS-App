package com.example.statspos.data.repository.warehouse

import com.example.statspos.data.remote.warehouse.WarehousesApi
import com.example.statspos.domain.models.warehouse.Warehouses
import com.example.statspos.domain.repository.warehouse.WarehousesRepository
import com.example.statspos.utils.DB
import com.example.statspos.utils.HP
import com.example.statspos.utils.Resource
import com.example.statspos.utils.safeApiCall
import com.google.gson.JsonObject
import javax.inject.Inject

class WarehousesRepositoryImpl @Inject constructor(
    private val api: WarehousesApi
) : WarehousesRepository {
    override suspend fun loadWarehouses(body: JsonObject): Resource<JsonObject> {
        return safeApiCall {
            api.loadWarehouses(
                DB.addParams(body)
            )
        }
    }

    override suspend fun insertWarehouse(warehouse: Warehouses): Resource<JsonObject> {
        val body = DB.getJsonObject(warehouse)
        return safeApiCall {
            api.insertWarehouse(
                DB.addParams(body)
            )
        }
    }

    override suspend fun updateWarehouse(warehouse: Warehouses): Resource<JsonObject> {
        val body = DB.getJsonObject(warehouse)
        return safeApiCall {
            api.updateWarehouse(
                DB.addParams(body)
            )
        }
    }

    override suspend fun deleteWarehouse(id: Long): Resource<JsonObject> {
        val body = JsonObject().apply {
            addProperty("id", id)
            addProperty("userId", HP.user.id)
        }
        return safeApiCall {
            api.deleteWarehouse(
                DB.addParams(body)
            )
        }
    }

    override suspend fun getWarehouse(id: Long): Resource<JsonObject> {
        val body = JsonObject().apply {
            addProperty("id", id)
        }
        return safeApiCall {
            api.getWarehouse(
                DB.addParams(body)
            )
        }
    }
}
