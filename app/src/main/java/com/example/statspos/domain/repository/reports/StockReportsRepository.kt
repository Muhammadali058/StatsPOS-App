package com.example.statspos.domain.repository.reports

import com.example.statspos.utils.Resource
import com.google.gson.JsonObject

interface StockReportsRepository {
    suspend fun stockReport(body: JsonObject): Resource<JsonObject>
}