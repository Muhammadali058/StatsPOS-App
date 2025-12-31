package com.example.statspos.data.repository.accounts

import com.example.statspos.data.remote.accounts.AccountCategoriesApi
import com.example.statspos.domain.models.accounts.AccountCategories
import com.example.statspos.domain.repository.accounts.AccountCategoriesRepository
import com.example.statspos.utils.DB
import com.example.statspos.utils.HP
import com.example.statspos.utils.Resource
import com.example.statspos.utils.safeApiCall
import com.google.gson.JsonObject
import javax.inject.Inject

class AccountCategoriesRepositoryImpl @Inject constructor(
    private val api: AccountCategoriesApi
) : AccountCategoriesRepository {
    override suspend fun loadAccountCategories(body: JsonObject): Resource<JsonObject> {
        return safeApiCall {
            api.loadAccountCategories(
                DB.addParams(body)
            )
        }
    }

    override suspend fun insertAccountCategory(accountCategory: AccountCategories): Resource<JsonObject> {
        val body = DB.getJsonObject(accountCategory)
        return safeApiCall {
            api.insertAccountCategory(
                DB.addParams(body)
            )
        }
    }

    override suspend fun updateAccountCategory(accountCategory: AccountCategories): Resource<JsonObject> {
        val body = DB.getJsonObject(accountCategory)
        return safeApiCall {
            api.updateAccountCategory(
                DB.addParams(body)
            )
        }
    }

    override suspend fun deleteAccountCategory(id: Long): Resource<JsonObject> {
        val body = JsonObject().apply {
            addProperty("id", id)
            addProperty("userId", HP.user.id)
        }
        return safeApiCall {
            api.deleteAccountCategory(
                DB.addParams(body)
            )
        }
    }

    override suspend fun getAccountCategory(id: Long): Resource<JsonObject> {
        val body = JsonObject().apply {
            addProperty("id", id)
        }
        return safeApiCall {
            api.getAccountCategory(
                DB.addParams(body)
            )
        }
    }
}
