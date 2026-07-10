package com.graphees.statspos.domain.repository.purchase

import com.graphees.statspos.domain.models.purchase.RejectedItems
import com.graphees.statspos.utils.Resource
import com.google.gson.JsonObject

interface RejectedItemsRepository {
    suspend fun loadRejectedItems(body: JsonObject): Resource<JsonObject>

    suspend fun insertRejectedItem(rejectedItem: RejectedItems): Resource<JsonObject>

    suspend fun updateRejectedItem(rejectedItem: RejectedItems): Resource<JsonObject>

    suspend fun deleteRejectedItem(id: Long): Resource<JsonObject>

    suspend fun getRejectedItem(id: Long): Resource<JsonObject>
}