package com.graphees.statspos.domain.repository.reports

import com.graphees.statspos.utils.Resource
import com.google.gson.JsonArray
import com.google.gson.JsonObject

interface SalesReportsRepository {
    suspend fun mainReport(body: JsonObject): Resource<JsonObject>

    suspend fun billWiseReport(body: JsonObject): Resource<JsonObject>

    suspend fun itemsReport(body: JsonObject): Resource<JsonObject>

    suspend fun briefReport(body: JsonObject): Resource<JsonObject>

    suspend fun chartDaily(body: JsonObject): Resource<JsonArray>

    suspend fun chartWeekly(body: JsonObject): Resource<JsonArray>

    suspend fun chartMonthly(body: JsonObject): Resource<JsonArray>

    suspend fun chartYearly(body: JsonObject): Resource<JsonArray>
}