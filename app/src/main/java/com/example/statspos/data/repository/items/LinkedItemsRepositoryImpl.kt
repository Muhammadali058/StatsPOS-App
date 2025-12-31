package com.example.statspos.data.repository.items

import com.example.statspos.data.remote.items.LinkedItemsApi
import com.example.statspos.domain.models.accounts.Accounts
import com.example.statspos.domain.models.items.LinkedItems
import com.example.statspos.domain.repository.items.LinkedItemsRepository
import com.example.statspos.utils.DB
import com.example.statspos.utils.HP
import com.example.statspos.utils.Resource
import com.example.statspos.utils.safeApiCall
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
