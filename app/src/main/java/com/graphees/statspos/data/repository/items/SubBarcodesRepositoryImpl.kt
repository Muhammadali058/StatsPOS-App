package com.graphees.statspos.data.repository.items

import com.graphees.statspos.data.remote.items.SubBarcodesApi
import com.graphees.statspos.domain.models.items.SubBarcodes
import com.graphees.statspos.domain.repository.items.SubBarcodesRepository
import com.graphees.statspos.utils.DB
import com.graphees.statspos.utils.HP
import com.graphees.statspos.utils.Resource
import com.graphees.statspos.utils.safeApiCall
import com.google.gson.JsonObject
import javax.inject.Inject

class SubBarcodesRepositoryImpl @Inject constructor(
    private val api: SubBarcodesApi
) : SubBarcodesRepository {
    override suspend fun loadSubBarcodes(body: JsonObject): Resource<JsonObject> {
        return safeApiCall {
            api.loadSubBarcodes(
                DB.addParams(body)
            )
        }
    }

    override suspend fun insertSubBarcode(subBarcode: SubBarcodes): Resource<JsonObject> {
        val body = DB.getJsonObject(subBarcode)
        body.addProperty("branchGroupId", HP.branchGroupId)

        return safeApiCall {
            api.insertSubBarcode(
                DB.addParams(body)
            )
        }
    }

    override suspend fun updateSubBarcode(subBarcode: SubBarcodes): Resource<JsonObject> {
        val body = DB.getJsonObject(subBarcode)
        body.addProperty("branchGroupId", HP.branchGroupId)

        return safeApiCall {
            api.updateSubBarcode(
                DB.addParams(body)
            )
        }
    }

    override suspend fun deleteSubBarcode(id: Long): Resource<JsonObject> {
        val body = JsonObject().apply {
            addProperty("id", id)
            addProperty("userId", HP.user.id)
        }
        return safeApiCall {
            api.deleteSubBarcode(
                DB.addParams(body)
            )
        }
    }

    override suspend fun getSubBarcode(id: Long): Resource<JsonObject> {
        val body = JsonObject().apply {
            addProperty("id", id)
        }
        return safeApiCall {
            api.getSubBarcode(
                DB.addParams(body)
            )
        }
    }
}
