package com.example.statspos.data.repository.accounts

import com.example.statspos.data.remote.accounts.AccountsApi
import com.example.statspos.domain.models.accounts.Entries
import com.example.statspos.domain.repository.accounts.AccountsRepository
import com.example.statspos.utils.DB
import com.example.statspos.utils.HP
import com.example.statspos.utils.Resource
import com.example.statspos.utils.safeApiCall
import com.google.gson.JsonObject
import javax.inject.Inject

class AccountsRepositoryImpl @Inject constructor(
    private val api: AccountsApi
) : AccountsRepository {

    override suspend fun getBalance(accountId: Long): Resource<JsonObject> {
        val body = JsonObject().apply {
            addProperty("accountId", accountId)
        }
        return safeApiCall {
            api.getBalance(
                DB.addParams(body)
            )
        }
    }

    override suspend fun loadEntries(body: JsonObject): Resource<JsonObject> {
        return safeApiCall {
            api.loadEntries(
                DB.addParams(body)
            )
        }
    }

    override suspend fun passEntry(entry: Entries): Resource<JsonObject> {
        val body = DB.getJsonObject(entry)
        body.addProperty("userId", HP.user.id)

        return safeApiCall {
            api.passEntry(
                DB.addParams(body)
            )
        }
    }

    override suspend fun deleteEntry(id: Long): Resource<JsonObject> {
        val body = JsonObject().apply {
            addProperty("id", id)
            addProperty("userId", HP.user.id)
        }
        return safeApiCall {
            api.deleteEntry(
                DB.addParams(body)
            )
        }
    }

    override suspend fun getEntry(id: Long): Resource<JsonObject> {
        val body = JsonObject().apply {
            addProperty("id", id)
        }
        return safeApiCall {
            api.getEntry(
                DB.addParams(body)
            )
        }
    }

    override suspend fun loadDuePayments(body: JsonObject): Resource<JsonObject> {
        return safeApiCall {
            api.loadDuePayments(
                DB.addParams(body)
            )
        }
    }

    override suspend fun deleteDuePayment(id: Long): Resource<JsonObject> {
        val body = JsonObject().apply {
            addProperty("id", id)
        }
        return safeApiCall {
            api.deleteDuePayment(
                DB.addParams(body)
            )
        }
    }
}
