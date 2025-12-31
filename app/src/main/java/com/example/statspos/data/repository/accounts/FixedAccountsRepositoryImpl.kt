package com.example.statspos.data.repository.accounts

import com.example.statspos.data.remote.accounts.FixedAccountsApi
import com.example.statspos.domain.models.accounts.Accounts
import com.example.statspos.domain.repository.accounts.FixedAccountsRepository
import com.example.statspos.utils.DB
import com.example.statspos.utils.HP
import com.example.statspos.utils.Resource
import com.example.statspos.utils.safeApiCall
import com.google.gson.JsonObject
import javax.inject.Inject

class FixedAccountsRepositoryImpl @Inject constructor(
    private val api: FixedAccountsApi
) : FixedAccountsRepository {
    override suspend fun loadFixedAccounts(body: JsonObject): Resource<JsonObject> {
        return safeApiCall {
            api.loadFixedAccounts(
                DB.addParams(body)
            )
        }
    }

    override suspend fun insertFixedAccount(fixedAccount: Accounts): Resource<JsonObject> {
        val body = DB.getJsonObject(fixedAccount)
        return safeApiCall {
            api.insertFixedAccount(
                DB.addParams(body)
            )
        }
    }

    override suspend fun updateFixedAccount(fixedAccount: Accounts): Resource<JsonObject> {
        val body = DB.getJsonObject(fixedAccount)
        return safeApiCall {
            api.updateFixedAccount(
                DB.addParams(body)
            )
        }
    }

    override suspend fun deleteFixedAccount(id: Long): Resource<JsonObject> {
        val body = JsonObject().apply {
            addProperty("id", id)
            addProperty("userId", HP.user.id)
        }
        return safeApiCall {
            api.deleteFixedAccount(
                DB.addParams(body)
            )
        }
    }

    override suspend fun getFixedAccount(id: Long): Resource<JsonObject> {
        val body = JsonObject().apply {
            addProperty("id", id)
        }
        return safeApiCall {
            api.getFixedAccount(
                DB.addParams(body)
            )
        }
    }
}
