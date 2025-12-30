package com.example.statspos.domain.repository.accounts

import com.example.statspos.domain.models.accounts.Entries
import com.example.statspos.domain.models.items.Categories
import com.example.statspos.utils.Resource
import com.google.gson.JsonObject

interface AccountsRepository {
    suspend fun getBalance(accountId: Long): Resource<JsonObject>

    suspend fun loadEntries(body: JsonObject): Resource<JsonObject>

    suspend fun passEntry(entry: Entries): Resource<JsonObject>

    suspend fun deleteEntry(id: Long): Resource<JsonObject>

    suspend fun getEntry(id: Long): Resource<JsonObject>

    suspend fun loadDuePayments(body: JsonObject): Resource<JsonObject>

    suspend fun deleteDuePayment(id: Long): Resource<JsonObject>
}