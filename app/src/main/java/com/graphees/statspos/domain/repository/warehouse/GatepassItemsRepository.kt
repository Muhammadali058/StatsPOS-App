package com.graphees.statspos.domain.repository.warehouse

import com.graphees.statspos.domain.models.warehouse.GatepassItems
import com.graphees.statspos.utils.Resource
import com.google.gson.JsonObject

interface GatepassItemsRepository {
    suspend fun loadGatepassItems(body: JsonObject): Resource<JsonObject>

    suspend fun insertGatepassItem(gatepassItem: GatepassItems): Resource<JsonObject>

    suspend fun updateGatepassItem(gatepassItem: GatepassItems): Resource<JsonObject>

    suspend fun deleteGatepassItem(id: Long): Resource<JsonObject>

    suspend fun getGatepassItem(id: Long): Resource<JsonObject>
}