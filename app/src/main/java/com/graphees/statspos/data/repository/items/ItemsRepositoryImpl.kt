package com.graphees.statspos.data.repository.items

import com.graphees.statspos.data.remote.items.ItemsApi
import com.graphees.statspos.domain.models.items.Items
import com.graphees.statspos.domain.repository.items.ItemsRepository
import com.graphees.statspos.utils.DB
import com.graphees.statspos.utils.HP
import com.graphees.statspos.utils.Resource
import com.graphees.statspos.utils.safeApiCall
import com.google.gson.JsonObject
import javax.inject.Inject

class ItemsRepositoryImpl @Inject constructor(
    private val api: ItemsApi
) : ItemsRepository {
    override suspend fun loadItems(body: JsonObject): Resource<JsonObject> {
        body.addProperty("branchGroupId", HP.branchGroupId)

        return safeApiCall {
            api.loadItems(
                DB.addParams(body)
            )
        }
    }

    override suspend fun insertItem(item: Items): Resource<JsonObject> {
        val body = DB.getJsonObject(item)
        body.addProperty("userId", HP.user.id)
        body.addProperty("branchGroupId", HP.branchGroupId)

        return safeApiCall {
            api.insertItem(
                DB.addParams(body)
            )
        }
    }

    override suspend fun updateItem(item: Items): Resource<JsonObject> {
        val body = DB.getJsonObject(item)
        body.addProperty("userId", HP.user.id)
        body.addProperty("branchGroupId", HP.branchGroupId)

        return safeApiCall {
            api.updateItem(
                DB.addParams(body)
            )
        }
    }

    override suspend fun deleteItem(id: Long): Resource<JsonObject> {
        val body = JsonObject().apply {
            addProperty("id", id)
            addProperty("userId", HP.user.id)
        }

        return safeApiCall {
            api.deleteItem(
                DB.addParams(body)
            )
        }
    }

    override suspend fun getItem(id: Long): Resource<JsonObject> {
        val body = JsonObject().apply {
            addProperty("id", id)
        }

        return safeApiCall {
            api.getItem(
                DB.addParams(body)
            )
        }
    }

    override suspend fun isBarcodeExists(barcode: String): Resource<JsonObject> {
        val body = JsonObject().apply {
            addProperty("barcode", barcode)
            addProperty("branchGroupId", HP.branchGroupId)
        }

        return safeApiCall {
            api.isBarcodeExists(
                DB.addParams(body)
            )
        }
    }

    override suspend fun getBarcode(): Resource<JsonObject> {
        val body = JsonObject().apply {
            addProperty("branchGroupId", HP.branchGroupId)
        }

        return safeApiCall {
            api.getBarcode(
                DB.addParams(body)
            )
        }
    }
}
