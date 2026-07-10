package com.graphees.statspos.domain.repository.reports

import com.graphees.statspos.utils.Resource
import com.google.gson.JsonObject

interface ItemsReportsRepository {
    suspend fun itemsList(body: JsonObject): Resource<JsonObject>

    suspend fun itemsRateChangedList(body: JsonObject): Resource<JsonObject>
}