package com.graphees.statspos.data.repository.accounts

import com.graphees.statspos.data.remote.accounts.EmployeesApi
import com.graphees.statspos.domain.models.accounts.Accounts
import com.graphees.statspos.domain.repository.accounts.EmployeesRepository
import com.graphees.statspos.utils.DB
import com.graphees.statspos.utils.HP
import com.graphees.statspos.utils.Resource
import com.graphees.statspos.utils.safeApiCall
import com.google.gson.JsonObject
import javax.inject.Inject

class EmployeesRepositoryImpl @Inject constructor(
    private val api: EmployeesApi
) : EmployeesRepository {

    override suspend fun loadEmployees(body: JsonObject): Resource<JsonObject> {
        return safeApiCall {
            api.loadEmployees(
                DB.addParams(body)
            )
        }
    }

    override suspend fun insertEmployee(employee: Accounts): Resource<JsonObject> {
        val body = DB.getJsonObject(employee)
        return safeApiCall {
            api.insertEmployee(
                DB.addParams(body)
            )
        }
    }

    override suspend fun updateEmployee(employee: Accounts): Resource<JsonObject> {
        val body = DB.getJsonObject(employee)
        return safeApiCall {
            api.updateEmployee(
                DB.addParams(body)
            )
        }
    }

    override suspend fun deleteEmployee(id: Long): Resource<JsonObject> {
        val body = JsonObject().apply {
            addProperty("id", id)
            addProperty("userId", HP.user.id)
        }
        return safeApiCall {
            api.deleteEmployee(
                DB.addParams(body)
            )
        }
    }

    override suspend fun getEmployee(id: Long): Resource<JsonObject> {
        val body = JsonObject().apply {
            addProperty("id", id)
        }
        return safeApiCall {
            api.getEmployee(
                DB.addParams(body)
            )
        }
    }
}
