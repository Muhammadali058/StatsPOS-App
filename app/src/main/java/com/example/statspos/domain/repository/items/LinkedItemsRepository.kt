package com.example.statspos.domain.repository.items

import com.example.statspos.domain.models.items.LinkedItems
import com.example.statspos.utils.Resource
import com.google.gson.JsonObject

interface LinkedItemsRepository {
    suspend fun loadLinkedItems(body: JsonObject): Resource<JsonObject>

    suspend fun insertLinkedItem(linkedItem: LinkedItems): Resource<JsonObject>

    suspend fun updateLinkedItem(linkedItem: LinkedItems): Resource<JsonObject>

    suspend fun deleteLinkedItem(id: Long): Resource<JsonObject>

    suspend fun getLinkedItem(id: Long): Resource<JsonObject>
}