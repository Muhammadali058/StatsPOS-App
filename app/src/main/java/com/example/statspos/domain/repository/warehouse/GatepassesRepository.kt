package com.example.statspos.domain.repository.warehouse

import com.example.statspos.domain.models.warehouse.Gatepasses
import com.example.statspos.utils.Resource
import com.google.gson.JsonObject

interface GatepassesRepository {
    suspend fun loadGatepasses(body: JsonObject): Resource<JsonObject>

    suspend fun insertGatepass(gatepass: Gatepasses): Resource<JsonObject>

    suspend fun updateGatepass(gatepass: Gatepasses): Resource<JsonObject>

    suspend fun deleteGatepass(id: Long): Resource<JsonObject>

    suspend fun getGatepass(id: Long): Resource<JsonObject>

    suspend fun loadGatepass(id: Long): Resource<JsonObject>
}