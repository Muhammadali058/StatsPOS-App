package com.example.statspos.domain.repository.accounts

import com.example.statspos.domain.models.accounts.Accounts
import com.example.statspos.utils.Resource
import com.google.gson.JsonObject

interface CustomersRepository{
    suspend fun loadCustomers(body: JsonObject): Resource<JsonObject>

    suspend fun insertCustomer(customer: Accounts): Resource<JsonObject>

    suspend fun updateCustomer(customer: Accounts): Resource<JsonObject>

    suspend fun deleteCustomer(id: Long): Resource<JsonObject>

    suspend fun getCustomer(id: Long): Resource<JsonObject>
}