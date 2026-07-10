package com.graphees.statspos.domain.repository.reports

import com.graphees.statspos.utils.Resource
import com.google.gson.JsonObject

interface StockReportsRepository {
    suspend fun mainReport(body: JsonObject): Resource<JsonObject>

    suspend fun stockReport(body: JsonObject): Resource<JsonObject>
}