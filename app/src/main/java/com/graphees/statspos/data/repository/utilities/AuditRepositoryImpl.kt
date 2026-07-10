package com.graphees.statspos.data.repository.utilities

import com.graphees.statspos.data.remote.utilities.AuditApi
import com.graphees.statspos.domain.models.utilities.AuditItems
import com.graphees.statspos.domain.repository.utilities.AuditRepository
import com.graphees.statspos.utils.DB
import com.graphees.statspos.utils.Resource
import com.graphees.statspos.utils.safeApiCall
import com.google.gson.JsonObject
import javax.inject.Inject

class AuditRepositoryImpl @Inject constructor(
    private val api: AuditApi
) : AuditRepository {
    override suspend fun loadAuditItems(body: JsonObject): Resource<JsonObject> {
        return safeApiCall {
            api.loadAuditItems(
                DB.addParams(body)
            )
        }
    }

    override suspend fun insertAuditItem(auditItem: AuditItems): Resource<JsonObject> {
        val body = DB.getJsonObject(auditItem)
        return safeApiCall {
            api.insertAuditItem(
                DB.addParams(body)
            )
        }
    }

    override suspend fun updateAuditItem(auditItem: AuditItems): Resource<JsonObject> {
        val body = DB.getJsonObject(auditItem)
        return safeApiCall {
            api.updateAuditItem(
                DB.addParams(body)
            )
        }
    }

    override suspend fun deleteAuditItem(id: Long): Resource<JsonObject> {
        val body = JsonObject().apply {
            addProperty("id", id)
        }
        return safeApiCall {
            api.deleteAuditItem(
                DB.addParams(body)
            )
        }
    }

    override suspend fun deleteAllAuditItems(warehouseId: Long): Resource<JsonObject> {
        val body = JsonObject().apply {
            addProperty("warehouseId", warehouseId)
        }
        return safeApiCall {
            api.deleteAllAuditItems(
                DB.addParams(body)
            )
        }
    }

    override suspend fun getAuditItem(id: Long): Resource<JsonObject> {
        val body = JsonObject().apply {
            addProperty("id", id)
        }
        return safeApiCall {
            api.getAuditItem(
                DB.addParams(body)
            )
        }
    }

    override suspend fun getAuditReport(body: JsonObject): Resource<JsonObject> {
        return safeApiCall {
            api.getAuditReport(
                DB.addParams(body)
            )
        }
    }

    override suspend fun postAudit(body: JsonObject): Resource<JsonObject> {
        return safeApiCall {
            api.postAudit(
                DB.addParams(body)
            )
        }
    }
}
