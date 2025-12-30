package com.example.statspos.domain.repository.accounts

import com.example.statspos.domain.models.accounts.Accounts
import com.example.statspos.utils.Resource
import com.google.gson.JsonObject

interface FixedAccountsRepository{
    suspend fun loadFixedAccounts(body: JsonObject): Resource<JsonObject>

    suspend fun insertFixedAccount(fixedAccount: Accounts): Resource<JsonObject>

    suspend fun updateFixedAccount(fixedAccount: Accounts): Resource<JsonObject>

    suspend fun deleteFixedAccount(id: Long): Resource<JsonObject>

    suspend fun getFixedAccount(id: Long): Resource<JsonObject>
}