package com.graphees.statspos.data.repository.warehouse

import com.graphees.statspos.data.remote.warehouse.GatepassItemsApi
import com.graphees.statspos.domain.models.warehouse.GatepassItems
import com.graphees.statspos.domain.repository.warehouse.GatepassItemsRepository
import com.graphees.statspos.utils.DB
import com.graphees.statspos.utils.Resource
import com.graphees.statspos.utils.safeApiCall
import com.google.gson.JsonObject
import javax.inject.Inject

class GatepassItemsRepositoryImpl @Inject constructor(
    private val api: GatepassItemsApi
) : GatepassItemsRepository {
    override suspend fun loadGatepassItems(body: JsonObject): Resource<JsonObject> {
        return safeApiCall {
            api.loadGatepassItems(
                DB.addParams(body)
            )
        }
    }

    override suspend fun insertGatepassItem(gatepassItem: GatepassItems): Resource<JsonObject> {
        val body = DB.getJsonObject(gatepassItem)
        return safeApiCall {
            api.insertGatepassItem(
                DB.addParams(body)
            )
        }
    }

    override suspend fun updateGatepassItem(gatepassItem: GatepassItems): Resource<JsonObject> {
        val body = DB.getJsonObject(gatepassItem)
        return safeApiCall {
            api.updateGatepassItem(
                DB.addParams(body)
            )
        }
    }

    override suspend fun deleteGatepassItem(id: Long): Resource<JsonObject> {
        val body = JsonObject().apply {
            addProperty("id", id)
        }
        return safeApiCall {
            api.deleteGatepassItem(
                DB.addParams(body)
            )
        }
    }

    override suspend fun getGatepassItem(id: Long): Resource<JsonObject> {
        val body = JsonObject().apply {
            addProperty("id", id)
        }
        return safeApiCall {
            api.getGatepassItem(
                DB.addParams(body)
            )
        }
    }
}
