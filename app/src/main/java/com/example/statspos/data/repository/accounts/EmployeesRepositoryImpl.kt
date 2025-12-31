package com.example.statspos.data.repository.accounts

import com.example.statspos.data.remote.accounts.EmployeesApi
import com.example.statspos.domain.models.accounts.Accounts
import com.example.statspos.domain.repository.accounts.EmployeesRepository
import com.example.statspos.utils.DB
import com.example.statspos.utils.HP
import com.example.statspos.utils.Resource
import com.example.statspos.utils.safeApiCall
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
