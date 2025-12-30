package com.example.statspos.domain.repository.reports

import com.example.statspos.utils.Resource
import com.google.gson.JsonObject

interface AccountReportsRepository {
    suspend fun ledger(body: JsonObject): Resource<JsonObject>

    suspend fun receipts(body: JsonObject): Resource<JsonObject>

    suspend fun payments(body: JsonObject): Resource<JsonObject>

    suspend fun expenses(body: JsonObject): Resource<JsonObject>

    suspend fun incomeStatement(body: JsonObject): Resource<JsonObject>

    suspend fun cashAccount(body: JsonObject): Resource<JsonObject>

    suspend fun debtors(body: JsonObject): Resource<JsonObject>

    suspend fun creditors(body: JsonObject): Resource<JsonObject>

    suspend fun customersBalanceList(body: JsonObject): Resource<JsonObject>
}