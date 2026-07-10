package com.graphees.statspos.domain.repository.accounts

import com.graphees.statspos.domain.models.accounts.Accounts
import com.graphees.statspos.utils.Resource
import com.google.gson.JsonObject

interface CustomersRepository{
    suspend fun loadCustomers(body: JsonObject): Resource<JsonObject>

    suspend fun insertCustomer(customer: Accounts): Resource<JsonObject>

    suspend fun updateCustomer(customer: Accounts): Resource<JsonObject>

    suspend fun deleteCustomer(id: Long): Resource<JsonObject>

    suspend fun getCustomer(id: Long): Resource<JsonObject>
}