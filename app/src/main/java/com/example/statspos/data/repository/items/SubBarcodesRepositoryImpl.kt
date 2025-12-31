package com.example.statspos.data.repository.items

import com.example.statspos.data.remote.items.SubBarcodesApi
import com.example.statspos.domain.models.items.SubBarcodes
import com.example.statspos.domain.repository.items.SubBarcodesRepository
import com.example.statspos.utils.DB
import com.example.statspos.utils.HP
import com.example.statspos.utils.Resource
import com.example.statspos.utils.safeApiCall
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
