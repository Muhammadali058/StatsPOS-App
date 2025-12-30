package com.example.statspos.domain.repository.accounts

import com.example.statspos.domain.models.accounts.Accounts
import com.example.statspos.utils.Resource
import com.google.gson.JsonObject

interface EmployeesRepository{
    suspend fun loadEmployees(body: JsonObject): Resource<JsonObject>

    suspend fun insertEmployee(employee: Accounts): Resource<JsonObject>

    suspend fun updateEmployee(employee: Accounts): Resource<JsonObject>

    suspend fun deleteEmployee(id: Long): Resource<JsonObject>

    suspend fun getEmployee(id: Long): Resource<JsonObject>
}