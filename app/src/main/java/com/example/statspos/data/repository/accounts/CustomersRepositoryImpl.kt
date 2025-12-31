package com.example.statspos.data.repository.accounts

import com.example.statspos.data.remote.accounts.CustomersApi
import com.example.statspos.domain.models.accounts.Accounts
import com.example.statspos.domain.repository.accounts.CustomersRepository
import com.example.statspos.utils.DB
import com.example.statspos.utils.HP
import com.example.statspos.utils.Resource
import com.example.statspos.utils.safeApiCall
import com.google.gson.JsonObject
import javax.inject.Inject

class CustomersRepositoryImpl @Inject constructor(
    private val api: CustomersApi
) : CustomersRepository {

    override suspend fun loadCustomers(body: JsonObject): Resource<JsonObject> {
        return safeApiCall {
            api.loadCustomers(
                DB.addParams(body)
            )
        }
    }

    override suspend fun insertCustomer(customer: Accounts): Resource<JsonObject> {
        val body = DB.getJsonObject(customer)
        return safeApiCall {
            api.insertCustomer(
                DB.addParams(body)
            )
        }
    }

    override suspend fun updateCustomer(customer: Accounts): Resource<JsonObject> {
        val body = DB.getJsonObject(customer)
        return safeApiCall {
            api.updateCustomer(
                DB.addParams(body)
            )
        }
    }

    override suspend fun deleteCustomer(id: Long): Resource<JsonObject> {
        val body = JsonObject().apply {
            addProperty("id", id)
            addProperty("userId", HP.user.id)
        }
        return safeApiCall {
            api.deleteCustomer(
                DB.addParams(body)
            )
        }
    }

    override suspend fun getCustomer(id: Long): Resource<JsonObject> {
        val body = JsonObject().apply {
            addProperty("id", id)
        }
        return safeApiCall {
            api.getCustomer(
                DB.addParams(body)
            )
        }
    }
}
