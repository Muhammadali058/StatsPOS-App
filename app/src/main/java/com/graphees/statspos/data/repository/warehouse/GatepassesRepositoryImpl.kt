package com.graphees.statspos.data.repository.warehouse

import com.graphees.statspos.data.remote.warehouse.GatepassesApi
import com.graphees.statspos.domain.models.warehouse.Gatepasses
import com.graphees.statspos.domain.repository.warehouse.GatepassesRepository
import com.graphees.statspos.utils.DB
import com.graphees.statspos.utils.HP
import com.graphees.statspos.utils.Resource
import com.graphees.statspos.utils.safeApiCall
import com.google.gson.JsonObject
import javax.inject.Inject

class GatepassesRepositoryImpl @Inject constructor(
    private val api: GatepassesApi
) : GatepassesRepository {
    override suspend fun loadGatepasses(body: JsonObject): Resource<JsonObject> {
        return safeApiCall {
            api.loadGatepasses(
                DB.addParams(body)
            )
        }
    }

    override suspend fun insertGatepass(gatepass: Gatepasses): Resource<JsonObject> {
        val body = DB.getJsonObject(gatepass)
        return safeApiCall {
            api.insertGatepass(
                DB.addParams(body)
            )
        }
    }

    override suspend fun updateGatepass(gatepass: Gatepasses): Resource<JsonObject> {
        val body = DB.getJsonObject(gatepass)
        return safeApiCall {
            api.updateGatepass(
                DB.addParams(body)
            )
        }
    }

    override suspend fun deleteGatepass(id: Long): Resource<JsonObject> {
        val body = JsonObject().apply {
            addProperty("id", id)
            addProperty("userId", HP.user.id)
        }
        return safeApiCall {
            api.deleteGatepass(
                DB.addParams(body)
            )
        }
    }

    override suspend fun getGatepass(id: Long): Resource<JsonObject> {
        val body = JsonObject().apply {
            addProperty("id", id)
        }
        return safeApiCall {
            api.getGatepass(
                DB.addParams(body)
            )
        }
    }

    override suspend fun loadGatepass(id: Long): Resource<JsonObject> {
        val body = JsonObject().apply {
            addProperty("id", id)
        }
        return safeApiCall {
            api.loadGatepass(
                DB.addParams(body)
            )
        }
    }
}
