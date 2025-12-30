package com.example.statspos.domain.repository.accounts

import com.example.statspos.domain.models.accounts.AccountCategories
import com.example.statspos.utils.Resource
import com.google.gson.JsonObject

interface AccountCategoriesRepository{
    suspend fun loadAccountCategories(body: JsonObject): Resource<JsonObject>

    suspend fun insertAccountCategory(accountCategory: AccountCategories): Resource<JsonObject>

    suspend fun updateAccountCategory(accountCategory: AccountCategories): Resource<JsonObject>

    suspend fun deleteAccountCategory(id: Long): Resource<JsonObject>

    suspend fun getAccountCategory(id: Long): Resource<JsonObject>
}