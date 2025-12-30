package com.example.statspos.data.remote.utilities

import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuditItemsApi {
    @POST("audit/loadAuditItems")
    suspend fun loadAuditItems(@Body body: JsonObject): Response<JsonObject>

    @POST("audit/insertAuditItem")
    suspend fun insertAuditItem(@Body body: JsonObject): Response<JsonObject>

    @POST("audit/updateAuditItem")
    suspend fun updateAuditItem(@Body body: JsonObject): Response<JsonObject>

    @POST("audit/deleteAuditItem")
    suspend fun deleteAuditItem(@Body body: JsonObject): Response<JsonObject>

    @POST("audit/deleteAllAuditItems")
    suspend fun deleteAllAuditItems(@Body body: JsonObject): Response<JsonObject>

    @POST("audit/getAuditItem")
    suspend fun getAuditItem(@Body body: JsonObject): Response<JsonObject>

    @POST("audit/auditReport")
    suspend fun getAuditReport(@Body body: JsonObject): Response<JsonObject>

    @POST("audit/postAudit")
    suspend fun postAudit(@Body body: JsonObject): Response<JsonObject>
}