package com.example.statspos.domain.repository.accounts

import com.example.statspos.domain.models.accounts.Accounts
import com.example.statspos.utils.Resource
import com.google.gson.JsonObject

interface SuppliersRepository{
    suspend fun loadSuppliers(body: JsonObject): Resource<JsonObject>

    suspend fun insertSupplier(supplier: Accounts): Resource<JsonObject>

    suspend fun updateSupplier(supplier: Accounts): Resource<JsonObject>

    suspend fun deleteSupplier(id: Long): Resource<JsonObject>

    suspend fun getSupplier(id: Long): Resource<JsonObject>
}