package com.example.statspos.domain.repository.accounts

import com.example.statspos.domain.models.accounts.Accounts
import com.example.statspos.utils.Resource
import com.google.gson.JsonObject

interface VendorsRepository{
    suspend fun loadVendors(body: JsonObject): Resource<JsonObject>

    suspend fun insertVendor(vendor: Accounts): Resource<JsonObject>

    suspend fun updateVendor(vendor: Accounts): Resource<JsonObject>

    suspend fun deleteVendor(id: Long): Resource<JsonObject>

    suspend fun getVendor(id: Long): Resource<JsonObject>
}