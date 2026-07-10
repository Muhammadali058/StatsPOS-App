package com.graphees.statspos.data.repository.warehouse

import com.graphees.statspos.data.remote.warehouse.WarehousesApi
import com.graphees.statspos.domain.models.warehouse.Warehouses
import com.graphees.statspos.domain.repository.warehouse.WarehousesRepository
import com.graphees.statspos.utils.DB
import com.graphees.statspos.utils.HP
import com.graphees.statspos.utils.Resource
import com.graphees.statspos.utils.safeApiCall
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
