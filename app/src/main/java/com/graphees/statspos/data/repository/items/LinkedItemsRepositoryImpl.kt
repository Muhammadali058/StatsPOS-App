package com.graphees.statspos.data.repository.items

import com.graphees.statspos.data.remote.items.LinkedItemsApi
import com.graphees.statspos.domain.models.items.LinkedItems
import com.graphees.statspos.domain.repository.items.LinkedItemsRepository
import com.graphees.statspos.utils.DB
import com.graphees.statspos.utils.Resource
import com.graphees.statspos.utils.safeApiCall
import com.google.gson.JsonObject
import javax.inject.Inject

class LinkedItemsRepositoryImpl @Inject constructor(
    private val api: LinkedItemsApi
) : LinkedItemsRepository {
    override suspend fun loadLinkedItems(body: JsonObject): Resource<JsonObject> {
        return safeApiCall {
            api.loadLinkedItems(
                DB.addParams(body)
            )
        }
    }

    override suspend fun insertLinkedItem(linkedItem: LinkedItems): Resource<JsonObject> {
        val body = DB.getJsonObject(linkedItem)
        return safeApiCall {
            api.insertLinkedItem(
                DB.addParams(body)
            )
        }
    }

    override suspend fun updateLinkedItem(linkedItem: LinkedItems): Resource<JsonObject> {
        val body = DB.getJsonObject(linkedItem)
        return safeApiCall {
            api.updateLinkedItem(
                DB.addParams(body)
            )
        }
    }

    override suspend fun deleteLinkedItem(id: Long): Resource<JsonObject> {
        val body = JsonObject().apply {
            addProperty("id", id)
        }
        return safeApiCall {
            api.deleteLinkedItem(
                DB.addParams(body)
            )
        }
    }

    override suspend fun getLinkedItem(id: Long): Resource<JsonObject> {
        val body = JsonObject().apply {
            addProperty("id", id)
        }
        return safeApiCall {
            api.getLinkedItem(
                DB.addParams(body)
            )
        }
    }
}
