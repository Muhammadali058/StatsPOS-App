package com.graphees.statspos.domain.repository.items

import com.graphees.statspos.domain.models.items.LinkedItems
import com.graphees.statspos.utils.Resource
import com.google.gson.JsonObject

interface LinkedItemsRepository {
    suspend fun loadLinkedItems(body: JsonObject): Resource<JsonObject>

    suspend fun insertLinkedItem(linkedItem: LinkedItems): Resource<JsonObject>

    suspend fun updateLinkedItem(linkedItem: LinkedItems): Resource<JsonObject>

    suspend fun deleteLinkedItem(id: Long): Resource<JsonObject>

    suspend fun getLinkedItem(id: Long): Resource<JsonObject>
}