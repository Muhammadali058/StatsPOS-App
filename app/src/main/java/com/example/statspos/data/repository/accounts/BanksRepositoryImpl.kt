package com.example.statspos.data.repository.accounts

import com.example.statspos.data.remote.accounts.BanksApi
import com.example.statspos.domain.models.accounts.Accounts
import com.example.statspos.domain.models.accounts.Banks
import com.example.statspos.domain.repository.accounts.BanksRepository
import com.example.statspos.utils.DB
import com.example.statspos.utils.HP
import com.example.statspos.utils.Resource
import com.example.statspos.utils.safeApiCall
import com.google.gson.JsonObject
import javax.inject.Inject

class BanksRepositoryImpl @Inject constructor(
    private val api: BanksApi
) : BanksRepository {
    // region Banks
    override suspend fun loadBanks(body: JsonObject): Resource<JsonObject> {
        return safeApiCall {
            api.loadBanks(
                DB.addParams(body)
            )
        }
    }

    override suspend fun insertBank(bank: Banks): Resource<JsonObject> {
        val body = DB.getJsonObject(bank)
        return safeApiCall {
            api.insertBank(
                DB.addParams(body)
            )
        }
    }

    override suspend fun updateBank(bank: Banks): Resource<JsonObject> {
        val body = DB.getJsonObject(bank)
        return safeApiCall {
            api.updateBank(
                DB.addParams(body)
            )
        }
    }

    override suspend fun deleteBank(id: Long): Resource<JsonObject> {
        val body = JsonObject().apply {
            addProperty("id", id)
            addProperty("userId", HP.user.id)
        }
        return safeApiCall {
            api.deleteBank(
                DB.addParams(body)
            )
        }
    }

    override suspend fun getBank(id: Long): Resource<JsonObject> {
        val body = JsonObject().apply {
            addProperty("id", id)
        }
        return safeApiCall {
            api.getBank(
                DB.addParams(body)
            )
        }
    }
    // endregion

    // region Sub-Banks
    override suspend fun loadSubBanks(body: JsonObject): Resource<JsonObject> {
        return safeApiCall {
            api.loadSubBanks(
                DB.addParams(body)
            )
        }
    }

    override suspend fun insertSubBank(subBank: Accounts): Resource<JsonObject> {
        val body = DB.getJsonObject(subBank)
        return safeApiCall {
            api.insertSubBank(
                DB.addParams(body)
            )
        }
    }

    override suspend fun updateSubBank(subBank: Accounts): Resource<JsonObject> {
        val body = DB.getJsonObject(subBank)
        return safeApiCall {
            api.updateSubBank(
                DB.addParams(body)
            )
        }
    }

    override suspend fun deleteSubBank(id: Long): Resource<JsonObject> {
        val body = JsonObject().apply {
            addProperty("id", id)
            addProperty("userId", HP.user.id)
        }
        return safeApiCall {
            api.deleteSubBank(
                DB.addParams(body)
            )
        }
    }

    override suspend fun getSubBank(id: Long): Resource<JsonObject> {
        val body = JsonObject().apply {
            addProperty("id", id)
        }
        return safeApiCall {
            api.getSubBank(
                DB.addParams(body)
            )
        }
    }
    // endregion
}
