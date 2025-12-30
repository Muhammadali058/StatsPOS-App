package com.example.statspos.domain.repository.reports

import com.example.statspos.utils.Resource
import com.google.gson.JsonObject

interface ProfitReportsRepository {
    suspend fun billWiseReport(body: JsonObject): Resource<JsonObject>

    suspend fun itemsReport(body: JsonObject): Resource<JsonObject>

    suspend fun briefReport(body: JsonObject): Resource<JsonObject>

    suspend fun chartDaily(body: JsonObject): Resource<JsonObject>

    suspend fun chartWeekly(body: JsonObject): Resource<JsonObject>

    suspend fun chartMonthly(body: JsonObject): Resource<JsonObject>

    suspend fun chartYearly(body: JsonObject): Resource<JsonObject>
}