package com.example.statspos.data.repository.accounts

import com.example.statspos.data.remote.accounts.VendorsApi
import com.example.statspos.domain.models.accounts.Accounts
import com.example.statspos.domain.repository.accounts.VendorsRepository
import com.example.statspos.utils.DB
import com.example.statspos.utils.HP
import com.example.statspos.utils.Resource
import com.example.statspos.utils.safeApiCall
import com.google.gson.JsonObject
import javax.inject.Inject

class VendorsRepositoryImpl @Inject constructor(
    private val api: VendorsApi
) : VendorsRepository {
    override suspend fun loadVendors(body: JsonObject): Resource<JsonObject> {
        return safeApiCall {
            api.loadVendors(
                DB.addParams(body)
            )
        }
    }

    override suspend fun insertVendor(vendor: Accounts): Resource<JsonObject> {
        val body = DB.getJsonObject(vendor)
        return safeApiCall {
            api.insertVendor(
                DB.addParams(body)
            )
        }
    }

    override suspend fun updateVendor(vendor: Accounts): Resource<JsonObject> {
        val body = DB.getJsonObject(vendor)
        return safeApiCall {
            api.updateVendor(
                DB.addParams(body)
            )
        }
    }

    override suspend fun deleteVendor(id: Long): Resource<JsonObject> {
        val body = JsonObject().apply {
            addProperty("id", id)
            addProperty("userId", HP.user.id)
        }
        return safeApiCall {
            api.deleteVendor(
                DB.addParams(body)
            )
        }
    }

    override suspend fun getVendor(id: Long): Resource<JsonObject> {
        val body = JsonObject().apply {
            addProperty("id", id)
        }
        return safeApiCall {
            api.getVendor(
                DB.addParams(body)
            )
        }
    }
}
