package com.example.statspos.data.repository.purchase

import com.example.statspos.data.remote.purchase.RejectedItemsApi
import com.example.statspos.domain.models.purchase.RejectedItems
import com.example.statspos.domain.repository.purchase.RejectedItemsRepository
import com.example.statspos.utils.DB
import com.example.statspos.utils.HP
import com.example.statspos.utils.Resource
import com.example.statspos.utils.safeApiCall
import com.google.gson.JsonObject
import javax.inject.Inject

class RejectedItemsRepositoryImpl @Inject constructor(
    private val api: RejectedItemsApi
) : RejectedItemsRepository {
    override suspend fun loadRejectedItems(body: JsonObject): Resource<JsonObject> {
        return safeApiCall {
            api.loadRejectedItems(
                DB.addParams(body)
            )
        }
    }

    override suspend fun insertRejectedItem(rejectedItem: RejectedItems): Resource<JsonObject> {
        val body = DB.getJsonObject(rejectedItem)
        return safeApiCall {
            api.insertRejectedItem(
                DB.addParams(body)
            )
        }
    }

    override suspend fun updateRejectedItem(rejectedItem: RejectedItems): Resource<JsonObject> {
        val body = DB.getJsonObject(rejectedItem)
        return safeApiCall {
            api.updateRejectedItem(
                DB.addParams(body)
            )
        }
    }

    override suspend fun deleteRejectedItem(id: Long): Resource<JsonObject> {
        val body = JsonObject().apply {
            addProperty("id", id)
            addProperty("userId", HP.user.id)
        }
        return safeApiCall {
            api.deleteRejectedItem(
                DB.addParams(body)
            )
        }
    }

    override suspend fun getRejectedItem(id: Long): Resource<JsonObject> {
        val body = JsonObject().apply {
            addProperty("id", id)
        }
        return safeApiCall {
            api.getRejectedItem(
                DB.addParams(body)
            )
        }
    }
}
