package com.example.statspos.data.repository.warehouse

import com.example.statspos.data.remote.warehouse.GatepassesApi
import com.example.statspos.domain.models.warehouse.Gatepasses
import com.example.statspos.domain.repository.warehouse.GatepassesRepository
import com.example.statspos.utils.DB
import com.example.statspos.utils.HP
import com.example.statspos.utils.Resource
import com.example.statspos.utils.safeApiCall
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
