package com.example.statspos.domain.repository.sales

import com.example.statspos.utils.Resource
import com.google.gson.JsonObject

interface SalesOrdersRepository {
    suspend fun orderValidation(jsonObject: JsonObject): Resource<JsonObject>
}