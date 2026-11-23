package com.graphees.statspos.data.repository.accounts

import com.google.gson.JsonObject
import com.graphees.statspos.data.remote.accounts.BanksApi
import com.graphees.statspos.domain.models.accounts.Accounts
import com.graphees.statspos.domain.repository.accounts.BanksRepository
import com.graphees.statspos.utils.DB
import com.graphees.statspos.utils.HP
import com.graphees.statspos.utils.Resource
import com.graphees.statspos.utils.safeApiCall
import javax.inject.Inject

class BanksRepositoryImpl @Inject constructor(
    private val api: BanksApi
) : BanksRepository {

    override suspend fun loadBanks(body: JsonObject): Resource<JsonObject> {
        return safeApiCall {
            api.loadBanks(
                DB.addParams(body)
            )
        }
    }

    override suspend fun insertBank(bank: Accounts): Resource<JsonObject> {
        val body = DB.getJsonObject(bank)
        return safeApiCall {
            api.insertBank(
                DB.addParams(body)
            )
        }
    }

    override suspend fun updateBank(bank: Accounts): Resource<JsonObject> {
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

}
