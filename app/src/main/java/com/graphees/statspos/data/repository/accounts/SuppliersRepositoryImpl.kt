package com.graphees.statspos.data.repository.accounts

import com.graphees.statspos.data.remote.accounts.SuppliersApi
import com.graphees.statspos.domain.models.accounts.Accounts
import com.graphees.statspos.domain.repository.accounts.SuppliersRepository
import com.graphees.statspos.utils.DB
import com.graphees.statspos.utils.HP
import com.graphees.statspos.utils.Resource
import com.graphees.statspos.utils.safeApiCall
import com.google.gson.JsonObject
import javax.inject.Inject

class SuppliersRepositoryImpl @Inject constructor(
    private val api: SuppliersApi
) : SuppliersRepository {
    override suspend fun loadSuppliers(body: JsonObject): Resource<JsonObject> {
        return safeApiCall {
            api.loadSuppliers(
                DB.addParams(body)
            )
        }
    }

    override suspend fun insertSupplier(supplier: Accounts): Resource<JsonObject> {
        val body = DB.getJsonObject(supplier)
        return safeApiCall {
            api.insertSupplier(
                DB.addParams(body)
            )
        }
    }

    override suspend fun updateSupplier(supplier: Accounts): Resource<JsonObject> {
        val body = DB.getJsonObject(supplier)
        return safeApiCall {
            api.updateSupplier(
                DB.addParams(body)
            )
        }
    }

    override suspend fun deleteSupplier(id: Long): Resource<JsonObject> {
        val body = JsonObject().apply {
            addProperty("id", id)
            addProperty("userId", HP.user.id)
        }
        return safeApiCall {
            api.deleteSupplier(
                DB.addParams(body)
            )
        }
    }

    override suspend fun getSupplier(id: Long): Resource<JsonObject> {
        val body = JsonObject().apply {
            addProperty("id", id)
        }
        return safeApiCall {
            api.getSupplier(
                DB.addParams(body)
            )
        }
    }
}
