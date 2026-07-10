package com.graphees.statspos.domain.repository.utilities

import com.graphees.statspos.domain.models.utilities.AuditItems
import com.graphees.statspos.utils.Resource
import com.google.gson.JsonObject

interface AuditRepository {
    suspend fun loadAuditItems(body: JsonObject): Resource<JsonObject>

    suspend fun insertAuditItem(auditItem: AuditItems): Resource<JsonObject>

    suspend fun updateAuditItem(auditItem: AuditItems): Resource<JsonObject>

    suspend fun deleteAuditItem(id: Long): Resource<JsonObject>

    suspend fun deleteAllAuditItems(warehouseId: Long): Resource<JsonObject>

    suspend fun getAuditItem(id: Long): Resource<JsonObject>

    suspend fun getAuditReport(body: JsonObject): Resource<JsonObject>

    suspend fun postAudit(body: JsonObject): Resource<JsonObject>
}